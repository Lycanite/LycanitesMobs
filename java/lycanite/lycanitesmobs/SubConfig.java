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
		this.loadSetting(this.featureInts, "Feature Control", "SpawnLimitSearchRadius", "Spawn Limit Search Radius", 64);
		this.loadSetting(this.featureBools, "Feature Control", "DisableAllSpawning", "Disable All Spawning", false);
		this.loadSetting(this.featureBools, "Feature Control", "DisableDungeonSpawners", "Disable Dungeon Spawners", false);
		
		// ========== Stat Multipliers ==========
		this.loadStatMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Easy", "Easy Difficulty Multiplier", "0.5");
		this.loadStatMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Normal", "Normal Difficulty Multiplier", "1.0");
		this.loadStatMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Hard", "Hard Difficulty Multiplier", "1.5");
		
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