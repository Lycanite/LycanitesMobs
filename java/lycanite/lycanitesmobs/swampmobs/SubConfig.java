package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "SWAMP");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "PoisonCloud", "Enable Poison Clouds", true);
		loadSetting(this.featureBools, "Feature Control", "AspidsOnPeaceful", "Aspid Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnAspidsNaturally", "Aspid Natural Despawning", false);
		
		// ========== Mob Control ==========
		loadMobSettings("GhoulZombie", 8, 10, 1, 3, "MONSTER");
		loadMobSettings("Dweller", 8, 5, 1, 3, "WATERCREATURE");
		loadMobSettings("Ettin", 3, 2, 1, 1, "MONSTER");
		loadMobSettings("Lurker", 6, 5, 1, 3, "MONSTER");
		loadMobSettings("Eyewig", 3, 5, 1, 1, "MONSTER");
		loadMobSettings("Aspid", 12, 10, 1, 3, "CREATURE");
		loadMobSettings("Remobra", 4, 10, 1, 3, "MONSTER");
		
		// ========== Block IDs ==========
		loadSetting(blockIDs, "Block IDs", "PoisonCloud", "Poison Cloud Block ID", 3853);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "SwampEgg", "Swamp Spawn Egg ID", 24030);
		loadSetting(itemIDs, "Item IDs", "Chitin", "Chitin ID", 24031);
		loadSetting(itemIDs, "Item IDs", "AspidMeatRaw", "Raw Aspid Meat ID", 24032);
		loadSetting(itemIDs, "Item IDs", "AspidMeatCooked", "Cooked Aspid Meat ID", 24033);
		loadSetting(itemIDs, "Item IDs", "PoisonGland", "Poison Gland ID", 24034);
		loadSetting(itemIDs, "Item IDs", "PoisonRayScepter", "Poison Ray Scepter ID", 24035);
		loadSetting(itemIDs, "Item IDs", "VenomShotScepter", "Venom Shot Scepter ID", 24036);
		loadSetting(itemIDs, "Item IDs", "MossPie", "Moss Pie ID", 24037);
	}
}