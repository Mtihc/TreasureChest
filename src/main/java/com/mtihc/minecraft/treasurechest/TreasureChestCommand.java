package com.mtihc.minecraft.treasurechest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.treasurechest.commands.CountCommand;
import com.mtihc.minecraft.treasurechest.commands.DeleteCommand;
import com.mtihc.minecraft.treasurechest.commands.ForgetAllCommand;
import com.mtihc.minecraft.treasurechest.commands.ForgetCommand;
import com.mtihc.minecraft.treasurechest.commands.IgnoreProtectionCommand;
import com.mtihc.minecraft.treasurechest.commands.ListAllCommand;
import com.mtihc.minecraft.treasurechest.commands.ListCommand;
import com.mtihc.minecraft.treasurechest.commands.RandomCommand;
import com.mtihc.minecraft.treasurechest.commands.SetCommand;
import com.mtihc.minecraft.treasurechest.commands.SetForgetCommand;
import com.mtihc.minecraft.treasurechest.commands.SetMessageCommand;
import com.mtihc.minecraft.treasurechest.commands.UnlimitedCommand;
import com.mtihc.minecraft.treasurechest.core.BukkitCommand;

public class TreasureChestCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public TreasureChestCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(null, name, "", "Reloads the configuration", aliases);
		this.plugin = plugin;
		
		ArrayList<String> help = new ArrayList<String>();
		help.add(ChatColor.DARK_AQUA + description);
		help.add(ChatColor.GREEN + "Nested commands:");
		
		// count
		BukkitCommand count = new CountCommand(plugin, this, "count", null);
		help.add("  " + count.getUsage());
		
		// delete
		ArrayList<String> aliasesDelete = new ArrayList<String>();
		aliasesDelete.add("del");
		BukkitCommand delete = new DeleteCommand(plugin, this, "delete", aliasesDelete);
		help.add("  " + delete.getUsage());
		
		// forget
		BukkitCommand forget = new ForgetCommand(plugin, this, "forget", null);
		// forget -allplayers
		ArrayList<String> aliasesAllPlayers = new ArrayList<String>();
		aliases.add("-all");
		aliases.add("-a");
		BukkitCommand forgetAll = new ForgetAllCommand(plugin, forget, "-allplayers", aliasesAllPlayers);
		help.add("  " + forget.getUsage());
		help.add("  " + forgetAll.getUsage());
		
		// set
		BukkitCommand set = new SetCommand(plugin, this, "set", null);
		help.add("  " + set.getUsage());
		
		// setforgettime
		ArrayList<String> aliasesSetForget = new ArrayList<String>();
		aliasesSetForget.add("setforget");
		BukkitCommand setForget = new SetForgetCommand(plugin, this, "setforgettime", aliasesSetForget);
		help.add("  " + setForget.getUsage());
		
		// setmessage
		ArrayList<String> aliasesSetMessage = new ArrayList<String>();
		aliasesSetMessage.add("setmsg");
		BukkitCommand setMessage = new SetMessageCommand(plugin, this, "setmessage", aliasesSetMessage);
		help.add("  " + setMessage.getUsage());
		
		// unlimited
		ArrayList<String> aliasesUnlimited = new ArrayList<String>();
		aliasesUnlimited.add("u");
		BukkitCommand unlimited = new UnlimitedCommand(plugin, this, "unlimited", aliasesUnlimited);
		help.add("  " + unlimited.getUsage());
		
		// random
		ArrayList<String> aliasesRandom = new ArrayList<String>();
		aliasesRandom.add("r");
		BukkitCommand random = new RandomCommand(plugin, this, "random", aliasesRandom);
		help.add("  " + random.getUsage());
		
		// ignore protection
		ArrayList<String> aliasesIgnoreProtection = new ArrayList<String>();
		aliasesIgnoreProtection.add("ip");
		BukkitCommand ip = new IgnoreProtectionCommand(plugin, this, "ignoreprotection", aliasesIgnoreProtection);
		help.add("  " + ip.getUsage());
		
		// list
		BukkitCommand list = new ListCommand(plugin, this, "list", null);
		help.add("  " + list.getUsage());
		
		// list -all
		ArrayList<String> listAllAliases = new ArrayList<String>();
		listAllAliases.add("-a");
		BukkitCommand listAll = new ListAllCommand(plugin, list, "-all", listAllAliases);
		help.add("  " + listAll.getUsage());
		
		// help
		setLongDescription(help);
	}

	@Override
	protected boolean onCommand(CommandSender sender, String label,
			String[] args) {
		
		// no nested command
		if(args.length != 0) {
			// but tried it
			String cmd = args[0];
			sender.sendMessage(ChatColor.RED + "Unknown command '/" + label + " " + cmd + "'");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}

		// no nested command and no arguments,
		// means reload command
		if(!sender.hasPermission(Permission.RELOAD.getNode())) {
			sender.sendMessage(ChatColor.RED + "You don't have permission to reload the configuration.");
			sender.sendMessage(ChatColor.RED + "To get command help, type: " + ChatColor.WHITE + "/" + label + " " + "help");
			return true;
		}
		
		// reload
		plugin.reloadConfig();
		// send message
		sender.sendMessage(ChatColor.GREEN + plugin.getDescription().getName() + " configuration file reloaded.");
		
		return true;
	}

	
}
