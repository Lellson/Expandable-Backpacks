package lellson.expandablebackpack.inventory.gui;

import java.io.IOException;

import org.lwjgl.opengl.GL11;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.container.ContainerUpgrade;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.misc.GuiLockButton;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.misc.StringHelper;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class GUIUpgrade extends GUIContainerExpanded {
	
	private static final ResourceLocation location = new ResourceLocation(ExpandableBackpack.MODID + ":textures/gui/guiUpgrade.png");
	
	private GuiLockButton btnLock;
	private GuiTextField entry;

	public GUIUpgrade(ContainerUpgrade container) {
		super(container);
	}
	
	@Override
	public void initGui() {
		super.initGui();
		
		btnLock = new GuiLockButton(0, guiLeft + 27, guiTop + 70, 34, 18, !invUp.stack.getTagCompound().getBoolean(Backpack.TAGPUBLIC));
		this.buttonList.add(btnLock);
		
		entry = new GuiTextField(0, fontRendererObj, guiLeft + 33, guiTop + 7, 115, 12);
		entry.writeText(TextFormatting.getTextWithoutFormattingCodes(invUp.stack.getDisplayName()));
		entry.setFocused(false);
		entry.setEnableBackgroundDrawing(false);
		
		entryList.add(entry);
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		super.actionPerformed(button);
		
		if (button == btnLock) {
			btnLock.changeLock(containerUp.player);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		ItemStack slot1 = invUp.getStackInSlot(0);
        String s = Integer.toString(BackpackInventory.getSlots(invUp.stack)) + " Slots";
        int defaultx = this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2;
        this.fontRendererObj.drawString(s, defaultx - 54, 47, 4210752);
        
        for (int i = 0; i < invUp.getUpgrades(); i++) {
        	ItemStack stack = invUp.getStackInSlot(i+1);
        	String s2 = getUpgradeName(stack);
        	defaultx = this.xSize / 2 - this.fontRendererObj.getStringWidth(s2) / 2;
        	
        	this.fontRendererObj.drawString(s2, defaultx + 43, 29 + i*19, getUpgradeColor(stack), true);
        }
        
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

	public static int getUpgradeColor(ItemStack stack) {
		
		if (stack != null && stack.getItem() == BackpackItems.compartment) {
			return EnumCompartment.getEnumFromMeta(stack.getMetadata()).getColor();
		}
		
		return 4210752;
	}

	public static String getUpgradeName(ItemStack stack) {
		
		if (stack != null && stack.getItem() == BackpackItems.compartment) {
			String name = EnumCompartment.getEnumFromMeta(stack.getMetadata()).getName();
			return StringHelper.uppercaseFirstLetter(name);
		}

		return "";
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(location);
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize + 6);
		
		int locks = 3 - invUp.getUpgrades();

		for (int i = 0; i < locks; i++) {
			this.drawTexturedModalRect(k + 70, l + 61 - i*19, 176, 0, 18, 18);
		}
		
		super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
	}
	
	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		
		ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(entry.getText(), 3));
	}
	
	@Override
	protected int getTabId() {

		return EnumTab.UPGRADE.getId();
	}
}
