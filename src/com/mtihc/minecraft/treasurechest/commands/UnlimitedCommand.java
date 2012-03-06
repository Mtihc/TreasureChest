package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core2.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class UnlimitedCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public UnlimitedCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "", "Make a treasure chest unlimited", aliases);
		this.plugin = plugin;
		setPermission(Permission.UNLIMITED.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to make unlimited treasure chests.");
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
		Chest chest = plugin.getTargetedChestBlock(player);
		
		if(chest == null || !plugin.getChests().hasChest(chest.getBlock())) {
			sender.sendMessage(ChatColor.RED + "That's not a treasure chest");
			return false;
		}
		String chestName = plugin.getChests().getChestNameFormatter().getChestName(chest.getBlock());
		boolean isUnlimited = plugin.getChests().switchUnlimited(chestName);
		if(isUnlimited) {
			sender.sendMessage(ChatColor.GOLD + "This chest is unlimited!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This chest is no longer unlimited.");
		}
		
		return true;
	}
	
	

}
