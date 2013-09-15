package com.mtihc.minecraft.treasurechest.v8.util.commands;

public class CommandException extends Exception {

	private static final long serialVersionUID = -4778069147590623206L;

	public CommandException() {
	}

	public CommandException(String msg) {
		super(msg);
	}

	public CommandException(Throwable msg) {
		super(msg);
	}

	public CommandException(String msg, Throwable cause) {
		super(msg, cause);
	}
	

}
