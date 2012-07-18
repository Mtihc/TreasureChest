package com.mtihc.minecraft.treasurechest.persistance;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * Only required to convert from v7 to v8
 * @author Mitch
 *
 */
@Deprecated
public class ChestsYaml extends YamlFile {

	public ChestsYaml(JavaPlugin plugin) {
		super(plugin, "chests");
		
	}
	
	public TChestCollection values() {
		TChestCollection result = (TChestCollection) getConfig().get("chests");
		if(result == null) {
			result = new TChestCollection();
			getConfig().set("chests", result);
		}
		return result;
	}

}
