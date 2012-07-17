package com.mtihc.minecraft.treasurechest.v7.commands;

import java.util.Collection;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.v7.core.BukkitCommand;
import com.mtihc.minecraft.treasurechest.v7.persistance.TreasureChest;

public class ListCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public ListCommand(TreasureChestPlugin plugin, BukkitCommand parent,
			String name, List<String> aliases) {
		super(parent, name, "[page]",
				"See the list of found treasure chest locations", aliases);
		this.plugin = plugin;
		setPermission(Permission.LIST.getNode());
		setPermissionMessage(ChatColor.RED
				+ "You don't have permission for the list command.");
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage("This command must be executed by a player, in game.");
			return false;
		}

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

		Collection<String> found = plugin.getMemory().getAllPlayerFound(
				(OfflinePlayer) sender);

		int total = found.size();
		int totalPerPage = 10;
		int pageTotal = total / totalPerPage + 1;

		if (page < 1 || page > pageTotal) {
			sender.sendMessage(ChatColor.RED + "Page " + page
					+ " does not exist.");
			return false;
		}

		sender.sendMessage(ChatColor.GOLD
				+ "List of all found treasures (page " + page + "/" + pageTotal
				+ "):");

		if (found == null || found.isEmpty()) {
			sender.sendMessage(ChatColor.RED
					+ "You have not found any treasures yet.");
		} else {

			String[] idArray = found.toArray(new String[total]);
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
