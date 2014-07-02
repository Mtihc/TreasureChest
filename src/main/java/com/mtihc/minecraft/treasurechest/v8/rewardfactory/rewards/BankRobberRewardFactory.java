package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestFoundUnlimitedEvent;
import com.mtihc.minecraft.treasurechest.v8.events.TreasureChestInventoryEvent;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class BankRobberRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public BankRobberRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
		Listener listener = new BankRobberListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	@Override
	public String getLabel() {
		return "bankrobber";
	}

	@Override
	public String getGeneralDescription() {
		return "unlootable, until looter is dead";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new BankRobberReward(this, info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
			return;
		}
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
			return;
		}
		
		
		BankRobberReward reward = new BankRobberReward(this);
		callback.onCreate(sender, args, reward);
	}
	

	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"When this treasure is looted,",
				"the looter must be killed (or disconnect) ",
				"before it can be looted again."
		};
	}
	
	public boolean hasRobber(UUID playerID) { //UUID Upgrade
		return robbers.containsKey(playerID); 
	}
	
	public BankRobber getRobber(UUID playerID) { //UUID Upgrade
		BankRobber robber = robbers.get(playerID);
		if(robber == null) {
			return null;
		}
		return robber;
	}
	
	private final HashMap<String, BankRobber> robberByBank = new HashMap<String, BankRobber>();
	private final HashMap<UUID, BankRobber> robbers = new HashMap<UUID, BankRobber>(); //UUID Upgrade
	
	public class BankRobber {
		
		private UUID playerID; //store UUID rather than player pointer
		private Location location;
		private ITreasureChest tchest;
		
		private BankRobber(UUID playerID, Location location, ITreasureChest tchest) {
			this.playerID = playerID;
			this.location = location;
			this.tchest = tchest;
		}
		
		public Player getPlayer() { return plugin.getServer().getPlayer(playerID); }
		
		public Location getLocation() { return location; }
		
		public ITreasureChest getTreasureChest() { return tchest; }
	}


	private void onTreasureFound(TreasureChestInventoryEvent event) {
		Player player = event.getPlayer();
		Location loc = event.getLocation();
		String locString = loc.toString();
		
		BankRobber robber = robberByBank.get(locString);
		if(robber != null && !robber.playerID.equals(player.getUniqueId())) {
			player.sendMessage(ChatColor.RED + "This was robbed by " + ChatColor.WHITE + robber.getPlayer().getDisplayName() + ChatColor.RED + ".");
			((Cancellable) event).setCancelled(true);
			// TODO onAlreadyBankRobbed
			return;
		}
		
		ITreasureChest tchest = event.getTreasureChest();
		List<RewardInfo> rewards = tchest.getRewards();
		boolean isBank = false;
		for (RewardInfo info : rewards) {
			if(info.getLabel().equals(getLabel())) {
				isBank = true;
				break;
			}
		}
		if(!isBank) {
			return;
		}
		
		robber = new BankRobber(player.getUniqueId(), loc, event.getTreasureChest());
		
		robbers.put(event.getPlayer().getUniqueId(), robber);
		robberByBank.put(loc.toString(), robber);
		// TODO onBankRob
	}
	
	private void onTreasureDrop(OfflinePlayer player) {
		
		BankRobber robber = robbers.remove(player.getUniqueId());
		if(robber == null) {
			return;
		}
		robberByBank.remove(robber.location.toString());
		if(player.getPlayer() != null) {
			player.getPlayer().sendMessage(ChatColor.GOLD + "The treasure can be robbed by someone else now.");
		}
	}
	

	void onTreasureChestFound(TreasureChestFoundEvent event) {
		onTreasureFound(event);
	}

	void onTreasureChestFoundUnlimited(TreasureChestFoundUnlimitedEvent event) {
		onTreasureFound(event);
	}


	private HashMap<UUID, ReconnectTimer> reconnecters = new HashMap<UUID, ReconnectTimer>(); //UUID
	
	void onPlayerJoin(PlayerJoinEvent event) {
		ReconnectTimer timer = reconnecters.remove(event.getPlayer().getUniqueId()); //UUID
		if(timer != null) {
			timer.cancel();
		}
	}
	
	void onPlayerQuit(PlayerQuitEvent event) {
		UUID playerID = event.getPlayer().getUniqueId(); //UUID
		if(robbers.containsKey(playerID)) {
			ReconnectTimer timer = new ReconnectTimer(playerID);
			reconnecters.put(playerID, timer);
			timer.schedule();
		}
	}

	void onPlayerKick(PlayerKickEvent event) {
		onTreasureDrop(event.getPlayer());
	}

	void onPlayerDeath(PlayerDeathEvent event) {
		onTreasureDrop(event.getEntity());
	}

	private class ReconnectTimer implements Runnable {

		private int taskId = -1;
		private UUID playerID;

		public ReconnectTimer(UUID playerID) {
			this.playerID = playerID;
		}
		
		@Override
		public void run() {
			cancel();
			
			onTreasureDrop(Bukkit.getOfflinePlayer(playerID));
		}
		
		public boolean isRunning() {
			return taskId != -1;
		}

		public void schedule() {
			if(isRunning()) {
				return;
			}
			BukkitTask task = plugin.getServer().getScheduler().runTaskLaterAsynchronously(plugin, this, 1200L);
			taskId = task.getTaskId();
		}

		public void cancel() {
			if(!isRunning()) {
				return;
			}
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
	}
}
