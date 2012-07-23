package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;
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
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts.SelectBlockPrompt;

public class ExplosionRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public ExplosionRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String args() {
		return "[power]";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new ExplosionReward(info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
			return;
		}
		
		
		int p;
		try {
			p = Integer.parseInt(args[0]);
		} catch(NullPointerException e) {
			p = 4;
		} catch(IndexOutOfBoundsException e) {
			p = 4;
		} catch(NumberFormatException e) {
			callback.onCreateException(sender, args, new RewardException("Expected explosion power, instead of text."));
			return;
		}
		

		
		if(args.length > 1) {
			callback.onCreateException(sender, args, new RewardException("Expected only the explosion power."));
			return;
		}
		
		final int power = p;
		
		new ConversationFactory(plugin)
		.withFirstPrompt(new SelectBlockPrompt(null, null, null, null) {
			
			
			
			/* (non-Javadoc)
			 * @see com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.SelectBlockPrompt#getPromptText(org.bukkit.conversations.ConversationContext)
			 */
			@Override
			public String getPromptText(ConversationContext context) {
				context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Select the location to blow up!");
				return super.getPromptText(context);
			}

			@Override
			protected Prompt onFinish(ConversationContext context, Block block) {
				callback.onCreate(sender, args, new ExplosionReward(block.getLocation(), power));
				return END_OF_CONVERSATION;
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Cancelled location selection."));
				return END_OF_CONVERSATION;
			}
		})
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation((Player) sender)
		.begin();
		
		
	}
	

	@Override
	public String getGeneralDescription() {
		return "an explosion of some power";
	}

	@Override
	public String getLabel() {
		return "explosion";
	}

	@Override
	public String[] help() {
		return new String[] {
				"Specify the explosion power (default 4)"
		};
	}

}
