package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactoryManager;

public abstract class SelectRewardTypePrompt extends ValidatingPrompt {

	private RewardFactoryManager manager;

	public SelectRewardTypePrompt(RewardFactoryManager manager) {
		this.manager = manager;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		String[] types = manager.getFactoryLabels();
		String typeString = "";
		for (String type : types) {
			typeString += ", " + type;
		}
		if (!typeString.isEmpty()) {
			typeString = typeString.substring(2);
		} else {
			return ChatColor.RED + "There are no registered reward factories.";
		}
		context.getForWhom().sendRawMessage(
				ChatColor.YELLOW + getMessage(context));
		return typeString;
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if (manager.getFactoryTotal() == 0) {
			// make sure we don't get stuck in the conversation, when there are
			// no factories
			return true;
		}

		if (input.startsWith("/")) {
			Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input);
			return false;
		}
		if (manager.hasFactory(input)) {
			return true;
		} else {
			context.getForWhom().sendRawMessage(
					ChatColor.RED + "Reward type \"" + input
							+ "\" doesn't exist.");
			return false;
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context,
			String input) {
		if (manager.getFactoryTotal() == 0) {
			// make sure we don't get stuck in the conversation, when there are
			// no factories
			return END_OF_CONVERSATION;
		}

		RewardFactory f = manager.getFactory(input);

		return onFinish(context, f);
	}

	protected abstract Prompt onFinish(ConversationContext context,
			RewardFactory factory);

	protected abstract String getMessage(ConversationContext context);
}