package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
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
		loadMobSettings("Belph", 100, 12, 3, 4, "MONSTER");
		loadMobSettings("Behemoth", 20, 2, 1, 1, "MONSTER");
		loadMobSettings("Pinky", 60, 1, 1, 3, "MONSTER");
		loadMobSettings("Trite", 120, 40, 5, 10, "MONSTER");
		loadMobSettings("Asmodi", 20, 1, 1, 1, "MONSTER");
		loadMobSettings("NetherSoul", 60, 12, 4, 8, "MONSTER");
		loadMobSettings("Cacodemon", 40, 1, 1, 1, "MONSTER");
		
		// ========== Block IDs ==========
		loadSetting(blockIDs, "Block IDs", "Hellfire", "Hellfire Block ID", 3856);
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "DemonEgg", "Demon Spawn Egg ID", 24060);
		loadSetting(itemIDs, "Item IDs", "DoomfireCharge", "Doomfire Charge ID", 24061);
		loadSetting(itemIDs, "Item IDs", "HellfireCharge", "Hellfire Charge ID", 24062);
		loadSetting(itemIDs, "Item IDs", "DevilstarCharge", "Devilstar Charge ID", 24063);
		loadSetting(itemIDs, "Item IDs", "DemonicLightningCharge", "Demonic Lightning Charge ID", 24064);
		loadSetting(itemIDs, "Item IDs", "PinkyMeatRaw", "Raw Pinky Meat ID", 24065);
		loadSetting(itemIDs, "Item IDs", "PinkyMeatCooked", "Cooked Pinky Meat ID", 24066);
		loadSetting(itemIDs, "Item IDs", "DoomfireScepter", "Doomfire Scepter ID", 24067);
		loadSetting(itemIDs, "Item IDs", "HellfireScepter", "Hellfire Scepter ID", 24068);
		loadSetting(itemIDs, "Item IDs", "DevilstarScepter", "Devilstar Scepter ID", 24069);
		loadSetting(itemIDs, "Item IDs", "DemonicLightningScepter", "Demonic Lightning Scepter ID", 24070);
		loadSetting(itemIDs, "Item IDs", "DevilLasagna", "Devil Lasagna ID", 24071);
	}
}