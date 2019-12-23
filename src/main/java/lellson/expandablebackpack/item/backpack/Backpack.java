package lellson.expandablebackpack.item.backpack;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.events.KeyHandler;
import lellson.expandablebackpack.inventory.container.ContainerUpgrade;
import lellson.expandablebackpack.inventory.gui.GUIUpgrade;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.inventory.iinventory.UpgradeInventory;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.PlayerHelper;
import lellson.expandablebackpack.misc.StringHelper;
import lellson.expandablebackpack.misc.models.ModelBackpack;
import lellson.expandablebackpack.network.ClientNetworkHandler;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.audio.ElytraSound;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Backpack extends ItemArmor {

	public static final String TAGSLOTS = "slots";
	public static final String TAGOWNER = "owner";
	public static final String TAGPUBLIC = "public";
	public static final String TAGISARMORD = "armored";
	public static final String TAGSENDING = "sending";
	public static final String TAGLIQUID = "liquid";
	public static final String TAGLIQUIDAMOUNT = "liquid_amount";
	
	public static final String[] colors = {"default", "black", "red", "green", "brown", "blue", "purple", "cyan", "lightGray", "gray", "pink", "lime", "yellow", "lightBlue", "magenta", "orange", "white"};

	public Backpack(ArmorMaterial material, String name) {
		super(material, 0, EntityEquipmentSlot.CHEST);
		setHasSubtypes(true);
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(null);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {

		return new ModelBackpack(0.25F);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack item) {
    	
        int i = MathHelper.clamp_int(item.getItemDamage(), 0, (colors.length - 1));
        return super.getUnlocalizedName() + "." + colors[i];
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    	
        for (int i = 0; i < colors.length; ++i) 
        {
            list.add(new ItemStack(item, 1, i));
        }
    }
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
		
		EnumColor color = EnumColor.getEnumFromMeta(stack.getItemDamage());

		return ExpandableBackpack.MODID + ":textures/models/armor/backpack_" + color.getName() + ".png";
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}
	
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
		
		createUpdateTag(stack, entity);
			
		if (entity instanceof EntityPlayer) 
		{
			EntityPlayer player = (EntityPlayer) entity;
			ContainerUpgrade.updateBackpack(player, stack);
		}
	}

	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		
		createUpdateTag(stack, player);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		
		ActionResult<ItemStack> result = new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
		
		if (storeLiquid(player, world, stack)) 
			return result;
		
		ServerNetworkHandler.openBackpack(stack, player, EnumTab.BACKPACK.getName());

		return result;
	}
	
	private boolean storeLiquid(EntityPlayer player, World world, ItemStack backpack) {
		
		if (getUpgrade(backpack, EnumCompartment.TANK.getId()) > 0 && isOwner(player, backpack, true)) 
		{
			RayTraceResult trace = getLiquidTrace(world, player, backpack);
			NBTTagCompound tag = backpack.getTagCompound();
			
			if (trace != null) 
			{
				BlockPos pos = trace.getBlockPos();
				Block block = world.getBlockState(pos).getBlock();
				
				tag.setString(TAGLIQUID, Block.REGISTRY.getNameForObject(block).toString());
				tag.setInteger(TAGLIQUIDAMOUNT, tag.getInteger(TAGLIQUIDAMOUNT) + 1);
				
				world.setBlockToAir(pos);
				player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
				
				return true;
			}
			else if (!tag.getString(TAGLIQUID).equals("") && tag.getInteger(TAGLIQUIDAMOUNT) > 0 && player.isSneaking()) 
			{
				RayTraceResult trace2 = this.rayTrace(world, player, false);
				if (trace2 == null) return false;
				BlockPos pos = world.getBlockState(trace2.getBlockPos()).getBlock().isReplaceable(world, trace2.getBlockPos()) && trace2.sideHit == EnumFacing.UP ? trace2.getBlockPos() : trace2.getBlockPos().offset(trace2.sideHit);
	            IBlockState iblockstate = world.getBlockState(pos);
	            Block block = iblockstate.getBlock();
	            
	            if (!block.isReplaceable(world, pos) || iblockstate.getMaterial().isLiquid() || iblockstate.getMaterial().isSolid()) 
	            {
	            	return false;
	            }
	            
	            Block place = Block.REGISTRY.getObject(new ResourceLocation(tag.getString(TAGLIQUID)));
	            tag.setInteger(TAGLIQUIDAMOUNT, tag.getInteger(TAGLIQUIDAMOUNT) - 1);
	            
	            if (tag.getInteger(TAGLIQUIDAMOUNT) < 1) 
	            {
	            	tag.setInteger(TAGLIQUIDAMOUNT, 0);
	            	tag.setString(TAGLIQUID, "");
	            }
	            
	            world.playSound(player, pos, SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
	            world.setBlockState(pos, place.getDefaultState(), 11);
	            
	            // -.- TODO: Better way
	            BlockPos pos2 = new BlockPos(pos.getX(), pos.getY() + 1, pos.getZ());
	            world.setBlockState(pos2, Blocks.STONE.getDefaultState());
	            world.setBlockToAir(pos2);
	            
	            return true;
			}
		}
		
		return false;
	}

	private RayTraceResult getLiquidTrace(World world, EntityPlayer player, ItemStack backpack) {
		
		RayTraceResult trace = this.rayTrace(world, player, true);
		
		if (trace == null) return null;
		
		Block hit = world.getBlockState(trace.getBlockPos()).getBlock();
		if (hit instanceof BlockFluidBase || hit instanceof BlockLiquid) 
		{
			String liquid = backpack.getTagCompound().getString(TAGLIQUID);
			int amount = backpack.getTagCompound().getInteger(TAGLIQUIDAMOUNT);
			return ((liquid.equals("") || liquid.equals(Block.REGISTRY.getNameForObject(hit).toString())) && amount < maxLiquidAmount(backpack)) ? trace : null;
		}

		return null;
	}

	public static boolean openEnderChest(ItemStack backpack, EntityPlayer player) {
		
		if (getUpgrade(backpack, EnumCompartment.ENDER.getId()) > 0) {
			
			InventoryEnderChest invEnder = player.getInventoryEnderChest();
			
			if (invEnder != null)
				player.displayGUIChest(invEnder);
			
			if (player instanceof EntityPlayerMP)
				ExpandableBackpack.networkClient.sendTo(new ClientNetworkHandler(2), (EntityPlayerMP) player);
			
			return true;
		}
		
		return false;
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if (player.isSneaking() && isOwner(player, stack, true)) {
			
			NBTTagCompound nbt = stack.getTagCompound();
			IInventory inv = TileEntityHopper.getInventoryAtPosition(world, pos.getX(), pos.getY(), pos.getZ());
			
			if (getUpgrade(stack,  EnumCompartment.SENDING.getId()) > 0 && nbt != null) {
				
				if (inv != null) {
					
					String position = (player.dimension + "," + pos.getX() + "," + pos.getY() + "," + pos.getZ());
					
					if (position.equals(nbt.getString(TAGSENDING))) {
						nbt.removeTag(TAGSENDING);
						if (!world.isRemote) player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Location removed!"));	
					} else {
						nbt.setString(TAGSENDING, position);
						if (!world.isRemote) player.addChatMessage(new TextComponentString(ChatFormatting.GREEN + "Location set! (" + position + ")"));	
					}
					
					return EnumActionResult.SUCCESS;
				}
			
			}
			
			if (getUpgrade(stack, EnumCompartment.EMPTY.getId()) > 0) {
				
				if (inv != null) {
					
					for (int i = 0; i < BackpackInventory.getSlots(stack); i++) 
					{
						ItemStack item = BackpackInventory.getStackForSlot(stack, i);
						
						ItemStack left = TileEntityHopper.putStackInInventoryAllSlots(inv, item, null);
						BackpackInventory.setStackForSlot(stack, left, i);;
					}
					
					return EnumActionResult.SUCCESS;
				}
			}
		}
		
		return super.onItemUse(stack, player, world, pos, hand, facing, hitX, hitY, hitZ);
	}
	
	public static boolean isOwner(EntityPlayer player, ItemStack backpack, boolean allowPublic) {
		
		if (backpack != null) {
			
			if (backpack.getTagCompound().getBoolean(TAGPUBLIC) && allowPublic) return true;
			
			if (backpack.hasTagCompound()) 
			{
				String ownerList = backpack.getTagCompound().getString(TAGOWNER);
				String[] owners = ownerList.split(", ");
				
				for (String owner : owners) {
					if (player.getName().equals(owner)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public static List<String> getOwnerList(EntityPlayer player, ItemStack stack) {
		
		List<String> list = new ArrayList<String>();
		
		if (stack != null) {
			if (stack.hasTagCompound()) 
			{
				String ownerList = stack.getTagCompound().getString(TAGOWNER);
				String[] owners = ownerList.split(", ");
				
				for (String owner : owners) {
					list.add(owner);
				}
			}
		}
		
		return list;
	}
	
	public static void updateOwners(EntityPlayer player, ItemStack stack, List<String> list) {
		
		if (stack != null) {
			if (stack.hasTagCompound()) 
			{
				NBTTagCompound nbt = stack.getTagCompound();
				String owners = "";
				
				for (String owner : list) {
					owners += ", " + owner;
				}
				
				if (owners.isEmpty()) {
					nbt.setString(TAGOWNER, player.getName());
					player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. Removing this owner makes the list empty! Added " + player.getName()));
				} else {
					nbt.setString(TAGOWNER, owners.substring(2));
				}
			}
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
		
		NBTTagCompound nbt = stack.getTagCompound();
		
		if (nbt != null && GuiScreen.isShiftKeyDown()) 
		{
			EnumColor color = EnumColor.getEnumFromMeta(stack.getItemDamage());
			
			tooltip.add("Open with " + TextFormatting.WHITE + "'" + KeyHandler.keyOpen.getKeyModifier().getLocalizedComboName(KeyHandler.keyOpen.getKeyCode()) + "'");
			tooltip.add("Owners: " + TextFormatting.WHITE + nbt.getString(TAGOWNER) + " (" + (nbt.getBoolean(TAGPUBLIC) ? TextFormatting.GREEN + "PUBLIC" : TextFormatting.RED + "PRIVATE") + TextFormatting.WHITE + ")");
			tooltip.add("Max Slots: " + TextFormatting.WHITE + nbt.getInteger(TAGSLOTS));
			tooltip.add("Filled Slots: " + TextFormatting.WHITE + BackpackInventory.getStacks(stack).size());
			
			int upgrades = UpgradeInventory.getUpgrades(stack);
			
			for (int i = 0; i < 3; i++) 
			{
				ItemStack upgradeStack = UpgradeInventory.getStackForSlot(stack, i+1);
				
				if (upgrades >= 1 && upgradeStack != null) {
					if (i == 0 ) tooltip.add("Upgrades:");
					tooltip.add(TextFormatting.WHITE + "- " + GUIUpgrade.getUpgradeName(upgradeStack));
				}
			}
			
			if (getUpgrade(stack, EnumCompartment.TANK.getId()) > 0 && isOwner(player, stack, true) && !nbt.getString(TAGLIQUID).equals("")) 
			{
				tooltip.add(getStoredLiquidInfos(stack));
			}
			
			if (getUpgrade(stack, EnumCompartment.SENDING.getId()) > 0 && isOwner(player, stack, true)) 
			{
				tooltip.add(TextFormatting.LIGHT_PURPLE + "Sends to:");
				tooltip.add(getSendingInfos(stack, player));
			}
			
			if (color.getId() != 0) tooltip.add(TextFormatting.fromColorIndex(EnumColor.getFormattingColorFromMeta(stack.getItemDamage())) + "Dyed");
			
		} else {
			tooltip.add(TextFormatting.DARK_GRAY + "SHIFT");
		}
	}

	private String getStoredLiquidInfos(ItemStack backpack) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		
		String s = nbt.getString(TAGLIQUID);
		int amount = nbt.getInteger(TAGLIQUIDAMOUNT);
		String[] strings = s.split(":");
		
		if (strings.length == 2)
			s = StringHelper.uppercaseFirstLetter(strings[1]);
		
		s = StringHelper.uppercaseFirstLetter(s);
		
		TextFormatting color = amount == maxLiquidAmount(backpack) ? TextFormatting.RED : TextFormatting.AQUA;
		
		return "Stored Liquid: " + color + amount + 'x' + s;
	}
	
	private int maxLiquidAmount(ItemStack backpack) {
		
		return getUpgrade(backpack, EnumCompartment.TANK.getId()) * 8;
	}

	private String getSendingInfos(ItemStack backpack, EntityPlayer player) {
		
		String notFound = TextFormatting.RED + "No inventory found! Shift-Rightclick an inventory block";
		
		if (backpack == null || player == null) return notFound;
		
		String nbt = backpack.getTagCompound().getString(TAGSENDING);
		String[] pieces = nbt.split(",");
		IInventory inv = getSendingInventory(pieces, player);
		
		if ("".equals(nbt) || inv == null) return notFound;
		
		String text = TextFormatting.LIGHT_PURPLE + "Inv: " + TextFormatting.WHITE + inv.getDisplayName().getUnformattedText() + TextFormatting.LIGHT_PURPLE + ", Dim: " + TextFormatting.WHITE + pieces[0] 
				 	+ TextFormatting.LIGHT_PURPLE +", X: " + TextFormatting.WHITE + pieces[1] + TextFormatting.LIGHT_PURPLE +", Y: " + TextFormatting.WHITE + pieces[2] + TextFormatting.LIGHT_PURPLE +", Z: " + TextFormatting.WHITE + pieces[3];

		return text;
	}
	
	public static IInventory getSendingInventory(String[] strings, EntityPlayer player) {
		
		if (strings == null || player == null) return null;
		
		if (strings.length == 4) {
			
			if (!strings[0].equals("")) 
			{
				WorldServer world = DimensionManager.getWorld(Integer.valueOf(strings[0]));
				
				if (world != null && (Integer.valueOf(strings[0]) == 0  || Integer.valueOf(strings[0]) == player.dimension))
					return TileEntityHopper.getInventoryAtPosition(world.init(), Integer.valueOf(strings[1]), Integer.valueOf(strings[2]), Integer.valueOf(strings[3]));
			}
		}
		return null;
	}

	public static void createUpdateTag(ItemStack stack, Entity entity) {
		
		if (stack.getTagCompound() == null) 
		{
			NBTTagCompound nbt = new NBTTagCompound();
			nbt.setInteger(TAGSLOTS, 1);
			nbt.setString(TAGOWNER, entity.getName());
			nbt.setBoolean(TAGPUBLIC, false);
			stack.setTagCompound(nbt);
		} 
		else 
		{
			NBTTagCompound nbt = stack.getTagCompound();
			
			if (nbt.getInteger(TAGSLOTS) <= 0) {
				nbt.setInteger(TAGSLOTS, 1);
			}
			
			if (nbt.getString(TAGOWNER).isEmpty()) {
				nbt.setString(TAGOWNER, entity.getName());
			}
			
			int value = BackpackInventory.getSlots(stack);
			if (nbt.getInteger(TAGSLOTS) != value) {
				nbt.setInteger(TAGSLOTS, value);
			}
		}
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
		
		if (!isOwner(player, stack, true) && !world.isRemote) 
		{
			PlayerHelper.addStackToPlayer(player, stack);
			player.setItemStackToSlot(this.armorType, null);
			player.addChatMessage(new TextComponentString(ChatFormatting.RED + "You are not allowed to use this backpack!"));
		}
		
		ContainerUpgrade.updateBackpack(player, stack);
	}
	
	public static List<ItemStack> getBackpacks(EntityPlayer player, boolean handsFirst) {
		
		List<ItemStack> backpacks = new ArrayList<ItemStack>();
		
		if (handsFirst) {
			for (EntityEquipmentSlot slot : PlayerHelper.HANDS) {
				ItemStack stack = player.getItemStackFromSlot(slot);
				
				if (stack != null && stack.getItem() instanceof Backpack) {
					backpacks.add(stack);
				}
			}
		}
		
		BackpackSlotInventory invSlotBackpack = new BackpackSlotInventory(player);
		ItemStack stack = invSlotBackpack.getStackInSlot(0);
		
		if (stack != null && stack.getItem() instanceof Backpack) {
			backpacks.add(stack);
		}
		
		for (EntityEquipmentSlot slot : handsFirst ? PlayerHelper.ARMOR : EntityEquipmentSlot.values())
		{
			ItemStack stack1 = player.getItemStackFromSlot(slot);
			
			if (stack1 != null && stack1.getItem() instanceof Backpack) {
				backpacks.add(stack1);
			}
		}
		
		for (ItemStack stack1 : player.inventory.mainInventory) 
		{
			if (stack1 != null && stack1.getItem() instanceof Backpack) {
				if (!backpacks.contains(stack1)) {
					backpacks.add(stack1);
				}
			}
		}
		
		return backpacks;
	}
	
	public static boolean setBackpack(EntityPlayer player, Object slot, IInventory inv, ItemStack backpack) {
		
		if (slot instanceof EntityEquipmentSlot) {
			player.setItemStackToSlot((EntityEquipmentSlot) slot, backpack);
			return true;
		}
		
		if (slot instanceof Integer) {
			if (inv != null) {
				if (inv instanceof BackpackSlotInventory) 
				{
					BackpackSlotInventory.setStackForSlot(player, backpack, (Integer) slot);
					return true;
				} 
				else if (inv instanceof InventoryPlayer) 
				{
					player.inventory.setInventorySlotContents((Integer) slot, backpack);
					return true;
				}
			}
		}
		
		return false;
	}
	
	public static int getUpgrade(ItemStack backpack, int meta) {
		
		int amount = 0;
		
		ItemStack itemUpgrade;
		for (int i = 0; i < 3; i++) {
			itemUpgrade = UpgradeInventory.getStackForSlot(backpack, (i+1));
			
			if (itemUpgrade != null && itemUpgrade.getItem() == BackpackItems.compartment && itemUpgrade.getMetadata() == meta) {
				amount++;
			}
		}
		
		return amount;
	}
	
	public static List<EntityPlayer> getOnlineOwners(ItemStack backpack, World world) {
		
		List<EntityPlayer> list = new ArrayList<EntityPlayer>();
		
		if (backpack.hasTagCompound()) 
		{
			String[] owners = backpack.getTagCompound().getString(TAGOWNER).split(", ");
			
			for (String owner : owners) 
			{
				EntityPlayer player = world.getPlayerEntityByName(owner);
				
				if (player != null)
					list.add(player);
			}
		}
		
		return list;
	}
}
