package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.ChestsYaml;

public class SetCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public SetCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Save the chest you are looking at", "", aliases);
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
			sender.sendMessage(ChatColor.RED + "You don't have permission to create treasure chests.");
			return false;
		}
		
		if(args != null && args.length > 0) {
			sender.sendMessage(ChatColor.RED + "Expected no arguments");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player player = (Player) sender;
		
		ChestsYaml chests = plugin.getChests();
		
		Chest chest = plugin.getTargetedChestBlock(player);
		
		if(chest != null) {
			if(chests.hasChest(chest.getBlock())) {
				chests.setItemStacks(chest);
				chest.getInventory().clear();
				Chest neighbor = chests.getNeighbourChestOf(chest.getBlock());
				if(neighbor != null) {
					neighbor.getInventory().clear();
				}
				sender.sendMessage(ChatColor.GOLD + "Treasure chest contents updated.");
				
				return true;
			}
			chests.setChest(chest);
			chest.getInventory().clear();
			sender.sendMessage(ChatColor.GOLD + "Treasure chest saved");
			return true;
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
		return Permission.SET.getNode();
	}

	
}
