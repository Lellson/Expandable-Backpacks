package lellson.expandablebackpack.inventory.container;

import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.inventory.misc.SlotBackpack;
import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerSlotBackpack extends Container {
	
	public final BackpackSlotInventory invSlotBack;
	public final InventoryPlayer invPlayer;
	public final EntityPlayer player;
	
	public ContainerSlotBackpack(EntityPlayer player, InventoryPlayer invPlayer, BackpackSlotInventory invBack) {
		
		this.invSlotBack = invBack;
		this.invPlayer = invPlayer;
		this.player = player;
		
		this.addSlotToContainer(new SlotBackpack(invSlotBack, 0, 98, 18));
		
		int i = 51;
        for (int l = 0; l < 3; ++l) {
            for (int k = 0; k < 9; ++k) {
                this.addSlotToContainer(new Slot(invPlayer, k + l * 9 + 9, 8 + k * 18, l * 18 + i));
            }
        }

        for (int i1 = 0; i1 < 9; ++i1) {
            this.addSlotToContainer(new Slot(invPlayer, i1, 8 + i1 * 18, 58 + i));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {

		return invSlotBack.isUseableByPlayer(playerIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.invSlotBack.getSizeInventory() && isAllowedItem(index, itemstack1)) {
                if (!this.mergeItemStack(itemstack1, this.invSlotBack.getSizeInventory(), this.inventorySlots.size(), true)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.invSlotBack.getSizeInventory(), false)) {
                return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }
        }

        return itemstack;
    }

	public static boolean isAllowedItem(int index, ItemStack stack) {

		return index == 0 ? stack != null && stack.getItem() instanceof Backpack : true;
	}
}
