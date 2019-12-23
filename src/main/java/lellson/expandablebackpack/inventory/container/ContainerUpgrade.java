package lellson.expandablebackpack.inventory.container;

import java.util.List;

import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.inventory.iinventory.UpgradeInventory;
import lellson.expandablebackpack.inventory.misc.SlotUpgrade;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.PlayerHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class ContainerUpgrade extends Container {
	
	public final UpgradeInventory invUp;
	public final EntityPlayer player;
	public int slots;
	
	public ContainerUpgrade(EntityPlayer player, InventoryPlayer invPlayer, UpgradeInventory invUp) {
		
		this.invUp = invUp;
		this.slots = BackpackInventory.getSlots(invUp.stack);
		this.player = player;
		
		this.addSlotToContainer(new SlotUpgrade(this, this.invUp, 0, 26, 24));
		
		int i;
		for (i = 0; i < 3; i++) {
			this.addSlotToContainer(new SlotUpgrade(this, this.invUp, 1+i, 71, 24 + i*19));
		}
		
		this.addSlotToContainer(new SlotUpgrade(this, this.invUp, 4, 8, 71));
		
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 93 + i * 18));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(invPlayer, i, 8 + i * 18, 151));
		}
	}
	
	@Override
	public boolean canMergeSlot(ItemStack stack, Slot slotIn) {

		return false;
	}

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {

		return invUp.isUseableByPlayer(playerIn);
	}
	
	@Override
	public ItemStack slotClick(int slot, int dragType, ClickType clickTypeIn, EntityPlayer player) {
		
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == invUp.stack) {
			return null;
		}
		
		return super.slotClick(slot, dragType, clickTypeIn, player);
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		int upgrades = invUp.getUpgrades();
		
		for (int i = 4; i > upgrades+1; i--) {
			Slot s = getSlotFromInventory(invUp, i-1);
			if (s.getHasStack() && player != null) {
				PlayerHelper.addStackToPlayer(player, s.getStack());
				s.putStack(null);
			}
		}
		
		Backpack.setBackpack(player, 0, new BackpackSlotInventory(player), updateBackpack(player, BackpackSlotInventory.getStackForSlot(player, 0)));
	}
	
	public static ItemStack updateBackpack(EntityPlayer player, ItemStack stack) {
		
		if (stack == null || !(stack.getItem() instanceof Backpack) || !stack.hasTagCompound()) return null;
		
		ItemStack dye = UpgradeInventory.getStackForSlot(stack, 4);
		
		if (dye == null || (dye != null && !(dye.getItem() instanceof ItemDye))) {
			if (stack.getItemDamage() != 0) stack.setItemDamage(0);
		} else {
			int meta = dye.getItemDamage()+1;
			if (stack.getItemDamage() != meta) stack.setItemDamage(meta);
		}
		
		int upgradeArmor = Backpack.getUpgrade(stack, EnumCompartment.ARMORED.getId());
		boolean armored = stack.getTagCompound().getBoolean(Backpack.TAGISARMORD);
		
		if (upgradeArmor > 0 && !armored) 
		{
			stack.getTagCompound().setBoolean(Backpack.TAGISARMORD, true);
			stack.setItem(BackpackItems.expandableBackpackArmored);
			
		} 
		else if (upgradeArmor == 0 && armored) 
		{
			stack.getTagCompound().setBoolean(Backpack.TAGISARMORD, false);
			stack.setItem(BackpackItems.expandableBackpack);
		}
		
		return stack;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		
		int slots2 = BackpackInventory.getSlots(invUp.stack);
		
		for (int i = slots-slots2; i > 0 ; i--) {
			int slot = slots-i;
			ItemStack stackSlot = BackpackInventory.getStackForSlot(invUp.stack, slot);
			if (stackSlot != null) {
				BackpackInventory.setStackForSlot(invUp.stack, null, slot);
				player.dropItem(stackSlot, false);
			}
		}
		
		List<ItemStack> backpacks = Backpack.getBackpacks(player, false);
		
		for (ItemStack backpack : backpacks) {
			int infinite = Backpack.getUpgrade(backpack, EnumCompartment.INFINITE.getId());
			
			if (infinite == 0) {
				
				List<Integer> slots = BackpackInventory.getSlotsForItem(backpack, BackpackItems.expandableBackpack);
				List<Integer> slots3 = BackpackInventory.getSlotsForItem(backpack, BackpackItems.expandableBackpackArmored);
				
				for (int slot : slots) {
					PlayerHelper.addStackToPlayer(player, BackpackInventory.getStackForSlot(backpack, slot));
					BackpackInventory.setStackForSlot(backpack, null, slot);
				}
				
				for (int slot : slots3) {
					PlayerHelper.addStackToPlayer(player, BackpackInventory.getStackForSlot(backpack, slot));
					BackpackInventory.setStackForSlot(backpack, null, slot);
				}
			}
		}
	}
	
	@Override
	public boolean mergeItemStack(ItemStack stack, int begin, int end, boolean backwards) {
		
		int i = backwards ? end - 1 : begin;
		int increment = backwards ? -1 : 1;
		
		boolean flag = false;
		
		while (stack.stackSize > 0 && i >= begin && i < end) {
			
			boolean flag2 = true;
			
			if (begin == 0 && end == 5) {
				flag2 = stack.getItem() == SlotUpgrade.getValidIndexItem(i, invUp.getUpgrades());
			}
			
			if (flag2) {
				Slot slot = this.getSlot(i);
				ItemStack slotStack = slot.getStack();
				
				int slotStacklimit = i < this.invUp.getSizeInventory() ? slot.getSlotStackLimit() : 64;
				int totalLimit = slotStacklimit < stack.getMaxStackSize() ? slotStacklimit : stack.getMaxStackSize();
				
				if (slotStack == null) {
					
					int transfer = totalLimit < stack.stackSize ? totalLimit : stack.stackSize;
					ItemStack stackToPut = stack.copy();
					stackToPut.stackSize = transfer;
					slot.putStack(stackToPut);
					slot.onSlotChanged();
					stack.stackSize -= transfer;
					flag = true;
					
				} else if (slotStack.getItem() == stack.getItem() && (!stack.getHasSubtypes() || stack.getItemDamage() == slotStack.getItemDamage()) && ItemStack.areItemStackTagsEqual(stack, slotStack)) {
					
					int maxTransfer = totalLimit - slotStack.stackSize;
					int transfer = maxTransfer > stack.stackSize ? stack.stackSize : maxTransfer;
					slotStack.stackSize += transfer;
					slot.onSlotChanged();
					stack.stackSize -= transfer;
					flag = true;
				}
			}

			i += increment;
		}

		return flag;

	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotIndex) {

		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(slotIndex);

		if (slot != null && slot.getHasStack()) {

			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (slotIndex >= 0 && slotIndex < 5) {

				if (!this.mergeItemStack(itemstack1, 5, 41, false)) {
					return null;
				}

			} else if (slotIndex >= 5 && slotIndex < 41) {

				if (!this.mergeItemStack(itemstack1, 0, 5, false)) {
					return null;
				}
			}

			if (itemstack1.stackSize == 0) {

				slot.putStack((ItemStack) null);

			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {

				return null;
			}

			slot.onPickupFromSlot(player, itemstack1);
		}

		return itemstack;
	}
}
