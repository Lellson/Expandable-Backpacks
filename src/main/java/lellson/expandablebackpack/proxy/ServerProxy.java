package lellson.expandablebackpack.proxy;

import java.util.List;

import lellson.expandablebackpack.inventory.container.ContainerBackpack;
import lellson.expandablebackpack.inventory.container.ContainerBackpackTank;
import lellson.expandablebackpack.inventory.container.ContainerBackpackWorkbench;
import lellson.expandablebackpack.inventory.container.ContainerSlotBackpack;
import lellson.expandablebackpack.inventory.container.ContainerUpgrade;
import lellson.expandablebackpack.inventory.gui.GUIBackpack;
import lellson.expandablebackpack.inventory.gui.GUIBackpackEnder;
import lellson.expandablebackpack.inventory.gui.GUIBackpackTank;
import lellson.expandablebackpack.inventory.gui.GUIBackpackWorkbench;
import lellson.expandablebackpack.inventory.gui.GUISlotBackpack;
import lellson.expandablebackpack.inventory.gui.GUIUpgrade;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.iinventory.BackpackSlotInventory;
import lellson.expandablebackpack.inventory.iinventory.InventoryTank;
import lellson.expandablebackpack.inventory.iinventory.UpgradeInventory;
import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class ServerProxy implements IGuiHandler {

	public void register() {}
	
	public static final int BACKPACK_SLOT = 0;
	public static final int BACKPACK = 1;
	public static final int UPGRADE = 2;
	public static final int ENDER = 3;
	public static final int WORKBENCH = 4;
	public static final int TANK = 5;
	
	public static final int PLAYER_INV = 99;

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		List<ItemStack> backpacks = Backpack.getBackpacks(player, true);
		
		switch(ID) {
			case BACKPACK_SLOT: 
				return new ContainerSlotBackpack(player, player.inventory, new BackpackSlotInventory(player));
			case BACKPACK: 
				if (!backpacks.isEmpty()) return new ContainerBackpack(player, player.inventory, new BackpackInventory(backpacks.get(0), player));
			case UPGRADE: 
				if (!backpacks.isEmpty()) return new ContainerUpgrade(player, player.inventory, new UpgradeInventory(backpacks.get(0), player));
			case ENDER: 
				if (!backpacks.isEmpty()) return new ContainerChest(player.inventory, player.getInventoryEnderChest(), player);
			case WORKBENCH:
				if (!backpacks.isEmpty()) return new ContainerBackpackWorkbench(player.inventory, player, backpacks.get(0));
			case TANK:
				if (!backpacks.isEmpty()) return new ContainerBackpackTank(player, player.inventory, new InventoryTank(), backpacks.get(0));
			case PLAYER_INV:
				return new ContainerPlayer(player.inventory, true, player);
			default: 
				return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		
		List<ItemStack> backpacks = Backpack.getBackpacks(player, true);
		
		switch(ID) {
			case BACKPACK_SLOT: 
				return new GUISlotBackpack(new ContainerSlotBackpack(player, player.inventory, new BackpackSlotInventory(player)));
			case BACKPACK: 
				if (!backpacks.isEmpty()) return new GUIBackpack(new ContainerBackpack(player, player.inventory, new BackpackInventory(backpacks.get(0), player)));
			case UPGRADE: 
				if (!backpacks.isEmpty()) return new GUIUpgrade(new ContainerUpgrade(player, player.inventory, new UpgradeInventory(backpacks.get(0), player)));
			case ENDER: 
				if (!backpacks.isEmpty()) return new GUIBackpackEnder(new ContainerChest(player.inventory, player.getInventoryEnderChest(), player), backpacks.get(0));
			case WORKBENCH:
				if (!backpacks.isEmpty()) return new GUIBackpackWorkbench(new ContainerBackpackWorkbench(player.inventory, player, backpacks.get(0)), backpacks.get(0));
			case TANK:
				if (!backpacks.isEmpty()) return new GUIBackpackTank(new ContainerBackpackTank(player, player.inventory, new InventoryTank(), backpacks.get(0)), player, backpacks.get(0));
			case PLAYER_INV:
				return new GuiInventory(player);
			default: 
				return null;
		}
	}
}
