package com.mtihc.minecraft.treasurechest.events;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.Permission;
import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.persistance.TChestCollection;
import com.mtihc.minecraft.treasurechest.persistance.TreasureChest;

public class ChestOpenListener implements Listener{

	
	private TreasureChestPlugin plugin;
	
	public ChestOpenListener(TreasureChestPlugin plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerInteract(final PlayerInteractEvent event) {
		
		TreasureChest chest;
		
		// get TreasureChest object
		try {
			String id = TChestCollection.getChestId(event.getClickedBlock().getLocation());
			chest = plugin.getChests().values().getChest(id);
		} catch(NullPointerException e) {
			chest = null;
		}
		
		if(chest == null) {
			return;
		}
		
		
		
		
		// check permission
		if(!allowAccess(event.getPlayer(), chest.isUnlimited())) {
			event.getPlayer().sendMessage(ChatColor.RED + "You don't have permission to access this Treasure Chest.");
			event.setUseInteractedBlock(Result.DENY);
			return;
		}
		
		// undo protection by other plugins
		if(chest.ignoreProtection()) {
			event.setUseInteractedBlock(Result.ALLOW);
		}
		
	}
	
	public void onContainerBlockOpen(Player player, Block block, InventoryOpenEvent event) {
		// get treasure chest for block's location
		String id = TChestCollection.getChestId(block.getLocation());
		// try to get treasure chest
		TreasureChest tchest = plugin.getChests().values().getChest(id);
		
		
		if(tchest == null) {
			// not opening a treasure chest
			// we have nothing to do with this event
			return;
		}
		// opening a treasure chest
		
		
		// prevent players looking in the same chest
		// only for non-unlimited chests
		if(!tchest.isUnlimited() && event.getViewers().size() > 1) {
			player.sendMessage(ChatColor.RED + "Somebody else is already looking in this Treasure Chest.");
			player.sendMessage(ChatColor.RED + "Please give him/her a minute.");
			return;
		}
		
		
		
		if(tchest.isUnlimited()) {
			// chest is unlimited
			// reset items
			tchest.toInventoryHolder((InventoryHolder)block.getState());
			// message
			String message = tchest.getMessage(TreasureChest.Message.FOUND_UNLIMITED);
			if(message != null) {
				player.sendMessage(ChatColor.GOLD + message);
			}
		}
		else {
			// chest is not-unlimited
			// when has player found before
			long time = plugin.getMemory().whenHasPlayerFound((OfflinePlayer)player, id);
			
			if(time != 0 && !hasForgotten(time, tchest.getForgetTime())) {
				// already found and not forgotten
				
				// message
				String alreadyFoundMessage = tchest.getMessage(TreasureChest.Message.FOUND_ALREADY);
				if(alreadyFoundMessage != null) {
					player.sendMessage(ChatColor.GOLD + alreadyFoundMessage);
				}
				
			}
			else {
				// found for the first time (or after forget time)
				// remember that this player found it
				plugin.getMemory().rememberPlayerFound((OfflinePlayer)player, id);
				
				// set items to chest
				tchest.toInventoryHolder((InventoryHolder)block.getState());
				
				// message
				String foundMessage = tchest.getMessage(TreasureChest.Message.FOUND);
				if(foundMessage != null) {
					player.sendMessage(ChatColor.GOLD + foundMessage);
				}
				
			}
		}
		return;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.event.player.PlayerListener#onPlayerInteract(org.bukkit.event.player.PlayerInteractEvent)
	 */
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryOpen(final InventoryOpenEvent event) {
		// get player
		Player player;
		if(event.getPlayer() instanceof Player) {
			player = (Player) event.getPlayer();
		}
		else {
			return;
		}
		
		// get inventory holder
		// could be a DoubleChest (which is not a block)
		InventoryHolder holder = event.getInventory().getHolder();
		
		if(holder instanceof DoubleChest) {
			// holder is a DoubleChest
			// get left and right side blocks
			DoubleChest chest = (DoubleChest) holder;
			Block left = getBlock(chest.getLeftSide());
			Block right = getBlock(chest.getRightSide());
			
			// handle left and right side seperately
			onContainerBlockOpen(player, right, event);
			onContainerBlockOpen(player, left, event);
		}
		else if(holder instanceof BlockState) {
			// holder not a DoubleChest
			// must be a normal container block
			Block block = getBlock(event.getInventory().getHolder());
			onContainerBlockOpen(player, block, event);
		}
		else {
			// this inventory does not belong to a block or double chest.
			return;
		}
	}
	
	private Block getBlock(InventoryHolder holder) {
		// convert an InventoryHolder to Block
		// (impossible for DoubleChest and others like HumanEntity)
		try {
			return ((BlockState)holder).getBlock();
		} catch(ClassCastException e) {
			return null;
		}
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
			return humanEntity.hasPermission(Permission.ACCESS_UNLIMITED.getNode());
		}
		else {
			return humanEntity.hasPermission(Permission.ACCESS_TREASURE.getNode());
		}
	}
	
	
}
