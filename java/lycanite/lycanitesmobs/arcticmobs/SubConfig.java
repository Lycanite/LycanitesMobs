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
		loadSetting(this.featureBools, "Feature Control", "Frostfire", "Enable Frostfire", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Reiver", 8, 3, 1, 3, "FROSTFIRE", 8);
		loadMobSettings("Frostweaver", 10, 5, 1, 2, "MONSTER");
		loadMobSettings("Yeti", 10, 5, 2, 4, "CREATURE");
		loadMobSettings("Wendigo", 4, 1, 1, 1, "MONSTER");
	}
}