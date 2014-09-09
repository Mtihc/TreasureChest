package com.mtihc.minecraft.treasurechest.v8.plugin;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.core.TreasureException;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.util.commands.Command;
import com.mtihc.minecraft.treasurechest.v8.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.util.commands.ICommand;
import com.mtihc.minecraft.treasurechest.v8.util.commands.SimpleCommand;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

public class RegionCommand extends SimpleCommand {
	private enum TaskType {
		SET, SET_SHARED, GROUP_ADD, GROUP_REMOVE, DELETE,
		/**
		 * @deprecated Will become obsolete once the book-config feature is implemented.
		 */
		@Deprecated
		USE_META_DATA;
	}
	
	private final EnumSet<Material> ContainerBlocks = EnumSet.of(Material.DISPENSER, Material.CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.TRAPPED_CHEST, Material.HOPPER, Material.DROPPER);

	private TreasureManager manager;

	public RegionCommand(TreasureManager manager, ICommand parent) {
		super(parent, new String[]{"region"}, "", "Manipulation of chests within a region (create, destroy, etc).", null);
		this.manager = manager;

		addNested("set");
		addNested("setShared");
		addNested("groupAdd");
		addNested("groupRemove");
		// TODO remove when useMetaData method is removed
		addNested("useMetaData");
		addNested("delete");
	}

	@Command(aliases = { "set" }, args = "[block,white-list]", desc = "Create/update all treasure in the selected region", help = { "" })
	public void set(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}
		
		if(args == null || args.length > 1) {
			throw new CommandException("Expected no arguments or optional block type filter (comma seperated without spaces)");
		}

