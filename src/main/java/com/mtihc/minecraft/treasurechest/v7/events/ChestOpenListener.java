package com.mtihc.minecraft.treasurechest.v7.events;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;
import com.mtihc.minecraft.treasurechest.v7.Permission;
import com.mtihc.minecraft.treasurechest.v7.TreasureChestPlugin;

public class ChestOpenListener implements Listener{

	
	private TreasureChestPlugin plugin;
	private Map<String, TreasureInventory> inventories;
	
	public ChestOpenListener(TreasureChestPlugin plugin) {
		this.plugin = plugin;
		this.inventories = new HashMap<String, TreasureInventory>();
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		
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
		if(block.getState() instanceof InventoryHolder) {
			
			// block is an InventoryHolder
			holder = (InventoryHolder) block.getState();
			
			if(holder.getInventory().getHolder() instanceof DoubleChest) {
				
				// block is part of a DoubleChest
				
				// adjust holder and block to be DoubleChest's
				holder = holder.getInventory().getHolder();
				block = ((DoubleChest)holder).getLocation().getBlock();
			}
			
		}
		else {
			// block is not an InventoryHolder
			return;
		}
		
		
		
		TreasureChest tchest;
		
		// get treasure chest id for that location
		String id = TChestCollection.getChestId(block.getLocation());
		
		// get the treasure chest object
		try {
			tchest = plugin.getChests().values().getChest(id);
		} catch(NullPointerException e) {
			tchest = null;
		}
		if(tchest == null) {
			// not a treasure chest
			return;
		}
		
		//
		// right clicked a treasure chest !
		//
		
		// check/ignore protection
		if(!tchest.ignoreProtection() && event.useInteractedBlock().equals(Result.DENY)) {
			// protected, and protection is not ignored
			return;
		}
		// deny anyway, because the player will open a "fake inventory"
		event.setUseInteractedBlock(Result.DENY);
		
		
		Player player = event.getPlayer();
		
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
			tchest.toInventory(inventory);
			
			// message unlimited!
			String message = tchest.getMessage(TreasureChest.Message.FOUND_UNLIMITED);
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
			long time = plugin.getMemory().whenHasPlayerFound((OfflinePlayer)player, id);
			
			
			if(time != 0 && !hasForgotten(time, tchest.getForgetTime())) {
				// already found and not forgotten that it was found
				
				// message found already
				String alreadyFoundMessage = tchest.getMessage(TreasureChest.Message.FOUND_ALREADY);
				if(alreadyFoundMessage != null) {
					player.sendMessage(ChatColor.GOLD + alreadyFoundMessage);
				}
				
			}
			else {
				// found for the first time (or after forget time)
				// remember that this player found it at this time
				plugin.getMemory().rememberPlayerFound((OfflinePlayer)player, id);
				
				// set items to chest
				// (calls setContents(ItemStack[]) )
				tchest.toInventory(inventory);
				
				
				// message found treasure!
				String foundMessage = tchest.getMessage(TreasureChest.Message.FOUND);
				if(foundMessage != null) {
					player.sendMessage(ChatColor.GOLD + foundMessage);
				}
				
			}
		}
		
		// player opens "fake" inventory,
		// this ensures players don't interfere with eachother's items 
		// (prevents ninjas, and treasure chest campers)
		player.openInventory(inventory);
	}
	/* (non-Javadoc)
	 * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
//	@EventHandler
//	public void onInventoryOpen(final InventoryOpenEvent event) {
//		
//	}
//	
//	public void onContainerBlockOpen(Player player, Block block, InventoryOpenEvent event) {
//		
//		// opening a treasure chest
//		
//		// prevent players looking in the same chest
//		// only for non-unlimited chests
//		if(!tchest.isUnlimited() && event.getViewers().size() > 1) {
//			player.sendMessage(ChatColor.RED + "Somebody else is already looking in this Treasure Chest.");
//			player.sendMessage(ChatColor.RED + "Please give him/her a minute.");
//			return;
//		}
//		
//		
//		
//		if(tchest.isUnlimited()) {
//			// chest is unlimited
//			// reset items
//			tchest.toInventoryHolder((InventoryHolder)block.getState());
//			// message
//			String message = tchest.getMessage(TreasureChest.Message.FOUND_UNLIMITED);
//			if(message != null) {
//				player.sendMessage(ChatColor.GOLD + message);
//			}
//		}
//		else {
//			// chest is not-unlimited
//			// when has player found before
//			long time = plugin.getMemory().whenHasPlayerFound((OfflinePlayer)player, id);
//			
//			if(time != 0 && !hasForgotten(time, tchest.getForgetTime())) {
//				// already found and not forgotten
//				
//				// message
//				String alreadyFoundMessage = tchest.getMessage(TreasureChest.Message.FOUND_ALREADY);
//				if(alreadyFoundMessage != null) {
//					player.sendMessage(ChatColor.GOLD + alreadyFoundMessage);
//				}
//				
//			}
//			else {
//				// found for the first time (or after forget time)
//				// remember that this player found it
//				plugin.getMemory().rememberPlayerFound((OfflinePlayer)player, id);
//				
//				// set items to chest
//				tchest.toInventoryHolder((InventoryHolder)block.getState());
//				
//				// message
//				String foundMessage = tchest.getMessage(TreasureChest.Message.FOUND);
//				if(foundMessage != null) {
//					player.sendMessage(ChatColor.GOLD + foundMessage);
//				}
//				
//			}
//		}
//		return;
//	}
//	
//	/* (non-Javadoc)
//	 * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
//	 */
//	@EventHandler(priority = EventPriority.HIGHEST)
//	public void onInventoryOpen(final InventoryOpenEvent event) {
//		// get player
//		Player player;
//		if(event.getPlayer() instanceof Player) {
//			player = (Player) event.getPlayer();
//		}
//		else {
//			return;
//		}
//		
//		// get inventory holder
//		// could be a DoubleChest (which is not a block)
//		InventoryHolder holder = event.getInventory().getHolder();
//		
//		if(holder instanceof DoubleChest) {
//			// holder is a DoubleChest
//			// get left and right side blocks
//			DoubleChest chest = (DoubleChest) holder;
//			Block left = getBlock(chest.getLeftSide());
//			Block right = getBlock(chest.getRightSide());
//			
//			// handle left and right side seperately
//			onContainerBlockOpen(player, right, event);
//			onContainerBlockOpen(player, left, event);
//		}
//		else if(holder instanceof BlockState) {
//			// holder not a DoubleChest
//			// must be a normal container block
//			Block block = getBlock(event.getInventory().getHolder());
//			onContainerBlockOpen(player, block, event);
//		}
//		else {
//			// this inventory does not belong to a block or double chest.
//			return;
//		}
//	}
//	
//	private Block getBlock(InventoryHolder holder) {
//		// convert an InventoryHolder to Block
//		// (impossible for DoubleChest and others like HumanEntity)
//		try {
//			return ((BlockState)holder).getBlock();
//		} catch(ClassCastException e) {
//			return null;
//		}
//	}
//	


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
			return humanEntity.hasPermission(Permission.ACCESS_UNLIMITED.getNode());
		}
		else {
			return humanEntity.hasPermission(Permission.ACCESS_TREASURE.getNode());
		}
	}
	
	
}
