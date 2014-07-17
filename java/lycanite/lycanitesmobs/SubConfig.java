package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.info.ObjectLists;


public class SubConfig extends OldConfig {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		// ========== Feature Control ==========
		this.loadSetting(this.featureBools, "Feature Control", "OwnerTags", "Show Pet Owner Tags", true);
		this.loadSetting(this.featureBools, "Feature Control", "MobTaming", "Allow Mob Taming", true);
		this.loadSetting(this.featureBools, "Feature Control", "MobMounting", "Allow Mob Mounting", true);
		this.loadSetting(this.featureBools, "Feature Control", "MobsAttackVillagers", "Mobs Attack Villagers", false);
		this.loadSetting(this.featureBools, "Feature Control", "PredatorsAttackAnimals", "Predators Attack Animals", true);
		this.loadSetting(this.featureBools, "Feature Control", "CustomEffects", "Enable Custom Potion Effects", true);
		this.loadSetting(this.featureBools, "Feature Control", "InventoryTabs", "Show Inventory Tabs", true);
		this.loadSetting(this.featureBools, "Feature Control", "FriendlyFire", "Friendly Fire", true);
		
		// ========== Global Spawn Control ==========
		this.loadSetting(this.featureInts, "Spawn Control", "SpawnLimitRange", "Spawn Limit Search Range", 32);
		this.loadSetting(this.featureBools, "Spawn Control", "DisableAllSpawning", "Disable All Spawning", false);
		this.loadSetting(this.featureBools, "Spawn Control", "DisableDungeonSpawners", "Disable Dungeon Spawners", false);
		this.loadSetting(this.featureBools, "Spawn Control", "EnforceBlockCost", "Enforce Block Cost", true);
		this.loadSetting(this.featureDoubles, "Spawn Control", "SpawnWeightScale", "Spawn Weight Scale", 1.0D);
		this.loadSetting(this.featureDoubles, "Spawn Control", "DungeonSpawnerWeightScale", "Dungeon Spawner Weight Scale", 1.0D);
		
		this.loadCustomSpawnerType("Fire", 400, 0.5D, 32, 32, 32);
		this.loadCustomSpawnerType("Frostfire", 400, 0.5D, 32, 32, 32);
		this.loadCustomSpawnerType("Lava", 400, 0.25D, 64, 64, 32);
		this.loadCustomSpawnerType("Portal", 1200, 0.25D, 32, 32, 1);
		this.loadCustomSpawnerType("Rock", 0, 0.03D, 2, 128, 1);
		this.loadCustomSpawnerType("Storm", 800, 0.125D, 64, 32, 32);
		this.config.addCustomCategoryComment("Spawn Control", "Global spawn controls, applied to every group. Custom spawners such as Fire Spawning can be configured here too.");
		
		// ========== Stat Multipliers ==========
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Easy", "0.5");
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Normal", "1.0");
		this.loadDifficultyMultiplier(this.difficultyMultipliers, "Stat Multipliers", "Hard", "1.5");
		this.config.addCustomCategoryComment("Stat Multipliers", "Use these to scale the stats of mobs in different difficulties. 1.0 = 100%, 0.5 = 50%, 1.5 = 150%, etc.");
		
		// ========== Debugging ==========
		this.loadSetting(this.debugBools, "Debugging", "MobSetup", "Print Mob Setup", false);
		this.loadSetting(this.debugBools, "Debugging", "MobSpawns", "Print Mob Spawns", false);
		this.loadSetting(this.debugBools, "Debugging", "CustomSpawner", "Print Custom Spawner Operations", false);
		this.loadSetting(this.debugBools, "Debugging", "ItemSetup", "Print Custom Item Lists Setup", false);
		this.loadSetting(this.debugBools, "Debugging", "EffectsSetup", "Print Custom Effects Setup", false);
		this.config.addCustomCategoryComment("Debugging", "Any debugging options set to true will print out extra info to the console, very useful for bug reporting or testing custom configurations.");
		
		// ========== Mob Control ==========
		// No global mobs.
		
		// ========== Items ==========
		for(String itemListName : ObjectLists.itemListNames) {
			this.loadSetting(this.itemLists, "Item Lists", itemListName, itemListName, "");
		}
		
		// ========== Effect IDs ==========
		this.loadSetting(this.effectIDs, "Effect IDs", "Paralysis", "Paralysis Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Leech", "Leech Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Penetration", "Penetration Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Recklessness", "Recklessness Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Rage", "Rage Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Weight", "Weight Effect ID Override", 0);
		this.loadSetting(this.effectIDs, "Effect IDs", "Swiftswimming", "Swiftswimming Effect ID Override", 0);
		this.config.addCustomCategoryComment("Effect IDs", "If not set or 0, effect IDs will be automatically applied. Use the Overrides, other efect ID entries are from older configs and should be removed.");
	}
	
	// ==================================================
	//               Custom Spawner Settings
	// ==================================================
	public void loadCustomSpawnerType(String typeName, int rate, double chance, int range, int blockLimit, int mobLimit) {
		this.loadSetting(this.featureBools, "Spawn Control", typeName + "SpawnEnabled", typeName + " Spawning Enabled", true);
		this.loadSetting(this.featureInts, "Spawn Control", typeName + "SpawnTick", typeName + " Spawning Tick Rate", rate);
		this.loadSetting(this.featureDoubles, "Spawn Control", typeName + "SpawnChance", typeName + " Spawning Chance", chance);
		this.loadSetting(this.featureInts, "Spawn Control", typeName + "SpawnRange", typeName + " Spawning Search Range", range);
		this.loadSetting(this.featureInts, "Spawn Control", typeName + "SpawnBlockLimit", typeName + " Spawning Block Limit", blockLimit);
		this.loadSetting(this.featureInts, "Spawn Control", typeName + "SpawnMobLimit", typeName + " Spawning Mob Limit", mobLimit);
	}
}