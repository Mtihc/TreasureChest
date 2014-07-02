package com.mtihc.minecraft.treasurechest.v8.util.commands;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Class representing a command. 
 * 
 * <p>This class can be extended to add nested commands. 
 * It is recommended to add nested commands in the constructor, 
 * using any of the overloads of method <code>addNested</code>.</p>
 * 
 * @author Mitch
 *
 */
public class SimpleCommand implements ICommand {

	protected String[] aliases;
	protected String argumentSyntax;
	protected String desc;
	protected String[] help;
	
	protected final Map<String, CommandFactory> factories = new LinkedHashMap<String, CommandFactory>();
	protected final Set<String> labels = new LinkedHashSet<String>();
	
	protected final ICommand parent;
	
	/**
	 * Constructor.
	 * @param parent the parent command
	 * @param aliases the command aliases. The first alias is considered the command label
	 * @param argumentSyntax the argument syntax
	 * @param desc the short command description
	 * @param help the help messages
	 */
	public SimpleCommand(ICommand parent, String[] aliases, String argumentSyntax, String desc, String[] help) {
		
		this.parent = parent;
		
		this.aliases = aliases;
		this.argumentSyntax = argumentSyntax;
		this.desc = desc;
		this.help = (help == null ? new String[0] : help);
		
	}


	@Override
	public ICommand getParent() {
		return parent;
	}
	
	@Override
	public String getLabel() {
		return aliases[0];
	}

	@Override
	public String[] getAliases() {
		try {
			return Arrays.copyOfRange(aliases, 1, aliases.length);
		} catch(Exception e) {
			return new String[0];
		}
	}

	@Override
	public String getArgumentSyntax() {
		return argumentSyntax;
	}

	@Override
	public String getDescription() {
		return desc;
	}

	@Override
	public String getUsage() {
		return getUsage(this);
	}
	
	@Override
	public String getUniqueLabel() {
		return getUniqueLabel(this);
	}
	
	@Override
	public String[] getHelp() {
		return help;
	}
	
	
	@Override
	public ICommand getNested(String label) {
		CommandFactory f = factories.get(label.toLowerCase());
		if(f == null) {
			return null;
		}
		else {
			return f.getCommand();
		}
	}
	
	@Override
	public String[] getNestedLabels() {
		return labels.toArray(new String[labels.size()]);
	}
	
	@Override
	public boolean hasNested() {
		return !labels.isEmpty();
	}
	
	
	
	

	@Override
	public void execute(CommandSender sender, String[] args) throws CommandException {
		String firstArgument;
		if(args.length == 0) {
			// no arguments
			onCommand(sender, args);
			return;
		}
		else {
			firstArgument = args[0];
			
			if(firstArgument.equals("?") || firstArgument.equalsIgnoreCase("help")) {
				sendHelp(sender, this);
				return;
			}
			
			// first argument is not help
			ICommand nested = getNested(firstArgument);

			if(nested != null) {
				// first argument is nested command
				try {
					// remove first argument
					args = Arrays.copyOfRange(args, 1, args.length);
				} catch(Exception e) {
					args = new String[0];
				}
				
				try {
					firstArgument = args[0];
					if(firstArgument.equals("?") || firstArgument.equalsIgnoreCase("help")) {
						// new first argument is help
						sendHelp(sender, nested);
						return;
					}
				} catch(IndexOutOfBoundsException e) { }
				
				// new first argument is not help
				
				// execute nested command
				nested.execute(sender, args);
				return;
			} else {
				// first argument is not a nested command
				
				// execute this command
				onCommand(sender, args);
				return;
			}
			//removed double "else" loop
		}
	}

	/**
	 * Sends help messages to the command sender.
	 * <p>This method is used in method <code>execute</code>. 
	 * This method can be overridden to implement different command behavior. 
	 * But usually the main command simply sends information about it's nested commands.</p>
	 * @param sender the command sender
	 * @param args the command arguments
	 * @throws CommandException thrown when the command could not be executed
	 */
	protected void onCommand(CommandSender sender, String[] args) throws CommandException {
		int page;
		try {
			page = Integer.parseInt(args[0]);
		} catch(IndexOutOfBoundsException e) {
			sendHelp(sender, this, -1);
			return;
		} catch(NumberFormatException e) {
			String lbl = getUniqueLabel();
			sender.sendMessage(ChatColor.RED + "Unknown command: " + ChatColor.WHITE + "/" + lbl + " " + args[0]);
			throw new CommandException("To get command help, type: " + ChatColor.WHITE + "/" + lbl + " ?");
		}
		sendHelp(sender, this, page);
	}
	
	
	
	
	
	
	
	
	/**
	 * Adds every method with the <code>Command</code> annotation as nested command.
	 */
	protected final void findNestedCommandMethods() {

		Method[] methods = getClass().getMethods();
		for (Method method : methods) {
			if(method.isAnnotationPresent(Command.class)) {
				addNested(method);
			}
		}
	}
	
