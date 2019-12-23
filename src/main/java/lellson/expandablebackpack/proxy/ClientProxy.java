package lellson.expandablebackpack.proxy;

import lellson.expandablebackpack.events.KeyHandler;
import lellson.expandablebackpack.events.RenderEvents;
import lellson.expandablebackpack.item.BackpackItems;

public class ClientProxy extends ServerProxy {
	
	@Override
	public void register() {
		BackpackItems.registerRenderers();
		KeyHandler.init();
		RenderEvents.init();
	}
}
