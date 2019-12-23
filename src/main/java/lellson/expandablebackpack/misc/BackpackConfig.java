package lellson.expandablebackpack.misc;

import static lellson.expandablebackpack.ExpandableBackpack.config;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.item.compartment.Compartment;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import net.minecraftforge.fml.common.Loader;

public class BackpackConfig {
	
	private static final String GENERAL = "general";
	private static final String BUTTONS = "buttons";
	private static final String COMPARTMENT = "compartment";
	private static final String BACKPACK_SIZE = "backpack_size";
	
	public static String[] blacklistNormal;
	private static final String[] blacklistDefault = new String[]{ExpandableBackpack.MODID + ":expandableBackpack", ExpandableBackpack.MODID + ":expandableBackpackArmored"};
	public static String[] blacklistInfinite;
	
	public static String[] compartmentItem;
	
	public static boolean whohasmybackpackAllowed;
	public static int buttonVisibility;
	public static int buttonVisibility2;
	public static int x;
	public static int y;
	public static int xCreative;
	public static int yCreative;
	public static int initialBackpackSize;
	public static int maxBackpackSize;
	public static int slotsPerUpgrade;
	public static boolean moveButtonsOnPotionActive;
	public static boolean jeiInstalled = Loader.isModLoaded("JEI");
	
	public static void init() {
		
		config.load();
		
		config.addCustomCategoryComment(COMPARTMENT, "Recipe items for all upgrades/compartments. The recipe will be 1 basic compartment plus the item from the\nconfig option. Let the config option empty if you want to disable the recipe. You can also define metadata\nvalues for your item by typing '-' followed by the metadata value after the item.\nExample: minecraft:wool-4 - The compartment could only be crafted with yellow wool and a basic compartment");
		config.addCustomCategoryComment(BACKPACK_SIZE, "These values are used to determine the backpack size. The maxBackpackSize value and the initialBackpackSize\nvalue should be divisible by the slotsPerUpgrade value for better results");
		
		blacklistNormal = config.getStringList("itemBlacklist", GENERAL, blacklistDefault, "Items which can't be put in an expandable backpack (without infinite upgrade)");
		blacklistInfinite = config.getStringList("itemBlacklistInfinite", GENERAL, new String[]{}, "Items which can't be put in an expandable backpack (with infinite upgrade)");
		
		whohasmybackpackAllowed = config.getBoolean("commandAllowed", GENERAL, true, "Are non operators or non creative mode players allowed to use the '/backpack whohasmybackpack' command?");
		buttonVisibility = config.getInt("buttonVisibilitySlot", BUTTONS, 0, 0, 2, "Usability of the backpack slot button in player's inventory GUI:\n"
																			 + "0: Only enabled if the player has at least one backpack.\n"
																			 + "1: Always enabled.\n"
																			 + "2: Always disabled.");
		buttonVisibility2 = config.getInt("buttonVisibilityOpen", BUTTONS, 0, 0, 2, "Usability of the open backpack button in player's inventory GUI:\n"
																			 + "0: Only enabled if the player has at least one backpack.\n"
																			 + "1: Always enabled.\n"
																			 + "2: Always disabled.");
		x = config.getInt("buttonsXPosition", BUTTONS, 97, 0, 1000, "The horizontal (x) position for the buttons in the normal inventory");
		y = config.getInt("buttonsYPosition", BUTTONS, 63, 0, 1000, "The vertical (y) position for the buttons in the normal inventory");
		xCreative = config.getInt("buttonsXPositionCreative", BUTTONS, 158, 0, 1000, "The horizontal (x) position for the buttons in the creative inventory");
		yCreative = config.getInt("buttonsYPositionCreative", BUTTONS, 3, 0, 1000, "The vertical (y) position for the buttons in the creative inventory");
		moveButtonsOnPotionActive = config.getBoolean("moveButtonsOnPotionActive", BUTTONS, !jeiInstalled, "Should the buttons move to the right if the player has an active potion effect?");
		
		compartmentItem = new String[Compartment.compartments.length];
		compartmentItem[0] = EnumCompartment.BASIC.getDefaultItem();
		
		for (int i = 1; i < Compartment.compartments.length; i++) {
			EnumCompartment com = EnumCompartment.getEnumFromMeta(i);
			compartmentItem[i] = config.getString(com.getName() + "Item", COMPARTMENT, com.getDefaultItem(), "Required item to craft the " + com.getName() + " compartment");
		}
		
		initialBackpackSize = config.getInt("initialBackpackSize", BACKPACK_SIZE, 1, 0, 54, "Value which is used to set the initial slot amount for backpacks when created");
		maxBackpackSize = config.getInt("maxBackpackSize", BACKPACK_SIZE, 54, 1, 54, "The maximum amount of slots which backpacks can have");
		slotsPerUpgrade = config.getInt("slotsPerUpgrade", BACKPACK_SIZE, 1, 1, 54, "Amount of slots which each slot upgrade adds to the backpack");
		
		config.save();
	}
}
