package lellson.expandablebackpack.inventory.misc;

import com.mojang.realmsclient.gui.ChatFormatting;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.gui.GUIContainerExpanded;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentString;

public class GuiLockButton extends GuiButton {
	
	public boolean locked;

	public GuiLockButton(int buttonId, int x, int y, int widthIn, int heightIn, boolean locked) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.locked = locked;
	}
	
	public void changeLock(EntityPlayer player) {
		this.locked = !this.locked;
		player.addChatMessage(new TextComponentString(this.locked ? ChatFormatting.RED + "The backpack is now private!" : ChatFormatting.GREEN + "The backpack is now public!"));
		ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(2));
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
        int i = getHoverState(this.hovered);
        int j = getLockedState(this.locked);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        this.drawTexturedModalRect(this.xPosition, this.yPosition, 0 + j*34, 0 + i*18, 34, 18);
        this.mouseDragged(mc, mouseX, mouseY);
    }
	
	private int getLockedState(boolean locked) {

		return locked ? 0 : 1;
	}

	@Override
	protected int getHoverState(boolean mouseOver) {

		return mouseOver ? 1 : 0;
	}

}
