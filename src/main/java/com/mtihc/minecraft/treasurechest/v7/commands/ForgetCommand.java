package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;

public class ForgetCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public ForgetCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "[player]", "As if you, or someone else, never found this chest", aliases);
		this.plugin = plugin;
		setPermission(Permission.FORGET.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission for the forget command.");
	}
	
	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected only the optional player name.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return false;
		}
		
		if(!testPermission(sender)) {
			return false;
		}
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.FORGET_OTHERS.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to make a chest forget another player.");
			return false;
		}
		
		OfflinePlayer p = plugin.getServer().getOfflinePlayer(playerName);
		if(p == null || !p.hasPlayedBefore()) {
			sender.sendMessage(ChatColor.RED + "Player \"" + playerName + "\" does not exist.");
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
		
		// forget (large) chest
		plugin.getMemory().forgetPlayerFound(p, id);
		
		
		sender.sendMessage(ChatColor.GOLD + "Treasure chest forgot that " + ChatColor.WHITE + "'" + playerName + "'" + ChatColor.GOLD + " found it :)");
		
		
		return true;
	}

}
