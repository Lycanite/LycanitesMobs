package lycanite.lycanitesmobs;


public class SubConfig extends Config {
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	@Override
	public void loadSettings() {
		// ========== Feature Control ==========
		loadSetting(featureBools, "Feature Control", "OwnerTags", "Show Pet Owner Tags", true);
		loadSetting(featureBools, "Feature Control", "NightmareDifficulty", "Enable Nightmare Difficulty", false);
		loadSetting(featureBools, "Feature Control", "MobTaming", "Allow Mob Taming", true);
		loadSetting(featureBools, "Feature Control", "MobMounting", "Allow Mob Mounting", true);
		
		// ========== Mob Control ==========
		// No global mobs.
		
		// ========== Block IDs ==========
		// No global blocks.
		
		// ========== Item IDs ==========
		// No global items.
		
		// ========== Effect IDs ==========
		int effectStartID = 64;
		loadSetting(effectIDs, "Effect IDs", "Paralysis", "Paralysis Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Leech", "Leech Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Penetration", "Penetration Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Recklessness", "Recklessness Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Rage", "Rage Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Weight", "Weight Effect ID", effectStartID++);
		loadSetting(effectIDs, "Effect IDs", "Swiftswimming", "Swiftswimming Effect ID", effectStartID++);
	}
}