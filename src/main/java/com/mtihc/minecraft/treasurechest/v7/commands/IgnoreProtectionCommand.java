package com.mtihc.minecraft.treasurechest.v7.commands;

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

public class IgnoreProtectionCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public IgnoreProtectionCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "", "Ignore access protection by other plugins", aliases);
		this.plugin = plugin;
		setPermission(Permission.IGNORE_PROTECTION.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission for that command.");
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
		TreasureChest tchest = plugin.getChests().values().getChest(id);
		
		if(tchest == null) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		
		boolean ignoreProtection = !tchest.ignoreProtection();
		tchest.ignoreProtection(ignoreProtection);
		if(ignoreProtection) {
			sender.sendMessage(ChatColor.GOLD + "This chest is now accessible, even if another plugin is protecting it!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This chest is no longer accessible, if another plugin is protecting it.");
		}
		plugin.getChests().save();
		
		return true;
	}
	
	

}
