package lycanite.lycanitesmobs.mountainmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "MOUNTAIN");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");
		
		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "TrollGriefing", "Troll Griefing", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Jabberwock", 8, 10, 1, 2, "MONSTER");
		loadMobSettings("Troll", 4, 5, 1, 2, "MONSTER");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "MountainEgg", "Mountain Spawn Egg ID", 24080);
		loadSetting(itemIDs, "Item IDs", "YaleMeatRaw", "Yale Meat Raw ID", 24081);
		loadSetting(itemIDs, "Item IDs", "YaleMeatCooked", "Yale Meat Cooked ID", 24082);
		loadSetting(itemIDs, "Item IDs", "PeaksKebab", "Peaks Kebab ID", 24083);
		loadSetting(itemIDs, "Item IDs", "BoulderBlastCharge", "Boulder Blast Charge ID", 24084);
		loadSetting(itemIDs, "Item IDs", "BoulderBlastScepter", "Boulder Blast Scepter ID", 24085);
	}
}