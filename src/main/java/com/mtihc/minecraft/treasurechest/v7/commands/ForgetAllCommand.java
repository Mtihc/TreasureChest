package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;

public class ForgetAllCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public ForgetAllCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "", "As if nobody ever found the chest", aliases);
		this.plugin = plugin;
		setPermission(Permission.FORGET_ALL.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to make a chest forget that anybody found it.");
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
			sender.sendMessage(ChatColor.RED + "Expected no arguments");
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
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		plugin.getMemory().forgetChest(id);
		sender.sendMessage(ChatColor.GOLD + "Treasure chest is as good as new :)");
		
		
		
		
		
		return true;
	}
	

	
}
