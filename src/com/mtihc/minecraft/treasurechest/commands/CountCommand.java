package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;

public class CountCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;
	
	public CountCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Count how many chest you, or someone else, found", "[player]", aliases);
		this.plugin = plugin;
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if(super.execute(sender, label, args))
		{
			return true;
		}
		
		if(args.length > 1) {
			sender.sendMessage(ChatColor.RED + "Expected only the optional player name.");
			sender.sendMessage(getUsage());
			return false;
		}

		if(!sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.COUNT_OTHERS.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to see how many chests other players have found.");
			return false;
		}
		List<String> found = plugin.getMemory().getAllPlayerFound(playerName);
		int count;
		if(found == null) {
			count = 0;
		}
		else {
			count = found.size();
		}
		int total = plugin.getChests().getChestTotal();
		String message = count + " out of " + total + " treasure chests";
		if(other) {
			sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.WHITE + playerName + ChatColor.GOLD + " has found " + message);
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "You have found " + message);
		}
		
		
		
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.COUNT.getNode();
	}

	

}
