package lellson.expandablebackpack.inventory.misc;

import lellson.expandablebackpack.inventory.container.ContainerSlotBackpack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBackpack extends Slot {
	
	private int index;
	
	public SlotBackpack(IInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		this.index = index;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		
		return ContainerSlotBackpack.isAllowedItem(index, stack);
	}
}
	