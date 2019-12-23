package lellson.expandablebackpack.inventory.gui;

import java.awt.Color;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidUtil;

public class GUIBackpackTank extends GUIContainerExpanded {
	
	private static final ResourceLocation TEXTURE = new ResourceLocation(ExpandableBackpack.MODID + ":textures/gui/guiTank.png");
	
	private ItemStack backpack;
	private Container container;
	private EntityPlayer player;

	public GUIBackpackTank(Container container, EntityPlayer player, ItemStack backpack) {
		super(container, backpack);
		this.backpack = backpack;
		this.container = container;
		this.player = player;
	}

	@Override
	protected int getTabId() {

		return EnumTab.TANK.getId();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
        this.fontRendererObj.drawString("Tank", this.xSize / 2 - 80, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 3, 4210752);
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        
        int amount = backpack.getTagCompound().getInteger(Backpack.TAGLIQUIDAMOUNT);
        String blockName = backpack.getTagCompound().getString(Backpack.TAGLIQUID);
        Block block = Block.getBlockFromName(blockName);
        Color color = new Color(getColorForFluidBlock(block, blockName));
		boolean k = true;
		
		container.detectAndSendChanges();
		
		GlStateManager.color((float)color.getRed() / 255F, (float)color.getGreen() / 255F, (float)color.getBlue() / 255F, 0.5F);
		for (int i1 = 0; i1 < amount; i1++) 
		{
			int x1 = i + 68 + (k ? 0 : 24);
			int y1 = j + 59 - ((i1/2) * 16);
			this.drawTexturedModalRect(x1, y1, 176, 0, 16, 16);
			k = !k;
		}
		
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
	
	private int getColorForFluidBlock(Block block, String name) {
		
		int c = 0x3399FF;
		
		if (block == null)
			return c;
        
        for (Fluid fluid : FluidRegistry.getRegisteredFluids().values()) 
        {
        	if (fluid.getBlock() == block) 
        	{
        		c = fluid.getColor();
        	}
        }
        
        if (c == -1 || block == Blocks.FLOWING_LAVA) {
        	if (block.getDefaultState().getMaterial() == Material.LAVA) 
        	{
        		c = 0xFF3C1A;
        	}
        	else
        	{
        		c = 0x3399FF;
        	}
        }
        
        return c;
	}
}
