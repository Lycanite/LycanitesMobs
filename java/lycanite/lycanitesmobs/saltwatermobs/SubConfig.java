package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "OCEAN, BEACH");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		loadSetting(this.featureInts, "Feature Control", "AbtuSwarmLimit", "Abtu Swarm Limit", 40);

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Lacedon", 8, 3, 1, 3, "WATERCREATURE");
		loadMobSettings("Skylus", 6, 3, 1, 3, "WATERCREATURE");
        loadMobSettings("ika", 6, 3, 1, 3, "WATERCREATURE");
        loadMobSettings("abtu", 8, 32, 1, 5, "WATERCREATURE");
	}
}