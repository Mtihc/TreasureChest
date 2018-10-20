package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestDeleteEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundAlreadyEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundUnlimitedEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestOpenEvent;
import com.mtihc.minecraft.treasurechest.v8.plugin.Permission;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactoryManager;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

/**
 * The manager class that makes the TreasureChest plugin work.
 * 
 * <p>This class creates and registers an event listener with bukkit. 
 * The listener is not accessible, but it can call methods on this class.</p>
 * 
 * <p>This class also creates the reward manager. 
 * The reward manager is returned by method <code>getRewardManager()</code>. 
 * You can use it to add new reward factories.</p>
 * 
 * @author Mitch
 *
 */
public class TreasureManager extends TreasureDataFacade {
	
	static {
		ConfigurationSerialization.registerClass(ItemStackWrapper.class);
		ConfigurationSerialization.registerClass(TreasureChest.class);
		ConfigurationSerialization.registerClass(TreasureChestGroup.class);
		ConfigurationSerialization.registerClass(BlockInventory.class);
		ConfigurationSerialization.registerClass(DoubleBlockInventory.class);
		ConfigurationSerialization.registerClass(RewardInfo.class, "RewardInfo");
	}
	
	public static boolean isInventoryHolder(Block block) {
		return block.getState() instanceof InventoryHolder;
	}
	
	public static boolean isDoubleChest(InventoryHolder holder) {
		return holder.getInventory() instanceof DoubleChestInventory;
	}
	
