package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "ALL");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0, -1, 1, 7");

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Cinder", 8, 3, 1, 3, "FIRE", 8);
		loadMobSettings("Lobber", 2, 2, 1, 2, "LAVA", 16);
	}
}