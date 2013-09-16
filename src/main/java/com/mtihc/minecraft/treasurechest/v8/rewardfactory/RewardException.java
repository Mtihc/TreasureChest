package com.mtihc.minecraft.treasurechest.v8.rewardfactory;

/**
 * Exception class used for reward related exceptions
 * 
 * @author Mitch
 *
 */
public class RewardException extends Exception {

	private static final long serialVersionUID = 1842767588930372392L;

	public RewardException() {
		
	}

	public RewardException(String msg) {
		super(msg);
	}

	public RewardException(Throwable cause) {
		super(cause);
	}

	public RewardException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
