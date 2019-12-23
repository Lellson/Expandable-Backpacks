package lellson.expandablebackpack.inventory.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;

public class GuiTabButton extends GuiButton {

	public GuiTabButton(int buttonId, int x, int y) {
		super(buttonId, x, y, 22, 22, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY) {
	}
	
	@Override
	public void playPressSound(SoundHandler soundHandlerIn) {
	}
}
