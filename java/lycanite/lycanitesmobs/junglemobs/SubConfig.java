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
		loadSetting(this.featureInts, "Feature Control", "ConcapedeSizeLimit", "Concapede Size Limit", 20);
		
		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "DespawnConcapedesOnPeaceful", "Concapede Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnConcapedesNaturally", "Concapede Natural Despawning", false);
		
		// ========== Mob Control ==========
		loadMobSettings("Geken", 8, 3, 1, 3, "MONSTER");
		loadMobSettings("Uvaraptor", 5, 3, 1, 3, "MONSTER");
		loadMobSettings("Concapede", 18, 3, 1, 3, "CREATURE");
		loadMobSettings("ConcapedeSegment", 0, 0, 0, 0, "CREATURE", "NONE", "NONE");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "JungleEgg", "Jungle Spawn Egg ID", 24040);
		loadSetting(itemIDs, "Item IDs", "ConcapedeMeatRaw", "Concapede Meat Raw ID", 24041);
		loadSetting(itemIDs, "Item IDs", "ConcapedeMeatCooked", "Concapede Meat Cooked ID", 24042);
		loadSetting(itemIDs, "Item IDs", "TropicalCurry", "Tropical Curry ID", 24043);
	}
}