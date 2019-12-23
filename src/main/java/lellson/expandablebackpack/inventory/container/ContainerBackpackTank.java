package lellson.expandablebackpack.inventory.container;

import lellson.expandablebackpack.inventory.iinventory.InventoryTank;
import lellson.expandablebackpack.inventory.misc.SlotBucket;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.misc.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.FluidBucketWrapper;

public class ContainerBackpackTank extends Container {
	
	private EntityPlayer player;
	private ItemStack backpack;
	private InventoryTank invTank;
	
	public ContainerBackpackTank(EntityPlayer player, InventoryPlayer playerInventory, InventoryTank invTank, ItemStack backpack) {
		
		this.player = player;
		this.backpack = backpack;
		this.invTank = invTank;
		
		this.addSlotToContainer(new SlotBucket(invTank, 0, 26, 35));
		this.addSlotToContainer(new SlotBucket(invTank, 1, 134, 35));
		
		for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (int k = 0; k < 9; ++k)
        {
            this.addSlotToContainer(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		for (int i = 0; i < 2; i++) 
		{
			ItemStack stack = getSlot(i).getStack();
			
			if (stack != null && SlotBucket.isBucket(stack)) 
			{
				if (stack.getItem() != Items.BUCKET && i == 0) addLiquid(stack);
				else if (stack.getItem() == Items.BUCKET && i == 1) removeLiquid(stack);
			}
		}
	}
	
	private void removeLiquid(ItemStack bucket) {
		
		NBTTagCompound comp = backpack.getTagCompound();
		int amount = comp.getInteger(Backpack.TAGLIQUIDAMOUNT);
        String blockName = comp.getString(Backpack.TAGLIQUID);
        Block block = Block.getBlockFromName(blockName);
        FluidStack fluid = getFluid(block);
        
        if (block != null && amount > 0 && bucket != null && fluid != null) 
        {
        	new FluidBucketWrapper(bucket).fill(fluid, true);
            
            comp.setInteger(Backpack.TAGLIQUIDAMOUNT, amount-1);
            
            if (comp.getInteger(Backpack.TAGLIQUIDAMOUNT) < 1) 
            {
            	comp.setInteger(Backpack.TAGLIQUIDAMOUNT, 0);
            	comp.setString(Backpack.TAGLIQUID, "");
            }
        }
	}
	
	private FluidStack getFluid(Block block) {

		if((block == Blocks.WATER || block == Blocks.FLOWING_WATER))
		{
			return new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
		}
		else if((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA))
		{
			return new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
		}
		else if(block instanceof IFluidBlock)
		{
			return new FluidStack(((IFluidBlock)block).getFluid(), Fluid.BUCKET_VOLUME);
		}

		return null;
	}

	private void addLiquid(ItemStack bucket) {
		
		NBTTagCompound comp = backpack.getTagCompound();
		int amount = comp.getInteger(Backpack.TAGLIQUIDAMOUNT);
        String blockName = comp.getString(Backpack.TAGLIQUID);
        Block block = Block.getBlockFromName(blockName);
        FluidStack fluid = FluidUtil.getFluidContained(bucket);
        
        if (fluid != null && (fluid.getFluid().getBlock() == block || block == null) && amount < 8) 
        {
        	if (block == null) 
        	{
        		comp.setString(Backpack.TAGLIQUID, Block.REGISTRY.getNameForObject(fluid.getFluid().getBlock()).toString());
        	}
        	
    		comp.setInteger(Backpack.TAGLIQUIDAMOUNT, amount+1);
    		bucket.setItem(Items.BUCKET);
        }
	}
	
	@Override
	public boolean mergeItemStack(ItemStack stack, int begin, int end, boolean backwards) {
		
		int i = backwards ? end - 1 : begin;
		int increment = backwards ? -1 : 1;
		
		boolean flag = false;
		
		while (stack.stackSize > 0 && i >= begin && i < end) {
			
			Slot slot = this.getSlot(i);
			ItemStack slotStack = slot.getStack();
			
			int slotStacklimit = i < this.invTank.getSizeInventory() ? slot.getSlotStackLimit() : 64;
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

			i += increment;
		}

		return flag;

	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index)
    {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index != 1 && index != 0)
            {
                if (itemstack1 != null && SlotBucket.isBucket(itemstack1) && itemstack1.getItem() != Items.BUCKET)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (itemstack1 != null && itemstack1.getItem() == Items.BUCKET)
                {
                    if (!this.mergeItemStack(itemstack1, 1, 2, false))
                    {
                        return null;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
    }
	
	@Override
	public void onContainerClosed(EntityPlayer playerIn)
    {
        super.onContainerClosed(playerIn);

        if (!playerIn.worldObj.isRemote)
        {
            for (int i = 0; i < 9; ++i)
            {
                ItemStack itemstack = this.invTank.removeStackFromSlot(i);

                if (itemstack != null)
                {
                	PlayerHelper.addStackToPlayer(playerIn, itemstack);
                }
            }
        }
    }

	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}
}
