package lellson.expandablebackpack.inventory.iinventory;

import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.misc.PlayerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class BackpackSlotInventory implements IInventory {
	
	private EntityPlayer player;
	private ItemStack[] invStacks;
	
	public static final String NAME = "Backpack Inventory";
	public static final String KEY = "backpackSlotInventory";
	
	public BackpackSlotInventory(EntityPlayer player) {
		
		this.player = player;
		invStacks = new ItemStack[1];
		
		readFromNBT(player.getEntityData());
	}

	@Override
	public String getName() {

		return NAME;
	}

	@Override
	public boolean hasCustomName() {

		return true;
	}

	@Override
	public ITextComponent getDisplayName() {

		return new TextComponentString(NAME);
	}

	@Override
	public int getSizeInventory() {

		return invStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {

		return invStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {

		ItemStack stack = invStacks[index];
		
		if(stack != null) {
			if(stack.stackSize > count) {
				stack = stack.splitStack(count);
				markDirty();
			} else {
				setInventorySlotContents(index, null);
			}
		}
		
		return stack;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {

		setInventorySlotContents(index, null);
		return invStacks[index];
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
		if (index == 0 && stack != null && !(stack.getItem() instanceof Backpack)) 
		{
			setInventorySlotContents(index, null);
			PlayerHelper.addStackToPlayer(player, stack);
			return;
		}
		
		invStacks[index] = stack;
		
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {

		return 1;
	}

	@Override
	public void markDirty() {

		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				invStacks = null;
			}
		}
		
		writeToNBT(player.getEntityData());
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {

		return true;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {

		return stack.getItem() instanceof Backpack;
	}

	@Override
	public int getField(int id) {

		return 0;
	}

	@Override
	public void setField(int id, int value) {
		
	}

	@Override
	public int getFieldCount() {

		return 0;
	}

	@Override
	public void clear() {}
	
	public void readFromNBT(NBTTagCompound compound) {
		
		NBTTagList items = compound.getTagList("PlayerInventoryBackpack", Constants.NBT.TAG_COMPOUND);
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("SlotInvBackpack");
	
			if (slot >= 0 && slot < getSizeInventory()) {
				invStacks[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound) {
		
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < getSizeInventory(); ++i) {
			NBTTagCompound item = new NBTTagCompound();
			item.setInteger("SlotInvBackpack", i);
			if (getStackInSlot(i) != null) getStackInSlot(i).writeToNBT(item);
			items.appendTag(item);
		}
		
		tagcompound.setTag("PlayerInventoryBackpack", items);
	}
	
	public static ItemStack getStackForSlot(EntityPlayer player, int slotindex) {
		
		NBTTagCompound nbt = player.getEntityData();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("PlayerInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotInvBackpack");
		
				if (slot == slotindex) {
					return ItemStack.loadItemStackFromNBT(item);
				}
			}
		}
		
		return null;
	}
	
	public static void setStackForSlot(EntityPlayer player, ItemStack stack, int slotindex) {
		
		NBTTagCompound nbt = player.getEntityData();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("PlayerInventoryBackpack", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotInvBackpack");
		
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
	
	public static NBTTagCompound newNBTTagCompound(int id) {
		
		NBTTagCompound tag = new NBTTagCompound();
		tag.setInteger("SlotInvBackpack", id);
		
		return tag;
	}
}
