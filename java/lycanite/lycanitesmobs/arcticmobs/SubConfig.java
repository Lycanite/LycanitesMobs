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
		loadSetting(this.featureBools, "Feature Control", "Dimensions", "Enable Frostwebbing", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Reiver", 8, 4, 1, 3, "Monster");
		loadMobSettings("Frostweaver", 6, 4, 1, 2, "Monster");
		
		// ========== Block IDs ==========
		loadSetting(blockIDs, "Block IDs", "Frostweb", "Frostweb Block ID", 3890);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "ArcticEgg", "Arctic Spawn Egg ID", 24090);
		loadSetting(itemIDs, "Item IDs", "FrostboltCharge", "Frostbolt Charge ID", 24091);
		loadSetting(itemIDs, "Item IDs", "FrostboltScepter", "Frostbolt Scepter ID", 24092);
		loadSetting(itemIDs, "Item IDs", "FrostwebCharge", "Frostweb Charge ID", 24093);
		loadSetting(itemIDs, "Item IDs", "FrostwebScepter", "Frostweb Scepter ID", 24094);
	}
}