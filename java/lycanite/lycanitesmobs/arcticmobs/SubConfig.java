package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "COLD, SNOWY, CONIFEROUS, -END");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0, 7");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "Frostweb", "Enable Frostwebs", true);
		loadSetting(this.featureBools, "Feature Control", "Frostcloud", "Enable Frostclouds", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Reiver", 8, 5, 1, 3, "Monster");
		loadMobSettings("Frostweaver", 10, 5, 1, 2, "Monster");
		loadMobSettings("Yeti", 10, 5, 2, 4, "Creature");
	}
}