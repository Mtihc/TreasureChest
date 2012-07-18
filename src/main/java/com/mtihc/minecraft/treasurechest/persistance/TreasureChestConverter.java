package com.mtihc.minecraft.treasurechest.persistance;

import java.io.File;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import com.mtihc.minecraft.treasurechest.v8.core.BlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.IBlockInventory;
import com.mtihc.minecraft.treasurechest.v8.core.ITreasureChest.Message;
import com.mtihc.minecraft.treasurechest.v8.core.TreasureManager;

public class TreasureChestConverter {

	private TreasureManager manager;
	private ChestsYaml config;
	private MemoryYaml mem;

	public TreasureChestConverter(TreasureManager manager) {
		this.manager = manager;
		this.config = new ChestsYaml(manager.getPlugin());
	}
	
	public void start(Player sender) {
		
		new ConversationFactory(manager.getPlugin())
		.withFirstPrompt(new AcceptPrompt())
		.withLocalEcho(true)
		.withModality(false)
		.buildConversation(sender)
		.begin();
		
	}
	
	private void startConvert(Conversable sender) {
		config.reload();
		TChestCollection values = config.values();
		Collection<TreasureChest> chests = values.getChests();
		for (TreasureChest chest : chests) {
			
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			IBlockInventory container = new BlockInventory(chest.getLocation(), chest.getContents());
			map.put("container", container);
			
			Map<String, Object> msgSection = new LinkedHashMap<String, Object>();
			msgSection.put(Message.FOUND.name(), chest.getMessage(com.mtihc.minecraft.treasurechest.persistance.TreasureChest.Message.FOUND));
			msgSection.put(Message.FOUND_ALREADY.name(), chest.getMessage(com.mtihc.minecraft.treasurechest.persistance.TreasureChest.Message.FOUND_ALREADY));
			msgSection.put(Message.UNLIMITED.name(), chest.getMessage(com.mtihc.minecraft.treasurechest.persistance.TreasureChest.Message.FOUND_UNLIMITED));
			map.put("messages", msgSection);
			
			map.put("unlimited", chest.isUnlimited());
			map.put("random", chest.getAmountOfRandomlyChosenStacks());
			map.put("forget-time", chest.getForgetTime());
			map.put("ignore-protection", chest.ignoreProtection());
			
			
			Map<String, Object> rewardSection = new LinkedHashMap<String, Object>();
			map.put("rewards", rewardSection);
			
			
			
			manager.save(chest.getLocation(), new com.mtihc.minecraft.treasurechest.v8.core.TreasureChest(map));
		}

		sender.sendRawMessage(ChatColor.GREEN + "Converting finished!");
		
		new File(manager.getPlugin().getDataFolder() + "/chests.yml").delete();
		deleteDir(new File(manager.getPlugin().getDataFolder() + "/memory"));
	}
	
	public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
    
        return dir.delete();
    }
	
	private class AcceptPrompt extends ValidatingPrompt {

		@Override
		public String getPromptText(ConversationContext context) {
			context.getForWhom().sendRawMessage(ChatColor.RED + "> Did you make a backup of the folder \"plugins/Treasurechest\" ?");
			context.getForWhom().sendRawMessage(ChatColor.RED + ">" + ChatColor.WHITE + " Type NO to stop.");
			context.getForWhom().sendRawMessage(ChatColor.RED + ">" + ChatColor.WHITE + " Type YES to start converting.");
			return ChatColor.RED + "> The old files in \"plugins/TreasureChest\" will be deleted. So you should really make a backup.";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext context,
				String input) {
			if(input.equalsIgnoreCase("YES")) {
				context.getForWhom().sendRawMessage("Starting conversion!");
				startConvert(context.getForWhom());
				return END_OF_CONVERSATION;
			}
			else {
				return END_OF_CONVERSATION;
			}
		}

		@Override
		protected boolean isInputValid(ConversationContext context, String input) {
			return input.equalsIgnoreCase("YES") || input.equalsIgnoreCase("NO");
		}
		
	}

}
