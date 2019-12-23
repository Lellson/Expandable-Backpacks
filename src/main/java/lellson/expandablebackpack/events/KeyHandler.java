package lellson.expandablebackpack.events;

import org.lwjgl.input.Keyboard;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class KeyHandler {
	
	public static KeyBinding keyOpen;
	public static KeyBinding keySwap;
	public static KeyBinding keyBackpackSlotOpen;

	public static void init() {
		
		keyBackpackSlotOpen = new KeyBinding("Open Backpack Slot Inventory", Keyboard.KEY_X, ExpandableBackpack.MODNAME);
		keyOpen = new KeyBinding("Open Backpack", Keyboard.KEY_C, ExpandableBackpack.MODNAME);
		keySwap = new KeyBinding("Swap Hotbar", Keyboard.KEY_V, ExpandableBackpack.MODNAME);
		
		ClientRegistry.registerKeyBinding(keyBackpackSlotOpen);
		ClientRegistry.registerKeyBinding(keyOpen);
		ClientRegistry.registerKeyBinding(keySwap);
		
		MinecraftForge.EVENT_BUS.register(new KeyHandler());
	}
	
	@SubscribeEvent
	public void keyInput(KeyInputEvent event) {
		
		if (keyOpen.isPressed()) {
			ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(EnumTab.BACKPACK.getName(), 0));
		}
		
		if (keySwap.isPressed()) {
			ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(1));
		}
		
		if (keyBackpackSlotOpen.isPressed()) {
			ExpandableBackpack.networkServer.sendToServer(new ServerNetworkHandler(4));
		}
	}

}
