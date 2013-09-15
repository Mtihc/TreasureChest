package com.mtihc.minecraft.treasurechest.v8.plugin;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureException;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.Command;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.ICommand;
import com.mtihc.minecraft.treasurechest.v8.plugin.util.commands.SimpleCommand;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactoryManager;

public class TreasureChestCommand extends SimpleCommand {

	private TreasureManager manager;

	public TreasureChestCommand(TreasureManager manager, ICommand parent) {
		super(parent, new String[]{"tchest", "treasurechest"}, "", "This is the main command.", null);
		this.manager = manager;
		

		addNested("count");
		addNested("list");
		addNested("listAll");
		addNested("delete");
		addNested("set");
		addNested("setShared");
		addNested("random");
		addNested("unlimited");
		addNested("ignoreProtection");
		addNested("setmessage");
		addNested("setforget");
		addNested("forget");
		addNested("forgetAll");
		addNested("reload");
		addNested("peek");
		
		addNested(RewardCommand.class, manager, this);
		addNested(RankCommand.class, manager, this);
		addNested(GroupCommand.class, manager, this);
		addNested(RegionCommand.class, manager, this);
		
	}
	
	public TreasureChestCommand(TreasureManager manager, RewardFactoryManager rewardManager, ICommand parent) {
		this(manager, parent);
		
		if(rewardManager != null) {
			addNested(RewardCommand.class, manager, rewardManager, this);
		}
	}
	
