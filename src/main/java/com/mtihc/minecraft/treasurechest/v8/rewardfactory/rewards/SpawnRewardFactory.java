package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts.SelectEntityTypePrompt;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts.SelectRegionPrompt;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class SpawnRewardFactory extends RewardFactory {

	private JavaPlugin plugin;
	private WorldEditPlugin worldEdit;

	public SpawnRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
		
		Plugin we = plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		if(we != null && we instanceof WorldEditPlugin) {
			worldEdit = (WorldEditPlugin) we;
		}
		else {
			worldEdit = null;
		}
	}

	@Override
	public String getLabel() {
		return "spawn";
	}

	@Override
	public String getGeneralDescription() {
		return "spawn mobs in a region";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new SpawnReward(info);
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
		
		final SpawnReward reward = new SpawnReward(new RewardInfo(getLabel(), new HashMap<String, Object>()));
		
		
		Prompt prompt = new SelectEntityTypePrompt() {
			
			@Override
			protected Prompt onFinish(ConversationContext context, EntityType type) {
				reward.setEntityType(type);
				
				context.getForWhom().sendRawMessage(ChatColor.GREEN + "> Selected " + ChatColor.WHITE + type.name().toLowerCase().replace("_", " ") + ChatColor.GREEN + ".");
				
				return new SelectRegionPrompt(worldEdit) {
					
					@Override
					protected Prompt onFinish(ConversationContext context, World world,
							Vector min, Vector max) {
						reward.setRegion(world, min, max);
						
						context.getForWhom().sendRawMessage(ChatColor.GREEN + "> Selected region " + ChatColor.WHITE + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + " " + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + ChatColor.GREEN + " in " + ChatColor.WHITE + world.getName() + ChatColor.GREEN + ".");
						return new MobAmountPrompt() {

							@Override
							protected Prompt onFinish(
									ConversationContext context, int amount) {
								reward.setAmount(amount);
								callback.onCreate(sender, args, reward);
								return END_OF_CONVERSATION;
							}
							
						};
					}
					
					@Override
					protected Prompt onCancel(ConversationContext context) {
						callback.onCreateException(sender, args, new RewardException("Cancelled region selection."));
						return END_OF_CONVERSATION;
					}
				};
			}
			
			@Override
			protected Prompt onCancel(ConversationContext context) {
				callback.onCreateException(sender, args, new RewardException("Cancelled mob type selection."));
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
	
	
	private abstract class MobAmountPrompt extends ValidatingPrompt {

		protected abstract Prompt onFinish(ConversationContext context, int amount);
		
		@Override
		public String getPromptText(ConversationContext context) {
			
			return ChatColor.GOLD + "> How many mobs should spawn?";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context,
				String input) {
			int amount = (int) context.getSessionData("amount");
			return onFinish(context, amount);
		}

		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			if(input.startsWith("/")) {
				Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
				return false;
			}
			int amount;
			try {
				amount = Integer.parseInt(input);
			} catch(NumberFormatException e) {
				context.getForWhom().sendRawMessage(ChatColor.RED + "Expected a number instead of text.");
				return false;
			}
			
			context.setSessionData("amount", amount);
			return true;
		}
		
	}

	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"The plugin will ask you which mob.",
				"And how many. And in which region."
		};
	}

}
