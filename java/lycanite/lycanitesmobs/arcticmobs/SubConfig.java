package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "FROZEN");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Reiver", 8, 4, 1, 3, "Monster");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "ArcticEgg", "Arctic Spawn Egg ID", 24090);
		loadSetting(itemIDs, "Item IDs", "FrostboltCharge", "Frostbolt Charge ID", 24091);
		loadSetting(itemIDs, "Item IDs", "FrostboltScepter", "Frostbolt Scepter ID", 24092);
	}
}