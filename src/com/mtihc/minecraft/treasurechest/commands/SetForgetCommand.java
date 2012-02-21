package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class SetForgetCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public SetForgetCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Define after how long this chest can be accessed again, per player", "<days> <hours> <min> <sec>", aliases);
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
		
		int days, hours, minutes, seconds;
		ArgumentIterator arguments = new ArgumentIterator(args);
		try {
			days = arguments.nextInt();
			hours = arguments.nextInt();
			minutes = arguments.nextInt();
			seconds = arguments.nextInt();
		} catch(Exception e) {
			sender.sendMessage(ChatColor.RED + "Expected days, hours, minutes, seconds.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(arguments.hasNext()) {
			sender.sendMessage(ChatColor.RED + "Too many arguments.");
			sender.sendMessage(getUsage());
			return false;
		}
		
		Player player = (Player) sender;
		Chest chest = plugin.getTargetedChestBlock(player);
		
		if(chest == null || !plugin.getChests().hasChest(chest.getBlock())) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		long secsIn = seconds + (minutes * 60) + (hours * 3600) + (days * 86400);
		int realDays = (int) (secsIn / 86400);
		int remainder = (int) (secsIn % 86400);
		int realHours = remainder / 3600;
		remainder = remainder % 3600;
		int realMinutes = remainder / 60;
		remainder = remainder % 60;
		int realSeconds = remainder;
		
		String chestName = plugin.getChests().getChestNameFormatter().getChestName(chest.getBlock());
		plugin.getChests().setForgetTime(chestName, secsIn * 1000);
		if(days + hours + minutes + seconds == 0) {
			sender.sendMessage(ChatColor.GOLD + "Cleared forget time");
		}else {
			sender.sendMessage(ChatColor.GOLD + "Changed forget time to " + ChatColor.WHITE + realDays + " days, " + realHours + " hours, " + realMinutes + " minutes, and " + realSeconds + " seconds");
		}
		
		
		
		return true;
	}


}
