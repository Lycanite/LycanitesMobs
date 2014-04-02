package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "WATER, -RIVER, BEACH");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		// None
		
		// ========== Mob Control ==========
		loadMobSettings("Lacedon", 8, 3, 1, 3, "WATERCREATURE");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "SaltwaterEgg", "Saltwater Spawn Egg ID", 24120);
	}
}