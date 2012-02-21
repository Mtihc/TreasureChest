package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class DeleteCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public DeleteCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Delete the chest you're looking at", "", aliases);
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args))
		{
			return true;
		}
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return false;
		}

		if(!sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to delete treasure chests.");
			return false;
		}
		
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player player = (Player) sender;
		Chest chest = plugin.getTargetedChestBlock(player);
		
		if(chest != null) {
			if(!plugin.getChests().hasChest(chest.getBlock())) {
				sender.sendMessage(ChatColor.YELLOW + "Treasure chest doesn't exist, or is already deleted.");
				return false;
			}
			else {
				plugin.getChests().removeChest(chest.getBlock().getLocation());
				plugin.getMemory().forgetChest(plugin.getChestName(chest.getBlock()));
				sender.sendMessage(ChatColor.YELLOW + "Treasure chest deleted.");
				return true;
			}
		}
		else {
			sender.sendMessage(ChatColor.RED + "You are not looking at a chest.");
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.DEL.getNode();
	}


}
