package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;
import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;

public class SetMessageCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public SetMessageCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "<id> <message>", "Change one of the messages of the chest you are looking at", aliases);
		ArrayList<String> longDescription = new ArrayList<String>();
		longDescription.add(description);
		longDescription.add("1" + ChatColor.GRAY + " found for the first time");
		longDescription.add("2" + ChatColor.GRAY + " already found");
		longDescription.add("3" + ChatColor.GRAY + " is unlimited");
		setLongDescription(longDescription);
		this.plugin = plugin;
		setPermission(Permission.SET.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to change a chest's messages.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return false;
		}

		if(!testPermission(sender)) {
			return false;
		}
		
		Player player = (Player) sender;
		Block block = plugin.getTargetedContainerBlock(player);
		if(block == null) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a container block.");
			return false;
		}
		String id = TChestCollection.getChestId(block.getLocation());
		if(!plugin.getChests().values().hasChest(id)) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		int msgId;
		int argIndex = 0;
		try {
			msgId = Integer.parseInt(args[argIndex]);
			argIndex++;
			
		} catch (Exception e) {
			msgId = 1;
		}
		
		String message;
		try {
			message = "";
			for (int i = argIndex; i < args.length; i++) {
				message += " " + args[i];
			}
			if(!message.isEmpty()) {
				message = message.substring(1);
			}
			else {
				message = null;
			}
			
		} catch (Exception e) {
			message = null;
		}
		
		
		TreasureChest tchest = plugin.getChests().values().getChest(id);
		if(msgId == 1) {
			tchest.setMessage(TreasureChest.Message.FOUND, message);
		}
		else if(msgId == 2) {
			tchest.setMessage(TreasureChest.Message.FOUND_ALREADY, message);
		}
		else if(msgId == 3) {
			tchest.setMessage(TreasureChest.Message.FOUND_UNLIMITED, message);
		}
		else {
			sender.sendMessage(ChatColor.RED + "Incorrect message id: '" + msgId + "'.");
			List<String> desc = getLongDescription();
			for (String line : desc) {
				sender.sendMessage(line);
			}
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(message == null) {
			sender.sendMessage(ChatColor.GOLD + "Treasure chest message cleared.");
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "Treasure chest message changed.");
		}
		plugin.getChests().save();
		
		return true;
	}


}
