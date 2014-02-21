package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "JUNGLE");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");
		
		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "DespawnConcapedesOnPeaceful", "Concapede Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnConcapedesNaturally", "Concapede Natural Despawning", false);
		
		// ========== Mob Control ==========
		loadMobSettings("Geken", 8, 3, 1, 3, "MONSTER");
		loadMobSettings("Uvaraptor", 4, 3, 1, 3, "MONSTER");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "JungleEgg", "Jungle Spawn Egg ID", 24040);
	}
}