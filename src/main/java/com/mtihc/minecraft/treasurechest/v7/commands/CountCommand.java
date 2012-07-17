package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;

public class CountCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public CountCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "[player]", "Count how many chest you, or someone else, found", aliases);
		this.plugin = plugin;
		setPermission(Permission.COUNT.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission for the count command.");
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected only the optional player name.");
			sender.sendMessage(getUsage());
			return false;
		}

		if(!testPermission(sender)) {
			return false;
		}
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.COUNT_OTHERS.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to see how many chests other players have found.");
			return false;
		}
		
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
		if(player == null || !player.hasPlayedBefore()) {
			sender.sendMessage(ChatColor.RED + "Player \"" + playerName + "\" does not exist.");
			return false;
		}
		
		Collection<String> found = plugin.getMemory().getAllPlayerFound(player);
		int count;
		if(found == null) {
			count = 0;
		}
		else {
			count = found.size();
		}
		int total = plugin.getChests().values().getChestTotal();
		String message = count + " out of " + total + " treasure chests";
		if(other) {
			sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.WHITE + playerName + ChatColor.GOLD + " has found " + message);
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "You have found " + message);
		}
		
		
		
		
		return true;
	}

	

}
