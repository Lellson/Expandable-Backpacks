package lellson.expandablebackpack.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

public class StringHelper {
	
	public static String uppercaseFirstLetter(String string) {
		
		return string.substring(0,1).toUpperCase() + string.substring(1);
	}
	
	public static Item getItemFromString(String itemName) {
		
		return Item.REGISTRY.getObject(new ResourceLocation(itemName));
	}
	
	public static ItemStack getItemStackFromString(String itemName, boolean outputError) {
		
		String[] pieces = itemName.split("-");
		Item item = Item.REGISTRY.getObject(new ResourceLocation(pieces[0]));
		
		if (item == null) {
			if (outputError && !itemName.equals("")) {
				FMLLog.warning("'%s' is not a valid ItemStack! Returns null.", pieces[0]);
			}
			return null;
		}
		
		if (pieces.length > 1) {
			return new ItemStack(item, 1, Integer.valueOf(pieces[1]));
		} else {
			return new ItemStack(item);
		}
	}
}
