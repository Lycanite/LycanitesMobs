package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "SWAMP");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0, 7");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "PoisonCloud", "Enable Poison Clouds", true);
		loadSetting(this.featureBools, "Feature Control", "EttinGriefing", "Ettin Griefing", true);
		
		// ========== Mob Control ==========
		loadMobSettings("GhoulZombie", 8, 10, 1, 3, "MONSTER");
		loadMobSettings("Dweller", 8, 5, 1, 3, "WATERCREATURE");
		loadMobSettings("Ettin", 3, 2, 1, 1, "MONSTER");
		loadMobSettings("Lurker", 6, 5, 1, 3, "MONSTER");
		loadMobSettings("Eyewig", 3, 5, 1, 1, "MONSTER");
		loadMobSettings("Aspid", 12, 10, 1, 3, "CREATURE");
		loadMobSettings("Remobra", 4, 10, 1, 3, "MONSTER");
	}
}