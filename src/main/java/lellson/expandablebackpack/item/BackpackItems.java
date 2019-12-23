package lellson.expandablebackpack.item;

import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.Compartment;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.BackpackTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BackpackItems {
	
	public static Item obsidianLeather;
	public static Item sendingController;
	public static Item expandableBackpack;
	public static Item expandableBackpackArmored;
	public static Item compartment;
	
	public static CreativeTabs tab;
	
	public static ArmorMaterial backpack = EnumHelper.addArmorMaterial("backpack", "backpack", -1, new int[]{1,1,1,1}, 0, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F);
	public static ArmorMaterial backpackArmored = EnumHelper.addArmorMaterial("backpackArmored", "backpackArmored", -1, new int[]{6,6,6,6}, 0, SoundEvents.ITEM_ARMOR_EQUIP_IRON, 0.0F);
	
	public static void init() {
		
		obsidianLeather = new Item().setUnlocalizedName("obsidianLeather").setRegistryName("obsidianLeather");
		sendingController = new Item().setUnlocalizedName("sendingController").setRegistryName("sendingController");
		expandableBackpack = new Backpack(backpack, "expandableBackpack");
		expandableBackpackArmored = new Backpack(backpackArmored, "expandableBackpackArmored");
		compartment = new Compartment();
		
		register(obsidianLeather);
		register(sendingController);
		register(expandableBackpack);
		register(expandableBackpackArmored);
		register(compartment);
		
		GameRegistry.addShapelessRecipe(new ItemStack(obsidianLeather), Blocks.OBSIDIAN, Items.LEATHER, Items.STRING, Items.STRING);
		GameRegistry.addShapedRecipe(new ItemStack(sendingController), new Object[]{"RER","ESE","RER", 'E', Items.ENDER_EYE, 'R', Blocks.REDSTONE_BLOCK, 'S', Items.NETHER_STAR});
		GameRegistry.addShapedRecipe(new ItemStack(sendingController), new Object[]{"ERE","RSR","ERE", 'E', Items.ENDER_EYE, 'R', Blocks.REDSTONE_BLOCK, 'S', Items.NETHER_STAR});
		GameRegistry.addShapedRecipe(new ItemStack(sendingController), new Object[]{"RER","ESE","RER", 'E', Items.ENDER_EYE, 'R', Blocks.REDSTONE_BLOCK, 'S', Blocks.DIAMOND_BLOCK});
		GameRegistry.addShapedRecipe(new ItemStack(sendingController), new Object[]{"ERE","RSR","ERE", 'E', Items.ENDER_EYE, 'R', Blocks.REDSTONE_BLOCK, 'S', Blocks.DIAMOND_BLOCK});
		
		GameRegistry.addShapelessRecipe(new ItemStack(expandableBackpack), obsidianLeather, Blocks.CHEST);
		
		GameRegistry.addShapelessRecipe(new ItemStack(compartment, 1, 0), obsidianLeather);
		GameRegistry.addShapelessRecipe(new ItemStack(obsidianLeather), compartment);
		
		for (int i = 0; i < Compartment.compartments.length-1; i++)
			addUpgradeRecipe(i+1);
		
		new BackpackTab();
	}

	public static void registerRenderers() {
		
		render(obsidianLeather);
		render(sendingController);
		
		registerItemVariants(expandableBackpack, Backpack.colors);
		renderMeta(expandableBackpack, Backpack.colors);
		
		registerItemVariants(expandableBackpackArmored, Backpack.colors);
		renderMeta(expandableBackpackArmored, Backpack.colors);
		
		registerItemVariants(compartment, Compartment.compartments);
		renderMeta(compartment, Compartment.compartments);
	}
	
	private static void register(Item item) {
		
		GameRegistry.register(item);
	}
	
	public static void registerItemVariants(Item item, String... names) {
		
		for (String name : names) {
			ModelBakery.registerItemVariants(item, new ResourceLocation(item.getRegistryName() + "_" + name));
		}
	}

	private static void render(Item item) {
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
	public static void renderMeta(Item item, String... names) {
		
		for (int i = 0; i < names.length; i++) {
			Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i, new ModelResourceLocation(item.getRegistryName() + "_" + names[i], "inventory"));	
		}
	}
	
	private static void addUpgradeRecipe(int meta) {
		
		Object obj = EnumCompartment.getEnumFromMeta(meta).getItem();
		
		if (obj != null)
			GameRegistry.addShapelessRecipe(new ItemStack(compartment, 1, meta), compartment, obj);
	}
}
