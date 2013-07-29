package com.mtihc.minecraft.treasurechest.v8.plugin;

import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChestGroup;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureChestGroup;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.Command;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.ICommand;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.SimpleCommand;

public class GroupCommand extends SimpleCommand {

	private TreasureManager manager;

	public GroupCommand(TreasureManager manager, ICommand parent) {
		super(parent, new String[]{"group", "groups"}, "", "Create/delete/list groups, etc.", null);
		this.manager = manager;
		
		

		addNested("groupCreate");
		addNested("groupDelete");
		addNested("groupAdd");
		addNested("groupRemove");
		addNested("groupForget");
		addNested("groupForgetAll");
		addNested("groupCopy");
		addNested("groupRandom");
		addNested("groupList");
	}
	

	@Command(aliases = { "create" }, args = "<name>", desc = "Create a treasure group", help = { "" })
	public void groupCreate(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure groups.");
		}	
		
		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}
		
		String name = args[0];
		
		if (manager.groupExists(name)) {
			throw new CommandException("Group " + name + " already exists!");
		}
			
		Player player = (Player) sender;
		String world = player.getLocation().getWorld().getName();
		ITreasureChestGroup tcgroup = new TreasureChestGroup(world, name);

		sender.sendMessage(ChatColor.GOLD + "Created treasure group " + name + ".");
		
		manager.saveGroup(name, tcgroup);
	}

	@Command(aliases = { "delete" }, args = "<name>", desc = "Delete a treasure group", help = { "This only deletes the group data, individual treasures are not deleted." })
	public void groupDelete(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure groups.");
		}	
		
		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}
		
		String name = args[0];
		
		if(!manager.groupExists(name)) {
			throw new CommandException("Group doesn't exist, or is already deleted.");
		}
		else {
			if(!manager.groupDelete(name)) {
				throw new CommandException("Failed to delete group \"" + name + "\".");
			}
			sender.sendMessage(ChatColor.YELLOW + "Group deleted.");
			return;
		}
	}
	
	@Command(aliases = { "add" }, args = "<name>", desc = "Add a treasure", help = { "" })
	public void groupAdd(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to add a treasure to a group.");
		}

		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}

		String name = args[0];
		
		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist");
		}
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.load(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
	
		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name + ".");
		}
		
		if (!tcgroup.addChest(tchest)) {
			throw new CommandException(tcgroup.getError());
		}
			

		sender.sendMessage(ChatColor.GOLD + "Treasure added to group " + name + ".");
		
		manager.saveGroup(name, tcgroup);
	
	}

	@Command(aliases = { "remove" }, args = "<name>", desc = "Remove a treasure.", help = { "This does not delete the treasure." })
	public void groupRemove(CommandSender sender, String[] args) throws CommandException {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to remove a teasure from a group.");
		}

		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}
		
		String name = args[0];

		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist!");
		}
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.load(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
	
		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name);
		}
		
		if (!tcgroup.removeChest(tchest)) {
			throw new CommandException(tcgroup.getError());
		}

		sender.sendMessage(ChatColor.GOLD + "Treasure removed from group " + name + ".");
		
		manager.saveGroup(name, tcgroup);
	}
	
	@Command(aliases = { "forget" }, args = "<name> [player]", desc = "Tell all treasures in a group, to forget that a player found them.", help = { "This is like executing the forget command, on every treasure in the group." })
	public void groupForget(CommandSender sender, String[] args) throws CommandException {
	
		if(!sender.hasPermission(Permission.FORGET.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that somebody has found them.");
		}

		if(args == null || args.length < 1 || args.length > 2) {
			throw new CommandException("Expected a group name. And optionally a player name.");
		}
		
		String name = args[0];
		String playerName;
		OfflinePlayer p;
		try {
			playerName = args[1];
			p = manager.getPlugin().getServer().getOfflinePlayer(playerName);
		}
		catch(IndexOutOfBoundsException e) {
			// player name argument is optional... if sender is a player
			if(sender instanceof Player) {
				p = (Player) sender;
				playerName = p.getName();
			}
			else {
				throw new CommandException("Expected a group name and a player name.");
			}
		}

		
		if(p == null || !p.hasPlayedBefore()) {
			throw new CommandException("Player \"" + playerName + "\" does not exist.");
		}
		
		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist!");
		}

		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name);
		}

		Set<Location> locs = tcgroup.getLocations();
		Iterator<Location> i = locs.iterator();
	
		while(i.hasNext()) {
			manager.forgetPlayerFound(p, i.next());
		}
		sender.sendMessage(ChatColor.GOLD + "Treasure(s) in group " + name + " forgot " + playerName + ".");
	}
	
	@Command(aliases = { "forget-all" }, args = "<name>", desc = "Tell all treasures in a group, to forget that anybody found them.", help = { "This is like executing the forget-all command, on every treasure in the group." })
	public void groupForgetAll(CommandSender sender, String[] args) throws CommandException {
	
		if(!sender.hasPermission(Permission.FORGET.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that anybody has found it.");
		}

		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}

		String name = args[0];

		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist!");
		}

		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name);
		}
		
		Set<Location> locs = tcgroup.getLocations();
		Iterator<Location> i = locs.iterator();
	
		while(i.hasNext()) {
			manager.forgetChest(i.next());
		}
		sender.sendMessage(ChatColor.GOLD + "Treasure(s) in group " + name + " as good as new :).");
	}

	@Command(aliases = { "copy" }, args = "<name>", desc = "Copy the contents of the treasure you're looking at, to all treasures in the specified group", help = { "" })
	public void groupCopy(CommandSender sender, String[] args) throws CommandException {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}	
	
		if(!sender.hasPermission(Permission.FORGET.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that anybody has found it.");
		}

		if(args == null || args.length != 1) {
			throw new CommandException("Expected group name");
		}

		String name = args[0];

		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist!");
		}

		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name);
		}
	
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		if(!manager.has(loc)) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		ITreasureChest tchest = manager.load(loc);
		
		if (tchest == null) {
			throw new CommandException("Failed to load treasure.");
		}
		
		Set<Location> locs = tcgroup.getLocations();
		Iterator<Location> i = locs.iterator();

		// Check all chests are of the same type as the reference chest before we do anything
		while(i.hasNext()) {
			Location locTmp = i.next();
			ITreasureChest chestTmp = manager.load(locTmp);
			if (chestTmp == null) {
				throw new CommandException("Failed to load treasure (for check) at " + locTmp.toString());
			}
			if ((!tchest.getContainer().getType().equals(chestTmp.getContainer().getType())) || 
					(!(tchest.getContainer().getSize() == chestTmp.getContainer().getSize()))) {
				throw new CommandException("Chest @ " + 
											locTmp.toVector().getBlockX() + "," +
											locTmp.toVector().getBlockY() + "," +
											locTmp.toVector().getBlockZ() +
											" doesn't match reference chest");
			}
		}

		Iterator<Location> i2 = locs.iterator();

		// Now make the change
		while(i2.hasNext()) {
			Location locTmp = i2.next();
			manager.forgetChest(locTmp);
			ITreasureChest chestTmp = manager.load(locTmp);
			if (chestTmp == null) {
				throw new CommandException("Failed to load treasure (for change) at " + locTmp.toString());
			}
			chestTmp.getContainer().setContents(tchest.getContainer().getContents());
			manager.save(locTmp, chestTmp);
		}
		sender.sendMessage(ChatColor.GOLD + "Treasure(s) in group " + name + " have been copied.");
	}

	@Command(aliases = { "random" }, args = "<name> <amount>", desc = "Make all treasures in a group randomized.", help = { "This is like executing the random command, on every treasure in the group." })
	public void groupRandom(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
	
		if(!sender.hasPermission(Permission.RANDOM.getNode())) {
			throw new CommandException("You don't have permission to make a treasure randomized.");
		}
	
		if(args == null || args.length != 2) {
			throw new CommandException("Expected group name and amount");
		}
		
		String name = args[0];
		String amount = args[1];

		if (!manager.groupExists(name)) {
			throw new CommandException("Group " + name + " doesn't exist!");
		}

		ITreasureChestGroup tcgroup = manager.loadGroup(name);
		
		if (tcgroup == null) {
			throw new CommandException("Failed to load group " + name);
		}
		
		
		int randomness;
		try {
			randomness = Integer.parseInt(amount);
			if(randomness < 1) {
				TreasureChestCommand.sendRandomCommandIllegalArgumentMessage(sender);
				return;
			}
		} catch(NullPointerException e) {
			randomness = 0;
		} catch(IndexOutOfBoundsException e) {
			randomness = 0;
		} catch(Exception e) {
			TreasureChestCommand.sendRandomCommandIllegalArgumentMessage(sender);
			return;
		}
		
		Set<Location> locs = tcgroup.getLocations();
		Iterator<Location> i = locs.iterator();

		/* Loop through all chests setting them to random */
		while(i.hasNext()) {
			Location locTmp = i.next();
			ITreasureChest chestTmp = manager.load(locTmp);

			manager.forgetChest(locTmp);
			ItemStack[] contents = chestTmp.getContainer().getContents();
			int total = 0;
			for (ItemStack item : contents) {
				if(item == null || item.getTypeId() == 0) {
					continue;
				}
				total++;
			}

			if(randomness >= total) {
				sender.sendMessage(ChatColor.RED + "Unable to make a random treasure at " + locTmp.getBlockX() + "," + locTmp.getBlockY() + "," + locTmp.getBlockZ() + ".");
				if(total <= 1) {
					throw new CommandException("This treasure contains " + total + " items.");
				}
				else {
					throw new CommandException("Expected a number from 1 to " + (total - 1) + ", including.");
				}
			}


			chestTmp.setAmountOfRandomlyChosenStacks(randomness);

			manager.save(locTmp, chestTmp);
		}

		if(randomness > 0) {
			sender.sendMessage(ChatColor.GOLD + "All treasures in group " + name + " are now random!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "All treasures in group " + name + " are no longer random.");
		}
		return;
	}
	
	@Command(aliases = { "list" }, args = "", desc = "List all groups.", help = { "" })
	public void groupList(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to list treasure groups.");
		}
	
		if(args != null && args.length != 0) {
			throw new CommandException("Expected no parameters.");
		}

		sender.sendMessage(ChatColor.GREEN + "Teasure groups.");
		Set<String> groupList = manager.getGroups();
		Iterator<String> i = groupList.iterator();
		while(i.hasNext()) {
			String group = i.next();
			sender.sendMessage(ChatColor.YELLOW + group);
		}
	}
	
}
