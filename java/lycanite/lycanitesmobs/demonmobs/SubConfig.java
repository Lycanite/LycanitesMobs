package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "NETHER");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "-1");
		
		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "Hellfire", "Enable Hellfire", true);
		loadSetting(this.featureBools, "Feature Control", "PinkiesOnPeaceful", "Pinkies Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnPinkiesNaturally", "Pinkies Natural Despawning", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Belph", 100, 10, 3, 4, "NETHER");
		loadMobSettings("Behemoth", 20, 5, 1, 1, "NETHER");
		loadMobSettings("Pinky", 60, 5, 1, 3, "NETHER");
		loadMobSettings("Trite", 120, 40, 5, 10, "NETHER");
		loadMobSettings("Asmodi", 20, 1, 1, 1, "NETHER");
		loadMobSettings("NetherSoul", 60, 10, 4, 8, "NETHER");
		loadMobSettings("Cacodemon", 40, 2, 1, 1, "NETHER");
	}
}