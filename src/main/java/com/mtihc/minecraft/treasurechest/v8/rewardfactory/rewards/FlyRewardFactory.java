package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class FlyRewardFactory extends RewardFactory {

	private final JavaPlugin plugin;
	private final Map<String, FlyTimer> active = new HashMap<String, FlyTimer>();
	
	public FlyRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
		
		Listener listener = new FlyListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	@Override
	public String getLabel() {
		return "fly";
	}

	@Override
	public String getGeneralDescription() {
		return "allow flight for some time";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new FlyReward(this, info);
	}

	@Override
	public void createReward(CommandSender sender, String[] args,
			CreateCallback callback) {
		int seconds;
		try {
			seconds = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			callback.onCreateException(sender, args, new RewardException("Not enough arguments. Expected an amount of seconds."));
			return;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected an amount of seconds, instead of text."));
			return;
		}
		if(seconds < 1) {
			callback.onCreateException(sender, args, new RewardException("Expected an amount of seconds, larger than zero."));
			return;
		}

		callback.onCreate(sender, args, new FlyReward(this, seconds));
		
	}

	@Override
	public String args() {
		return "seconds";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify how many seconds the player can fly."
		};
	}

	
	
	
	
	
	
	protected void startFlight(Player player, FlyReward flyReward) {
		new FlyTimer(player).schedule(flyReward.getSeconds());
	}

	protected void cancelFlight(Player player) {
		FlyTimer timer = active.remove(player.getName());
		if(timer != null) {
			timer.cancel();
		}
	}
	
	protected void cancelAllFlight() {
		Iterator<FlyTimer> it = active.values().iterator();
		while(it.hasNext()) {
			FlyTimer timer = it.next();
			it.remove();
			timer.cancel();
		}
	}
	
	private class FlyTimer implements Runnable {

		private int taskId;
		private Player player;
		private boolean originalAllowFlight;

		private FlyTimer(Player player) {
			taskId = -1;
			this.player = player;
			this.originalAllowFlight = player.getAllowFlight();
		}
		
		public boolean isRunning() {
			return taskId != -1;
		}
		
		public void schedule(int seconds) {
			// reschedule without changing anything
			doCancel();
			taskId = plugin.getServer().getScheduler().scheduleAsyncDelayedTask(plugin, this, seconds * 20L);

			player.setAllowFlight(true);
			player.setFlying(true);
			active.put(player.getName(), this);
		}
		
		private boolean doCancel() {
			if(isRunning()) {
				plugin.getServer().getScheduler().cancelTask(taskId);
				taskId = -1;
				return true;
			}
			else {
				return false;
			}
		}
		
		@Override
		public void run() {
			cancel();
		}

		
		public boolean cancel() {
			if(doCancel()) {
				// stop flying

				player.setFlying(false);
				player.setAllowFlight(originalAllowFlight);
				active.remove(player.getName());
				return true;
			}
			else {
				return false;
			}
		}
	}

}
