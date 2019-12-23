package lellson.expandablebackpack.inventory.misc;

import lellson.expandablebackpack.inventory.iinventory.InventoryTank;
import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.UniversalBucket;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidContainerItemWrapper;

public class SlotBucket extends Slot {

	public SlotBucket(InventoryTank inv, int index, int xPosition, int yPosition) {
		super(inv, index, xPosition, yPosition);
	}
	
	@Override
	public int getSlotStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return isBucket(stack);
	}
	
	public static boolean isBucket(ItemStack stack) {
		return stack != null && (stack.getItem() instanceof ItemBucket || stack.getItem() instanceof UniversalBucket);
	}
}