	/**
	 * Adds a nested command using a class reference. The class should implement the <code>ICommand</code> interface.
	 * @param commandClass the class reference
	 * @param args the arguments that will be passed to the constructor
	 */
	protected <T extends ICommand> void addNested(Class<T> commandClass, Object... args) {
		CommandFactory f = new CommandObjectFactory(commandClass, args);
		
		ICommand cmd = f.getCommand();
		String lbl = cmd.getLabel().toLowerCase();
		factories.put(lbl, f);
		labels.add(lbl);
		String[] aliases = cmd.getAliases();
		for (String alias : aliases) {
			factories.put(alias.toLowerCase(), f);
		}
	}
	
	/**
	 * Adds a nested command using a method name. The method should have the <code>Command</code> annotation.
	 * @param methodName the method name
	 */
	protected void addNested(String methodName) {
		Method method = null;
		Exception exception = null;
		try {
			method = getClass().getMethod(methodName, CommandSender.class, String[].class);
		} catch (NoSuchMethodException e) {
			exception = e;
		} catch(SecurityException e) {
			exception = e;
		}
		
		if(exception != null) {
			Bukkit.getLogger().log(Level.SEVERE, "Couldn't add method \"" + methodName + "\" as subcommand of \"" + getUsage() + "\"", exception);
			return;
		}
		addNested(method);
	}
	
	/**
	 * Adds a nested command using a reference to the method. The method should have the <code>Command</code> annotation.
	 * @param method the method reference
	 */
	protected void addNested(Method method) {
		if(!method.isAnnotationPresent(Command.class)) {
			throw new IllegalArgumentException("Method \"" + method.getName() + "\" of class \"" + method.getDeclaringClass().getCanonicalName() + "\" doesn't have the " + Command.class.getName() + " annotation.");
		}
		Command command = method.getAnnotation(Command.class);
		CommandFactory f = new CommandMethodFactory(this, method);
		
		String[] aliases = command.aliases();
		labels.add(aliases[0]);
		for (String alias : aliases) {
			factories.put(alias.toLowerCase(), f);
		}
	}
	

	
	
	
	
	
	
	
	
	
	/**
	 * Helper method to get a command's usage string
	 * @param command the command
	 * @return the usage string
	 */
	public static String getUsage(ICommand command) {
		return "/" + getUniqueLabel(command) + " " + command.getArgumentSyntax();
	}
	
	/**
	 * Helper method to get a command's unique label
	 * @param command the command
	 * @return the unique label
	 */
	public static String getUniqueLabel(ICommand command) {
		String lbl = command.getLabel();
		ICommand cmd = command;
		while(cmd.getParent() != null) {
			cmd = cmd.getParent();
			lbl = cmd.getLabel() + " " + lbl;
			
		}
		return lbl;
	}
	
	public static void sendHelp(CommandSender sender, ICommand cmd) throws CommandException {
		sendHelp(sender, cmd, -1);
	}

