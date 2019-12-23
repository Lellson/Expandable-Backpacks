package lellson.expandablebackpack.inventory.misc;

import lellson.expandablebackpack.inventory.gui.GUIContainerExpanded;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;

public class GuiBackpackButton extends GuiButton {
	
	private int index;
	private GuiContainer gui;

	public GuiBackpackButton(int buttonId, int x, int y, int index) {
		super(buttonId, x, y, 13, 13, "");
		this.index = index;
	}
	
	
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
			
		if (!this.visible || !this.enabled) {
			return;
		}
    
        FontRenderer fontrenderer = mc.fontRendererObj;
        mc.getTextureManager().bindTexture(GUIContainerExpanded.COMPONENTS);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 + index*13, 36 + getHoverState(this.hovered)*13, 13, 13);
        this.mouseDragged(mc, mouseX, mouseY);
    }
	
	@Override
	protected int getHoverState(boolean mouseOver) {

		return mouseOver ? 1 : 0;
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn) {
	}
}
