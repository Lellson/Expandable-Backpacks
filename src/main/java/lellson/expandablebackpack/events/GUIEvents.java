package lellson.expandablebackpack.events;

import java.util.List;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.misc.GuiBackpackButton;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.misc.BackpackConfig;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GUIEvents {
	
	public static final int BACKPACK_SLOT_BUTTON = 1860;
	public static final int OPEN_BACKPACK_BUTTON = 1861;
	
	public boolean noClient;
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new GUIEvents());
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void initGUI(GuiScreenEvent.InitGuiEvent.Post event) {
		
		if (Minecraft.getMinecraft().theWorld == null) 
			noClient = false;
		
		if (event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative) {
			
			if (noClient) return;
			
			EntityPlayer player = Minecraft.getMinecraft().thePlayer;
			GuiContainer gui = (GuiContainer) event.getGui();
			Container container = gui.inventorySlots;
			
			int x = 0, y = 0;
			
			boolean potion = BackpackConfig.moveButtonsOnPotionActive && !player.getActivePotionEffects().isEmpty();
			
			if (event.getGui() instanceof GuiInventory) 
			{
				x = (gui.width - 176) / 2 + BackpackConfig.x + (potion ? 60 : 0);
				y = (gui.height - 166) / 2 + BackpackConfig.y;
			} 
			else if (event.getGui() instanceof GuiContainerCreative) 
			{
				x = (gui.width - 195) / 2 + BackpackConfig.xCreative + (potion ? 60 : 0);
				y = (gui.height - 136) / 2 + BackpackConfig.yCreative;
			} 
			else 
			{
				return;
			}
			
			for (Slot s : container.inventorySlots) {
				if (gui instanceof GuiContainerCreative || s instanceof SlotCrafting) 
				{
					if (!(gui instanceof GuiContainerCreative)) 
					{
						addButton(event.getButtonList(), player, x, y);
					} 
					else 
					{
						if (s.getSlotIndex() != 15) continue;
						addButton(event.getButtonList(), player, x, y);
					}
				}
			}
		}
	}
	
	private void addButton(List<GuiButton> buttonList, EntityPlayer player, int x, int y) {
		
		int i = 0;
		boolean flag = (Backpack.getBackpacks(player, false).size() > 0 && BackpackConfig.buttonVisibility == 0) || BackpackConfig.buttonVisibility == 1;
		boolean flag2 = (Backpack.getBackpacks(player, false).size() > 0 && BackpackConfig.buttonVisibility2 == 0) || BackpackConfig.buttonVisibility2 == 1;
		
		if (flag) {
			buttonList.add(new GuiBackpackButton(BACKPACK_SLOT_BUTTON, x, y, 0));
			i = 16;
		}
		
		if (flag2) {
			buttonList.add(new GuiBackpackButton(OPEN_BACKPACK_BUTTON, x + i, y, 1));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void buttonClicked(GuiScreenEvent.ActionPerformedEvent.Pre event) {
		
		if (event.getButton() instanceof GuiBackpackButton) 
		{
			if (event.getButton().id == BACKPACK_SLOT_BUTTON) {
				ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(4));
				event.setCanceled(true);
			}
			
			if (event.getButton().id == OPEN_BACKPACK_BUTTON) {
				ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(EnumTab.BACKPACK.getName(), 0));
				event.setCanceled(true);
			}
		}
	}

}
