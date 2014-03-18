package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "ALL");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0, -1, 1");

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Cinder", 8, 4, 1, 3, "AMBIENT");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "InfernoEgg", "Inferno Spawn Egg ID", 24100);
		loadSetting(itemIDs, "Item IDs", "EmberCharge", "Ember Charge ID", 24101);
		loadSetting(itemIDs, "Item IDs", "EmberScepter", "Ember Scepter ID", 24102);
	}
}