package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts.SelectRegionPrompt;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class TeleportRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public TeleportRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
		
		Listener listener = new TeleportRewardListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	@Override
	public String getLabel() {
		return "tp";
	}

	@Override
	public String getGeneralDescription() {
		return "teleport to some location";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new TeleportReward(this, info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
			return;
		}
		
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
			return;
		}
		
		Player player = (Player) sender;
		
		
		
		WorldEditPlugin worldEdit;
		
		Plugin we = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if(we != null && we instanceof WorldEditPlugin) {
			worldEdit = (WorldEditPlugin) we;
		}
		else {
			worldEdit = null;
		}
		
		Prompt firstPrompt = new SelectRegionPrompt(worldEdit) {
			
			@Override
			protected Prompt onFinish(ConversationContext context, World world,
					Vector min, Vector max) {
				
				context.getForWhom().sendRawMessage(ChatColor.GREEN + "Selected region " + ChatColor.WHITE + "(" + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + ") (" + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + ")" + ChatColor.GREEN + " in world " + ChatColor.WHITE + "\"" + world.getName() + "\"" + ChatColor.GREEN + ".");
				
				context.setSessionData("reward", new TeleportReward(TeleportRewardFactory.this, world, min, max, 10));
				
				return new ValidatingPrompt() {
					
					@Override
					public String getPromptText(ConversationContext context) {
						return ChatColor.GOLD + "> After how many seconds should the player teleport?";
					}
					
					@Override
					protected boolean isInputValid(ConversationContext context, String input) {
						if(input.startsWith("/")) {
							Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
							return false;
						}
						else if(input.equalsIgnoreCase("CANCEL")) {
							return true;
						}
						else {
							int delay;
							try {
								delay = Integer.parseInt(input);
							} catch(NumberFormatException e) {
								context.getForWhom().sendRawMessage(ChatColor.RED + "Expected a number, instead of text.");
								return false;
							}
							context.setSessionData("delay", delay);
							return true;
						}
					}
					
					@Override
					protected Prompt acceptValidatedInput(ConversationContext context, String input) {
						if(input.equalsIgnoreCase("CANCEL")) {
							context.getForWhom().sendRawMessage(ChatColor.RED + "Cancelled delay input.");
							return END_OF_CONVERSATION;
						}
						else {
							int delay = (Integer) context.getSessionData("delay");
							TeleportReward reward = (TeleportReward) context.getSessionData("reward");
							reward.setDelay(delay);
							
							callback.onCreate(sender, args, reward);
							
							return END_OF_CONVERSATION;
						}
					}
				};
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				context.getForWhom().sendRawMessage(ChatColor.RED + "Cancelled region selection.");
				return END_OF_CONVERSATION;
			}
		};
		
		Map<Object, Object> data = new HashMap<Object, Object>();
		
		new ConversationFactory(plugin)
		.withFirstPrompt(firstPrompt)
		.withInitialSessionData(data)
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation(player)
		.begin();
	}

	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"The plugin will ask you for ", 
				"a region and a delay in seconds."
		};
	}
	
	
	
	void teleport(Player player, TeleportReward reward) {
		Location loc = getNextRandomLocation(reward.getWorld(), reward.getMin(), reward.getMax(), new Random());
		TeleportTimer timer = new TeleportTimer(player, loc, reward.getDelay());
		timers.put(player.getName(), timer);
		timer.schedule();
	}
	
	private void onPlayerLeave(PlayerEvent event) {
		TeleportTimer timer = timers.remove(event.getPlayer().getName());
		if(timer != null) {
			timer.run();
		}
	}
	
	void onPlayerQuit(PlayerQuitEvent event) {
		onPlayerLeave(event);
	}
	
	void onPlayerKick(PlayerKickEvent event) {
		onPlayerLeave(event);
	}
	
	private final HashMap<String, TeleportTimer> timers = new HashMap<String, TeleportTimer>();
	
	private class TeleportTimer implements Runnable {

		private Player player;
		private Location location;
		private int delay;

		private int taskId = -1;
		
		public TeleportTimer(Player player, Location location, int delay) {
			this.player = player;
			this.location = location;
			this.delay = delay;
		}
		
		public boolean isRunning() {
			return taskId != -1;
		}
		
		@Override
		public void run() {
			cancel();
			player.teleport(location);
		}
		
		public void schedule() {
			if(isRunning()) {
				return;
			}
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, this, delay * 20);
		}
		
		public void cancel() {
			if(!isRunning()) {
				return;
			}
			plugin.getServer().getScheduler().cancelTask(taskId);
			taskId = -1;
		}
		
	}

	private Location getNextRandomLocation(World world, Vector min, Vector max, Random random) {
		int x = random.nextInt(max.getBlockX() - min.getBlockX() + 1) - 1 + min.getBlockX();
		int y = min.getBlockY() + 1;
		int z = random.nextInt(max.getBlockZ() - min.getBlockZ() + 1) - 1 + min.getBlockZ();
		int yaw = random.nextInt(360);
		Block block = new Location(world, x, y, z).getBlock();
		Block above = block.getRelative(0, 1, 0);
		while(!block.isEmpty() && !above.isEmpty()) {
			block = above;
			above = block.getRelative(0, 1, 0);
		}
		Location loc = block.getLocation();
		loc.setYaw(yaw);
		return loc;
	}
}
