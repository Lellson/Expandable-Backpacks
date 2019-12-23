package lellson.expandablebackpack.item.compartment;

import lellson.expandablebackpack.misc.BackpackConfig;
import lellson.expandablebackpack.misc.StringHelper;
import net.minecraft.util.IStringSerializable;

public enum EnumCompartment implements IStringSerializable {
	
	BASIC(0, "basic", 0xAAAAAA, "", "Used to craft other compartments"),
	SOULBOUND(1, "soulbound", 0xFFFF55, "minecraft:nether_star", "Keeps the upgraded backpack with all items on death"),
	PICKUP(2, "pickup", 0x55FF55, "minecraft:hopper", "Picked up items go automatically in the upgraded backpack if they are already at least once in it"),
	EMPTY(3, "empty", 0xFF5555, "minecraft:cauldron", "Shift-Rightclick a inventory block to move everything from the upgraded backpack in the inventory block"),
	INFINITE(4, "infinite", 0x55FFFF, "minecraft:diamond_block", "Allows you to put other backpacks in the upgraded backpack"),
	INCINERATE(5, "incinerate", 0xAA0000, "minecraft:lava_bucket", "Destroys all items in the upgraded backpack's inventory"),
	ARMORED(6, "armored", 0x555555, "minecraft:iron_chestplate", "Gives the upgraded backpack a higher protection value"),
	SWAP(7, "swap", 0xFFAA00, "minecraft:gold_block", "Press 'Swap Hotbar' to swap all items of your hotbar with all items of the backpack's first 9 slots"),
	GUARDED(8, "guarded", 0x00AA00, "minecraft:golden_sword", "Non-owners who try to open the backpack take damage and receive slowness"),
	QUIVERED(9, "quivered", 0x5555FF, "minecraft:leather", "Shooting with a bow, consumes arrows from the upgraded backpack"),
	ENDER(10, "ender", 0x00AAAA, "minecraft:ender_chest", "Adds a new tab with player's ender chest inventory to the upgraded backpack"), 
	SENDING(11, "sending", 0xFF55FF, "expandablebackpack:sendingController", "Shift-Rightclick while looking at an inventory block to select a location. All items in the backpack will be sent to the inventory. You have to be in the same dimension or the inventory block has to be in the overworld"),
	CRAFTING(12, "crafting", 0x000000, "minecraft:crafting_table", "Adds a new tab with a workbench inventory to the upgraded backpack"),
	CREATIVE(13, "creative", 0xAA00AA, "", "Creative only. Items in the backpack will not get removed if you try to take them out"), 
	TANK(14, "tank", 0x0000AA, "minecraft:iron_block", "Adds a new tab which shows the stored liquid. Rightclick any liquid source block to store it in the backpack. You can store up to 8 buckets of one kind of liquid. Shift-Rigthclick to place the stored liquid");
	
	private int id;
	private String name;
	private int color;
	private String defaultItem;
	private String description;
	
	private EnumCompartment(int id, String name, int color, String defaultItem, String description) {
		this.id = id;
		this.name = name;
		this.color = color;
		this.defaultItem = defaultItem;
		this.description = description;
	}
	
	public static EnumCompartment getEnumFromMeta(int meta) {
		
		if (meta >= values().length || meta < 0) 
			return null;
		
		return values()[meta];
	}
	
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
	public Object getItem() {
		return StringHelper.getItemStackFromString(BackpackConfig.compartmentItem[getId()], true);
	}

	public int getColor() {
		return color;
	}
	
	public String getDefaultItem() {
		return defaultItem;
	}

	public String getDescription() {
		return description;
	}
}
