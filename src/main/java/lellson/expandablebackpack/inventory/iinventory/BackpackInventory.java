package lellson.expandablebackpack.inventory.iinventory;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.BackpackConfig;
import lellson.expandablebackpack.misc.PlayerHelper;
import lellson.expandablebackpack.network.ClientNetworkHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class BackpackInventory implements IInventory {

	public final ItemStack stack;
	public ItemStack[] invStacks;
	public EntityPlayer player;
	public boolean creative;
	private static final String NAME = "Expandable Backpack";
	
	public BackpackInventory(ItemStack stack, EntityPlayer player) {
		
		this.stack = stack != null ? stack : Backpack.getBackpacks(player, true).get(0);
		this.player = player;
		
		invStacks = new ItemStack[getSlots(this.stack)];
		
		this.creative = Backpack.getUpgrade(this.stack, EnumCompartment.CREATIVE.getId()) > 0;
		
		if (!this.stack.hasTagCompound()) {
			this.stack.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(this.stack.getTagCompound());
	}
	
	public static int getSlots(ItemStack backpack) {
		
		ItemStack upgradeSlotStack = UpgradeInventory.getStackForSlot(backpack, 0);
		return upgradeSlotStack != null ? (upgradeSlotStack.stackSize * BackpackConfig.slotsPerUpgrade) + BackpackConfig.initialBackpackSize : BackpackConfig.initialBackpackSize;
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public boolean hasCustomName() {

		return NAME.length() > 0;
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(NAME);
	}

	@Override
	public int getSizeInventory() {

		return invStacks != null ? invStacks.length : 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return invStacks != null ? invStacks[i] : null;
	}
	
	public boolean hasNoStacks() {
		
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		
		ItemStack stack = invStacks[slot];
		
		if (creative) return stack;
		
		if(stack != null) {
			if(stack.stackSize > amount) {
				stack = stack.splitStack(amount);
				markDirty();
			} else {
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		
		if (!creative) setInventorySlotContents(index, null);
			
		return invStacks[index];
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
		if (stack == this.stack) 
		{
			setInventorySlotContents(index, null);
			PlayerHelper.addStackToPlayer(player, this.stack);
			return;
		}
		
		if (Backpack.getUpgrade(this.stack, EnumCompartment.INCINERATE.getId()) > 0 && stack != null) 
		{
			player.playSound(SoundEvents.BLOCK_LAVA_EXTINGUISH, 0.7F, 1.0F);
			return;
		}
		
		if (Backpack.getUpgrade(this.stack, EnumCompartment.SENDING.getId()) > 0 && stack != null) 
		{
			if (addToSendingInventory(stack, index)) {
				markDirty();
				return;
			}
		}
		
		invStacks[index] = stack;
		
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		
		markDirty();
	}

	public boolean addToSendingInventory(ItemStack toAdd, int index) {
		
		String sendString = this.stack.getTagCompound().getString(Backpack.TAGSENDING);
		IInventory inv = Backpack.getSendingInventory(sendString.split(","), player);
		
		if (!sendString.equals("") && inv != null) 
		{
			if (!player.worldObj.isRemote) 
			{
				ItemStack left = TileEntityHopper.putStackInInventoryAllSlots(inv, toAdd, null);
				
				if (left == null && player instanceof EntityPlayerMP) 
					ExpandableBackpack.networkClient.sendTo(new ClientNetworkHandler(2), (EntityPlayerMP) player);
				else
					player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Inventory full!"));
				
				PlayerHelper.addStackToPlayer(player, left);	
			}
			
			return true;
		}
		
		return false;
	}

	@Override
	public int getInventoryStackLimit() {

		return 64;
	}

	@Override
	public void markDirty() {
		
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				
				if (creative) {
					getStackInSlot(i).stackSize = 64;
				} else {
					invStacks = null;
				}
			}
		}
		
		writeToNBT(stack.getTagCompound());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		
		return true;
	}

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {
		
		for (int i = 0; i < getSizeInventory(); i++) {
			setInventorySlotContents(i, null);
		}
	}
	
	public void readFromNBT(NBTTagCompound compound) {
		
		NBTTagList items = compound.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("SlotBackpack");
	
			if (slot >= 0 && slot < getSizeInventory()) {
				invStacks[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound)
	{
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < getSizeInventory(); ++i) {
			NBTTagCompound item = new NBTTagCompound();
			item.setInteger("SlotBackpack", i);
			if (getStackInSlot(i) != null) getStackInSlot(i).writeToNBT(item);
			items.appendTag(item);
		}
		
		tagcompound.setTag("ItemInventoryBackpack", items);
	}
	
	public static NBTTagCompound newNBTTagCompound(int id) {
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("SlotBackpack", id);
		
		return tag;
	}
	
	public static ItemStack getStackForSlot(ItemStack backpack, int slotindex) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotBackpack");
		
				if (slot == slotindex) {
					return ItemStack.loadItemStackFromNBT(item);
				}
			}
		}
		
		return null;
	}
	
	public static void setStackForSlot(ItemStack backpack, ItemStack stack, int slotindex) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		
		if (Backpack.getUpgrade(backpack, EnumCompartment.INCINERATE.getId()) > 0) {
			return;
		}
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotBackpack");
		
				if (slot == slotindex) {
					if (stack != null) {
						stack.writeToNBT(item);
					} else {
						item = newNBTTagCompound(slot);
					}
				}
				
				items.set(i, item);
			}
		}
	}
	
	public static ItemStack addToInventory(ItemStack backpack, EntityPlayer player, ItemStack stack) {
		
	    return TileEntityHopper.putStackInInventoryAllSlots(new BackpackInventory(backpack, player), stack, null);
	}
	
	public static boolean isFull(ItemStack backpack) {
		
		for (int i = 0; i < getSlots(backpack); i++) {
			
			ItemStack stack = getStackForSlot(backpack, i);
			
			if (stack == null || stack.stackSize < stack.getMaxStackSize()) {

				return false;
			}
		}
		
		return true;
	}
	
	public static List<ItemStack> getStacks(ItemStack backpack) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		List<ItemStack> list = new ArrayList<ItemStack>();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotBackpack");
				
				ItemStack stack = ItemStack.loadItemStackFromNBT(item);
				if (stack != null) list.add(stack);
			}
		}
		
		return list;
	}
	
	public static boolean hasStack(ItemStack backpack, ItemStack stack) {
		
		for (ItemStack item : getStacks(backpack)) {
			if (item.getItem() == stack.getItem() && item.getItemDamage() == stack.getItemDamage()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static int getEmptySlot(ItemStack backpack) {
		
		for (int i = 0; i < getSlots(backpack); i++) {
			if (getStackForSlot(backpack, i) == null) {
				return i;
			}
		}

		return -1;
	}
	
	public static int getEmptySlots(ItemStack backpack) {
		
		int slots = 0;
		
		for (int i = 0; i < getSlots(backpack); i++) {
			if (getStackForSlot(backpack, i) == null) {
				slots++;
			}
		}

		return slots;
	}
	
	public static boolean isEmpty(ItemStack backpack) {
		
		return getEmptySlots(backpack) == getSlots(backpack);
	}

	public static int getSlotForStack(ItemStack backpack, ItemStack stack) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				ItemStack itemStack = ItemStack.loadItemStackFromNBT(item);
		
				if (ItemStack.areItemStacksEqual(itemStack, stack)) {
					return item.getInteger("SlotBackpack");
				}
			}
		}

		return -1;
	}
	
	public static List<Integer> getSlotsForItem(ItemStack backpack, Item item) {
		
		NBTTagCompound nbt = backpack.getTagCompound();
		List<Integer> slots = new ArrayList<Integer>();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound tagitem = (NBTTagCompound) items.getCompoundTagAt(i);
				ItemStack itemStack = ItemStack.loadItemStackFromNBT(tagitem);
		
				if (itemStack != null && itemStack.getItem() == item) {
					slots.add(tagitem.getInteger("SlotBackpack"));
				}
			}
		}

		return slots;
	}

	public static void clearInventory(ItemStack backpack) {

		for (int i = 0; i < getSlots(backpack); i++) {
			setStackForSlot(backpack, null, i);
		}
	}
}
