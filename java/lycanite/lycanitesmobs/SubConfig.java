package lycanite.lycanitesmobs;


public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		// ========== Feature Control ==========
		this.loadSetting(this.featureBools, "Feature Control", "OwnerTags", "Show Pet Owner Tags", true);
		this.loadSetting(this.featureBools, "Feature Control", "MobTaming", "Allow Mob Taming", true);
		this.loadSetting(this.featureBools, "Feature Control", "MobMounting", "Allow Mob Mounting", true);
		
		// ========== Global Spawn Control ==========
		this.loadSetting(this.featureInts, "Spawn Control", "SpawnLimitRange", "Spawn Limit Search Range", 32);
		this.loadSetting(this.featureBools, "Spawn Control", "DisableAllSpawning", "Disable All Spawning", false);
		this.loadSetting(this.featureBools, "Spawn Control", "DisableDungeonSpawners", "Disable Dungeon Spawners", false);
		
		this.loadSetting(this.featureBools, "Spawn Control", "FireSpawnEnabled", "Fire Spawning Enabled", true);
		this.loadSetting(this.featureInts, "Spawn Control", "FireSpawnTick", "Fire Spawning Tick Rate", 400);
		this.loadSetting(this.featureDoubles, "Spawn Control", "FireSpawnChance", "Fire Spawning Chance", 0.25D);
		this.loadSetting(this.featureInts, "Spawn Control", "FireSpawnRange", "Fire Spawning Search Range", 32);
		this.loadSetting(this.featureInts, "Spawn Control", "FireSpawnBlockLimit", "Fire Spawning Block Limit", 32);
		this.loadSetting(this.featureInts, "Spawn Control", "FireSpawnMobLimit", "Fire Spawning Mob Limit", 32);
		
		// ========== Stat Multipliers ==========
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Easy", "0.5");
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Normal", "1.0");
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Hard", "1.5");
		
		// ========== Debugging ==========
		this.loadSetting(this.debugBools, "Debugging", "MobSetup", "Print Mob Setup", false);
		this.loadSetting(this.debugBools, "Debugging", "MobSpawns", "Print Mob Spawns", false);
		
		// ========== Mob Control ==========
		// No global mobs.
		
		// ========== Block IDs ==========
		// No global blocks.
		
		// ========== Item IDs ==========
		// No global items.
		
		// ========== Effect IDs ==========
		int effectStartID = 64;
		this.loadSetting(this.effectIDs, "Effect IDs", "Paralysis", "Paralysis Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Leech", "Leech Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Penetration", "Penetration Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Recklessness", "Recklessness Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Rage", "Rage Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Weight", "Weight Effect ID", effectStartID++);
		this.loadSetting(this.effectIDs, "Effect IDs", "Swiftswimming", "Swiftswimming Effect ID", effectStartID++);
	}
}