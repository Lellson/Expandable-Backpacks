package lellson.expandablebackpack.events;

import java.util.List;
import java.util.Random;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.BackpackConfig;
import lellson.expandablebackpack.misc.InventoryHelper;
import lellson.expandablebackpack.misc.PlayerHelper;
import lellson.expandablebackpack.network.ClientNetworkHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CompartmentEvents {
	
	private static final String TAG_RESPAWN = ExpandableBackpack.MODID + ":respawnBackpack";
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new CompartmentEvents());
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void dropEvent(PlayerDropsEvent event) {
		
		for (int i = 0; i < event.getDrops().size(); i++) {
			
			ItemStack item = event.getDrops().get(i).getEntityItem();
			
			if (item != null && item.getItem() instanceof Backpack && item.hasTagCompound() && Backpack.getUpgrade(item, EnumCompartment.SOULBOUND.getId()) > 0) {
				PlayerHelper.addStackToPlayer(event.getEntityPlayer(), event.getDrops().get(i).getEntityItem());
			}
		}
	}
	
	@SubscribeEvent
	public void cloneEvent(PlayerEvent.Clone event) {
		
		EntityPlayer playerNew = event.getEntityPlayer();
		EntityPlayer playerOld = event.getOriginal();
		ItemStack invBackpackStack = BackpackSlotInventory.getStackForSlot(playerOld, 0);
		
		if (event.isWasDeath()) 
		{
			Random rnd = playerNew.worldObj.rand;
			
			if (invBackpackStack != null && invBackpackStack.getItem() instanceof Backpack) 
			{	
				if (Backpack.getUpgrade(invBackpackStack, EnumCompartment.SOULBOUND.getId()) > 0 || playerNew.worldObj.getGameRules().getBoolean("keepInventory")) 
				{
					if (!playerNew.inventory.addItemStackToInventory(invBackpackStack)) 
					{
						NBTTagCompound tag = new NBTTagCompound();
						invBackpackStack.writeToNBT(tag);
						playerNew.getEntityData().setTag(TAG_RESPAWN, tag);
					}
				}
			}
		}
		else if (invBackpackStack != null)
		{
			if (!playerNew.inventory.addItemStackToInventory(invBackpackStack)) 
			{
				NBTTagCompound tag = new NBTTagCompound();
				invBackpackStack.writeToNBT(tag);
				playerNew.getEntityData().setTag(TAG_RESPAWN, tag);
			}
		}
	}
	
	@SubscribeEvent
	public void respawn(PlayerRespawnEvent event) {
		
		NBTTagCompound playerData = event.player.getEntityData();
		
		if (playerData.getTag(TAG_RESPAWN) != null)
		{
			event.player.dropItem(ItemStack.loadItemStackFromNBT((NBTTagCompound) playerData.getTag(TAG_RESPAWN)), true);
			playerData.removeTag(TAG_RESPAWN);
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
	public void addToDrops(LivingDropsEvent event) {
		
		if (event.getEntityLiving() instanceof EntityPlayer) 
		{
			EntityPlayer player = (EntityPlayer) event.getEntityLiving();
			
			ItemStack invBackpackStack = BackpackSlotInventory.getStackForSlot(player, 0);
			
			if (invBackpackStack != null && invBackpackStack.getItem() instanceof Backpack) 
			{
				if (Backpack.getUpgrade(invBackpackStack, EnumCompartment.SOULBOUND.getId()) <= 0 && !player.worldObj.getGameRules().getBoolean("keepInventory")) 
				{
					event.getDrops().add(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, invBackpackStack));
				}
			}
		}
	}
	
	@SubscribeEvent
	public void pickupEvent(EntityItemPickupEvent event) {
		
		List<ItemStack> backpacks = Backpack.getBackpacks(event.getEntityPlayer(), true);
		ItemStack picked = event.getItem().getEntityItem();
		
		for (ItemStack backpack : backpacks) {
			if ((BackpackInventory.hasStack(backpack, picked) || InventoryHelper.hasStack(Backpack.getSendingInventory(backpack.getTagCompound().getString(Backpack.TAGSENDING).split(","), event.getEntityPlayer()), picked)) && Backpack.getUpgrade(backpack, EnumCompartment.PICKUP.getId()) > 0 && Backpack.isOwner(event.getEntityPlayer(), backpack, true)) {
				if (picked != null) {
					
					ItemStack left = null;
					
					if (Backpack.getUpgrade(backpack, EnumCompartment.SENDING.getId()) > 0 && !"".equals(backpack.getTagCompound().getString(Backpack.TAGSENDING))) 
					{
						left = TileEntityHopper.putStackInInventoryAllSlots(Backpack.getSendingInventory(backpack.getTagCompound().getString(Backpack.TAGSENDING).split(","), event.getEntityPlayer()), picked, null);	
					} 
					else 
					{
						left = BackpackInventory.addToInventory(backpack, event.getEntityPlayer(), picked);
					}
					
					PlayerHelper.addStackToPlayer(event.getEntityPlayer(), left);
					
					event.setCanceled(true);
					event.getItem().setDead();
					event.setResult(Result.ALLOW);
					
					if (event.getEntityPlayer() instanceof EntityPlayerMP)
						ExpandableBackpack.networkClient.sendTo(new ClientNetworkHandler(1), (EntityPlayerMP) event.getEntityPlayer());
					
					break;
				}
			}
		}
	}
	
	@SubscribeEvent
	public void arrowEvent(ArrowLooseEvent event) {
		
		EntityPlayer player = event.getEntityPlayer();
		
		for (ItemStack backpack : Backpack.getBackpacks(player, false)) {
			if (Backpack.getUpgrade(backpack, EnumCompartment.QUIVERED.getId()) > 0 && !player.capabilities.isCreativeMode && Backpack.isOwner(player, backpack, true) && event.hasAmmo() && 
					event.getCharge() >= 3 && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, event.getBow()) == 0) {
				
				List<Integer> slotsBP = BackpackInventory.getSlotsForItem(backpack, Items.ARROW);
				List<Integer> slotsPlayer = PlayerHelper.getSlotsForStack(player, new ItemStack(Items.ARROW));
				
				if (!slotsBP.isEmpty() && !slotsPlayer.isEmpty()) {
					
					ItemStack arrowBP = BackpackInventory.getStackForSlot(backpack, slotsBP.get(0));
					arrowBP.stackSize--;
					if (arrowBP.stackSize <= 0) arrowBP = null;
					BackpackInventory.setStackForSlot(backpack, arrowBP, slotsBP.get(0));
					
					ItemStack arrowPlayer = player.inventory.getStackInSlot(slotsPlayer.get(0));
					arrowPlayer.stackSize++;
					player.inventory.setInventorySlotContents(slotsPlayer.get(0), arrowPlayer);
					
					break;
				}
			}
		}
	}

}
