package lellson.expandablebackpack.inventory.iinventory;

import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.misc.BackpackConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.util.Constants;

public class UpgradeInventory implements IInventory {
	
	public ItemStack stack;
	public EntityPlayer player;
	public ItemStack[] invStacks = new ItemStack[5];
	private static final String NAME = "Upgrades";
	
	public UpgradeInventory(ItemStack stack, EntityPlayer player) {
		
		this.stack = stack != null ? stack : Backpack.getBackpacks(player, true).get(0);
		this.player = player;
		
		if (!this.stack.hasTagCompound()) {
			this.stack.setTagCompound(new NBTTagCompound());
		}
		
		readFromNBT(this.stack.getTagCompound());
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

		return invStacks == null ? 0 : invStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {

		return invStacks == null ? null : invStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		
		ItemStack stack = invStacks[slot];
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
		
		setInventorySlotContents(index, null);
		return invStacks[index];
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		
		if (invStacks == null) 
			return;
		
		invStacks[index] = stack;
		
		if (stack != null && stack.stackSize > getInventoryStackLimit()) {
			stack.stackSize = getInventoryStackLimit();
		}
		
		markDirty();
	}

	@Override
	public int getInventoryStackLimit() {

		return (BackpackConfig.maxBackpackSize - BackpackConfig.initialBackpackSize) / BackpackConfig.slotsPerUpgrade;
	}

	@Override
	public void markDirty() {
		
		for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				invStacks = null;
			}
		}
		
		writeToNBT(stack.getTagCompound());
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
		
		return !(stack.getItem() instanceof Backpack);
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
	public void clear() {}
	
	public void readFromNBT(NBTTagCompound compound) {
		
		NBTTagList items = compound.getTagList("ItemInventoryUpgrade", Constants.NBT.TAG_COMPOUND);
		
		for (int i = 0; i < items.tagCount(); ++i) {
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("SlotUpgrade");
	
			if (slot >= 0 && slot < getSizeInventory()) {
				invStacks[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	public void writeToNBT(NBTTagCompound tagcompound)
	{
		NBTTagList items = new NBTTagList();
		
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (getStackInSlot(i) != null) {
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("SlotUpgrade", i);
				getStackInSlot(i).writeToNBT(item);
				items.appendTag(item);
			}
		}
		
		tagcompound.setTag("ItemInventoryUpgrade", items);
	}
	
	public static ItemStack getStackForSlot(ItemStack backpack, int slotindex) {
		
		if (backpack == null || !(backpack.getItem() instanceof Backpack) || !backpack.hasTagCompound())
			return null;
		
		NBTTagCompound nbt = backpack.getTagCompound();
		
		if (nbt != null) {
			NBTTagList items = nbt.getTagList("ItemInventoryUpgrade", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
				int slot = item.getInteger("SlotUpgrade");
		
				if (slot == slotindex) {
					return ItemStack.loadItemStackFromNBT(item);
				}
			}
		}
		
		return null;
	}
	
	public int getUpgrades() {
		ItemStack stack = getStackInSlot(0); 
		return stack != null ? stack.stackSize >= 35 ? 3 : stack.stackSize >= 17 ? 2 : 1 : 1;
	}
	
	public static int getUpgrades(ItemStack backpack) {
		ItemStack stack = getStackForSlot(backpack, 0);
		return stack != null ? stack.stackSize >= 35 ? 3 : stack.stackSize >= 17 ? 2 : 1 : 1;
	}
}
