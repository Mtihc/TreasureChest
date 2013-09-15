package com.mtihc.minecraft.treasurechest.v8.plugin.util.commands;

import org.bukkit.command.CommandSender;

public interface ICommand {

	String getLabel();
	
	String[] getAliases();
	
	String getArgumentSyntax();
	
	String getDescription();
	
	String[] getHelp();
	
	String getUniqueLabel();
	
	String getUsage();
	
	void execute(CommandSender sender, String[] args) throws CommandException;
	
	ICommand getParent();
	
	ICommand getNested(String label);
	
	String[] getNestedLabels();
	
	boolean hasNested();
}
