package lellson.expandablebackpack.item.backpack;

import net.minecraft.util.IStringSerializable;

public enum EnumColor implements IStringSerializable {
	
	DEFAULT(0, "default", 0),
	BLACK(1, "black", 0x191919),
	RED(2, "red", 0x993333),
	GREEN(3, "green", 0x667F33),
	BROWN(4, "brown", 0x664C33),
	BLUE(5, "blue", 0x334CB2),
	PURPLE(6, "purple", 0x7F3FB2),
	CYAN(7, "cyan", 0x4C7F99),
	LIGHT_GRAY(8, "lightGray", 0x999999),
	GRAY(9, "gray", 0x4C4C4C),
	PINK(10, "pink", 0xF27FA5),
	LIME(11, "lime", 0x7FCC19),
	YELLOW(12, "yellow", 0xE5E533),
	LIGHT_BLUE(13, "lightBlue", 0x6699D8),
	MAGENTA(14, "magenta", 0xB24CD8),
	ORANGE(15, "orange", 0xD87F33),
	WHITE(16, "white", 0xFFFFFF);
	
	private int id;
	private String name;
	private int color;

	private EnumColor(int id, String name, int color) {
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public static EnumColor getEnumFromMeta(int meta) {
			
		if (meta >= values().length || meta < 0) 
			return null;
		
		return values()[meta];
	}
	
	public static int getFormattingColorFromMeta(int meta) {
		
		switch(meta) {
			case 1: return 0;
			case 2: return 12;
			case 3: return 2;
			case 4: return 4;
			case 5: return 9;
			case 6: return 5;
			case 7: return 3;
			case 8: return 7;
			case 9: return 8;
			case 10: return 13;
			case 11: return 10;
			case 12: return 14;
			case 13: return 11;
			case 14: return 5;
			case 15: return 6;
			case 16: return 15;
			default: return 15;
		}
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
	
	public int getColor() {
		return color;
	}
}
