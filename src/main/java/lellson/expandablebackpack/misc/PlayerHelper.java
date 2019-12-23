package lellson.expandablebackpack.misc;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

public class PlayerHelper {
	
	public static final EntityEquipmentSlot[] HANDS = new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND, EntityEquipmentSlot.OFFHAND};
	public static final EntityEquipmentSlot[] ARMOR = new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET};
		
	public static void addStackToPlayer(EntityPlayer player, ItemStack stack) {
		
		if (!player.inventory.addItemStackToInventory(stack)) player.dropItem(stack, false);
	}


	public static List<Integer> getSlotsForStack(EntityPlayer player, ItemStack stack) {
		
		List<Integer> list = new ArrayList<Integer>();

		for (int i = 0; i < player.inventory.mainInventory.length; i++) 
		{
			ItemStack in = player.inventory.getStackInSlot(i);
			
			if (in != null && in.getItem() == stack.getItem() && in.getItemDamage() == stack.getItemDamage()) 
			{
				list.add(i);
			}
		}
		
		return list;
	}
}
