package lellson.expandablebackpack.network;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import io.netty.buffer.ByteBuf;
import lellson.expandablebackpack.ExpandableBackpack;
import lellson.expandablebackpack.inventory.iinventory.BackpackInventory;
import lellson.expandablebackpack.inventory.misc.TabHelper;
import lellson.expandablebackpack.inventory.misc.TabHelper.EnumTab;
import lellson.expandablebackpack.item.backpack.Backpack;
import lellson.expandablebackpack.item.compartment.EnumCompartment;
import lellson.expandablebackpack.proxy.ServerProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerNetworkHandler implements IMessage, IMessageHandler<ServerNetworkHandler, ServerNetworkHandler> {
	
	public int type;
	public String text;
	
	public ServerNetworkHandler() {}
	
	public ServerNetworkHandler(int type) {
		this.type = type;
		this.text = "";
	}
	
	public ServerNetworkHandler(String text, int type) {
		this.type = type;
		this.text = text;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		
		this.type = buf.readInt();
		this.text = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		
		buf.writeInt(this.type);
		ByteBufUtils.writeUTF8String(buf, this.text);
	}
	
	@Override
	public ServerNetworkHandler onMessage(final ServerNetworkHandler message, final MessageContext ctx) {
		
		IThreadListener main = (WorldServer) ctx.getServerHandler().playerEntity.worldObj;
		
		main.addScheduledTask(new Runnable() {
			
			@Override
			public void run() {
				
				EntityPlayer player = ctx.getServerHandler().playerEntity;
				World world = player.worldObj;
				
				switch(message.type) {
					case 0: openBackpack(getBackpack(player), player, message.text); break;
					case 1: swapHotbar(getBackpack(player), player); break;
					case 2: changePrivacy(player, getBackpack(player)); break;
					case 3: renameBackpack(getBackpack(player), message.text); break;
					case 4: openBackpackSlot(player); break;
					case 5: openPlayerInventory(player); break;
				}
			}
		});
		
		return null;
	}
	
	private ItemStack getBackpack(EntityPlayer player) {
		
		if (Backpack.getBackpacks(player, true).isEmpty()) 
		{
			player.addChatMessage(new TextComponentString(ChatFormatting.RED + "You don't have any backpacks!"));
			return null;
		}
		
		return Backpack.getBackpacks(player, true).get(0);
	}
	
	private void openPlayerInventory(EntityPlayer player) {

		player.openGui(ExpandableBackpack.instance, ServerProxy.PLAYER_INV, player.worldObj, 0, 0, 0);
	}
	
	private void openBackpackSlot(EntityPlayer player) {
		
		player.openGui(ExpandableBackpack.instance, ServerProxy.BACKPACK_SLOT, player.worldObj, 0, 0, 0);
	}
	
	private void renameBackpack(ItemStack backpack, String text) {
		
		if (backpack != null && !backpack.getDisplayName().equals(text)) {
			backpack.setStackDisplayName(TextFormatting.WHITE + text);
		}
	}

	private void changePrivacy(EntityPlayer player, ItemStack backpack) {
		
		if (backpack == null) return;

		if (backpack.getTagCompound().getBoolean(Backpack.TAGPUBLIC)) {
			backpack.getTagCompound().setBoolean(Backpack.TAGPUBLIC, false);
		} else {
			backpack.getTagCompound().setBoolean(Backpack.TAGPUBLIC, true);
		}
	}

	private void swapHotbar(ItemStack backpack, EntityPlayer player) {
		
		if (backpack == null || Backpack.getUpgrade(backpack, EnumCompartment.SWAP.getId()) < 1) return;
		
		int times = Backpack.isOwner(player, backpack, true) ? BackpackInventory.getSlots(backpack) > 9 ? 9 : BackpackInventory.getSlots(backpack) : 0;
		
		for (int i = 0; i < times; i++) {
			ItemStack stackPlayer = player.inventory.getStackInSlot(i);
			ItemStack stackBackpack = BackpackInventory.getStackForSlot(backpack, i);
			
			if ((stackPlayer == null || !(stackPlayer.getItem() instanceof Backpack)) && !BackpackInventory.isEmpty(backpack)) {
				player.inventory.setInventorySlotContents(i, stackBackpack);
				new BackpackInventory(backpack, player).setInventorySlotContents(i, stackPlayer);
			}
		}
	}

	public static void openBackpack(ItemStack stack, EntityPlayer player, String text) {
		
		if (stack != null && stack.getItem() instanceof Backpack && !player.worldObj.isRemote) {
			if (Backpack.isOwner(player, stack, true)) 
			{
				if (player instanceof EntityPlayerMP)
					ExpandableBackpack.networkClient.sendTo(new ClientNetworkHandler(0), (EntityPlayerMP) player);
				
				List<EnumTab> list = TabHelper.getTabs(stack);
				
				for (int i = 0; i < list.size(); i++) {
					EnumTab tab = list.get(i);
					
					if (text.equals(tab.getName())) {
						player.openGui(ExpandableBackpack.instance, tab.getGuiId(), player.worldObj, 0, 0, 0);
					}
				}
			} 
			else 
			{
				debuffPlayer(player, stack);
				player.addChatMessage(new TextComponentString(ChatFormatting.RED + "You are not allowed to use this backpack!"));
			}
		}
	}
	
	public static void debuffPlayer(EntityPlayer player, ItemStack stack) {
		
		int guarded = Backpack.getUpgrade(stack, EnumCompartment.GUARDED.getId());
		
		if (guarded > 0) 
		{
			for (EntityPlayer owner : Backpack.getOnlineOwners(stack, player.worldObj)) {
				owner.addChatMessage(new TextComponentString(ChatFormatting.RED + player.getName() + " tried to open your guarded backpack!"));
			}
			
			player.attackEntityFrom(DamageSource.magic, guarded * 3);
			player.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 400, 2 + guarded));
			player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0));
		}
		
	}

}
