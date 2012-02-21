package com.mtihc.minecraft.treasurechest;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import com.mtihc.minecraft.core1.BukkitCommand;
import com.mtihc.minecraft.treasurechest.commands.CountCommand;
import com.mtihc.minecraft.treasurechest.commands.DeleteCommand;
import com.mtihc.minecraft.treasurechest.commands.ForgetAllCommand;
import com.mtihc.minecraft.treasurechest.commands.ForgetCommand;
import com.mtihc.minecraft.treasurechest.commands.SetCommand;
import com.mtihc.minecraft.treasurechest.commands.SetForgetCommand;
import com.mtihc.minecraft.treasurechest.commands.SetMessageCommand;
import com.mtihc.minecraft.treasurechest.commands.UnlimitedCommand;

public class TreasureChestCommand extends BukkitCommand {

	private TreasureChestPlugin plugin;

	public TreasureChestCommand(TreasureChestPlugin plugin, String name, List<String> aliases) {
		super(name, "Reloads the configuration.", "", aliases);
		this.plugin = plugin;
		Server server = plugin.getServer();
		
		ArrayList<String> help = new ArrayList<String>();
		help.add(ChatColor.DARK_AQUA + description);
		help.add(ChatColor.GREEN + "Nested commands:");
		
		// count
		BukkitCommand count = new CountCommand(plugin, "count", null);
		addNested(count, server);
		help.add("  " + count.getUsage());
		
		// delete
		ArrayList<String> aliasesDelete = new ArrayList<String>();
		aliasesDelete.add("del");
		BukkitCommand delete = new DeleteCommand(plugin, "delete", aliasesDelete);
		addNested(delete, server);
		help.add("  " + delete.getUsage());
		
		// forget
		BukkitCommand forget = new ForgetCommand(plugin, "forget", null);
		// forget -allplayers
		ArrayList<String> aliasesAllPlayers = new ArrayList<String>();
		aliases.add("-all");
		aliases.add("-a");
		BukkitCommand forgetAll = new ForgetAllCommand(plugin, "-allplayers", aliasesAllPlayers);
		forget.addNested(forgetAll, server);
		addNested(forget, server);
		help.add("  " + forget.getUsage());
		help.add("  " + forgetAll.getUsage());
		
		// set
		BukkitCommand set = new SetCommand(plugin, "set", null);
		addNested(set, server);
		help.add("  " + set.getUsage());
		
		// setforgettime
		ArrayList<String> aliasesSetForget = new ArrayList<String>();
		aliasesSetForget.add("setforget");
		BukkitCommand setForget = new SetForgetCommand(plugin, "setforgettime", aliasesSetForget);
		addNested(setForget, server);
		help.add("  " + setForget.getUsage());
		
		// setmessage
		ArrayList<String> aliasesSetMessage = new ArrayList<String>();
		aliasesSetMessage.add("setmsg");
		BukkitCommand setMessage = new SetMessageCommand(plugin, "setmessage", aliasesSetMessage);
		addNested(setMessage, server);
		help.add("  " + setMessage.getUsage());
		
		// unlimited
		ArrayList<String> aliasesUnlimited = new ArrayList<String>();
		aliasesUnlimited.add("u");
		BukkitCommand unlimited = new UnlimitedCommand(plugin, "unlimited", aliasesUnlimited);
		addNested(unlimited, server);
		help.add("  " + unlimited.getUsage());
		
		// help
		setLongDescription(help);
	}

	/* (non-Javadoc)
	 * @see com.mtihc.minecraft.core1.BukkitCommand#execute(org.bukkit.command.CommandSender, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		// try to execute a nested command
		if(super.execute(sender, label, args)) {
			return true;
		}
		
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
