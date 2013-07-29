package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts.StringListPrompt;

public class BroadcastRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public BroadcastRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getLabel() {
		return "broadcast";
	}

	@Override
	public String getGeneralDescription() {
		return "broadcast a message";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new BroadcastReward(info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
			return;
		}
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
			return;
		}
		
		Prompt prompt = new StringListPrompt() {
			
			@Override
			protected void onLineAdd(ConversationContext context, int lineNumber, String line) {
				context.getForWhom().sendRawMessage(ChatColor.GREEN + line);
			}
			
			@Override
			protected Prompt onFinish(ConversationContext context,
					ArrayList<String> result) {
				BroadcastReward reward = new BroadcastReward(result);
				callback.onCreate(sender, args, reward);
				return END_OF_CONVERSATION;
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Cancelled message input."));
				return END_OF_CONVERSATION;
			}
		};
		new ConversationFactory(plugin)
		.withFirstPrompt(prompt)
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation((Player) sender)
		.begin();
	}

	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"You will be asked to type lines of text.", 
				"You can use @p in your message. It will automatically ", 
				"be replaced with the player's name."
		};
	}

}
