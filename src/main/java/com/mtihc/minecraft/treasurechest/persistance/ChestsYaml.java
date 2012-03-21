package com.mtihc.minecraft.treasurechest.persistance;

import com.mtihc.minecraft.treasurechest.TreasureChestPlugin;
import com.mtihc.minecraft.treasurechest.core.YamlFile;

public class ChestsYaml extends YamlFile {

	public ChestsYaml(TreasureChestPlugin plugin) {
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
