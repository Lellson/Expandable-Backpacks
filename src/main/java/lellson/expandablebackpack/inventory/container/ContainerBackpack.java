package lellson.expandablebackpack.inventory.container;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.BackpackConfig;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ContainerBackpack extends Container {
	
	public final BackpackInventory invBack;
	public final InventoryPlayer invPlayer;
	public int slots;
	
	public ContainerBackpack(EntityPlayer player, InventoryPlayer invPlayer, BackpackInventory invBack) {
		
		this.invBack = invBack;
		this.invPlayer = invPlayer;
		this.slots = invBack.getSlots(invBack.stack);
		
		int l = slots > 27 ? 25 : 0;
		
		int rows = slots / 9 + 1;
		int amount = slots;
		int rest;
		
		int i;
		for (i = 0; i < rows; ++i) {
			rest = amount > 9 ? 9 : amount;
			for (int j = 0; j < rest; ++j) {
				this.addSlotToContainer(new Slot(this.invBack, j + i * 9, 8 + j * 18, (18 + i * 18) - l));
				amount--;
			}
		}
		
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, (getPlayerSlotY() + i * 18) - l));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, (getPlayerSlotY() + 58) - l));
		}
	}
	
	public ResourceLocation getGUITexture() {
		return new ResourceLocation(ExpandableBackpack.MODID + ":textures/gui/guiBackpack" + getStage() + ".png");
	}
	
	public int getStage() {

		return slots > 45 ? 6 : slots > 36 ? 5 : slots > 27 ? 4 : slots > 18 ? 3 : slots > 9 ? 2 : 1;
	}

	public int getPlayerSlotY() {
		
		return slots > 45 ? 140 : slots > 36 ? 121 : slots > 27 ? 103 : slots > 18 ? 84 : slots > 9 ? 68 : 51;
	}
	
	public int getInventoryStringY() {
		
		return (slots > 45 ? 129 : slots > 36 ? 110 : slots > 27 ? 92 : slots > 18 ? 73 : slots > 9 ? 57 : 40) - (slots > 27 ? 25 : 0);
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {

		return invBack.isUseableByPlayer(playerIn);
	}
	
	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		
		if (slot >= 0 && getSlot(slot) != null && (getSlot(slot).getStack() == invBack.stack || isBlacklisted(getSlot(slot).getStack()))) {
			return null;
		}
		
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
	
	private boolean isBlacklisted(ItemStack stack) {
		
		int upgrades = Backpack.getUpgrade(invBack.stack, EnumCompartment.INFINITE.getId());
		String[] blacklist = (upgrades > 0 ? BackpackConfig.blacklistInfinite : BackpackConfig.blacklistNormal);
		
		if (stack != null) {
			for (String listed : blacklist) {
				
				String[] pieces = listed.split("-");
				Item item = Item.REGISTRY.getObject(new ResourceLocation(pieces[0]));
				
				if (stack.getItem() == item) {
					
					return pieces.length >= 2 ? stack.getItemDamage() == Integer.valueOf(pieces[1]) : true;
				}
			}
		}
		
		return false;
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index < this.slots) {
                if (!this.mergeItemStack(itemstack1, this.slots, this.inventorySlots.size(), true)) {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 0, this.slots, false)) {
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
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		if (invBack.creative) {
			for (int i = 0; i < invBack.getSlots(invBack.stack); i++) {
				Slot slot = getSlotFromInventory(invBack, i);
				
				if (slot != null) {
					ItemStack stack = slot.getStack();
					
					if (stack != null) {
						stack.stackSize = stack.getMaxStackSize();
					}
				}
			}
		}
	}
}
