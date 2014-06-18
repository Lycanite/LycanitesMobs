package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.OldConfig;

public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "JUNGLE");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");
		loadSetting(this.featureInts, "Feature Control", "ConcapedeSizeLimit", "Concapede Size Limit", 10);
		
		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "ConcapedesOnPeaceful", "Concapede Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnConcapedesNaturally", "Concapede Natural Despawning", false);
		loadSetting(this.featureBools, "Feature Control", "QuickWeb", "Enable Quick Webs", true);
		
		// ========== Mob Control ==========
		loadMobSettings("Geken", 8, 10, 1, 3, "MONSTER");
		loadMobSettings("Uvaraptor", 5, 10, 1, 3, "MONSTER");
		loadMobSettings("Concapede", 18, 10, 1, 3, "CREATURE");
		loadMobSettings("ConcapedeSegment", 0, 0, 0, 0, "CREATURE", "NONE", "NONE");
		loadMobSettings("Tarantula", 6, 10, 1, 2, "MONSTER");
	}
}