	public static void sendHelp(CommandSender sender, ICommand cmd, int page) throws CommandException {
		
		sender.sendMessage(ChatColor.GOLD + "Command:" + ChatColor.WHITE + " " + cmd.getUsage());
		
		if(cmd.getDescription() != null) {
			sender.sendMessage(ChatColor.YELLOW + cmd.getDescription());
		}
		
		String[] help = cmd.getHelp();
		if(help != null) {
			for (String string : help) {
				sender.sendMessage(ChatColor.YELLOW + string);
			}
		}
		
		
		if(cmd.hasNested()) {
			
			String[] labels = cmd.getNestedLabels();
			
			int total = labels.length;
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
			sender.sendMessage(ChatColor.GOLD + "Nested commands: (page "
					+ page + "/" + totalPages + "):");
			
			for (int i = startIndex; i < endIndex && i < total; i++) {
				String lbl = labels[i];
				ICommand nested = cmd.getNested(lbl);
				sender.sendMessage(nested.getUsage() + ChatColor.YELLOW + " " + nested.getDescription());
			}
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	private interface CommandFactory {
		ICommand getCommand();
	}
	
	
	
	
	
	
	
	
	
	
	private class CommandObjectFactory implements CommandFactory {

		private Class<?> commandClass;
		private Object[] args;
		private Constructor<?> constructor;

		private <T extends ICommand> CommandObjectFactory(Class<T> commandClass,
				Object[] args) {
			this.commandClass = commandClass;
			this.args = args;

			findConstructor(commandClass, args);
		}

		private void findConstructor(Class<?> commandClass, Object... args) {

			Constructor<?> result = null;
			Constructor<?>[] ctrs = commandClass.getConstructors();
			for (int i = 0; result == null && i < ctrs.length; i++) {
				result = ctrs[i];
				Class<?>[] parameterTypes = result.getParameterTypes();
				if (parameterTypes.length != args.length) {
					result = null;
					continue;
				}
				parameter_iterator: for (int j = 0; j < parameterTypes.length; j++) {
					if (!parameterTypes[j].isAssignableFrom(args[j].getClass())) {
						result = null;
						break parameter_iterator;
					}
				}
			}

			if (result == null) {
				String typeString = "";
				for (Object arg : args) {
					typeString += ", " + arg.getClass().getName();
				}
				if (!typeString.isEmpty()) {
					typeString = typeString.substring(2);
				}
				throw new IllegalArgumentException("Class \""
						+ commandClass.getCanonicalName()
						+ "\" doesn't have a constructor with parameter types ("
						+ typeString + ")");
			}

			this.constructor = result;
		}

		private ICommand newInstance(Constructor<?> constructor) {
			constructor.setAccessible(true);
			Exception exception = null;
			try {
				return (ICommand) constructor.newInstance(args);
			} catch (InstantiationException e) {
				exception = e;
			} catch (IllegalAccessException e) {
				exception = e;
			} catch (IllegalArgumentException e) {
				exception = e;
			} catch (InvocationTargetException e) {
				exception = e;
			}
			Logger.getLogger(getClass().getCanonicalName()).log(
					Level.SEVERE,
					"Failed to create command class \""
							+ commandClass.getCanonicalName() + "\".", exception);

			return null;
		}

		@Override
		public ICommand getCommand() {
			return newInstance(constructor);
		}
	}
	
	
	
	
	
	
	
	
	
	
	private class CommandMethodFactory implements CommandFactory {

		private ICommand parent;
		private Method method;

		private CommandMethodFactory(ICommand parent, Method method) {
			this.parent = parent;
			this.method = method;
		}

		@Override
		public ICommand getCommand() {
			return new CommandMethod(parent, method);
		}

	}
	
	
	
	
	
	
	
	
	
	
	private class CommandMethod implements ICommand {

		private Method method;
		private Command command;
		private ICommand parent;

		private CommandMethod(ICommand parent, Method method) {
			this.parent = parent;
			this.method = method;
			this.command = method.getAnnotation(Command.class);
		}

		@Override
		public String getLabel() {
			return command.aliases()[0];
		}

		@Override
		public String[] getAliases() {
			String[] aliases = command.aliases();
			try {
				return Arrays.copyOfRange(aliases, 1, aliases.length);
			} catch (Exception e) {
				return new String[0];
			}
		}

		@Override
		public String getArgumentSyntax() {
			return command.args();
		}

		@Override
		public String getDescription() {
			return command.desc();
		}

		@Override
		public String[] getHelp() {
			return command.help();
		}

		@Override
		public void execute(CommandSender sender, String[] args)
				throws CommandException {
			
			Object instance = parent;
			Exception exception = null;
			try {
				method.invoke(instance, sender, args);
			} catch (IllegalAccessException e) {
				exception = e;
			} catch (IllegalArgumentException e) {
				exception = e;
			} catch (InvocationTargetException e) {
				exception = e;
			}
			
			if(exception == null) {
				return;
			}
			
			if(exception.getCause() != null && exception.getCause() instanceof CommandException) {
				throw (CommandException) exception.getCause();
			}
			else {
				Logger.getLogger(getClass().getCanonicalName()).log(
						Level.SEVERE,
						"Failed to invoke command method \"" + method.getName()
								+ "\" of class \""
								+ instance.getClass().getCanonicalName() + "\"", exception);
			}
		}

		@Override
		public ICommand getParent() {
			return parent;
		}

		@Override
		public String getUniqueLabel() {
			return SimpleCommand.getUniqueLabel(this);
		}

		@Override
		public String getUsage() {
			return SimpleCommand.getUsage(this);
		}

		@Override
		public ICommand getNested(String label) {
			return null;
		}

		@Override
		public String[] getNestedLabels() {
			return null;
		}

		@Override
		public boolean hasNested() {
			return false;
		}
	}
}
