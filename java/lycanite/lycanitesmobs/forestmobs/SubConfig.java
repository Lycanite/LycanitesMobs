package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "FOREST");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Ent", 6, 10, 1, 3, "MONSTER");
		loadMobSettings("Trent", 1, 2, 1, 1, "MONSTER");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "ForestEgg", "Forest Spawn Egg ID", 24020);
	}
}