package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class ListAllCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public ListAllCommand(TreasureChestPlugin plugin, BukkitCommand parent,
			String name, List<String> aliases) {
		super(parent, name, "[page]",
				"See the list of all treasure chest locations", aliases);
		this.plugin = plugin;
		setPermission(Permission.LIST_ALL.getNode());
		setPermissionMessage(ChatColor.RED
				+ "You don't have permission to list all treasures.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only the optional page number.");
			sender.sendMessage(getUsage());
			return false;
		}

		if (!testPermission(sender)) {
			return false;
		}

		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch (Exception e) {
			page = 1;
		}

		Collection<String> allChests = plugin.getChests().values().getChestIds();

		int total = allChests.size();
		int totalPerPage = 10;
		int pageTotal = total / totalPerPage + 1;

		if (page < 1 || page > pageTotal) {
			sender.sendMessage(ChatColor.RED + "Page " + page
					+ " does not exist.");
			return false;
		}

		sender.sendMessage(ChatColor.GOLD
				+ "List of all treasures on this server (page " + page + "/" + pageTotal
				+ "):");

		if (allChests == null || allChests.isEmpty()) {
			sender.sendMessage(ChatColor.RED
					+ "There are no treasures yet.");
		} else {

			String[] idArray = allChests.toArray(new String[total]);
			int startIndex = (page - 1) * totalPerPage;
			int endIndex = startIndex + totalPerPage;
			for (int i = startIndex; i < idArray.length && i < endIndex; i++) {
				TreasureChest chest = plugin.getChests().values()
						.getChest(idArray[i]);
				if (chest == null) {
					continue;
				}
				Location loc = chest.getLocation();
				// send coordinates
				sender.sendMessage("  " + ChatColor.GOLD + (i + 1) + ". "
						+ ChatColor.WHITE + loc.getWorld().getName() + ChatColor.GRAY + " x " + ChatColor.WHITE 
						+ loc.getBlockX() + ChatColor.GRAY + " y " + ChatColor.WHITE + loc.getBlockY() + ChatColor.GRAY + " z " + ChatColor.WHITE
						+ loc.getBlockZ());
			}

			if(pageTotal > 1) {
				int nextPage = (page == pageTotal ? 1 : page + 1);
				sender.sendMessage(ChatColor.GOLD + "To see the next page, type: "
						+ ChatColor.WHITE
						+ getUsage().replace("[page]", String.valueOf(nextPage)));
			}
			
		}

		return true;
	}

}
