package lellson.expandablebackpack.misc;

import java.util.ArrayList;
import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import lellson.expandablebackpack.item.backpack.Backpack;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class BackpackCommand implements ICommand {
	
	private final String[] commands = {"addowner", "removeowner", "whohasmybackpack", "help"};

	@Override
	public int compareTo(ICommand arg0) {

		return 0;
	}

	@Override
	public String getCommandName() {

		return "backpack";
	}

	@Override
	public String getCommandUsage(ICommandSender sender) {

		return "Command Usage: /backpack <Command>. Use /backpack help for a list of all backpack commands!";
	}

	@Override
	public List<String> getCommandAliases() {
		
		List<String> list = new ArrayList<String>();
		list.add("backpack");

		return list;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		Entity entity = sender.getCommandSenderEntity();
		
		if (entity != null && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			ItemStack held = player.getHeldItemMainhand();
			ItemStack chest = player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
			
			if (args.length == 0) {
				player.addChatComponentMessage(new TextComponentString(ChatFormatting.RED + "Error. " + getCommandUsage(sender)));
				return;
			}
			
			if (args[0].equals(commands[0])) {
				changeOwner(player, held, chest, args, sender, true);
			} else if (args[0].equals(commands[1])) {
				changeOwner(player, held, chest, args, sender, false);
			} else if (args[0].equals(commands[2])) {
				findThief(player, server);
			} else if (args[0].equals(commands[3])) {
				help(player);
			} else {
				player.addChatComponentMessage(new TextComponentString(ChatFormatting.RED + "Error. " + getCommandUsage(sender)));
			}
		}
	}

	private void help(EntityPlayer player) {
				
		player.addChatMessage(new TextComponentString(ChatFormatting.BOLD + "------------------COMMANDS------------------\n" + 
													ChatFormatting.GOLD + "/backpack" + ChatFormatting.GREEN + " addowner" + ChatFormatting.LIGHT_PURPLE + " <Player>\n" +
													ChatFormatting.AQUA + "Adds the given player to the owner list of your held or worn backpack.\n" +
													ChatFormatting.GOLD + "/backpack" + ChatFormatting.GREEN +  " removeowner" + ChatFormatting.LIGHT_PURPLE + " <Player>\n" +
													ChatFormatting.AQUA + "Removes the given player from the owner list of your held or worn backpack.\n" +
													ChatFormatting.GOLD + "/backpack" + ChatFormatting.GREEN +  " whohasmybackpack\n" +
													ChatFormatting.AQUA + "Lists every player who has one of your backpacks! This is a good way to find thieves.\n" +
													ChatFormatting.GOLD + "/backpack" + ChatFormatting.GREEN +  " help\n" +
													ChatFormatting.AQUA + "Lists every command in this mod.\n" +
													ChatFormatting.BOLD + "--------------------------------------------"));
	}

	private void findThief(EntityPlayer searcher, MinecraftServer server) {
		
		if (!BackpackConfig.whohasmybackpackAllowed && (!server.worldServers[0].getWorldInfo().areCommandsAllowed() || !searcher.capabilities.isCreativeMode)) {
			searcher.addChatMessage(new TextComponentString(ChatFormatting.RED + "You are not allowed to use this command!"));
			return;
		}
		
		List<EntityPlayerMP> players = server.getPlayerList().getPlayerList();
		boolean found = false;
		
		for (EntityPlayerMP player : players) {
			
			List<ItemStack> backpacks = Backpack.getBackpacks(player, false);
			
			for (ItemStack backpack : backpacks) {
				if (backpack != null && backpack.hasTagCompound() && player != searcher) {
					if (Backpack.isOwner(searcher, backpack, false)) {
						if (!backpack.getTagCompound().getBoolean(Backpack.TAGPUBLIC)) {
							searcher.addChatMessage(new TextComponentString(ChatFormatting.GOLD + player.getName() + ChatFormatting.WHITE + " has one of your backpacks" + (Backpack.isOwner(player, backpack, false) ? " but he is an Owner of it too!" : "!")));
						} else {
							searcher.addChatMessage(new TextComponentString(ChatFormatting.GOLD + player.getName() + ChatFormatting.WHITE + " has one of your backpacks but the backpack is public!"));
						}
						found = true;
					}
				}
			}
		}
		
		if (found == false) {
			searcher.addChatMessage(new TextComponentString(ChatFormatting.RED + "Nobody on the server has one of your backpacks!"));
		} else {
			searcher.addChatMessage(new TextComponentString(ChatFormatting.ITALIC + "No more backpacks found!"));
		}
	}
	
	private void changeOwner(EntityPlayer player, ItemStack held, ItemStack chest, String[] args, ICommandSender sender, boolean add) {
		
		if (args.length == 2) {
			if ((held != null && held.getItem() instanceof Backpack)) {
				
				if (add) addOwner(player, held, args[1]);
				else removeOwner(player, held, args[1]);
				
			} else if (chest != null && chest.getItem() instanceof Backpack) {
				
				if (add) addOwner(player, chest, args[1]);
				else removeOwner(player, chest, args[1]);
				
			} else {
				player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. You don't hold or wear a backpack!"));
			}
		} else {
			player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. Invalid amount of arguments! " + getCommandUsage(sender)));
		}
	}

	private void addOwner(EntityPlayer player, ItemStack stack, String name) {
		
		if (Backpack.isOwner(player, stack, false)) {
			if (!Backpack.getOwnerList(player, stack).contains(name)) {
				NBTTagCompound nbt = stack.getTagCompound();
				nbt.setString(Backpack.TAGOWNER, nbt.getString(Backpack.TAGOWNER) + ", " + name);
				player.addChatMessage(new TextComponentString(ChatFormatting.GREEN + name + " can now use this backpack!"));
			} else {
				player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. The player is already on the list!"));
			}
		} else {
			player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. You are not allowed to use this backpack!"));
		}
	}
	
	private void removeOwner(EntityPlayer player, ItemStack stack, String name) {
		
		if (!player.getName().equals(name)) {
			if (Backpack.isOwner(player, stack, false)) {
				
				List<String> owners = Backpack.getOwnerList(player, stack);
				
				if (owners.contains(name)) {
					NBTTagCompound nbt = stack.getTagCompound();
					owners.remove(name);
					Backpack.updateOwners(player, stack, owners);
					player.addChatMessage(new TextComponentString(ChatFormatting.GOLD + name + " can no longer use this backpack!"));
				} else {
					player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. The player isn't on the list!"));
				}
			} else {
				player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. You are not allowed to use this backpack!"));
			}
		} else {
			player.addChatMessage(new TextComponentString(ChatFormatting.RED + "Error. You can't remove yourself from the list!"));
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {

		return true;
	}

	@Override
	public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos) {
		
		List<String> list = new ArrayList<String>();
		
		if (args.length == 1) {
			
			for (String command : commands) {
				list.add(command);
			}
		}
		
		if (args.length == 2) {
			PlayerList plist = server.getPlayerList();

			for (String name : plist.getAllUsernames()) {
				list.add(name);
			}
		}

		return list;
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {

		return index == 1;
	}

}
