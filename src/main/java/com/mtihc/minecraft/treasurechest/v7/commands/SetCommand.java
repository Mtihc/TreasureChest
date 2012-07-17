package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.persistance.ChestsYaml;
import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;
import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;

public class SetCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public SetCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "", "Save the chest you are looking at", aliases);
		this.plugin = plugin;
		setPermission(Permission.SET.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to create treasure chests.");
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
		
		ChestsYaml chests = plugin.getChests();
		
		Block block = plugin.getTargetedContainerBlock(player);
		if(block == null) {
			//TODO check more specific dispenser/chest/furnace/enchantment
			sender.sendMessage(ChatColor.RED + "You're not looking at a container block.");
			return false;
		}
		
		InventoryHolder holder = (InventoryHolder) block.getState();
		String id = TChestCollection.getChestId(block.getLocation());
		TreasureChest tchest = chests.values().getChest(id);
		
		if(tchest != null) {
			
			
			tchest.setContents(holder.getInventory().getContents());
			holder.getInventory().clear();
			
			sender.sendMessage(ChatColor.GOLD + "Treasure chest contents updated.");
			
		}
		else {
			tchest = new TreasureChest(block);
			for (TreasureChest.Message messageId : TreasureChest.Message.values()) {
				tchest.setMessage(messageId, plugin.getDefaultMessage(messageId));
			}
			tchest.ignoreProtection(plugin.getDefaultIgnoreProtection());
			chests.values().setChest(tchest);
			holder.getInventory().clear();

			sender.sendMessage(ChatColor.GOLD + "Treasure chest saved");
		}
		chests.save();
		return true;
		
	}
	
	
}
