package lellson.expandablebackpack.inventory.iinventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class InventoryTank implements IInventory {
	
    private final ItemStack[] stacks = new ItemStack[2];

    public int getSizeInventory() {
        return stacks.length;
    }

    public ItemStack getStackInSlot(int index) {
        return this.stacks[index];
    }

    public String getName() {
        return "Tank";
    }

    public boolean hasCustomName() {
        return false;
    }

    public ITextComponent getDisplayName() {
        return (ITextComponent)(this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName(), new Object[0]));
    }

    @Nullable
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndRemove(this.stacks, index);
    }

    @Nullable
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.stacks, index);
    }

    public void setInventorySlotContents(int index, @Nullable ItemStack stack) {
        this.stacks[index] = stack;
    }

    public int getInventoryStackLimit() {
        return 1;
    }

    public void markDirty(){
    }

    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    public void openInventory(EntityPlayer player){
    }

    public void closeInventory(EntityPlayer player){
    }

    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return stack != null && stack.getItem() instanceof ItemBucket;
    }

    public int getField(int id){
        return 0;
    }

    public void setField(int id, int value){
    }

    public int getFieldCount(){
        return 0;
    }

    public void clear() {
    	
	    for (int i = 0; i < this.stacks.length; ++i)
	    {
	        this.stacks[i] = null;
	    }
    }
}