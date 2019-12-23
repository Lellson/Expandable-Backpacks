package lellson.expandablebackpack.inventory.gui;

import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GUIBackpackWorkbench extends GUIContainerExpanded {
	
	private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

	public GUIBackpackWorkbench(Container container, ItemStack backpack) {
		super(container, backpack);
	}

	@Override
	protected int getTabId() {

		return EnumTab.WORKBENCH.getId();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
        this.fontRendererObj.drawString("Crafting", 28, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 3, 4210752);
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CRAFTING_TABLE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
        
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

}