	@Command(aliases = { "peek" }, args = "", desc = "Open the real inventory.", help = { "Look at a treasure, ", "then execute this command.", "Useful for testing and working with unlimited dispensers/droppers." })
	public void peek(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("This command must be executed by a player, in game.");
		}
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments.");
		}
		
		if(!sender.hasPermission(Permission.PEEK.getNode())) {
			throw new CommandException("You don't have permission to open a treasure's real inventory.");
		}
		
		Player player = (Player) sender;
		

		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		if(!manager.hasTreasure(loc)) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		InventoryHolder holder = (InventoryHolder) block.getState();
		player.openInventory(holder.getInventory());
	}
	
	@Command(aliases = { "count" }, args = "[player]", desc = "Count found treasures.", help = { "Counts how many treasures you, or someone else, found in this world.", "Specify a player name to count another player's found treasures." })
	public void count(CommandSender sender, String[] args) throws CommandException {
		
		
		
		if(args.length > 1) {
			throw new CommandException("Expected only the optional player name.");
		}

		if(!sender.hasPermission(Permission.COUNT.getNode())) {
			throw new CommandException("You don't have permission to count how many treasures you've found.");
		}
		
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.COUNT_OTHERS.getNode())) {
			throw new CommandException("You don't have permission to see how many treasures other players have found.");
		}
		
		JavaPlugin plugin = manager.getPlugin();
		
		OfflinePlayer player = plugin.getServer().getOfflinePlayer(playerName);
		if(player == null || !player.hasPlayedBefore()) {
			throw new CommandException("Player \"" + playerName + "\" does not exist.");
		}
		
		World world;
		if(player.getPlayer() != null) {
			world = player.getPlayer().getWorld();
		}
		else {
			if(!(sender instanceof Player)) {
				throw new CommandException("This command must be executed by a player, in game.");
			}
			world = ((Player) sender).getWorld();
		}
		
		
		
		Collection<Location> found = manager.getAllPlayerFound(player, world);
		int count;
		if(found == null) {
			count = 0;
		}
		else {
			count = found.size();
		}
		int total = manager.getTreasureLocations(world.getName()).size();
		String message = count + " out of " + total + " treasures";
		if(other) {
			sender.sendMessage(ChatColor.GOLD + "Player " + ChatColor.WHITE + playerName + ChatColor.GOLD + " has found " + message);
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "You have found " + message);
		}
		
		
	}
	
	@Command(aliases = { "list" }, args = "[page]", desc = "List treasures you've found.", help = { "" })
	public void list(CommandSender sender, String[] args) throws CommandException {
	
		if (!(sender instanceof Player)) {
			sender.sendMessage("This command must be executed by a player, in game.");
			return;
		}
	
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only the optional page number.");
			sender.sendMessage(getUsage());
			return;
		}
	
	
		if(!sender.hasPermission(Permission.LIST.getNode())) {
			throw new CommandException("You don't have permission to list all treasures you've found.");
		}
		
	
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch (Exception e) {
			page = 1;
		}
	
		Player player = (Player) sender;
		
		Collection<Location> found = manager.getAllPlayerFound(player, player.getWorld());
	
		int total = found.size();
		int totalPerPage = 10;
		int pageTotal = total / totalPerPage + 1;
	
		if (page < 1 || page > pageTotal) {
			throw new CommandException("Page " + page
					+ " does not exist.");
		}
	
		sender.sendMessage(ChatColor.GOLD
				+ "List of all found treasures (page " + page + "/" + pageTotal
				+ "):");
	
		if (found == null || found.isEmpty()) {
			sender.sendMessage(ChatColor.RED
					+ "You have not found any treasures yet.");
		} else {
	
			Location[] idArray = found.toArray(new Location[total]);
			int startIndex = (page - 1) * totalPerPage;
			int endIndex = startIndex + totalPerPage;
			for (int i = startIndex; i < idArray.length && i < endIndex; i++) {
				Location loc = idArray[i];
				ITreasureChest chest = manager.getTreasure(loc);
				if (chest == null) {
					continue;
				}
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
	}

	@Command(aliases = { "list-all" }, args = "[page]", desc = "List all treasures.", help = { "" })
	public void listAll(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			throw new CommandException("This command must be executed by a player, in game.");
		}
		
		if (args.length > 1) {
			sender.sendMessage(ChatColor.RED
					+ "Expected only the optional page number.");
			sender.sendMessage(getUsage());
			return;
		}
	
	
		if(!sender.hasPermission(Permission.LIST_ALL.getNode())) {
			throw new CommandException("You don't have permission to list all treasures.");
		}
		
	
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch (Exception e) {
			page = 1;
		}
		
		Player player = (Player) sender;
	
		Collection<Location> allChests = manager.getTreasureLocations(player.getWorld().getName());
	
		int total = allChests.size();
		int totalPerPage = 10;
		int pageTotal = total / totalPerPage + 1;
	
		if (page < 1 || page > pageTotal) {
			throw new CommandException("Page " + page
					+ " does not exist.");
		}
	
		
	
		if (allChests == null || allChests.isEmpty()) {
			sender.sendMessage(ChatColor.RED
					+ "There are no treasures yet.");
		} else {
			sender.sendMessage(ChatColor.GOLD
					+ "List of all treasures on this server (page " + page + "/" + pageTotal
					+ "):");
			Location[] idArray = allChests.toArray(new Location[total]);
			int startIndex = (page - 1) * totalPerPage;
			int endIndex = startIndex + totalPerPage;
			for (int i = startIndex; i < idArray.length && i < endIndex; i++) {
				Location loc = idArray[i];
				ITreasureChest chest = manager.getTreasure(loc);
				if (chest == null) {
					continue;
				}
				
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
		
	}

	@Command(aliases = { "delete", "del" }, args = "", desc = "Delete a treasure", help = { "Look at a treasure, then execute this command." })
	public void delete(CommandSender sender, String[] args) throws CommandException {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}

		if(!sender.hasPermission(Permission.DEL.getNode())) {
			throw new CommandException("You don't have permission to delete treasures");
		}
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments.");
		}
		
		Player player = (Player) sender;
		
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		try {
			manager.treasureDelete(player, block, true);
		} catch (TreasureException e) {
			throw new CommandException(e.getMessage(), e);
		}
	}
	
	@Command(aliases = { "set" }, args = "", desc = "Create/update a treasure.", help = { "Put items in a container block, ", "look at it, then execute this command." })
	public void set(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasures.");
		}
		
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments");
		}
		
		Player player = (Player) sender;
		
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		manager.treasureSet(player, block, true);
	}

	@Command(aliases = { "set-shared" }, args = "", desc = "Create/update a treasure with a single invertory.", help = { "Put items in a container block, ", "look at it, then execute this command." })
	public void setShared(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasures.");
		}
		
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments");
		}
		
		Player player = (Player) sender;
		
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		manager.treasureSetShared(player, block, true);
	}
	
	@Command(aliases = { "random", "setrandom", "r" }, args = "[amount]", desc = "Make a treasure randomized.", help = { "The argument is the amount of item-stacks that will be included in the treasure at random." })
	public void random(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
	
		if(!sender.hasPermission(Permission.RANDOM.getNode())) {
			throw new CommandException("You don't have permission to make a treasure randomized.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		int randomness;
		try {
			randomness = Integer.parseInt(args[0]);
			if(randomness < 1) {
				sendRandomCommandIllegalArgumentMessage(sender);
				return;
			}
		} catch(NullPointerException e) {
			randomness = 0;
		} catch(IndexOutOfBoundsException e) {
			randomness = 0;
		} catch(Exception e) {
			sendRandomCommandIllegalArgumentMessage(sender);
			return;
		}
		
		ItemStack[] contents = tchest.getContainer().getContents();
		int total = 0;
		for (ItemStack item : contents) {
			if(item == null || item.getTypeId() == 0) {
				continue;
			}
			total++;
		}
		
		if(randomness >= total) {
			sender.sendMessage(ChatColor.RED + "Unable to make a random treasure.");
			if(total <= 1) {
				throw new CommandException("This treasure contains " + total + " items.");
			}
			else {
				throw new CommandException("Expected a number from 1 to " + (total - 1) + ", including.");
			}
		}
		
		
		tchest.setAmountOfRandomlyChosenStacks(randomness);
		
		if(randomness > 0) {
			sender.sendMessage(ChatColor.GOLD + "This treasure is random!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This treasure is no longer random.");
		}
		manager.setTreasure(tchest);
		return;
	}

	@Command(aliases = { "unlimited", "setunlimited", "u" }, args = "", desc = "Make a treasure unlimited.", help = { "Will not use forget-time. ", "Will be refilled everytime it's opened." })
	public void unlimited(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
	
		if(!sender.hasPermission(Permission.UNLIMITED.getNode())) {
			throw new CommandException("You don't have permission to make treasure unlimited.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		boolean isUnlimited = !tchest.isUnlimited();
		tchest.setUnlimited(isUnlimited);
		if(isUnlimited) {
			sender.sendMessage(ChatColor.GOLD + "This treasure is unlimited!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This treasure is no longer unlimited.");
		}
		manager.setTreasure(tchest);
	}

	@Command(aliases = { "ip", "ignoreprotection" }, args = "", desc = "Make a treasure ignore protection.", help = { "When protection is ignored, players can open a treasures ", "even if it's protected by another plugin." })
	public void ignoreProtection(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
	
		if(!sender.hasPermission(Permission.IGNORE_PROTECTION.getNode())) {
			throw new CommandException("You don't have permission to make a treasure ignore protection by other plugins.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		
		boolean ignoreProtection = !tchest.ignoreProtection();
		tchest.ignoreProtection(ignoreProtection);
		if(ignoreProtection) {
			sender.sendMessage(ChatColor.GOLD + "This treasure is now accessible, even if another plugin is protecting it!");
		}
		else {
			sender.sendMessage(ChatColor.YELLOW + "This treasure is no longer accessible, if another plugin is protecting it.");
		}
		manager.setTreasure(tchest);
	}

	@Command(aliases = { "setmsg", "setmessage" }, args = "<number> <message>", desc = "Set messages", help = { "Specify a message number, and the message text.", "Valid message numbers are: ", "1: found", "2: already found", "3: unlimited" })
	public void setmessage(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to edit a treasure's messages.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		int msgId;
		int argIndex = 0;
		try {
			msgId = Integer.parseInt(args[argIndex]);
			argIndex++;
			
		} catch (Exception e) {
			msgId = 1;
		}
		
		String message;
		try {
			message = "";
			for (int i = argIndex; i < args.length; i++) {
				message += " " + args[i];
			}
			if(!message.isEmpty()) {
				message = message.substring(1);
			}
			else {
				message = null;
			}
			
		} catch (Exception e) {
			message = null;
		}
		
		if(msgId == 1) {
			tchest.setMessage(ITreasureChest.Message.FOUND, message);
		}
		else if(msgId == 2) {
			tchest.setMessage(ITreasureChest.Message.FOUND_ALREADY, message);
		}
		else if(msgId == 3) {
			tchest.setMessage(ITreasureChest.Message.UNLIMITED, message);
		}
		else {
			
			sender.sendMessage(ChatColor.RED + "Correct message numbers are:");
			sender.sendMessage("1" + ChatColor.GRAY + " found for the first time");
			sender.sendMessage("2" + ChatColor.GRAY + " already found");
			sender.sendMessage("3" + ChatColor.GRAY + " is unlimited");
			throw new CommandException("Incorrect message number: '" + msgId + "'.");
		}
		
		if(message == null) {
			sender.sendMessage(ChatColor.GOLD + "Treasure message cleared.");
		}
		else {
			sender.sendMessage(ChatColor.GOLD + "Treasure message changed.");
		}
		manager.setTreasure(tchest);
	}
	
	@Command(aliases = { "setforget", "setforgettime" }, args = "<days> <hours> <min> <sec>", desc = "Set forget-time", help = { "Defines after how long a treasure can be looted again, per player." })
	public void setforget(CommandSender sender, String[] args) throws CommandException {
	
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}
	
	
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to set a treasure's forget time.");
		}
		
		
		int days, hours, minutes, seconds;
		try {
			days = Integer.parseInt(args[0]);
			hours = Integer.parseInt(args[1]);
			minutes = Integer.parseInt(args[2]);
			seconds = Integer.parseInt(args[3]);
		} catch(Exception e) {
			throw new CommandException("Expected days, hours, minutes, seconds.");
		}
		
		if(args.length > 4) {
			throw new CommandException("Too many arguments.");
		}
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		long secsIn = seconds + (minutes * 60) + (hours * 3600) + (days * 86400);
		int realDays = (int) (secsIn / 86400);
		int remainder = (int) (secsIn % 86400);
		int realHours = remainder / 3600;
		remainder = remainder % 3600;
		int realMinutes = remainder / 60;
		remainder = remainder % 60;
		int realSeconds = remainder;
		
		tchest.setForgetTime(secsIn * 1000);
		if(days + hours + minutes + seconds == 0) {
			sender.sendMessage(ChatColor.GOLD + "Cleared forget time");
		}else {
			sender.sendMessage(ChatColor.GOLD + "Changed forget time to " + ChatColor.WHITE + realDays + " days, " + realHours + " hours, " + realMinutes + " minutes, and " + realSeconds + " seconds");
		}
		
		manager.setTreasure(tchest);
	}

	@Command(aliases = { "forget" }, args = "[player]", desc = "Make a treasure forget you/others.", help = { "It will be as if you, or the specified player, never found it." })
	public void forget(CommandSender sender, String[] args) throws CommandException {

		if(args.length > 1) {
			throw new CommandException("Expected only the optional player name.");
		}
		
		String playerName;
		try {
			playerName = args[0];
		} catch(Exception e) {
			playerName = sender.getName();
		}
		
		
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		

		if(!sender.hasPermission(Permission.FORGET.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that you've found it.");
		}
		
		
		boolean other = !sender.getName().equalsIgnoreCase(playerName);
		if(other && !sender.hasPermission(Permission.FORGET_OTHERS.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that a player has found it.");
		}
		
		OfflinePlayer p = manager.getPlugin().getServer().getOfflinePlayer(playerName);
		if(p == null || !p.hasPlayedBefore()) {
			throw new CommandException("Player \"" + playerName + "\" does not exist.");
		}
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		if(!manager.hasTreasure(loc)) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		// forget (large) chest
		manager.forgetPlayerFound(p, loc);
		
		
		sender.sendMessage(ChatColor.GOLD + "Treasure forgot that " + ChatColor.WHITE + "'" + playerName + "'" + ChatColor.GOLD + " found it :)");
		
	}
	
	@Command(aliases = { "forget-all", "forget-allplayers" }, args = "", desc = "Make a treasure forget all players.", help = { "It will be as if nobody ever found this treasure." })
	public void forgetAll(CommandSender sender, String[] args) throws CommandException {

		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}


		if(!sender.hasPermission(Permission.FORGET_ALL.getNode())) {
			throw new CommandException("You don't have permission to make a treasure forget that anybody has found it.");
		}
		
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments");
		}
		
		Player player = (Player) sender;

		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		if(!manager.hasTreasure(loc)) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		manager.forgetChest(loc);
		sender.sendMessage(ChatColor.GOLD + "Treasure is as good as new :)");
		
		
		
		
	}
	
	@Command(aliases = { "reload" }, args = "", desc = "Reload the config", help = { "" })
	public void reload(CommandSender sender, String[] args) throws CommandException {

		if(!sender.hasPermission(Permission.FORGET_ALL.getNode())) {
			throw new CommandException("You don't have permission to reload the config.");
		}
		
		
		if(args != null && args.length > 0) {
			throw new CommandException("Expected no arguments");
		}
		
		manager.getPlugin().reloadConfig();
		
		sender.sendMessage(ChatColor.GOLD + "Configuration reloaded.");
		
	}

	public static void sendRandomCommandIllegalArgumentMessage(CommandSender sender) throws CommandException {
		throw new CommandException("Expected a number that represents how many item stacks should be chosen randomly. Or expected no arguments, to indicate the treasure should not be random.");
	}
	
}
