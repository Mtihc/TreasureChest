package com.mtihc.minecraft.treasurechest.v8.util.prompts;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class SelectBlockPrompt extends ValidatingPrompt {

	private Material type;
	private String worldName;
	private Vector min;
	private Vector max;
	private HashSet<Byte> invisible;

	public SelectBlockPrompt(Material type, String worldName, Vector min, Vector max) {
		this.type = type;
		this.worldName = worldName;
		this.min = min;
		this.max = max;
		
		invisible = new HashSet<Byte>();
		invisible.add((byte) Material.AIR.getId());
		invisible.add((byte) Material.WATER.getId());
		invisible.add((byte) Material.STATIONARY_WATER.getId());
		invisible.add((byte) Material.LAVA.getId());
		invisible.add((byte) Material.STATIONARY_LAVA.getId());
	}
	
	

	protected abstract Prompt onCancel(ConversationContext context);
	protected abstract Prompt onFinish(ConversationContext context, Block block);
	
	@Override
	public String getPromptText(ConversationContext context) {
		if(getType() != null) {
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Look at a block " + ChatColor.WHITE + "(" + getType().name().toLowerCase().replace("_", " ") + ")");
		}
		else {
			context.getForWhom().sendRawMessage(ChatColor.GOLD + "> Look at a block.");
		}
		return ChatColor.GOLD + "> Type " + ChatColor.WHITE + "OK" + ChatColor.GOLD + ". Or type " + ChatColor.WHITE + "CANCEL" + ChatColor.GOLD + " to stop.";
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		if(input.equalsIgnoreCase("OK")) {
			Block block = (Block) context.getSessionData("block");
			return onFinish(context, block);
		}
		else {
			return onCancel(context);
		}
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if(input.startsWith("/")) {
			Bukkit.dispatchCommand((CommandSender) context.getForWhom(), input.substring(1));
			return false;
		}
		else if(input.equalsIgnoreCase("CANCEL")) {
			return true;
		}
		else if(input.equalsIgnoreCase("OK")) {
			Player player = (Player) context.getForWhom();
			Block block = player.getTargetBlock(invisible, 8);
			if(block == null || block.getTypeId() == 0) {
				player.sendRawMessage(ChatColor.RED + "You're not looking at a block.");
				return false;
			}
			
			if(getWorldName() != null && !block.getWorld().getName().equals(getWorldName())) {
				context.getForWhom().sendRawMessage(ChatColor.RED + "You're in a different world. Go to " + ChatColor.WHITE + "\"" + getWorldName() + "\"" + ChatColor.RED + ".");
				return false;
			}
			
			if(min != null && max != null) {
				if(block.getX() < min.getBlockX() || block.getX() > max.getBlockX() || block.getY() < min.getBlockY() || block.getY() > max.getBlockY() || block.getZ() < min.getBlockZ() || block.getZ() > max.getBlockZ()) {
					context.getForWhom().sendRawMessage(ChatColor.RED + "Location is outside of the region " + ChatColor.WHITE + "( " + min.getBlockX() + "," + min.getBlockY() + "," + min.getBlockZ() + " " + max.getBlockX() + "," + max.getBlockY() + "," + max.getBlockZ() + " )" + ChatColor.GOLD + ".");
					return false;
				}
			}
			
			if(type != null) {
				if(block.getType() != type) {
					context.getForWhom().sendRawMessage(ChatColor.RED + "You're not looking at " + ChatColor.WHITE + "\"" + type.name().toLowerCase().replace("_", " ") + "\"" + ChatColor.RED + ".");
					return false;
				}
			}
			
			context.setSessionData("block", block);
			return true;
		}
		else {
			return false;
		}
	}



	/**
	 * @return the type
	 */
	public Material getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(Material type) {
		this.type = type;
	}



	/**
	 * @return the worldName
	 */
	public String getWorldName() {
		return worldName;
	}



	/**
	 * @param worldName the worldName to set
	 */
	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}



	/**
	 * @return the min
	 */
	public Vector getMin() {
		return min;
	}



	/**
	 * @param min the min to set
	 */
	public void setMin(Vector min) {
		this.min = min;
	}



	/**
	 * @return the max
	 */
	public Vector getMax() {
		return max;
	}



	/**
	 * @param max the max to set
	 */
	public void setMax(Vector max) {
		this.max = max;
	}

}
