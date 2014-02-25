package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.Config;

public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		super.loadSettings();
		
		// ========== Feature Control ==========
		loadSetting(this.featureStrings, "Feature Control", "BiomeTypes", "Group Biome Types", "DESERT");
		loadSetting(this.featureStrings, "Feature Control", "Dimensions", "Group Dimensions", "0");

		// ========== Special Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "DespawnJoustsOnPeaceful", "Joust Peaceful Spawning", true);
		loadSetting(this.featureBools, "Feature Control", "DespawnJoustsNaturally", "Joust Natural Despawning", false);
		loadSetting(this.featureInts, "Feature Control", "GorgomiteSwarmLimit", "Gorgomite Swarm Limit", 20);
		
		// ========== Mob Control ==========
		loadMobSettings("CryptZombie", 8, 4, 1, 3, "MONSTER");
		loadMobSettings("Crusk", 2, 1, 1, 1, "MONSTER");
		loadMobSettings("Clink", 6, 4, 1, 3, "MONSTER");
		loadMobSettings("Joust", 6, 4, 3, 4, "CREATURE");
		loadMobSettings("JoustAlpha", 2, 2, 1, 2, "CREATURE");
		loadMobSettings("Erepede", 4, 2, 1, 2, "MONSTER");
		loadMobSettings("Gorgomite", 6, 4, 1, 3, "MONSTER");
		loadMobSettings("Manticore", 4, 4, 1, 5, "MONSTER");
		
		// ========== Block IDs ==========
		
		
		// ========== Item IDs ==========
		loadSetting(itemIDs, "Item IDs", "DesertEgg", "Desert Spawn Egg ID", 24050);
		loadSetting(itemIDs, "Item IDs", "ThrowingScythe", "Throwing Scythe ID", 24051);
		loadSetting(itemIDs, "Item IDs", "JoustMeatRaw", "Raw Joust Meat ID", 24052);
		loadSetting(itemIDs, "Item IDs", "JoustMeatCooked", "Cooked Joust Meat ID", 24053);
		loadSetting(itemIDs, "Item IDs", "MudshotCharge", "Mudshot Charge ID", 24054);
		loadSetting(itemIDs, "Item IDs", "ScytheScepter", "Scythe Scepter ID", 24055);
		loadSetting(itemIDs, "Item IDs", "MudshotScepter", "Mudshot Scepter ID", 24056);
		loadSetting(itemIDs, "Item IDs", "AmberCake", "Amber Cake ID", 24057);
	}
}