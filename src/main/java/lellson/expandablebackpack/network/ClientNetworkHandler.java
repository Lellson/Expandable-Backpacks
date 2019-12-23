package lellson.expandablebackpack.network;

import io.netty.buffer.ByteBuf;
import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientNetworkHandler implements IMessage, IMessageHandler<ClientNetworkHandler, ClientNetworkHandler> {
	
	public int type;
	
	public ClientNetworkHandler() {}
	
	public ClientNetworkHandler(int type) {
		
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.type = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(type);
	}
	
	@Override
	public ClientNetworkHandler onMessage(final ClientNetworkHandler message, MessageContext ctx) {
		
		IThreadListener main = Minecraft.getMinecraft();
		
		main.addScheduledTask(new Runnable() {
			
			@Override
			public void run() {
				
				EntityPlayer player = Minecraft.getMinecraft().thePlayer;
				World world = player.worldObj;
				
				switch(message.type) {
					case 0: player.playSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.7F, 0.8F); break;
					case 1: player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 0.5F, 1.8F - (world.rand.nextFloat() * 0.5F)); break;
					case 2: player.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 0.6F, 1.5F); break;
				}
			}
		});

		return null;
	}
}
