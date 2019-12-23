package lellson.expandablebackpack;

import lellson.expandablebackpack.events.CompartmentEvents;
import lellson.expandablebackpack.events.GUIEvents;
import lellson.expandablebackpack.events.MiscEvents;
import lellson.expandablebackpack.item.BackpackItems;
import lellson.expandablebackpack.misc.BackpackCommand;
import lellson.expandablebackpack.misc.BackpackConfig;
import lellson.expandablebackpack.network.ClientNetworkHandler;
import lellson.expandablebackpack.network.ServerNetworkHandler;
import lellson.expandablebackpack.proxy.ServerProxy;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ExpandableBackpack.MODID, name = ExpandableBackpack.MODNAME, version = "1.1", acceptedMinecraftVersions = "[1.9.4,1.10.2]")
public class ExpandableBackpack {
	
	public static final String MODID = "expandablebackpack";
	public static final String MODNAME = "Expandable Backpacks";
	
	public static Configuration config;
	
	@Instance(MODID)
	public static ExpandableBackpack instance;
	
	public static SimpleNetworkWrapper networkClient;
	public static SimpleNetworkWrapper networkServer;
	
	@SidedProxy(clientSide="lellson.expandablebackpack.proxy.ClientProxy",serverSide="lellson.expandablebackpack.proxy.ServerProxy")
	public static ServerProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		config = new Configuration(event.getSuggestedConfigurationFile());
		BackpackConfig.init();
		
		networkClient = new SimpleNetworkWrapper("backpackClient");
		networkClient.registerMessage(ClientNetworkHandler.class, ClientNetworkHandler.class, 0, Side.CLIENT);
		
		networkServer = new SimpleNetworkWrapper("backpackServer");
		networkServer.registerMessage(ServerNetworkHandler.class, ServerNetworkHandler.class, 1, Side.SERVER);
		
		MiscEvents.init();
		BackpackItems.init();
		CompartmentEvents.init();
		GUIEvents.init();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		
		proxy.register();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ServerProxy());
	}
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		
	    event.registerServerCommand(new BackpackCommand());
	}
}
