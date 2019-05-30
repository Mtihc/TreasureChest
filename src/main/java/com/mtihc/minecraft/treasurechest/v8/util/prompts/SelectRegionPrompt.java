package com.mtihc.minecraft.treasurechest.v8.util.prompts;

import com.mtihc.minecraft.treasurechest.v8.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;


public abstract class SelectRegionPrompt extends ValidatingPrompt {

	private WorldEditPlugin worldEdit;

	public SelectRegionPrompt() {
		this(null);
	}

	public SelectRegionPrompt(WorldEditPlugin worldEdit) {
		this.worldEdit = worldEdit;
	}

	public boolean hasWorldEdit() {
		return worldEdit != null;
	}

	@Override
	public String getPromptText(ConversationContext context) {
		context.getForWhom().sendRawMessage(
				ChatColor.GOLD + "> Select a region.");
		if (hasWorldEdit()) {
			context.getForWhom().sendRawMessage(
					ChatColor.GOLD + "> Use WorldEdit's command: "
							+ ChatColor.LIGHT_PURPLE + "//wand");
		} else {
			context.getForWhom().sendRawMessage(
					ChatColor.GOLD + "> Look at a block");
			Vector pos1 = (Vector) context.getSessionData("pos1");
			context.getForWhom().sendRawMessage(
					ChatColor.GOLD + "> Type " + ChatColor.WHITE + "pos1"
							+ ChatColor.GOLD
							+ " to select the 1st point of the region.");
			if (pos1 == null) {
				context.getForWhom().sendRawMessage(
						ChatColor.GOLD + "> Type " + ChatColor.WHITE + "pos2"
								+ ChatColor.GOLD
								+ " to select the 2nd point of the region.");
			}
		}
		return ChatColor.GOLD + "> Type " + ChatColor.WHITE + "OK"
				+ ChatColor.GOLD + " when you're done. Or type "
				+ ChatColor.WHITE + "CANCEL" + ChatColor.GOLD + " to stop.";
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if (input.startsWith("/")) {
			CommandSender sender = (CommandSender) context.getForWhom();
			Bukkit.dispatchCommand(sender, input.substring(1));
			return false;
		} else if (input.equalsIgnoreCase("CANCEL")) {
			return true;
		} else if (input.equalsIgnoreCase("OK")) {

			Player player = (Player) context.getForWhom();
			if (hasWorldEdit()) {
				
				Region sel = null;
				try {
					sel = worldEdit.getSession(player).getSelection(BukkitAdapter.adapt(player.getWorld()));
				} catch (com.sk89q.worldedit.IncompleteRegionException ex) {
				}

				if (sel == null || sel.getMaximumPoint() == null
						|| sel.getMinimumPoint() == null) {
					player.sendRawMessage(ChatColor.RED + "You didn't select a region.");
					return false;
				}
				
				context.setSessionData("world", sel.getWorld());
				BlockVector3 point = sel.getMinimumPoint();
				Vector min = new Vector(point.getX(), point.getY(), point.getZ());
				context.setSessionData("min", min);
				point = sel.getMaximumPoint();
				Vector max = new Vector(point.getX(), point.getY(), point.getZ());
				context.setSessionData("max", max);

				return true;
			} else {
				Vector min = (Vector) context.getSessionData("min");
				Vector max = (Vector) context.getSessionData("max");
				if (min == null || max == null) {
					player.sendRawMessage(ChatColor.RED
							+ "You didn't select a region.");
					return false;
				}

				return true;
			}

		} else {
			if (hasWorldEdit()) {
				return false;
			}
			if (input.equalsIgnoreCase("pos1")) {
				Player player = (Player) context.getForWhom();
				Block block = getTargetBlock(player);
				if (block == null) {
					player.sendRawMessage(ChatColor.RED
							+ "You're not looking at a block.");
					return false;
				}
				World world = (World) context.getSessionData("world");
				if (world != null) {
					if (!world.getName().equals(block.getWorld().getName())) {
						player.sendRawMessage(ChatColor.RED
								+ "You're in a different world, go to \""
								+ world.getName() + "\".");
						return false;
					}
				} else {
					context.setSessionData("world", block.getWorld());
				}
				Vector vec = block.getLocation().toVector();
				context.setSessionData("min", vec);
				player.sendRawMessage(ChatColor.GREEN + "Pos1 set to "
						+ vec.getBlockX() + "," + vec.getBlockY() + ","
						+ vec.getBlockZ());
				return false;

			} else if (input.equalsIgnoreCase("pos2")) {
				Player player = (Player) context.getForWhom();
				Block block = getTargetBlock(player);
				if (block == null) {
					player.sendRawMessage(ChatColor.RED
							+ "You're not looking at a block.");
					return false;
				}
				World world = (World) context.getSessionData("world");
				if (world != null) {
					if (!world.getName().equals(block.getWorld().getName())) {
						player.sendRawMessage(ChatColor.RED
								+ "You're in a different world, go to \""
								+ world.getName() + "\".");
						return false;
					}
				} else {
					context.setSessionData("world", block.getWorld());
				}
				Vector vec = block.getLocation().toVector();
				context.setSessionData("max", vec);
				player.sendRawMessage(ChatColor.GREEN + "Pos2 set to "
						+ vec.getBlockX() + "," + vec.getBlockY() + ","
						+ vec.getBlockZ());
				return false;
			} else {
				return false;
			}
		}
	}

	private Block getTargetBlock(Player player) {
		Block block = player.getTargetBlock((Set<Material>) null, 8);
		if (block == null || block.getType() == Material.AIR) {
			return null;
		}
		return block;
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context,
			String input) {
		if (input.equalsIgnoreCase("OK")) {
			World world = (World) context.getSessionData("world");
			Vector min = (Vector) context.getSessionData("min");
			Vector max = (Vector) context.getSessionData("max");

			if (!hasWorldEdit()) {
				if (min.getBlockX() > max.getBlockX()) {
					int x = max.getBlockX();
					max.setX(min.getBlockX());
					min.setX(x);
				}
				if (min.getBlockY() > max.getBlockY()) {
					int y = max.getBlockY();
					max.setY(min.getBlockY());
					min.setY(y);
				}
				if (min.getBlockZ() > max.getBlockZ()) {
					int z = max.getBlockZ();
					max.setZ(min.getBlockZ());
					min.setZ(z);
				}
			}

			return onFinish(context, world, min, max);
		} else {
			return onCancel(context);
		}
	}

	protected abstract Prompt onFinish(ConversationContext context,
			World world, Vector min, Vector max);

	protected abstract Prompt onCancel(ConversationContext context);
}