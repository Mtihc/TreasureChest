package com.mtihc.minecraft.treasurechest.v8.plugin;

import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactory.CreateCallback;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardFactoryManager;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;
import com.mtihc.minecraft.treasurechest.v8.util.commands.Command;
import com.mtihc.minecraft.treasurechest.v8.util.commands.CommandException;
import com.mtihc.minecraft.treasurechest.v8.util.commands.ICommand;
import com.mtihc.minecraft.treasurechest.v8.util.commands.SimpleCommand;
import com.mtihc.minecraft.treasurechest.v8.util.prompts.SelectRewardTypePrompt;

public class RewardCommand extends SimpleCommand {

	private TreasureManager manager;
	private RewardFactoryManager rewardManager;

	public RewardCommand(TreasureManager manager, ICommand parent) {
		super(parent, new String[]{"reward", "rewards"}, "", "Add/remove/list rewards.", null);
		this.manager = manager;
		this.rewardManager = manager.getRewardManager();
		
		
		addNested("add");
		addNested("remove");
		addNested("clear");
		addNested("list");
		addNested("types");
	}
	

	@Command(aliases = { "add" }, args = "", desc = "Add a reward.", help = { "" })
	public void add(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to edit rewards.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		final Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		final ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		if(rewardManager.getFactoryTotal() == 0) {
			throw new CommandException("There are no registered reward factories.");
		}
		
		if(args.length == 0) {
			
			new ConversationFactory(manager.getPlugin())
			.withFirstPrompt(new SelectRewardTypePrompt(rewardManager) {


				@Override
				protected String getMessage(ConversationContext context) {
					return "What kind of reward will it be?";
				}
				
				@Override
				protected Prompt onFinish(ConversationContext context,
						RewardFactory f) {
					
					context.getForWhom().sendRawMessage(ChatColor.YELLOW + "> Use this command to create the reward:");
					
					String usage = "/" + getNested("add").getUniqueLabel() + " " + f.getLabel() + " " + f.args();
					context.getForWhom().sendRawMessage(ChatColor.YELLOW + "> Command: " + ChatColor.WHITE + usage);
					
					String[] help = f.help();
					if(help != null && help.length > 0) {
						context.getForWhom().sendRawMessage(ChatColor.YELLOW + "> Command help: " + ChatColor.WHITE + help[0]);
						for (int i = 1; i < help.length; i++) {
							context.getForWhom().sendRawMessage(ChatColor.WHITE + help[i]);
						}
					}
					
					return END_OF_CONVERSATION;
				}
				
			})
			.withLocalEcho(false)
			.withModality(false)
			.buildConversation((Player) sender)
			.begin();
			return;
		}
		
		String type = args[0];
		RewardFactory f = rewardManager.getFactory(type);
		if(f == null) {
			throw new CommandException("Reward type \"" + type + "\" does not exist.");
		}
		
		String[] newArgs;
		try {
			newArgs = Arrays.copyOfRange(args, 1, args.length);
		} catch(Exception e) {
			newArgs = new String[0];
		}
		
		f.createReward(sender, newArgs, new CreateCallback() {
			
			@Override
			public void onCreateException(CommandSender sender, String[] args, RewardException e) {
				sender.sendMessage(ChatColor.RED + "Failed to create reward. " + e.getMessage());
			}
			
			@Override
			public void onCreate(CommandSender sender, String[] args, IReward reward) {
				tchest.getRewards().add(reward.getInfo());
				manager.setTreasure(tchest);
				sender.sendMessage(ChatColor.GREEN + "Reward saved.");
			}
		});
	}
	
	@Command(aliases = { "remove" }, args = "<number>", desc = "Remove a reward.", help = { "The number corresponds to the number of the reward in the list."})
	public void remove(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to edit rewards.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		if(!tchest.hasRewards()) {
			throw new CommandException("There are no more rewards in that treasure.");
		}
		
		int number;
		try {
			number = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			number = -1;
		} catch(NumberFormatException e) {
			number = -1;
		}
		
		if(number < 1 || number > tchest.getRewardTotal()) {
			throw new CommandException("Expected a number between 1 and " + tchest.getRewardTotal() + ", representing the number of the reward in the list.");
		}
		
	
		List<RewardInfo> rewards = tchest.getRewards();
		rewards.remove(number - 1);
		tchest.setRewards(rewards);
		manager.setTreasure(tchest);
		
		sender.sendMessage(ChatColor.GREEN + "Reward " + ChatColor.WHITE + String.valueOf(number) + ChatColor.GREEN + " removed.");
		
	}


