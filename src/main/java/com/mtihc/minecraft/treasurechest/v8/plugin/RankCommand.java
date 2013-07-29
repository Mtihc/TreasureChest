package com.mtihc.minecraft.treasurechest.v8.plugin;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.Command;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.ICommand;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.SimpleCommand;

public class RankCommand extends SimpleCommand {

	private TreasureManager manager;

	public RankCommand(TreasureManager manager, ICommand parent) {
		super(parent, new String[]{"rank"}, "", "Add/remove ranks", new String[]{"Add ranks to the plugin in the config.yml file.", "Add some of those ranks to treasures using this command.", "Then, only players with permissions \"treasurechest.rank.RANK_NAME\" can access it."});
		this.manager = manager;
		
		addNested("list");
		addNested("add");
		addNested("remove");
	}

	
	@Command(aliases = { "list" }, args = "", desc = "List ranks", help = { "" })
	public void list(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to set a treasure's rank.");
		}
		

		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		final Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		final ITreasureChest tchest = manager.load(loc);
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure chest");
		}
		
		
		
		List<String> ranks = manager.getConfig().getRanks();
		List<String> tranks = tchest.getRanks();
		String addedRanks = "";
		String availableRanks = "";
		for (String rank : ranks) {
			if(!tranks.contains(rank.toLowerCase())) {
				availableRanks += ", " + rank;
			}
			else {
				addedRanks += ", " + rank;
			}
		}
		if(addedRanks.isEmpty()) {
			addedRanks = "(none)";
		}
		else {
			addedRanks = addedRanks.substring(2);//remove first comma and space
		}
		if(availableRanks.isEmpty()) {
			availableRanks = "(none)";
		}
		else {
			availableRanks = availableRanks.substring(2);//remove first comma and space
		}
		
		player.sendMessage(ChatColor.GREEN + "Already added ranks: " + ChatColor.WHITE + addedRanks);
		player.sendMessage(ChatColor.GREEN + "Not yet added ranks: " + ChatColor.WHITE + availableRanks);
	}
	
	
	
	@Command(aliases = { "add" }, args = "<rank>", desc = "Add a rank to this treasure", help = { "" })
	public void add(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to set a treasure's rank.");
		}
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		final Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		final ITreasureChest tchest = manager.load(loc);
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		
		String rank;
		try {
			rank = args[0].trim().toLowerCase();
			
			if(args.length > 1) {
				throw new CommandException("Expected only a rank name.");
			}
			
		} catch(Exception e) {
			throw new CommandException("Expected a rank name.");
		}
		
		if(tchest.getRanks().contains(rank)) {
			throw new CommandException("Rank \"" + rank + "\" was already added.");
		}
		
		if(!manager.getConfig().getRanks().contains(rank)) {
			throw new CommandException("Rank \"" + rank + "\" doesn't exist.");
		}
		
		boolean first = tchest.getRanks().isEmpty();
		tchest.getRanks().add(rank);
		manager.save(loc, tchest);
		
		if(first) {
			sender.sendMessage(ChatColor.GREEN + "Only rank " + ChatColor.WHITE + rank + ChatColor.GREEN + " can access this treasure.");
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "Rank " + ChatColor.WHITE + rank + ChatColor.GREEN + " can also access this treasure.");
		}
		
		
	}
	
	
	
	
	
	
	@Command(aliases = { "remove", "rem" }, args = "<rank>", desc = "Remove a rank from this treasure", help = { "" })
	public void remove(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to set a treasure's rank.");
		}
		

		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		final Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		final ITreasureChest tchest = manager.load(loc);
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		

		
		String rank;
		try {
			rank = args[0].trim().toLowerCase();
			
			if(args.length > 1) {
				throw new CommandException("Expected only a rank name.");
			}
			
		} catch(Exception e) {
			throw new CommandException("Expected a rank name.");
		}
		
		
		if(!tchest.getRanks().remove(rank)) {
			throw new CommandException("Rank \"" + rank + "\" isn't added to this treasure.");
		}
		
		manager.save(loc, tchest);
		
		if(tchest.getRanks().isEmpty()) {
			sender.sendMessage(ChatColor.YELLOW + "You no longer need a rank to access this treasure.");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "Rank " + ChatColor.WHITE + rank + ChatColor.YELLOW + " can no longer access this treasure.");
		}
		
	}
}
