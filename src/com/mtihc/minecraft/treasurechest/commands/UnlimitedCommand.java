package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class UnlimitedCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public UnlimitedCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Make a treasure chest unlimited", "", aliases);
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
			sender.sendMessage(ChatColor.RED + "You don't have permission to create unlimited chests.");
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

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.UNLIMITED.getNode();
	}

	

}
