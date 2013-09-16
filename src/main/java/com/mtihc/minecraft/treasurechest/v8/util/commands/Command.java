package com.mtihc.minecraft.treasurechest.v8.util.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotion for methods that represent a command. 
 * 
 * <p>Class <code>SimpleCommand</code> will look for this annotion, 
 * when a <code>Method</code> is added as a nested command.</p>
 * 
 * @author Mitch
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
	
	/**
	 * Array of aliases for this command. The first alias is considered the command label.
	 * @return the array of aliases
	 */
	String[] aliases();
	
	/**
	 * The command argument syntax
	 * @return the command argument syntax
	 */
	String args();
	
	/**
	 * Short command description
	 * @return short command description
	 */
	String desc();
	
	/**
	 * Array of help messages. Always sent together in the order of the array.
	 * @return array of help messages.
	 */
	String[] help();
}
