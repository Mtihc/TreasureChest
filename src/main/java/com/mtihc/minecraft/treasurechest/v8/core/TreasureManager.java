package com.mtihc.minecraft.treasurechest.v8.core;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
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

import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundAlreadyEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundUnlimitedEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestOpenEvent;

public class TreasureManager {
	
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
		DoubleChest doubleChest = getDoubleChest(holder);
		if(doubleChest != null) {
			return doubleChest.getLocation();
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
	private ITreasureChestRepository chests;
	private ITreasureChestMemory memory;
	
	private Map<String, TreasureInventory> inventories = new HashMap<String, TreasureInventory>();
	private String permAccessNormal;
	private String permAccessUnlimited;
	
	public TreasureManager(JavaPlugin plugin, ITreasureManagerConfiguration config, ITreasureChestRepository chests, ITreasureChestMemory memory, String permAccessNormal, String permAccessUnlimited) {
		this.plugin = plugin;
		this.config = config;
		this.chests = chests;
		this.memory = memory;
		
		this.permAccessNormal = permAccessNormal;
		this.permAccessUnlimited = permAccessUnlimited;
		
		Listener listener = new TreasureChestListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}
	
	public JavaPlugin getPlugin() {
		return plugin;
	}
	
	public ITreasureManagerConfiguration getConfig() {
		return config;
	}
	
	public ITreasureChest load(Location location) {
		return chests.load(location);
	}

	public void save(Location location, ITreasureChest value) {
		chests.save(location, value);
	}

	public boolean has(Location location) {
		return chests.has(location);
	}

	public void delete(Location location) {
		memory.forgetChest(location);
		chests.delete(location);
	}

	public Set<Location> getLocations(String worldName) {
		return chests.getLocations(worldName);
	}

	public Collection<Location> getAllPlayerFound(OfflinePlayer player, World world) {
		return memory.getAllPlayerFound(player, world);
	}

	public long whenHasPlayerFound(OfflinePlayer player, Location location) {
		return memory.whenHasPlayerFound(player, location);
	}

	public boolean hasPlayerFound(OfflinePlayer player, Location location) {
		return memory.hasPlayerFound(player, location);
	}

	public void rememberPlayerFound(OfflinePlayer player, Location location) {
		memory.rememberPlayerFound(player, location);
	}

	public void forgetPlayerFound(OfflinePlayer player, Location location) {
		memory.forgetPlayerFound(player, location);
	}

	public void forgetPlayerFoundAll(OfflinePlayer player, World world) {
		memory.forgetPlayerFoundAll(player, world);
	}

	public void forgetChest(Location location) {
		memory.forgetChest(location);
	}
	
	
	
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
		
		Block block;
		InventoryHolder holder;
		
		block = event.getClickedBlock();
		
		if(!(block.getState() instanceof InventoryHolder)) {
			// block is not an InventoryHolder
			return;
		}
		

		// block is an InventoryHolder
		holder = (InventoryHolder) block.getState();
		
		if(holder.getInventory().getHolder() instanceof DoubleChest) {
			
			// block is part of a DoubleChest
			
			// adjust holder and block to be DoubleChest's
			holder = holder.getInventory().getHolder();
			block = ((DoubleChest)holder).getLocation().getBlock();
		}
		
		
		
		ITreasureChest tchest;
		
		// get treasure chest id for that location
		Location id = block.getLocation();
		
		// get the treasure chest object
		tchest = load(id);
		if(tchest == null) {
			// not a treasure chest
			return;
		}
		
		//
		// right clicked a treasure chest !
		//

		Player player = event.getPlayer();
		
		if(player.getItemInHand().getType().equals(Material.CHEST)) {
			// open the treasure chest normally, without "fake inventory"
			return;
		}
		
		// check/ignore protection
		if(!tchest.ignoreProtection() && event.useInteractedBlock().equals(Result.DENY)) {
			// protected, and protection is not ignored
			return;
		}
		// deny anyway, because the player will open a "fake inventory"
		event.setUseInteractedBlock(Result.DENY);
		
		
		// check permission
		if(!allowAccess(player, tchest.isUnlimited())) {
			player.sendMessage(ChatColor.RED + "You don't have permission to access this Treasure Chest.");
			return;
		}
		
		Inventory inventory;
		
		// get unique key for player @ inventory location
		final String KEY = player.getName()+"@"+block.getLocation().toString();
		
		// get remembered inventory for player, or null
		TreasureInventory tInventory = inventories.get(KEY);
		
		if(tInventory != null) {
			// inventory still remembered, maybe there's still some old items in there
			inventory = tInventory.getInventory();
		}
		else {
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
		tInventory.schedule();
		
		
		
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
			
			String message = tchest.getMessage(TreasureChest.Message.UNLIMITED);
			if(message != null) {
				player.sendMessage(ChatColor.GOLD + message);
			}
		}
		//
		// if treasure is not unlimited,
		// check if/when the player has found the treasure before,
		// and compare the time with the treasure's "forget-time".
		//
		else {
			// when has player found before
			long time = whenHasPlayerFound((OfflinePlayer)player, id);
			
			
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
				rememberPlayerFound((OfflinePlayer)player, id);
				
				
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
	
	private boolean allowAccess(HumanEntity humanEntity, boolean isUnlimitedChest) {
		if(isUnlimitedChest) {
			return humanEntity.hasPermission(permAccessUnlimited);
		}
		else {
			return humanEntity.hasPermission(permAccessNormal);
		}
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

	private void toInventory(ItemStack[] contents, int random, Inventory inventory) {
		
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
	
	private static HashSet<Byte> getInvisibleBlocks() {
		HashSet<Byte> result = new HashSet<Byte>();
		result.add((byte) Material.AIR.getId());
		
		result.add((byte) Material.LAVA.getId());
		result.add((byte) Material.WATER.getId());
		result.add((byte) Material.STATIONARY_LAVA.getId());
		result.add((byte) Material.STATIONARY_WATER.getId());
		result.add((byte) Material.VINE.getId());
		
		return result;
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
