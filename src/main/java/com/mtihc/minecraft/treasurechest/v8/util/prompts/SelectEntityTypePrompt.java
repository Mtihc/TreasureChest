package com.mtihc.minecraft.treasurechest.v8.util.prompts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.EntityType;

public abstract class SelectEntityTypePrompt extends ValidatingPrompt {

	protected abstract Prompt onFinish(ConversationContext context,
			EntityType type);

	protected abstract Prompt onCancel(ConversationContext context);

	@Override
	public String getPromptText(ConversationContext context) {
		String mobs = "";
		EntityType[] values = EntityType.values();
		int i = 0;
		ChatColor color;
		for (EntityType type : values) {
			if (type.isSpawnable()) {
				
				if(i % 2 == 0) {
					color = ChatColor.GRAY;
				}
				else {
					color = ChatColor.WHITE;
				}
				
				mobs += ", " + type.toString() + color;	//attempted update
				i++;
			}
		}
		mobs = mobs.substring(2);

		context.getForWhom().sendRawMessage(
				ChatColor.GOLD + "> Select a mob type:");
		context.getForWhom().sendRawMessage(mobs);
		return ChatColor.GOLD + "> Type a mob name. Or type " + ChatColor.WHITE
				+ "CANCEL" + ChatColor.GOLD + " to stop.";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context,
			String input) {
		if (input.equalsIgnoreCase("CANCEL")) {
			return onCancel(context);
		} else {
			return onFinish(context,
					(EntityType) context.getSessionData("type"));
		}
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if (input.startsWith("/")) {
			Bukkit.dispatchCommand((CommandSender) context.getForWhom(),
					input.substring(1));
			return false;
		} else if (input.equalsIgnoreCase("CANCEL")) {
			return true;
		} else {
			EntityType type = EntityType.valueOf(input); 	//attempted update
			Bukkit.getLogger().info("name " + EntityType.EXPERIENCE_ORB.toString() + " input " + input + " are equal " + EntityType.EXPERIENCE_ORB.toString().equals(input));
			if (type == null || !type.isSpawnable()) {
				context.getForWhom().sendRawMessage(ChatColor.RED + 
						"Not a valid type: \"" + input + "\"");
				return false;
			}
			context.setSessionData("type", type);
			return true;
		}
	}

}