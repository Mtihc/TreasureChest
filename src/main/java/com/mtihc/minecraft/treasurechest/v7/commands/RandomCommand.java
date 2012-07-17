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

public class RandomCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public RandomCommand(TreasureChestPlugin plugin, BukkitCommand parent, String name, List<String> aliases) {
		super(parent, name, "[random stack amount]", "Make a treasure chest have somewhat different items every time", aliases);
		this.plugin = plugin;
		setPermission(Permission.RANDOM.getNode());
		setPermissionMessage(ChatColor.RED + "You don't have permission to create random chests.");
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
		
		int randomness;
		try {
			randomness = Integer.parseInt(args[0]);
			if(randomness < 1) {
				sendIllegalArgumentMessage(sender);
				return false;
			}
		} catch(NullPointerException e) {
			randomness = 0;
		} catch(IndexOutOfBoundsException e) {
			randomness = 0;
		} catch(Exception e) {
			sendIllegalArgumentMessage(sender);
			return false;
		}
		
		
		int total = plugin.getChests().values().getChestItemTotal(id);
		
		if(randomness >= total) {
			sender.sendMessage(ChatColor.RED + "Unable to make a random chest.");
			if(total <= 1) {
				sender.sendMessage(ChatColor.RED + "This treasure chest contains " + total + " items.");
			}
			else {
				sender.sendMessage(ChatColor.RED + "Expected a number from 1 to " + (total - 1) + ", including.");
			}
			sender.sendMessage(getUsage());
			return false;
		}
		
		TreasureChest chest = plugin.getChests().values().getChest(id);
		
		chest.setAmountOfRandomlyChosenStacks(randomness);
		
		if(randomness > 0) {
			sender.sendMessage(ChatColor.GOLD + "This chest is random!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This chest is no longer random.");
		}
		plugin.getChests().save();
		return true;
	}

	private void sendIllegalArgumentMessage(CommandSender sender) {
		sender.sendMessage(ChatColor.RED + "Expected a number that represents how many item stacks should be chosen randomly.");
		sender.sendMessage(ChatColor.RED + "Or expected no arguments, to indicate the chest should not be random.");
		sender.sendMessage(getUsage());
	}
}
