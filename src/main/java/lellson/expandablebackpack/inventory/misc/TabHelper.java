package lellson.expandablebackpack.inventory.misc;

import java.util.ArrayList;
import java.util.List;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import lellson.expandablebackpack.proxy.ServerProxy;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabHelper {
	
	public static List<EnumTab> getTabs(ItemStack backpack) {
		
		List<EnumTab> list = new ArrayList<TabHelper.EnumTab>();
		
		if (backpack == null) return list;
		
		for (int i = 0; i < EnumTab.values().length; i++) 
		{
			EnumTab e = EnumTab.values()[i];
			
			if (e.getCompartmentId() == 0 || Backpack.getUpgrade(backpack, EnumCompartment.getEnumFromMeta(e.getCompartmentId()).getId()) > 0) {
				list.add(e);
			}
		}
		
		return list;
	}
	
	public static void openGui(EnumTab tab) {

		ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(tab.getName(), 0));
	}
	
	public static enum EnumTab {
		
		BACKPACK(0, "backpack", BackpackItems.expandableBackpack, 0, ServerProxy.BACKPACK),
		UPGRADE(1, "upgrade", BackpackItems.compartment, 0, ServerProxy.UPGRADE),
		ENDERCHEST(2, "enderchest", Item.getItemFromBlock(Blocks.ENDER_CHEST), EnumCompartment.ENDER.getId(), ServerProxy.ENDER),
		WORKBENCH(3, "workbench", Item.getItemFromBlock(Blocks.CRAFTING_TABLE), EnumCompartment.CRAFTING.getId(), ServerProxy.WORKBENCH), 
		TANK(4, "tank", Items.BUCKET, EnumCompartment.TANK.getId(), ServerProxy.TANK);
		
		private int id;
		private String name;
		private Item iconItem;
		private int compartmentId;
		private int guiId;
		
		private EnumTab(int id, String name, Item iconItem, int compartmentId, int guiId) {
			this.id = id;
			this.name = name;
			this.iconItem = iconItem;
			this.compartmentId = compartmentId;
			this.guiId = guiId;
		}
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public Item getIconItem() {
			return iconItem;
		}
		
		public int getCompartmentId() {
			return compartmentId;
		}
		
		public int getGuiId() {
			return guiId;
		}
	}
}
