package lellson.expandablebackpack.inventory.container;

import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

public class ContainerBackpackWorkbench extends ContainerWorkbench {

	private EntityPlayer player;
	private ItemStack backpack;

	public ContainerBackpackWorkbench(InventoryPlayer playerInventory, EntityPlayer player, ItemStack backpack) {
		super(playerInventory, player.worldObj, BlockPos.ORIGIN);
		this.player = player;
		this.backpack = backpack;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {

		return true;
	}
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!player.worldObj.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack itemstack = this.craftMatrix.removeStackFromSlot(i);

                if (itemstack != null)
                {
                	ItemStack left = BackpackInventory.addToInventory(backpack, player, itemstack);
                    playerIn.dropItem(left, false);
                }
            }
        }
    }
}
