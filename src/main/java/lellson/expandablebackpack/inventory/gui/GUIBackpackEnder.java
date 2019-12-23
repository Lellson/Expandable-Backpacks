package lellson.expandablebackpack.inventory.gui;

import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GUIBackpackEnder extends GUIContainerExpanded {
	
	private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

	public GUIBackpackEnder(Container container, ItemStack backpack) {
		super(container, backpack);
	}

	@Override
	protected int getTabId() {

		return EnumTab.ENDERCHEST.getId();
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
        this.fontRendererObj.drawString("Ender Inventory", 8, 6, 4210752);
        this.fontRendererObj.drawString("Inventory", 8, this.ySize - 96 + 3, 4210752);
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, 71);
        this.drawTexturedModalRect(i, j + 71, 0, 126, this.xSize, 96);
        
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }

}
