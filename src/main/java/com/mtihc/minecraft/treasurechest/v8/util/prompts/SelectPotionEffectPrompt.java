package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public abstract class SelectPotionEffectPrompt extends ValidatingPrompt {

	public SelectPotionEffectPrompt() {
		
	}
	
	protected abstract Prompt onFinish(ConversationContext context, PotionEffect effect);
	protected abstract Prompt onCancel(ConversationContext context);

	@Override
	public String getPromptText(ConversationContext context) {
		String list = "";
		PotionEffectType[] types = PotionEffectType.values();
		for (PotionEffectType type : types) {
			if(type == null) {
				continue;
			}
			list += ", " + type.getName().toLowerCase();
		}
		if(!list.isEmpty()) {
			list = list.substring(2);
		}
		else {
			list = ChatColor.RED + "There were no potion effects found.";
		}
		
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Select a potion effect:");
		context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Or type " + ChatColor.WHITE + "CANCEL" + ChatColor.GOLD + " to stop.");
		return list;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		
		if(input.startsWith("/")) {
			Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
			return false;
		}
		
		if(input.equalsIgnoreCase("CANCEL")) {
			return true;
		}
		
		PotionEffectType type = PotionEffectType.getByName(input);
		if(type == null) {
			context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid potion effect \"" + input + "\".");
			return false;
		}
		
		context.setSessionData("type", type);
		return true;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		if(input.equalsIgnoreCase("CANCEL")) {
			return onCancel(context);
		}
		PotionEffectType type = (PotionEffectType) context.getSessionData("type");
		context.getForWhom().sendRawMessage(ChatColor.GREEN + "Potion effect " + ChatColor.WHITE + "\"" + type.getName().toLowerCase() + "\"" + ChatColor.GREEN + " selected.");
		return new ValidatingPrompt() {
			
			@Override
			public String getPromptText(ConversationContext context) {
				
				return ChatColor.GOLD + "> What is the duration, in seconds?";
			}
			
			@Override
			protected boolean isInputValid(ConversationContext context, String input) {
				if(input.startsWith("/")) {
					Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
					return false;
				}
				
				int duration;
				try {
					duration = Integer.parseInt(input);
				} catch(NumberFormatException e) {
					context.getForWhom().sendRawMessage(ChatColor.RED + "Expected a number, instead of text.");
					return false;
				}
				
				context.setSessionData("duration", duration);
				return true;
				
			}
			
			@Override
			protected Prompt acceptValidatedInput(ConversationContext context, String input) {
				context.getForWhom().sendRawMessage(ChatColor.GREEN + "Duration set to " + ChatColor.WHITE + ((Integer) context.getSessionData("duration")) + ChatColor.GREEN + " seconds.");
				
				return new ValidatingPrompt() {
					
					@Override
					public String getPromptText(ConversationContext context) {
						return ChatColor.GOLD + "> How strong is the effect? (default 1)";
					}
					
					@Override
					protected boolean isInputValid(ConversationContext context, String input) {
						if(input.startsWith("/")) {
							Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
							return false;
						}
						
						int amplifier;
						try {
							amplifier = Integer.parseInt(input);
						} catch(NumberFormatException e) {
							context.getForWhom().sendRawMessage(ChatColor.RED + "Expected a number, instead of text.");
							return false;
						}
						
						context.setSessionData("amplifier", amplifier);
						return true;
					}
					
					@Override
					protected Prompt acceptValidatedInput(ConversationContext context, String input) {
						PotionEffectType type = (PotionEffectType) context.getSessionData("type");
						int duration = ((Integer) context.getSessionData("duration") * 20);
						int amplifier = (Integer) context.getSessionData("amplifier");
						PotionEffect effect = type.createEffect((int) (duration / type.getDurationModifier()), amplifier);
						
						return onFinish(context, effect);
					}
				};
			}
		};
	}

}
