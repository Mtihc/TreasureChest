package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.util.prompts.SelectPotionEffectPrompt;

public class PotionRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public PotionRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getLabel() {
		return "potion";
	}

	@Override
	public String getGeneralDescription() {
		return "add some potion effect";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new PotionReward(info);
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
		
		Prompt firstPrompt = new SelectPotionEffectPrompt() {
			
			@Override
			protected Prompt onFinish(ConversationContext context, PotionEffect effect) {
				callback.onCreate(sender, args, new PotionReward(effect));
				return END_OF_CONVERSATION;
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Cancelled potion effect selection."));
				return END_OF_CONVERSATION;
			}
		};
		
		new ConversationFactory(plugin)
		.withFirstPrompt(firstPrompt)
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
				"The plugin will ask you which effect, ",
				"what the duration will be ",
				"and how strong the effect will be."
		};
	}

}