	@Command(aliases = { "clear" }, args = "", desc = "Remove all rewards from a treasure.", help = { ""})
	public void clear(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to edit rewards.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		tchest.setRewards(null);
		manager.setTreasure(tchest);
		sender.sendMessage(ChatColor.GREEN + "Cleared all rewards from this treasure.");
	}

	@Command(aliases = { "list" }, args = "", desc = "List rewards in a treasure.", help = { "" })
	public void list(CommandSender sender, String[] args) throws CommandException {
		if(!(sender instanceof Player)) {
			sender.sendMessage("Command must be executed by a player, in game.");
			return;
		}
		
		if(!sender.hasPermission(Permission.SET.getNode())) {
			throw new CommandException("You don't have permission to edit rewards.");
		}
		
		
		Player player = (Player) sender;
		Block block = TreasureManager.getTargetedContainerBlock(player);
		if(block == null) {
			throw new CommandException("You're not looking at a container block.");
		}
		
		Location loc = TreasureManager.getLocation((InventoryHolder) block.getState());
		
		ITreasureChest tchest = manager.getTreasure(loc);
		
		if(tchest == null) {
			throw new CommandException("You're not looking at a treasure.");
		}
		
		List<RewardInfo> rewards = tchest.getRewards();
		
		if(rewards == null || rewards.isEmpty()) {
			sender.sendMessage(ChatColor.RED + "There are no rewards set to the treasure at " + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ".");
			sender.sendMessage(ChatColor.RED + "How to add rewards: " + ChatColor.WHITE + "/" + getNested("add").getUniqueLabel() + " ?");
			return;
		}
		
		sender.sendMessage(ChatColor.GREEN + "List of rewards at " + ChatColor.WHITE + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + ChatColor.GREEN + ":");
		int index = 0;
		for (RewardInfo info : rewards) {
			String prefix = ChatColor.GRAY + String.valueOf(index + 1) + ". " + ChatColor.GOLD;
			
			RewardFactory f = rewardManager.getFactory(info.getLabel());
			IReward r;
			try {
				r = rewardManager.create(info);
			} catch (RewardException e) {
				throw new CommandException("Failed to create reward type \"" + info.getLabel() + "\": " + e.getMessage());
			}
			if(f == null) {
				sender.sendMessage(prefix.replace(ChatColor.WHITE.toString(), ChatColor.RED.toString()) + ChatColor.RED + " No factory for reward type \"" + info.getLabel() + "\".");
			}
			else {
				sender.sendMessage(prefix + r.getDescription());
			}
			index++;
		}
		sender.sendMessage(ChatColor.GOLD + "How to add/remove rewards: " + ChatColor.WHITE + getUsage());
	}


	@Command(aliases = { "types" }, args = "[page]", desc = "List all available reward types.", help = { "" })
	public void types(CommandSender sender, String[] args) throws CommandException {
		
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch(NumberFormatException e) {
			throw new CommandException("Expected the optional page number, instead of text.");
		} catch(IndexOutOfBoundsException e) {
			page = -1;
		} catch(NullPointerException e) {
			page = -1;
		}
		if(args.length > 1) {
			throw new CommandException("Incorrect number of arguments. Expected only the optional page number.");
		}
		
		String[] labelArray = rewardManager.getFactoryLabels();
		int total = labelArray.length;
		int totalPerPage;
		
		if(page == -1) {
			page = 1;
			totalPerPage = total;
		}
		else {
			totalPerPage = 10;
		}
		
		int startIndex = (page - 1) * totalPerPage;
		int endIndex = startIndex + totalPerPage;
	
		int totalPages = (int) Math.ceil((float) total / totalPerPage);
		if (page > totalPages || page < 1) {
			return;
		}
		sender.sendMessage(ChatColor.GREEN + "Reward types:" + " (page "
				+ page + "/" + totalPages + "):");
		
		for (int i = startIndex; i < endIndex && i < total; i++) {
			String lbl = labelArray[i];
			RewardFactory f = rewardManager.getFactory(lbl);
			sender.sendMessage(ChatColor.GRAY + String.valueOf(i + 1) + ". " + ChatColor.WHITE + lbl + " " + ChatColor.YELLOW + f.getGeneralDescription());
		}
	}
}