	public static DoubleChest getDoubleChest(InventoryHolder holder) {
		try {
			return (DoubleChest) holder.getInventory().getHolder();
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	public static Location getLocation(InventoryHolder holder) {
		InventoryHolder invHolder = holder.getInventory().getHolder();
		if(invHolder instanceof DoubleChest) {
			return ((DoubleChest)invHolder).getLocation();
		}
		else if(holder instanceof BlockState) {
			return ((BlockState)holder).getLocation();
		}
		else {
			throw new IllegalArgumentException("Parameter holder must be a BlockState, or DoubleChest."); 
		}
	}
	
	private JavaPlugin plugin;
	private ITreasureManagerConfiguration config;
	
	private LinkedHashMap<String, TreasureInventory> inventories = new LinkedHashMap<String, TreasureInventory>();
	private String permAccessNormal;
	private String permAccessUnlimited;
	private String permRank;
	
	private RewardFactoryManager rewardManager;
	
	
	
	/**
	 * Constructor.
	 * @param plugin The JavaPlugin
	 * @param config The configuration
	 * @param chests The chests repository
	 * @param groups The groups repository
	 * @param memory The memory/players repository
	 * @param permAccessNormal Normal treasure access permission string
	 * @param permAccessUnlimited Unlimited treasure access permission string
	 * @param permRank The first part of the rank permission string
	 */
	public TreasureManager(JavaPlugin plugin, ITreasureManagerConfiguration config,
			ITreasureChestRepository chests,
			ITreasureChestGroupRepository groups, ITreasureChestMemory memory, String permAccessNormal, String permAccessUnlimited, String permRank) {
		super(config, chests, groups, memory);
		this.plugin = plugin;

		this.permAccessNormal = permAccessNormal;
		this.permAccessUnlimited = permAccessUnlimited;
		this.permRank = permRank;
		
		this.rewardManager = new RewardFactoryManager();
		
		Listener listener = new TreasureChestListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	/**
	 * Returns the JavaPlugin
	 * @return the plugin
	 */
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	/**
	 * Returns the reward factory manager
	 * @return the reward factory manager
	 */
	public RewardFactoryManager getRewardManager() {
		return rewardManager;
	}
	
	

	@Override
	public boolean removeTreasure(Location location) {
		ITreasureChest tchest = getTreasure(location);
		if(tchest == null || !dispatchTreasureDelete(tchest)) {
			return false;
		}
		forgetChest(location);
		return super.removeTreasure(location);
	}

	@Override
	public void forgetChest(Location location) {
		ITreasureChest tchest = getTreasure(location);
		if (tchest != null && tchest.isShared()) {
			// shared chests don't have a timed removal, their inventory will persist until
			// server reset or a forget-all command is issued
			// TODO if we end up using the forget-time somehow, we will need timed removal
			Location loc =  tchest.getContainer().getLocation();
			final String KEY = loc.getWorld().getName() + "@" + loc.toVector().toString();
			inventories.remove(KEY);
		} else {
			super.forgetChest(location);
		}
	}
	
	
	
	
	
	
	/**
	 * Called automatically by the internal event listener that has a reference to this manager, whenever a player interact event occurs.
	 * @param event the player interact event
	 */
	void onPlayerInteract(final PlayerInteractEvent event) {
		
		// check action
		if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			return;
		}
		
		
		//
		// get InventoryHolder and Block
		// 
		// If it's a double chest...
		// The InventoryHolder will be instanceof DoubleChest.
		// The block will be DoubleChest.getLocation().getBlock() (which is always the left side)
		//
		
		
		Block block = event.getClickedBlock();
		if(!(block.getState() instanceof InventoryHolder)) {
			// block is not an InventoryHolder
			return;
		}
		
		ITreasureChest tchest = getTreasureChest(block);
		
		if(tchest == null) {
			// didn't click a treasure chest
			return;
		}
		
		if(isBlockedByBlockAbove(block)) {
			// there's a block on top that is blocking the chest from opening
			return;
		}
		
		//
		// right clicked a treasure chest !
		//
		
		Player player = event.getPlayer();
		
		// check/ignore protection
		if(!tchest.ignoreProtection() && event.useInteractedBlock().equals(Result.DENY)) {
			// protected, and protection is not ignored
			return;
		}
		// deny anyway, because the player will open a "fake inventory"
		event.setUseInteractedBlock(Result.DENY);

		
		// check permission
		if(!checkPermission(player, tchest)) {
			return;
		}
		// check rank (configured permission)
		if(!checkRank(player, tchest)) {
			return;
		}
		
		
		openTreasureInventory(player, tchest);
		
		
	}
	
	/**
	 * Helper method to open a treasure. 
	 * <p>Creates the fake inventory and it's contents. 
	 * Checks and remembers whether the treasure was found before. 
	 * And opens the fake inventory for the player.</p>
	 */
	public void openTreasureInventory(Player player, ITreasureChest tchest) {

		Inventory inventory;
		boolean chestJustCreated = false;

		if (tchest.isShared()) {
			inventory = createTreasureInventory(player, tchest, true);
			if (inventory == null) {
				chestJustCreated = true;
				inventory = createTreasureInventory(player, tchest, false);
			}
		} else {
			inventory = createTreasureInventory(player, tchest);
		}
		

		Location loc = tchest.getContainer().getLocation();
		
		//
		// copy the contents from the treasure chest, 
		// and open the fake inventory
		//
		
		//
		// if treasure is unlimited, 
		// copy the items from the treasure chest, no matter what
		//
		if(tchest.isUnlimited()) {
			// set items to chest
			toInventory(tchest.getContainer().getContents(), tchest.getAmountOfRandomlyChosenStacks(), inventory);
			
			// message unlimited!
			if(!dispatchTreasureFoundUnlimited(player, tchest, inventory)) {
				return;
			}
			
			giveRewards(player, tchest);
			
			String message = tchest.getMessage(TreasureChest.Message.UNLIMITED);
			if(message != null) {
				player.sendMessage(ChatColor.GOLD + message);
			}
		}
		//
		// if treasure is not unlimited and not a sinlgeton,
		// check if/when the player has found the treasure before,
		// and compare the time with the treasure's "forget-time".
		//
		else if(!tchest.isShared()){
			// when has player found before
			long time = whenHasPlayerFound((OfflinePlayer)player, loc);
			
			
			if(time != 0 && !hasForgotten(time, tchest.getForgetTime())) {
				// already found and not forgotten that it was found
				
				// message found already
				if(!dispatchTreasureFoundAlready(player, tchest, inventory)) {
					return;
				}
				
				String alreadyFoundMessage = tchest.getMessage(TreasureChest.Message.FOUND_ALREADY);
				if(alreadyFoundMessage != null) {
					player.sendMessage(ChatColor.GOLD + alreadyFoundMessage);
				}
				
			}
			else {
				// found for the first time (or after forget time)
				
				// set items to chest
				toInventory(tchest.getContainer().getContents(), tchest.getAmountOfRandomlyChosenStacks(), inventory);

				// message found treasure!
				if(!dispatchTreasureFound(player, tchest, inventory)) {
					return;
				}
				
				// remember that this player found it at this time
				rememberPlayerFound((OfflinePlayer)player, loc);
				
				giveRewards(player, tchest);
				
				String foundMessage = tchest.getMessage(TreasureChest.Message.FOUND);
				if(foundMessage != null) {
					player.sendMessage(ChatColor.GOLD + foundMessage);
				}
				
			}
		}
		else
		{
			if (chestJustCreated) {
				// For shared chests we don't have forget or rewards
				// TODO if we would remember who found it first, we could use the forget-time.
				// TODO if we would know who found it first, we could give rewards
				
				// set items to chest
				toInventory(tchest.getContainer().getContents(), tchest.getAmountOfRandomlyChosenStacks(), inventory);

				// message found treasure!
				if(!dispatchTreasureFound(player, tchest, inventory)) {
					return;
				}

				String foundMessage = tchest.getMessage(TreasureChest.Message.FOUND);
				if(foundMessage != null) {
					player.sendMessage(ChatColor.GOLD + foundMessage);
				}
			}
		}
		
		// player opens "fake" inventory,
		// this ensures players don't interfere with eachother's items 
		// (prevents ninjas, and treasure chest campers)

		dispatchTreasureOpen(player, tchest, inventory);
		player.openInventory(inventory);
	}
	
	public Inventory createTreasureInventory(Player player, ITreasureChest tchest) {
		return createTreasureInventory(player, tchest, false);
	}
	
	public Inventory createTreasureInventory(Player player, ITreasureChest tchest, boolean lookupOnly) {
		Location loc = tchest.getContainer().getLocation();
		Block block = loc.getBlock();
		
		if(!(block.getState() instanceof InventoryHolder)) {
			throw new IllegalArgumentException("There's no InventoryHolder at the location of the specified ITreasureChest object.");
		}
		
		InventoryHolder holder = (InventoryHolder) block.getState();
		holder = holder.getInventory().getHolder();
		
		// get unique key for player @ inventory location
		final String KEY;
		if (tchest.isShared()) {
			KEY = loc.getWorld().getName() + "@" + loc.toVector().toString();
		} else {
			KEY = player.getName() + "@" + loc.getWorld().getName() + "@" + loc.toVector().toString();			
		}
		// get remembered inventory for player, or null
		TreasureInventory tInventory = inventories.get(KEY);
		
		Inventory inventory;
		if(tInventory != null) {
			// inventory still remembered, maybe there's still some old items in there
			inventory = tInventory.getInventory();
		}
		else {
			if (lookupOnly) {
				return null;
			}
			// create new Inventory
			if(holder instanceof DoubleChest) {
				inventory = plugin.getServer().createInventory(holder, holder.getInventory().getSize());
			}
			else {
				inventory = plugin.getServer().createInventory(holder, holder.getInventory().getType());
			}
			
			// wrap inventory in an object that will clear itself from memory
			tInventory = new TreasureInventory(plugin, 600L, inventory) {
				
				@Override
				protected void execute() {
					inventories.remove(KEY);
				}
			};
			inventories.put(KEY, tInventory);
		}
		
		// inventory will clear itself from the map
		if (!tchest.isShared()) {
			tInventory.schedule();
		}
		
		return inventory;
		
	}

	
	public ITreasureChest getTreasureChest(Block clickedBlock) {
		
		if(!(clickedBlock.getState() instanceof InventoryHolder)) {
			// block is not an InventoryHolder
			return null;
		}
		
		// get inventory holder
		InventoryHolder holder = (InventoryHolder) clickedBlock.getState();
		// get inventory holder's location
		Location location = getLocation(holder);
		
		// get the treasure chest object
		return getTreasure(location);
	}
	
	public boolean isBlock(Block block) {
		
		// returns whether the given block would prevent a chest from opening
		// based on:
		// http://www.minecraftwiki.net/wiki/Chest
		
		if(block == null || block.getType().isTransparent()) {
			// translucent/transparent, or non-block
			return false;
		}
		switch (block.getType()) {
			// water and lava
		case LAVA:
		case WATER:
		case LEGACY_STATIONARY_LAVA:
		case LEGACY_STATIONARY_WATER:

			// plants
		case LEGACY_LEAVES:
		case CACTUS:
			
			// translucent blocks
		case GLASS:
		case LEGACY_MOB_SPAWNER:
		case SNOW:
		case ICE:
		case LEGACY_FENCE:
		case CAKE:
		case LEGACY_BED:
		case GLOWSTONE:
		case ANVIL:
		case BEACON:
		case CHEST:
		case SIGN:
			case LEGACY_SIGN_POST:
			// slabs
		case LEGACY_STEP:
			case LEGACY_WOOD_STEP:
			// stairs
			case OAK_STAIRS:
		case COBBLESTONE_STAIRS:
		case BRICK_STAIRS:
			case LEGACY_SMOOTH_STAIRS:
		case NETHER_BRICK_STAIRS:
		case SANDSTONE_STAIRS:
		case SPRUCE_STAIRS:
		case BIRCH_STAIRS:
			case JUNGLE_STAIRS:
		case QUARTZ_STAIRS:
			return false;
		default:
			return true;
		}
	}
	
	public boolean isBlockedByBlockAbove(Block block) {
		if(!(block.getState() instanceof InventoryHolder)) {
			return false;
		}
		if(block.getType() != Material.CHEST && block.getType() != Material.ENDER_CHEST && block.getType() != Material.TRAPPED_CHEST) {
			return false;
		}
		InventoryHolder holder = (InventoryHolder) block.getState();
		Block above = block.getRelative(BlockFace.UP);
		if(holder.getInventory() instanceof DoubleChestInventory) {
			DoubleChest dchest = (DoubleChest) holder.getInventory().getHolder();
			
			Block rightSide = ((BlockState)dchest.getRightSide()).getBlock();
			Block rightAbove = rightSide.getRelative(BlockFace.UP);
			if(isBlock(rightAbove)) {
				return true;
			}
			Block leftSide = ((BlockState)dchest.getLeftSide()).getBlock();
			above = leftSide.getRelative(BlockFace.UP);
		}
		
		return isBlock(above);
	}
	
	public Set<String> getRanks(ITreasureChest tchest) {
		HashSet<String> result = new HashSet<String>();
		List<String> chestRanks = tchest.getRanks();
		if(chestRanks == null || chestRanks.isEmpty()) {
			return result;
		}
		List<String> configRanks = config.getRanks();
		for (String rank : chestRanks) {
			
			for (String r : configRanks) {
				if(rank.equalsIgnoreCase(r)) {
					result.add(r);
				}
			}
		}
		return result;
	}
	
	private boolean checkPermission(Player player, ITreasureChest tchest) {
		if(tchest.isUnlimited()) {
			if(!player.hasPermission(permAccessUnlimited)) {
				player.sendMessage(ChatColor.RED + "You don't have permission \"" + permAccessUnlimited + "\".");
				return false;
			}
		}
		else {
			if(!player.hasPermission(permAccessNormal)) {
				player.sendMessage(ChatColor.RED + "You don't have permission \"" + permAccessNormal + "\".");
				return false;
			}
		}
		return true;
	}
	
	private boolean checkRank(Player player, ITreasureChest tchest) {
		// check rank permission
		String rankPerms = "";
		Set<String> ranks = getRanks(tchest);
		for (String rank : ranks) {
			String perm = permRank + "." + rank.toLowerCase();
			if(player.isPermissionSet(perm) && player.hasPermission(perm)) {
				return true;
			}
			rankPerms += ", " + perm;
		}
		if(rankPerms.isEmpty()) {
			return true;
		}
		else {
			if(player.hasPermission(Permission.SET.getNode())) {
				rankPerms = rankPerms.substring(2);//remove comma and string
				player.sendMessage(ChatColor.RED + "You require one of the following permissions, to open that treasure: " + ChatColor.GRAY + rankPerms);
			}
			else {
				player.sendMessage(ChatColor.RED + "You are not allowed to open this treasure.");
			}
			
			return false;
		}
	}

	private void dispatchTreasureOpen(Player player, ITreasureChest tchest,
			Inventory inventory) {
		TreasureChestEvent event = new TreasureChestOpenEvent(player, tchest, inventory);
		plugin.getServer().getPluginManager().callEvent(event);
	}

	private boolean dispatchTreasureFound(Player player, ITreasureChest tchest,
			Inventory inventory) {
		TreasureChestFoundEvent event = new TreasureChestFoundEvent(player, tchest, inventory);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	private boolean dispatchTreasureFoundAlready(Player player,
			ITreasureChest tchest, Inventory inventory) {
		TreasureChestFoundAlreadyEvent event = new TreasureChestFoundAlreadyEvent(player, tchest, inventory);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}

	private boolean dispatchTreasureFoundUnlimited(Player player,
			ITreasureChest tchest, Inventory inventory) {
		TreasureChestFoundUnlimitedEvent event = new TreasureChestFoundUnlimitedEvent(player, tchest, inventory);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
	
	private boolean dispatchTreasureDelete(ITreasureChest tchest) {
		TreasureChestDeleteEvent event = new TreasureChestDeleteEvent(tchest);
		plugin.getServer().getPluginManager().callEvent(event);
		return !event.isCancelled();
	}
	
	
	
	
	

	private boolean hasForgotten(long foundTime, long forgetTime) {
		if(foundTime <= 0) {
			// never found, so yeah... act like forgotten
			return true;
		}
		else if(forgetTime <= 0) {
			// never forgets, so no
			return false;
		}
		
		// now
		Calendar now = Calendar.getInstance();
		
		// when will the chest forget?
		Calendar forgot = Calendar.getInstance();
		forgot.setTimeInMillis(foundTime + forgetTime);
		
		// is "now" later than "forgot"? Then yes, is forgotten
		return now.compareTo(forgot) > 0;
	}
	

	private ItemStack[] getRandomizedInventory(ItemStack[] inventory, int randomAmount) {
		
		// copy inventory to result
		ItemStack[] result = new ItemStack[inventory.length];
		for (int i = 0; i < inventory.length; i++) {
			result[i] = inventory[i];
		}
		
		if(randomAmount < 1) {
			return result;
		}
		
		// find indices of non-empty inventory slots
		List<Integer> nonNulls = new ArrayList<Integer>();
		for (int i = 0; i < inventory.length; i++) {
			if(inventory[i] != null) {
				nonNulls.add(i);
			}
		}
		
		
		
		Random random = new Random(System.currentTimeMillis());
		int i = randomAmount;
		
		// select random item stacks
		while(i > 0 && nonNulls.size() > 0) {
			int index = random.nextInt(nonNulls.size());
			nonNulls.remove(index);
			i--;
		}
		
		// remove itemstacks, that were not randomly selected, from the result array
		for (Integer integer : nonNulls) {
			result[integer] = null;
		}
		
		
		return result;
		 
	}

	protected void toInventory(ItemStack[] contents, int random, Inventory inventory) {
		
		ItemStack[] tchest = getRandomizedInventory(contents, random);
		int size = tchest.length;
		int sizeTarget = inventory.getSize();
		
		ItemStack[] result = new ItemStack[sizeTarget];
		
		if(sizeTarget < size) {
			// forget about positions, just add as many as possible
			int index = 0;
			for (ItemStack itemStack : tchest) {
				if(itemStack == null) {
					continue;
				}
				// just add a non-null ItemStack at the next index
				result[index] = itemStack;
				index++;
			}
		}
		else {
			// add items at correct positions
			for (int i = 0; i < tchest.length; i++) {
				result[i] = tchest[i];
			}
		}
		
		inventory.setContents(result);
		
	}
	
	private static HashSet<Material> invisibleBlocks;
	
	private static HashSet<Material> getInvisibleBlocks() {
		if(invisibleBlocks == null) {
			invisibleBlocks  = new HashSet<Material>();
			Material[] mats = Material.values();
			for (Material mat : mats) {
				if(mat.isTransparent()) {
					invisibleBlocks.add(mat);
				}
			}
		}
		
		return invisibleBlocks;
	}
	
	public static Block getTargetedContainerBlock(Player player) {
		Block block = player.getTargetBlock(getInvisibleBlocks(), 8);
		if(block == null || !(block.getState() instanceof InventoryHolder)) {
			return null;
		}
		else {
			return block;
		}
	}
	
	private void giveRewards(Player player, ITreasureChest tchest) {
		List<RewardInfo> infos = tchest.getRewards();
		for (RewardInfo r : infos) {
			if(r == null) {
				continue;
			}
			IReward reward;
			try {
				reward = rewardManager.create(r);
				reward.give(player);
			} catch (RewardException e) {
				player.sendMessage(ChatColor.RED + e.getMessage());
			}
		}
	}
	
	public boolean treasureSet(Player player, Block block, boolean displayMessages) {
		return treasureSet(player, block, displayMessages, false);
	}
	
	public boolean treasureSetShared(Player player, Block block, boolean displayMessages) {
		return treasureSet(player, block, displayMessages, true);
	}
	
	public boolean treasureSet(Player player, Block block, boolean displayMessages, boolean shared) {
		InventoryHolder holder = (InventoryHolder) block.getState();
		Location loc = TreasureManager.getLocation(holder);
		
		ITreasureChest tchest = getTreasure(loc);
		
		if(tchest != null) {
			
			Inventory inventory = createTreasureInventory(player, tchest);
			tchest.getContainer().setContents(inventory.getContents());
			
			if(tchest.isShared()) {
				tchest.setShared(false);
				if (displayMessages) {
					player.sendMessage(ChatColor.GOLD + "Treasure contents updated. And is no longer shared.");
				}
			}
			else {
				if (displayMessages) {
					player.sendMessage(ChatColor.GOLD + "Treasure contents updated.");
				}
			}
			
		}
		else {
			tchest = new TreasureChest(block.getState());
			tchest.setShared(shared);
			for (ITreasureChest.Message messageId : ITreasureChest.Message.values()) {
				tchest.setMessage(messageId, getConfig().getDefaultMessage(messageId));
			}
			tchest.ignoreProtection(getConfig().getDefaultIgnoreProtection());
			holder.getInventory().clear();
	
			if (displayMessages) {
				player.sendMessage(ChatColor.GOLD + "Treasure saved");
			}
		}
		setTreasure(tchest);
		return true;
	}
	
	public boolean treasureDelete(Player player, Block block, boolean displayMessages) throws TreasureException {
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		if(!hasTreasure(loc)) {
			throw new TreasureException("Treasure doesn't exist, or is already deleted.");
		}
		else {
			int BlockX = loc.getBlockX();
			int BlockY = loc.getBlockY();
			int BlockZ = loc.getBlockZ();
			// Check if the chest is in any groups and if it is remove it from them
			Set<String> groupList = getGroupNames();
			Iterator<String> ig = groupList.iterator();
			while(ig.hasNext()) {
				String group = ig.next();
				ITreasureChestGroup tcgroup = getGroup(group);
				if (tcgroup == null) {
					continue;
				}
				Set<Location> locs = tcgroup.getLocations();
				Iterator<Location> il = locs.iterator();
				while(il.hasNext()) {
					Location tmpLoc = il.next();

					if ((BlockX == tmpLoc.getBlockX()) &&
							(BlockY == tmpLoc.getBlockY()) &&
							(BlockZ == tmpLoc.getBlockZ())){
						ITreasureChest tchest = getTreasure(loc);
						
						try {
							tcgroup.removeChest(tchest);
						} catch(Exception e) {
							player.sendMessage(ChatColor.RED + "Failed to remove treasure at " + tmpLoc.getBlockX() + "," + tmpLoc.getBlockY() + "," + tmpLoc.getBlockZ() + " from group \"" + group + "\". " + e.getMessage());
						}
						
						if (displayMessages) {
							player.sendMessage(ChatColor.GRAY + "Treasure removed from group " + group + ".");
						}
						setGroup(group, tcgroup);
					}
				}
			}
			
			if(!removeTreasure(loc)) {
				throw new TreasureException("Deletion of the Treasure was cancelled.");
			}
			if (displayMessages) {
				player.sendMessage(ChatColor.YELLOW + "Treasure chest deleted.");
			}
			return true;
		}
	}

	public boolean treasureGroupAdd(Player player, Block block, String name, boolean displayMessages) throws TreasureException {
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = getTreasure(loc);
		
		if(tchest == null) {
			throw new TreasureException("You're not looking at a treasure.");
		}
	
		ITreasureChestGroup tcgroup = getGroup(name);
		
		if (tcgroup == null) {
			throw new TreasureException("Failed to load group \"" + name + "\".");
		}
		
		try {
			tcgroup.addChest(tchest);
		} catch(Exception e) {
			Location tmpLoc = tchest.getContainer().getLocation();
			throw new TreasureException("Failed to add treasure at " + tmpLoc.getBlockX() + "," + tmpLoc.getBlockY() + "," + tmpLoc.getBlockZ() + " to group \"" + name + "\". " + e.getMessage());
		}
		if (displayMessages) {
			player.sendMessage(ChatColor.GOLD + "Treasure added to group \"" + name + "\".");
		}
		
		setGroup(name, tcgroup);
		return true;
	}

	public boolean treasureGroupRemove(Player player, Block block, String name, boolean displayMessages) throws TreasureException {
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = getTreasure(loc);
		
		if(tchest == null) {
			throw new TreasureException("You're not looking at a treasure.");
		}
	
		ITreasureChestGroup tcgroup = getGroup(name);
		
		if (tcgroup == null) {
			throw new TreasureException("Failed to load group \"" + name + "\".");
		}
		
		try {
			tcgroup.removeChest(tchest);
		} catch(Exception e) {
			Location tmpLoc = tchest.getContainer().getLocation();
			throw new TreasureException("Failed to remove treasure at " + tmpLoc.getBlockX() + "," + tmpLoc.getBlockY() + "," + tmpLoc.getBlockZ() + " from group \"" + name + "\". " + e.getMessage());
		}

		if (displayMessages) {
			player.sendMessage(ChatColor.GOLD + "Treasure removed from group \"" + name + "\".");
		}
		
		setGroup(name, tcgroup);
		return true;
	}
	
	abstract class TreasureInventory implements Runnable {

		private JavaPlugin plugin;
	    private long delay;
	    private int taskId;

	    private Inventory inventory;

	    TreasureInventory(JavaPlugin plugin, long delay, Inventory inventory) {
	        this.plugin = plugin;
	        this.delay = delay;
	        this.taskId = 0;
	        this.inventory = inventory;
	    }

	    /**
	     * @return the inventory
	     */
	    public Inventory getInventory() {
	        return inventory;
	    }

	    public void schedule() {
	        cancel();
	        taskId  = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay);
	    }

	    public void cancel() {
	        if (taskId != 0) {
	            plugin.getServer().getScheduler().cancelTask(taskId);
	            taskId = 0;
	        }
	    }

	    @Override
	    public void run() {
	        cancel();
	        execute();
	    }

	    protected abstract void execute();

	}
}
