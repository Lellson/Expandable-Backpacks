package lellson.expandablebackpack.inventory.gui;

import org.lwjgl.opengl.GL11;

import lellson.expandablebackpack.inventory.container.ContainerBackpack;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import net.minecraft.util.text.TextFormatting;

public class GUIBackpack extends GUIContainerExpanded {

	public GUIBackpack(ContainerBackpack container) {
		super(container);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		String s = TextFormatting.getTextWithoutFormattingCodes(this.invBack.stack.getDisplayName());
        this.fontRendererObj.drawString(s, this.xSize / 2 - 80, flag ? -19 : 6, 4210752);
        this.fontRendererObj.drawString("Inventory", this.xSize / 2 - 80, containerBack.getInventoryStringY(), 4210752);
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(containerBack.getGUITexture());
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		l -= flag ? 25 : 0;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize + (flag ? 60 : 0));
		
		int rows = containerBack.slots / 9 + 1;
		int amount = containerBack.slots;
		int rest;
		
		int i;
		for (i = 0; i < rows; ++i) {
			rest = amount > 9 ? 9 : amount;
			for (int j = 0; j < rest; ++j) {
				this.drawTexturedModalRect(k + 7 + j * 18, l + 17 + i * 18, 176, 0, 18, 18);
				amount--;
			}
		}
		
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}

	@Override
	protected int getTabId() {

		return EnumTab.BACKPACK.getId();
	}
}
