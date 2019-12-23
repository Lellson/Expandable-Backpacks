package lellson.expandablebackpack.inventory.misc;

import lellson.expandablebackpack.inventory.container.ContainerUpgrade;
import lellson.expandablebackpack.inventory.iinventory.UpgradeInventory;
import lellson.expandablebackpack.item.BackpackItems;
import net.minecraft.init.Items;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class SlotUpgrade extends Slot {
	
	private int index;
	private UpgradeInventory inv;
	private ContainerUpgrade container;

	public SlotUpgrade(ContainerUpgrade container, UpgradeInventory inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
		
		this.index = index;
		this.inv = inventoryIn;
		this.container = container;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		
		return stack.getItem() == getValidIndexItem(index, inv.getUpgrades());	
	}
	
	public static Item getValidIndexItem(int index, int upgrades) {
		
		switch(index) {
			case 0: return BackpackItems.obsidianLeather;
			case 1: case 2: case 3: return upgrades >= index ? BackpackItems.compartment : null;
			case 4: return Items.DYE;
			default: return null;
		}
	}

	@Override
	public int getSlotStackLimit() {

		return index == 0 ? inv.getInventoryStackLimit() : 1;
	}
}
