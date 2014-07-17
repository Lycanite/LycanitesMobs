package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "SANDY, WASTELAND, MESA, -COLD");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "JoustsOnPeaceful", "Joust Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnJoustsNaturally", "Joust Natural Despawning", false);
		loadSetting(this.featureInts, "Feature Control", "GorgomiteSwarmLimit", "Gorgomite Swarm Limit", 20);
		
		// ========== Mob Control ==========
		loadMobSettings("CryptZombie", 8, 10, 1, 3, "MONSTER");
		loadMobSettings("Crusk", 2, 3, 1, 1, "MONSTER");
		loadMobSettings("Clink", 6, 10, 1, 3, "MONSTER");
		loadMobSettings("Joust", 6, 10, 3, 4, "CREATURE");
		loadMobSettings("JoustAlpha", 2, 2, 1, 2, "CREATURE");
		loadMobSettings("Erepede", 4, 5, 1, 2, "MONSTER");
		loadMobSettings("Gorgomite", 6, 40, 1, 3, "MONSTER");
		loadMobSettings("Manticore", 4, 10, 1, 5, "MONSTER");
	}
}