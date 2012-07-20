package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.prompts;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;

public abstract class StringListPrompt extends ValidatingPrompt {

	public StringListPrompt() {
		
	}

	@Override
	public String getPromptText(ConversationContext context) {
		int line;
		try {
			line = (int) context.getSessionData("line");
		} catch(NullPointerException e) {
			line = 0;
			context.setSessionData("line", line);
		}
		if(line == 0) {
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Type the first line.");
		}
		else {
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Now, type line " + (line + 1) + ".");
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Or type " + ChatColor.WHITE + "OK" + ChatColor.GOLD + " if you're done.");
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Or type " + ChatColor.WHITE + "BACK" + ChatColor.GOLD + " to change the previous line.");
		}
		return (ChatColor.GOLD + "> Or type " + ChatColor.WHITE + "CANCEL" + ChatColor.GOLD + " to stop.");
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
		else if(input.equalsIgnoreCase("OK")) {
			int line = (int) context.getSessionData("line");
			if(line == 0) {
				context.getForWhom().sendRawMessage(ChatColor.RED + "You didn't type anything yet.");
				return false;
			}
			return true;
		}
		else if(input.equalsIgnoreCase("BACK")) {
			int line = (int) context.getSessionData("line");
			if(line > 0) {
				line--;
				getList(context).remove(line);
				context.setSessionData("line", line);
			}
			return false;
		}
		else {
			ArrayList<String> list = getList(context);
			list.add(input);
			onLineAdd(context, list.size(), input);
			int line = (int) context.getSessionData("line");
			context.setSessionData("line", line + 1);
			return false;
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		if(input.equalsIgnoreCase("OK")) {
			return onFinish(context, getList(context));
		}
		else {
			return onCancel(context);
		}
	}
	
	public ArrayList<String> getList(ConversationContext context) {
		@SuppressWarnings("unchecked")
		ArrayList<String> result = (ArrayList<String>) context.getSessionData("list");
		
		if(result == null) {
			result = new ArrayList<String>();
			context.setSessionData("list", result);
		}
		return result;
	}
	
	protected abstract Prompt onCancel(ConversationContext context);
	protected abstract Prompt onFinish(ConversationContext context, ArrayList<String> result);
	protected abstract void onLineAdd(ConversationContext context, int lineNumber, String line);
}
