package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.RestoreTaskQueue.Observer;
import com.mtihc.minecraft.treasurechest.v8.util.prompts.SelectRegionPrompt;
import com.mtihc.minecraft.treasurechest.v8.util.prompts.SelectSnapshotPrompt;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class RestoreRewardFactory extends RewardFactory implements Observer {

	private JavaPlugin plugin;
	private WorldEditPlugin worldEdit;
	private RestoreTaskQueue queue;
	private long subregionTicks = 10;
	private int subregionSize = 50;

	public RestoreRewardFactory(JavaPlugin plugin, int subregionTicks, int subregionSize) {
		this.plugin = plugin;
		this.subregionTicks = subregionTicks;
		this.subregionSize = subregionSize;

		this.queue = new RestoreTaskQueue(this);
		
	}
	
	public WorldEditPlugin getWorldEdit() {
		return worldEdit;
	}
	
	public boolean hasWorldEdit() {
		return worldEdit != null && worldEdit.isEnabled();
	}

	@Override
	public String args() {
		return "";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		if(!hasWorldEdit()) {
			throw new RewardException("WorldEdit was not found.");
		}
		return new RestoreReward(this, info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		if(!hasWorldEdit()) {
			callback.onCreateException(sender, args, new RewardException("WorldEdit was not found."));
			return;
		}
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Only a player can select a region."));
			return;
		}
		
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
			return;
		}
		
		new ConversationFactory(plugin)
		.thatExcludesNonPlayersWithMessage(ChatColor.RED + "Only a player can select a region.")
		.withFirstPrompt(new SelectSnapshotPrompt(worldEdit) {

			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Snapshot selection cancelled."));
				return END_OF_CONVERSATION;
			}

			@Override
			protected Prompt onSnapshotSelect(ConversationContext context, final Snapshot snapshot) {
				
				context.getForWhom().sendRawMessage(ChatColor.GREEN + "> Snapshot " + ChatColor.WHITE + snapshot.getName() + ChatColor.GREEN + " selected.");
				
				return new SelectRegionPrompt(worldEdit) {
					
					@Override
					protected Prompt onCancel(ConversationContext context) {
						callback.onCreateException(sender, args, new RewardException("Region selection cancelled."));
						return END_OF_CONVERSATION;
					}

					@Override
					protected Prompt onFinish(ConversationContext context,
							World world, Vector min, Vector max) {
						String snapshotName = snapshot.getName();
						RestoreReward reward = new RestoreReward(RestoreRewardFactory.this, snapshotName, world.getName(), min, max);
						callback.onCreate(sender, args, reward);
						return END_OF_CONVERSATION;
					}
				};
			}
			
		})
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation((Player) sender)
		.begin();
		
	}

	@Override
	public String getGeneralDescription() {
		return "restore a region";
	}

	@Override
	public String getLabel() {
		return "restore";
	}

	@Override
	public String[] help() {
		return new String[] {
				"The plugin will ask you to select a snapshot and a region."
		};
	}
	
	

	
	void restore(Player player, RestoreReward reward) throws RewardException {
		if(!hasWorldEdit()) {
			throw new RewardException("WorldEdit was not found.");
		}
		String worldName = reward.getWorldName();
		Vector min = reward.getMin();
		Vector max = reward.getMax();
		String snapshot = reward.getSnapshotName();
		
		String id = getTaskId(worldName, min, max, snapshot);
		if(!queue.has(id)) {
			queue.add(id, plugin, snapshot, worldName, min, max, subregionTicks, subregionSize);
		}
		
	}
	
	private String getTaskId(String worldName, Vector min, Vector max, String snapshot) {
		return "[RestoreTask " + "world=\"" + worldName + "\" min=\"" + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + "\" max=\"" + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + "\" snapshot=\"" + snapshot + "\"" + "]";
	}

	@Override
	public void onRestoreAddToQueue(RestoreTask restore) {
		plugin.getLogger().info("Added to queue: " + restore.getId());
	}
     
	@Override
	public void onRestoreStart(RestoreTask restore) {
		plugin.getLogger().info("Started: " + restore.getId());
	}

	@Override
	public void onRestoreFinish(RestoreTask restore) {
		plugin.getLogger().info("Finished: " + restore.getId());
	}

	@Override
	public void onRestoreCancel(RestoreTask restore) {
		plugin.getLogger().info("Cancelled: " + restore.getId());
	}
}
