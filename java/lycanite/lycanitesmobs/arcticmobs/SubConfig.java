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
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0, 7");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "Frostweb", "Enable Frostwebs", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Reiver", 8, 5, 1, 3, "Monster");
		loadMobSettings("Frostweaver", 10, 5, 1, 2, "Monster");
	}
}