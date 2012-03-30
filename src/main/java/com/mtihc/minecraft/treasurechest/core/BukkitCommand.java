package com.mtihc.minecraft.teamfactory.core.commands;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;


public abstract class BukkitCommand extends Command {

	public static String getUsageMessage(BukkitCommand command, String argumentSyntax) {
		return "/" + getUniqueName(command) + " " + argumentSyntax;
	}
	
	public static String getUniqueName(BukkitCommand command) {
		String result = command.getName().toLowerCase();
		if(command.hasParent()) {
			return getUniqueName(command.getParent()) + " " + result;
		}
		else {
			return result;
		}
	}
	private Map<String, BukkitCommand> nested;
	private BukkitCommand parent;
	private String argumentSyntax;
	private List<String> descriptionLong;
	
	public BukkitCommand(BukkitCommand parent, String name, String argumentSyntax, String description, List<String> aliases) {
		super(name, description, null, (aliases == null ? new ArrayList<String>() : aliases));
		this.parent = parent;
		this.argumentSyntax = argumentSyntax;
		this.setUsage(getUsageMessage(this, argumentSyntax));
		
		if(parent != null) {
			if(parent.nested == null) {
				parent.nested = new HashMap<String, BukkitCommand>();
			}
			parent.nested.put(name.toLowerCase(), this);
		}
	}
	
	public BukkitCommand getParent() {
		return parent;
	}
	
	public boolean hasParent() {
		return parent != null;
	}
	
	public final BukkitCommand getNested(String label) {
		return nested.get(label.toLowerCase());
	}

	public boolean hasLongDescription()
	{
		return descriptionLong != null && !descriptionLong.isEmpty();
	}
	
	public List<String> getLongDescription()
	{
		return descriptionLong;
	}
	
	public void setLongDescription(List<String> list)
	{
		descriptionLong = list;
	}
	
	protected abstract boolean onCommand(CommandSender sender, String label, String[] args);
	
	protected void sendCommandHelp(CommandSender sender, String usedAlias) {
		
		sender.sendMessage(ChatColor.GREEN + "Command usage: ");
		sender.sendMessage(ChatColor.GRAY + getUsage().replace(getName() + " ", ChatColor.WHITE + getName() + " " + ChatColor.GRAY));
		List<String> aliases = getAliases();
		if(aliases != null && !aliases.isEmpty()) {
			String aliasesString = "";
			
			for (String alias : aliases) {
				aliasesString += ", " + alias;
			}
			if(!aliasesString.isEmpty()) {
				aliasesString = aliasesString.substring(2);
			}
			sender.sendMessage(ChatColor.GREEN + "Aliases: " + ChatColor.WHITE + aliasesString);
		}
		
		
		if(hasLongDescription()) {
			sender.sendMessage(ChatColor.GREEN + "Description: ");
			List<String> desc = getLongDescription();
			for (String line : desc) {
				sender.sendMessage(line);
			}
		}
		else {
			sender.sendMessage(ChatColor.GREEN + "Description: " + ChatColor.WHITE + description);
		}
	}
	
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		boolean help;
		try {
			help = (args[0].equalsIgnoreCase("help") || args[0].equals("?"));
		} catch(NullPointerException e) {
			help = false;
		} catch(IndexOutOfBoundsException e) {
			help = false;
		}
		
		if(help)
		{
			sendCommandHelp(sender, label);
			return true;
		}
		else {
			BukkitCommand nestedCommand;
			try {
				nestedCommand = nested.get(args[0].toLowerCase());
			} catch(NullPointerException e) {
				nestedCommand = null;
			} catch(IndexOutOfBoundsException e) {
				nestedCommand = null;
			}
			if(nestedCommand != null) {
				return nestedCommand.execute(sender, args[0].toLowerCase(), unshiftArgument(args));
			}
			else {
				return onCommand(sender, label.toLowerCase(), args);
			}
		}
	}

	private String[] unshiftArgument(String[] args) {
		String[] result = new String[args.length - 1];
		for (int i = 1; i < args.length; i++) {
			result[i - 1] = args[i];
		}
		return result;
	}
//	
//	private String joinArguments(String label, String[] args) {
//		String result = label;
//		for (String element : args) {
//			result += " " + element;
//		}
//		return result;
//		
//	}

	/**
	 * @return the argument syntax
	 */
	public String getArgumentSyntax() {
		return argumentSyntax;
	}

	/**
	 * @param argumentSyntax the argument syntax
	 */
	public void setArgumentSyntax(String argumentSyntax) {
		this.argumentSyntax = argumentSyntax;
	}
}
