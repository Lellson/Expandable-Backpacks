package lellson.expandablebackpack.item.compartment;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Compartment extends Item {
	
	public static final String[] compartments = {"basic", "soulbound", "pickup", "empty", "infinite", "incinerate", "armored", "swap", "guarded", "quivered", "ender", "sending", "crafting", "creative", "tank"};
	
	public Compartment() {
		this.setHasSubtypes(true);
		setUnlocalizedName("compartment");
		setRegistryName("compartment");
	}
	
	@Override
	public String getUnlocalizedName(ItemStack item) {
    	
        int i = MathHelper.clamp_int(item.getItemDamage(), 0, (compartments.length - 1));
        return super.getUnlocalizedName() + "." + compartments[i];
    }
    
    @Override
    public void getSubItems(Item item, CreativeTabs tab, List list) {
    	
        for (int i = 0; i < compartments.length; ++i) {
        	
            list.add(new ItemStack(item, 1, i));
        }
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
    	
    	String description = EnumCompartment.getEnumFromMeta(stack.getMetadata()).getDescription();
		tooltip.add(GuiScreen.isShiftKeyDown() ? description : TextFormatting.DARK_GRAY + "SHIFT");
    }

}
