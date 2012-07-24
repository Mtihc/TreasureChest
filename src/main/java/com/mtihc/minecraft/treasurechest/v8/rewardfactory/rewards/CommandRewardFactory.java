package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards.CommandReward.Type;

public class CommandRewardFactory extends RewardFactory {

	private JavaPlugin plugin;

	public CommandRewardFactory(JavaPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public String getLabel() {
		return "command";
	}

	@Override
	public String getGeneralDescription() {
		return "execute some command";
	}

	@Override
	public IReward createReward(RewardInfo info) throws RewardException {
		return new CommandReward(info);
	}

	@Override
	public void createReward(final CommandSender sender, final String[] args,
			final CreateCallback callback) {
		
		if(args != null && args.length > 0) {
			callback.onCreateException(sender, args, new RewardException("Expected no arguments."));
			return;
		}
		
		if(!(sender instanceof Player)) {
			callback.onCreateException(sender, args, new RewardException("Expected a player."));
			return;
		}
		
		Player player = (Player) sender;
		
		
		Prompt firstPrompt = new StringPrompt() {
			
			@Override
			public String getPromptText(ConversationContext context) {
				return ChatColor.GOLD + "> Type the command, start with a forward-slash (/):";
			}
			
			@Override
			public Prompt acceptInput(ConversationContext context, String input) {
				context.setSessionData("command-line", input);
				
				return new ValidatingPrompt() {
					
					@Override
					public String getPromptText(ConversationContext context) {
						context.getForWhom().sendRawMessage(ChatColor.GOLD + "> How do you want the command to be executed?");
						context.getForWhom().sendRawMessage(ChatColor.GOLD + "> " + ChatColor.WHITE + Type.NORMAL.name() + ChatColor.GOLD + " Execute normally");
						context.getForWhom().sendRawMessage(ChatColor.GOLD + "> " + ChatColor.WHITE + Type.OP.name() + ChatColor.GOLD + " Make the player OP for a sec.");
						context.getForWhom().sendRawMessage(ChatColor.GOLD + "> " + ChatColor.WHITE + Type.CONSOLE.name() + ChatColor.GOLD + " Command doesn't need a player.");
						return ChatColor.GOLD + "> Type one of the options above.";
					}
					
					@Override
					protected boolean isInputValid(ConversationContext context, String input) {
						if(input.startsWith("/")) {
							Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
							return false;
						}
						
						Type[] types = Type.values();
						for (Type type : types) {
							if(input.equalsIgnoreCase(type.name())) {
								context.setSessionData("type", type);
								return true;
							}
						}
						context.getForWhom().sendRawMessage(ChatColor.RED + "Invalid input \"" + input + "\".");
						return false;
					}
					
					@Override
					protected Prompt acceptValidatedInput(ConversationContext context, String input) {
						String commandLine = (String) context.getSessionData("command-line");
						Type type = (Type) context.getSessionData("type");
						
						callback.onCreate(sender, args, new CommandReward(commandLine, type));
						return END_OF_CONVERSATION;
					}
				};
			}
		};
		
		new ConversationFactory(plugin)
		.withFirstPrompt(firstPrompt)
		.withLocalEcho(false)
		.withModality(false)
		.buildConversation(player)
		.begin();
	}

	@Override
	public String args() {
		return "";
	}

	@Override
	public String[] help() {
		return new String[] {
				"The plugin will ask you for a command line.", 
				"And whether the command should be executed ",
				"normally, or make the player OP for a second, ",
				"or let the command be executed in console."
		};
	}

}
