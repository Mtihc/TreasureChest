package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;
import com.mtihc.minecraft.treasurechest.v7.persistance.TChestCollection;

public class DeleteCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public DeleteCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "", "Delete the chest you're looking at", aliases);
		this.plugin = plugin;
		setPermission(Permission.DEL.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to delete treasure chests.");
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
		
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
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
			sender.sendMessage(ChatColor.YELLOW + "Treasure chest doesn't exist, or is already deleted.");
			return false;
		}
		else {
			plugin.getChests().values().removeChest(id);
			plugin.getMemory().forgetChest(TChestCollection.getChestId(block.getLocation()));
			plugin.getChests().save();
			sender.sendMessage(ChatColor.YELLOW + "Treasure chest deleted.");
			return true;
		}
	}


}
