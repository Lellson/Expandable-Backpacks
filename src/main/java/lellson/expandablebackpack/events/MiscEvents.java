package lellson.expandablebackpack.events;

import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.proxy.ServerProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class MiscEvents {
	
	public static void init() {
		MinecraftForge.EVENT_BUS.register(new MiscEvents());
	}
	
	@SubscribeEvent
	public void enterWorld(PlayerLoggedInEvent event) {
		
		initBackpackSlot(event.player);
	}
	
	@SubscribeEvent
	public void changedDimension(PlayerChangedDimensionEvent event) {
		
		initBackpackSlot(event.player);
	}
	
	@SubscribeEvent
	public void respawn(PlayerRespawnEvent event) {
		
		initBackpackSlot(event.player);
	}
	
	private void initBackpackSlot(EntityPlayer player) {
		
		if (!player.worldObj.isRemote) 
		{
			player.openGui(ExpandableBackpack.instance, ServerProxy.BACKPACK_SLOT, player.worldObj, 0, 0, 0); // TODO: Better way...
			player.closeScreen();
		}
	}
}
