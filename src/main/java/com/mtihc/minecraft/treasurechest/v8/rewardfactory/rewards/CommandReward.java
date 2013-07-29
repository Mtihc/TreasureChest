package com.mtihc.minecraft.treasurechest.v8.rewardfactory.rewards;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.rewardfactory.IReward;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardException;
import com.mtihc.minecraft.treasurechest.v8.rewardfactory.RewardInfo;

public class CommandReward implements IReward {

	public enum Type {
		NORMAL,
		OP,
		CONSOLE;
	}

	private RewardInfo info;
	
	public CommandReward(String commandLine, Type type) {
		this.info = new RewardInfo("command", new HashMap<String, Object>());
		setCommandLine(commandLine);
		setType(type);
	}
	
	CommandReward(RewardInfo info) {
		this.info = info;
	}

	@Override
	public RewardInfo getInfo() {
		return info;
	}
	
	public String getCommandLine() {
		return (String) info.getData("command-line");
	}
	
	public void setCommandLine(String commandLine) {
		if(!commandLine.startsWith("/")) {
			commandLine = "/" + commandLine;
		}
		info.setData("command-line", commandLine);
	}
	
	public Type getType() {
		return Type.valueOf((String) info.getData("type"));
	}
	
	public void setType(Type type) {
		info.setData("type", type.name());
	}

	@Override
	public String getDescription() {
		String typeString = "";
		switch(getType()) {
		case CONSOLE:
			typeString = " as console";
			break;
		case OP:
			typeString = " as OP";
			break;
		default:
			typeString = "";
			break;
		}
		return "execute command \"" + getCommandLine() + "\"" + typeString;
	}

	@Override
	public void give(Player player) throws RewardException {
		Type type = getType();
		String commandLine = getCommandLine().substring(1);
		commandLine = commandLine.replace("%player%", player.getName()).replace("@p", player.getName());
		
		
		if(type == Type.OP) {
			
			// remember if player was OP
			boolean oldOp = player.isOp();
			// make player OP
			player.setOp(true);
			
			
			// try to execute the command
			RewardException e;
			if(!Bukkit.dispatchCommand(player, commandLine)) {
				e = getUnknownCommandException("/" + commandLine);
			}
			else {
				e = null;
			}
			
			// turn OP off if necessary
			player.setOp(oldOp);
			
			// throw error if there was any
			if(e != null) {
				throw e;
			}
			
			
		}
		else if(type == Type.CONSOLE) {
			// let console execute the command
			if(!Bukkit.dispatchCommand(Bukkit.getConsoleSender(), commandLine)) {
				// command doesn't exist
				throw getUnknownCommandException("/" + commandLine);
			}
		}
		else {
			// let player execute the command
			if(!Bukkit.dispatchCommand(player, commandLine)) {
				// command doesn't exist
				throw getUnknownCommandException("/" + commandLine);
			}
		}
	}
	
	private RewardException getUnknownCommandException(String commandLine) {
		return new RewardException("Unknown command \"/" + commandLine + "\". Type \"help\" for help.");
	}

}