		String filter = null;
		if (args.length == 1) {
			filter = args[0];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.SET, filter);
	}

	@Command(aliases = {"set-shared" }, args = "[block,white-list]", desc = "Create/update all treasure with a shared inventory in the selected regio", help = { "" })
	public void setShared(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}

		if(args == null || args.length > 1) {
			throw new CommandException("Expected no arguments or optional block type filter (comma seperated without spaces)");
		}
		
		String filter = null;
		if (args.length == 1) {
			filter = args[0];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.SET_SHARED, filter);
	}
	
	@Command(aliases = {"group-add" }, args = "<group name> [block,white-list]", desc = "Add all treasure in the selected region to a group", help = { "" })
	public void groupAdd(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}
		
		if(args == null || args.length > 2) {
			throw new CommandException("Group name or group name with optional block type filter (comma seperated without spaces)");
		}

		String name = args[0];
		
		if (!manager.hasGroup(name)) {
			throw new CommandException("Group " + name + " doesn't exist");
		}
		
		String filter = null;
		if (args.length == 2) {
			filter = args[1];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.GROUP_ADD, name, filter);
	}
	
	@Command(aliases = { "group-remove" }, args = "<group name> [block,white-list]", desc = "Remove all treasure in the selected region from a group", help = { "" })
	public void groupRemove(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}

		if(args == null || args.length > 2) {
			throw new CommandException("Expected group name or group name with optional block type filter (comma seperated without spaces)");
		}
		
		String name = args[0];
		
		if (!manager.hasGroup(name)) {
			throw new CommandException("Group " + name + " doesn't exist");
		}
		
		String filter = null;
		if (args.length == 2) {
			filter = args[1];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.GROUP_REMOVE, name, filter);
	}

	/**
	 * @deprecated Will be obsolete once book-config feature is implemented.
	 */
	@Deprecated
	@Command(aliases = { "use-meta-data" }, args = "[block,white-list]", desc = "Create/update all treasure in the selected region using the meta-data of items in the chest (sticks with names where shared will make a chest shared and any other name will add the chest to a group with that name)", help = { "" })
	public void useMetaData(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}

		if(args == null || args.length > 1) {
			throw new CommandException("Expected no arguments or optional block type filter (comma seperated without spaces)");
		}
	
		String filter = null;
		if (args.length == 1) {
			filter = args[0];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.USE_META_DATA, filter);
	}
	
	@Command(aliases = { "delete" }, args = "[block,white-list]", desc = "Delete all treasure in the selected region", help = { "" })
	public void delete(CommandSender sender, String[] args) throws CommandException {
		
		if(!(sender instanceof Player)) {
			throw new CommandException("Command must be executed by a player, in game.");
		}

		if(!sender.hasPermission(Permission.DEL.getNode())) {
			throw new CommandException("You don't have permission to create treasure.");
		}
		
		if(args == null || args.length > 1) {
			throw new CommandException("Expected no arguments or optional block type filter (comma seperated without spaces)");
		}

		String filter = null;
		if (args.length == 1) {
			filter = args[0];
		}
		Player player = (Player) sender;
		schudleFindContainerBlocks(player, TaskType.DELETE, filter);
	}
	
	private void schudleFindContainerBlocks(Player player, TaskType task, String filter) throws CommandException {
		schudleFindContainerBlocks(player, task, null, filter);
	}
	
	private void schudleFindContainerBlocks(Player player, TaskType task, String group, String filter) throws CommandException {
		Set<Material> filterList = new HashSet<Material>();

		if (filter != null) {
			// Process the comma separated block list if we have one ...
			String[] materialNames = filter.split(",");

			for (int i=0;i<materialNames.length;i++) {
				String materialName = materialNames[i];
				Material checkMaterial = Material.getMaterial(materialName.toUpperCase());

				if (!ContainerBlocks.contains(checkMaterial)) {
					throw new CommandException( "Material " + materialName.toUpperCase() + " is not a container block, please select from " + ContainerBlocks.toString());
				} else {
					filterList.add(checkMaterial);
				}
			}
		} else {
			// ... or just copy all the container blocks into the filter list
			Iterator<Material> i = ContainerBlocks.iterator();
			while(i.hasNext()) {
				Material addMaterial = i.next();
				filterList.add(addMaterial);
			}
		}
		 
		// FIXME Should we be using RegionSelect?
		Plugin we = manager.getPlugin().getServer().getPluginManager().getPlugin("WorldEdit");
		if (!(we != null && we instanceof WorldEditPlugin)) {
			throw new CommandException("This command requires WE");
		}
		WorldEditPlugin worldEdit = (WorldEditPlugin) we;
		Selection sel = worldEdit.getSelection(player);
		if (sel == null || sel.getMaximumPoint() == null
				|| sel.getMinimumPoint() == null) {
			throw new CommandException("You didn't select a region.");
		}

		World world = sel.getWorld();
		Vector min = sel.getMinimumPoint().toVector();
		Vector max = sel.getMaximumPoint().toVector();

		player.sendMessage(ChatColor.GOLD + "Searching for container blocks in the region (" + world.getName() + ";" + min.toString() + ";" +  max.toString() + ") this may take some time");
		
		// Do the work of finding the containers in a worker thread as we don't want to timeout the server
		manager.getPlugin().getServer().getScheduler().runTaskAsynchronously(manager.getPlugin(), new FindContainers(manager, player, sel.getWorld(), min, max, task, group, filterList));
	}
	
	private class FindContainers implements Runnable {
		TreasureManager manager;
		Player player;
		World world;
		Vector min,max;
		TaskType task;
		String group;
		
		Set<Location> found;
		Set<Material> filterList;

		public FindContainers(TreasureManager manager, Player player, World world, Vector min, Vector max, TaskType task, String group, Set<Material> filterList) {
			this.manager = manager;
			this.player = player;
			this.world = world;
			this.min = min;
			this.max = max;
			this.task = task;
			this.found = new HashSet<Location>();
			this.group = group;
			this.filterList = filterList;
		}

		public void run() {
			int x,y,z;

			// FIXME Need to round up where the min/max is using the inner side or corner of selected cord
			// Search the selected cuboid for containers
			for (y=min.getBlockY();y<max.getBlockY();y++) {
				for (z=min.getBlockZ();z<max.getBlockZ();z++) {
					for (x=min.getBlockX();x<max.getBlockX();x++) {
						Iterator<Material> i = filterList.iterator();
						while (i.hasNext()) {
							Material checkMaterial = i.next();
							if (world.getBlockTypeIdAt(x,y,z) == checkMaterial.getId()) {
								found.add(new Location(world,x,y,z));
								break;
							}
						}
					}
				}
			}

			// Now we have the list we need to do the actual add in synchronously in the main thread
			manager.getPlugin().getServer().getScheduler().runTask(manager.getPlugin(), new AddContainers(manager, player, found, task, group));
		}
	}
	
	private class AddContainers implements Runnable {
		TreasureManager manager;
		Player player;
		Set<Location> found;
		TaskType task;
		String group;
		
		public AddContainers(TreasureManager manager, Player player, Set<Location> found, TaskType task, String group) {
			this.manager = manager;
			this.player = player;
			this.found = found;
			this.task = task;
			this.group = group;
		}

		/**
		 * @deprecated Will become obsolete once the book-config feature is implemented
		 */
		@Deprecated
		private void createChestFromMetadata(Player player, Block block) {
			InventoryHolder holder = (InventoryHolder) block.getState();
			Iterator<ItemStack> ic = holder.getInventory().iterator();
			Set<String> groups = new HashSet<String>();
			boolean shared = false;

			// First we loop through all the the data we need ...
			while(ic.hasNext()) {
				ItemStack item = ic.next();
				if (item != null) {
					// Sticks with custom names are use to store the Meta Data
					if ((item.getType() == Material.STICK) && item.getItemMeta().hasDisplayName()) {
						String name = item.getItemMeta().getDisplayName();
						if (name.equalsIgnoreCase("shared")) {
							shared = true;
						} else {
							groups.add(name);
						}
					}
				}
			}

			// ... then we check that all the groups are valid and if not bail ...
			if (!groups.isEmpty()) {
				Iterator<String> is = groups.iterator();
				while(is.hasNext()) {
					String tmpGroup = is.next();
					if (!manager.hasGroup(tmpGroup)) {
						Location loc = block.getLocation();
						player.sendMessage(ChatColor.RED + "Failed to find group " + tmpGroup + " for chest @ " + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ());
						return;
					}
				}
			}
			
			// ... finally all is good so we create the chest and add it to the groups
			manager.treasureSet(player, block, shared);

			if (!groups.isEmpty()) {
				Iterator<String> is = groups.iterator();
				while(is.hasNext()) {
					String tmpGroup = is.next();
					try {
						manager.treasureGroupAdd(player, block, tmpGroup, false);
					} catch (TreasureException e) {
						player.sendMessage(ChatColor.RED + e.getMessage());
					}
				}
			}

		}
		
		@Override
		public void run() {
			if (player.isOnline()) {
				player.sendMessage(ChatColor.GOLD + "Finished searching region and found " + found.size() + " container blocks");
			}
			
			// FIXME might want to only update a few chests per tick
			Iterator<Location> i = found.iterator();
			while (i.hasNext()) {
				Location treasure = i.next();
				Block block = treasure.getBlock();

				try {
					switch (task) {
					case SET:
						manager.treasureSet(player, block, false);
						break;
					case SET_SHARED:
						manager.treasureSetShared(player, block, false);
						break;
					case DELETE:
						manager.treasureDelete(player, block, false);
						break;
					case GROUP_ADD:
						manager.treasureGroupAdd(player, block, group, false);
						break;
					case USE_META_DATA:
						createChestFromMetadata(player, block);
						break;
					case GROUP_REMOVE:
						manager.treasureGroupRemove(player, block, group, false);
						break;
					default:
						break;
					}
				} catch (TreasureException e) {
					player.sendMessage(ChatColor.RED + e.getMessage());
				}
			}
		}
	}
}
