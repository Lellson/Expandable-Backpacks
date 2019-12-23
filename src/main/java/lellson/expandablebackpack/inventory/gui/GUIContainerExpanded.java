package lellson.expandablebackpack.inventory.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.container.ContainerBackpack;
import lellson.expandablebackpack.inventory.container.ContainerUpgrade;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.iinventory.UpgradeInventory;
import lellson.expandablebackpack.inventory.misc.GuiTabButton;
import lellson.expandablebackpack.inventory.misc.TabHelper;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class GUIContainerExpanded extends GuiContainer {
	
	public static final ResourceLocation COMPONENTS = new ResourceLocation(ExpandableBackpack.MODID + ":textures/gui/icons.png");
	
	protected List<GuiTextField> entryList = new ArrayList<GuiTextField>();
	protected UpgradeInventory invUp = null;
	protected ContainerUpgrade containerUp = null;
	protected BackpackInventory invBack = null;
	protected ContainerBackpack containerBack = null;
	protected boolean flag;
	protected GuiTabButton[] btnTab;
	protected ItemStack backpack;
	
	public GUIContainerExpanded(ItemStack backpack) {
		super(new Container() {
			
			@Override
			public boolean canInteractWith(EntityPlayer player) {
				return true;
			}
		});
		this.backpack = backpack;
	}
	
	public GUIContainerExpanded(Container container, ItemStack backpack) {
		super(container);
		this.backpack = backpack;
	}
	
	public GUIContainerExpanded(ContainerUpgrade container) {
		super(container);
		this.containerUp = container;
		this.invUp = container.invUp;
		ySize += 6;
	}
	
	public GUIContainerExpanded(ContainerBackpack container) {
		super(container);
		this.containerBack = container;
		this.invBack = container.invBack;
		flag = container.slots > 27;
	}
	
	@Override
	protected boolean checkHotbarKeys(int keyCode) {
		return false;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		btnTab = new GuiTabButton[getTabs().size()];
		
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		l -= flag ? 25 : 0;
		
		for (int i = 0; i < getTabs().size(); i++) {
			btnTab[i] = new GuiTabButton(i, k + 5 + i*26, l - 20);
			buttonList.add(btnTab[i]);
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		List<EnumTab> list = getTabs();
		
		for (int i = 0; i < Math.min(btnTab.length, list.size()); i++) 
		{
			EnumTab tab = list.get(i);
			if (button == btnTab[i] && tab.getId() != getTabId()) 
			{
				TabHelper.openGui(tab);
			}
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		
		List<EnumTab> list = getTabs();
		
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		l -= flag ? 25 : 0;
		
		for (int i = 0; i < list.size(); i++) 
		{
			EnumTab tab = list.get(i);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			
			this.mc.getTextureManager().bindTexture(COMPONENTS);
			this.drawTexturedModalRect(k + 4 + i*26, l - 21, tab.getId() == getTabId() ? 68 : 92, 0, 24, 24);
			drawItemIntoGUI(this.itemRender, new ItemStack(tab.getIconItem()), k + 8 + i*26, l - 17);
		}
	}
	
	public static void drawItemIntoGUI(RenderItem itemRenderer, ItemStack stack, int x, int y) {
		
		RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.enableRescaleNormal();
        GlStateManager.enableColorMaterial();
        GlStateManager.enableLighting();
        itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
		GlStateManager.disableLighting();
        GlStateManager.depthMask(true);
        GlStateManager.enableDepth();
	}

	protected abstract int getTabId();
	
	private List<EnumTab> getTabs() {
		return TabHelper.getTabs(invBack != null ? invBack.stack : invUp != null ? invUp.stack : backpack != null ? backpack : null);
	}

	@Override
	public void updateScreen() {
		super.updateScreen();
		
		for (GuiTextField entry : entryList)
			entry.updateCursorCounter();
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		
		super.drawScreen(mouseX, mouseY, partialTicks);
		
		for (GuiTextField entry : entryList)
			entry.drawTextBox();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		
		for (GuiTextField entry : entryList) {
			entry.textboxKeyTyped(typedChar, keyCode);
			if (entry.isFocused() && keyCode == Minecraft.getMinecraft().gameSettings.keyBindInventory.getKeyCode()) return;
		}
		
		super.keyTyped(typedChar, keyCode);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		
		for (GuiTextField entry : entryList)
			entry.mouseClicked(mouseX, mouseY, mouseButton);
	}
	
}
