package com.mtihc.minecraft.treasurechest.commands;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class ForgetCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public ForgetCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "As if you, or someone else, never found this chest", "[player]", aliases);
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
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return false;
		}
		
		if(!sender.hasPermission(getPermission())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to make a chest forget that you have found it.");
			return false;
		}
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.FORGET_OTHERS.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to make a chest forget another player.");
			return false;
		}
		
		Player player = (Player) sender;
		Chest chest = plugin.getTargetedChestBlock(player);
		
		if(chest == null || !plugin.getChests().hasChest(chest.getBlock())) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		// forget (large) chest
		String chestName = plugin.getChestName(chest.getBlock());
		TreasureChest tchest = plugin.getChests().getChest(chestName);
		if(tchest.isLinkedChest()) {
			plugin.getMemory().forgetPlayerFound(playerName, tchest.getLinkedChest());
		}
		plugin.getMemory().forgetPlayerFound(playerName.toLowerCase(), chestName.toLowerCase());
		
		
		sender.sendMessage(ChatColor.GOLD + "Treasure chest forgot that " + ChatColor.WHITE + "'" + playerName + "'" + ChatColor.GOLD + " found it :)");
		
		
		
		
		
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.FORGET.getNode();
	}


}
