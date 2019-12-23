package lellson.expandablebackpack.misc;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryHelper {
	
	public static boolean hasStack(IInventory inv, ItemStack stack) {
		
		if (inv == null || stack == null) return false;
		
		for (int i = 0; i < inv.getSizeInventory(); i++) {
			
			ItemStack invStack = inv.getStackInSlot(i);
			
			if (stack != null && invStack != null && stack.getItem() == invStack.getItem() && stack.getItemDamage() == invStack.getItemDamage()) {
				return true;
			}
		}
		
		return false;
	}
	
	public static boolean isFull(IInventory inv, boolean fullStacksOnly) {
		
		for (int i = 0; i < inv.getSizeInventory(); i++) 
		{
			ItemStack stack = inv.getStackInSlot(i);
			
			if (stack == null || (fullStacksOnly || stack != null && stack.stackSize < stack.getMaxStackSize())) 
				return false;
		}

		return true;
	}
}
