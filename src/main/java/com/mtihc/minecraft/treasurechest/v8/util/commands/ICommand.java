package com.mtihc.minecraft.treasurechest.v8.util.commands;

import org.bukkit.command.CommandSender;

/**
 * Interface representing a command. 
 * 
 * <code>A method with the <code>Command</code> annotation 
 * can be wrapped in a class that implements this interface.</p>
 * 
 * @author Mitch
 *
 */
public interface ICommand {

	/**
	 * The command label
	 * @return the command label
	 */
	String getLabel();
	
	/**
	 * The command aliases
	 * @return the command aliases
	 */
	String[] getAliases();
	
	/**
	 * The argument syntax
	 * @return the argument syntax
	 */
	String getArgumentSyntax();
	
	/**
	 * The short description of this command
	 * @return the short description
	 */
	String getDescription();
	
	/**
	 * The help messages. Will always be executed together, in the order of the array.
	 * @return the help messages
	 */
	String[] getHelp();
	
	/**
	 * The unique label. This is the label, prefixed with comma separated labels of all parent commands.
	 * @return the unique label
	 */
	String getUniqueLabel();
	
	/**
	 * Returns the usage string. This is a forward slash, 
	 * followed by the unique label, 
	 * followed by the argument syntax.
	 * @return the usage string
	 */
	String getUsage();
	
	/**
	 * Execute this command
	 * @param sender the command sender
	 * @param args the command arguments
	 * @throws CommandException thrown when the command could not be executed
	 */
	void execute(CommandSender sender, String[] args) throws CommandException;
	
	/**
	 * Returns the parent command
	 * @return the parent command
	 */
	ICommand getParent();
	
	/**
	 * Returns the nested command with the specified label
	 * @param label the label of the nested command
	 * @return the nested command
	 */
	ICommand getNested(String label);
	
	/**
	 * Returns the labels of all nested commands. Not including children of children.
	 * @return the labels of all nested commands.
	 */
	String[] getNestedLabels();
	
	/**
	 * Returns whether this command has any nested commands.
	 * @return true if this command has any nested commands, false if it has no nested commands
	 */
	boolean hasNested();
}
