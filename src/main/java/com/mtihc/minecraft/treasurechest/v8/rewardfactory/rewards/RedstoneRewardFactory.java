package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.material.RedstoneTorch;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.util.prompts.SelectBlockPrompt;

@SuppressWarnings("deprecation")
public class RedstoneRewardFactory extends RewardFactory {

	private JavaPlugin plugin;
	
	public RedstoneRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getLabel() {
		return "redstone";
	}

	@Override
	public String getGeneralDescription() {
		return "place a redstone torch somewhere";
	}
	
	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"The plugin will ask you to place a torch."
		};
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new RedstoneReward(info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
		}
		
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
		}
		
		new ConversationFactory(plugin)
		.withFirstPrompt(new SelectBlockPrompt(Material.LEGACY_REDSTONE_TORCH_ON, null, null, null) {
			
			@Override
			protected Prompt onFinish(ConversationContext context, Block block) {
				RedstoneTorch torch = (RedstoneTorch) Material.LEGACY_REDSTONE_TORCH_ON.getNewData(block.getData());
				RedstoneReward reward = new RedstoneReward(block.getRelative(torch.getAttachedFace()), torch.getFacing());
				callback.onCreate(sender, args, reward);
				return END_OF_CONVERSATION;
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Cancelled redstone torch selection."));
				return END_OF_CONVERSATION;
			}
		})
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation((Player) sender)
		.begin();
	}

}
