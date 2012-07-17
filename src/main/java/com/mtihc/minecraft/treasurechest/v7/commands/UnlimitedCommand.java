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
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

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
		Block block = plugin.getTargetedContainerBlock(player);
		if(block == null) {
			//TODO check more specific dispenser/chest/furnace/enchantment
			sender.sendMessage(ChatColor.RED + "You're not looking at a container block.");
			return false;
		}
		String id = TChestCollection.getChestId(block.getLocation());
		if(!plugin.getChests().values().hasChest(id)) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		TreasureChest tchest = plugin.getChests().values().getChest(id);
		boolean isUnlimited = !tchest.isUnlimited();
		tchest.setUnlimited(isUnlimited);
		if(isUnlimited) {
			sender.sendMessage(ChatColor.GOLD + "This chest is unlimited!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This chest is no longer unlimited.");
		}
		plugin.getChests().save();
		
		return true;
	}
	
	

}
