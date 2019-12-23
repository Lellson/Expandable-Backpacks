package lellson.expandablebackpack.misc;

import java.util.List;
import java.util.Random;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.item.BackpackItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BackpackTab extends CreativeTabs {

	public BackpackTab() {
		super(ExpandableBackpack.MODID);
		setNoScrollbar();
	}

	@Override
	public Item getTabIconItem() {

		return BackpackItems.expandableBackpack;
	}
	
	@Override
	public int getIconItemDamage() {

		return new Random().nextInt(17);
	}
	
	@Override
	public void displayAllRelevantItems(List<ItemStack> list) {
		
		list.add(new ItemStack(BackpackItems.expandableBackpack));
		list.add(new ItemStack(BackpackItems.obsidianLeather));
		list.add(new ItemStack(BackpackItems.sendingController));
		BackpackItems.compartment.getSubItems(BackpackItems.compartment, this, list);
	}
}
