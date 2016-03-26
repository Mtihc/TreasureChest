package com.mtihc.minecraft.treasurechest.v8.util.prompts;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.world.snapshot.InvalidSnapshotException;
import com.sk89q.worldedit.world.snapshot.Snapshot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;


public abstract class SelectSnapshotPrompt extends ValidatingPrompt {

	private WorldEditPlugin worldEdit;

	public SelectSnapshotPrompt(WorldEditPlugin worldEdit) {
		this.worldEdit = worldEdit;
	}
	
	protected abstract Prompt onCancel(ConversationContext context);

	protected abstract Prompt onSnapshotSelect(ConversationContext context,
			Snapshot snapshot);

	@Override
	public String getPromptText(ConversationContext context) {
		context.getForWhom().sendRawMessage(
				ChatColor.GOLD + "> Select a snapshot!");
		context.getForWhom().sendRawMessage(
				ChatColor.GOLD + "> List of snapshots: "
						+ ChatColor.LIGHT_PURPLE + "/snap list");
		return ChatColor.GOLD + "> Type a snapshot name. Or type "
				+ ChatColor.WHITE + "CANCEL" + ChatColor.GOLD + " to stop.";
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {
		if (input.startsWith("/")) {
			Bukkit.dispatchCommand((CommandSender) context.getForWhom(),
					input.substring(1));
			return false;
		} else if (input.equalsIgnoreCase("CANCEL")) {
			return true;
		} else {
			// check snapshot
			Player player = (Player) context.getForWhom();
			Snapshot snapshot;
			try {
				snapshot = worldEdit.getLocalConfiguration().snapshotRepo
						.getSnapshot(input);
			} catch (InvalidSnapshotException e) {
				snapshot = null;
			}
			if (snapshot == null) {
				player.sendRawMessage(ChatColor.RED
						+ "> Invalid snapshot name: " + input);
				return false;
			}
			context.setSessionData("snapshot", snapshot);
			return true;
		}
	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context,
			String input) {
		if (input.equalsIgnoreCase("CANCEL")) {
			return onCancel(context);
		} else {
			return onSnapshotSelect(context,
					(Snapshot) context.getSessionData("snapshot"));
		}
	}
}