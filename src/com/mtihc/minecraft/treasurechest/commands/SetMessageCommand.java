package com.mtihc.minecraft.treasurechest.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.core1.ArgumentIterator;
import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.core1.exceptions.ArgumentFormatException;
import com.mtihc.minecraft.core1.exceptions.ArgumentIndexException;
import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.ChestsYaml;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class SetMessageCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public SetMessageCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Change one of the messages of the chest you are looking at", "<id> <message>", aliases);
		ArrayList<String> longDescription = new ArrayList<String>();
		longDescription.add(description);
		longDescription.add("1" + ChatColor.GRAY + " found for the first time");
		longDescription.add("2" + ChatColor.GRAY + " already found");
		longDescription.add("3" + ChatColor.GRAY + " is unlimited");
		setLongDescription(longDescription);
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
			sender.sendMessage(ChatColor.RED + "You don't have permission for that command.");
			return false;
		}
		
		Player player = (Player) sender;
		Chest chest = plugin.getTargetedChestBlock(player);
		ChestsYaml chests = plugin.getChests();
		
		if(chest == null || !chests.hasChest(chest.getBlock())) {
			sender.sendMessage(ChatColor.RED + "You're not looking at a treasure chest");
			return false;
		}
		
		if(args == null || args.length != 1) {
			sender.sendMessage(ChatColor.RED + "Expected messs");
		}
		
		ArgumentIterator arguments = new ArgumentIterator(args);
		
		int msgId;
		try {
			msgId = arguments.nextInt();
		} catch (ArgumentIndexException e) {
			msgId = 1;
		} catch (ArgumentFormatException e) {
			msgId = 1;
		}
		
		String message;
		try {
			message = arguments.nextMessage();
		} catch (ArgumentIndexException e) {
			message = null;
		}
		
		String chestName = chests.getChestNameFormatter().getChestName(chest.getBlock());
		if(msgId == 1) {
			chests.setMessage(TreasureChest.Message.ChestFound, chestName, message);
		}
		else if(msgId == 2) {
			chests.setMessage(TreasureChest.Message.ChestAlreadyFound, chestName, message);
		}
		else if(msgId == 3) {
			chests.setMessage(TreasureChest.Message.ChestIsUnlimited, chestName, message);
		}
		else {
			sender.sendMessage(ChatColor.RED + "Incorrect message id: '" + msgId + "'.");
			List<String> desc = getLongDescription();
			for (String line : desc) {
				sender.sendMessage(line);
			}
			sender.sendMessage(getUsage());
			return false;
		}
		
		if(message == null) {
			sender.sendMessage(ChatColor.GOLD + "Treasure chest message cleared.");
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "Treasure chest message changed.");
		}
		
		
		
		
		return true;
	}

	/* (non-Javadoc)
	 * @see org.bukkit.command.Command#getPermission()
	 */
	@Override
	public String getPermission() {
		return Permission.SET.getNode();
	}


}
