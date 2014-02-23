package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "PLAINS");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "KoboldThievery", "Kobold Thievery", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnMakasOnPeaceful", "Maka Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnMakasNaturally", "Maka Natural Despawning", false);
		
		// ========== Mob Control ==========
		loadMobSettings("Kobold", 8, 4, 1, 3, "MONSTER");
		loadMobSettings("Ventoraptor", 4, 3, 1, 3, "MONSTER");
		loadMobSettings("Maka", 6, 3, 1, 3, "CREATURE");
		loadMobSettings("MakaAlpha", 2, 2, 1, 2, "CREATURE");
		
		// ========== Block IDs ==========
		//loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "PlainsEgg", "Plains Spawn Egg ID", 24010);
		loadSetting(itemIDs, "Item IDs", "MakaMeatRaw", "Raw Maka Meat ID", 24011);
		loadSetting(itemIDs, "Item IDs", "MakaMeatCooked", "Cooked Maka Meat ID", 24012);
		loadSetting(itemIDs, "Item IDs", "BulwarkBurger", "Bulwark Burger ID", 24013);
	}
}