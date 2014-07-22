package lycanite.lycanitesmobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.config.Configuration;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class OldConfig {
	// To read from this config use the maps.
	// Example of getting a block ID: Config.blockIDs.get("blockName");
	// A lot of settings can be added outside of this class too use the loadSetting functions and config.save();
	
	// Configuration:
	public Configuration config;
	
	// Feature Control:
	public Map<String, Boolean> featureBools = new HashMap<String, Boolean>();
	public Map<String, Integer> featureInts = new HashMap<String, Integer>();
	public Map<String, Double> featureDoubles = new HashMap<String, Double>();
	public Map<String, String> featureStrings = new HashMap<String, String>();
	
	// Stat Multipliers and Boosts:
	public Map<String, Double> difficultyMultipliers = new HashMap<String, Double>(); // Affects all stats, for each difficulty.
	public Map<String, Double> defenseMultipliers = new HashMap<String, Double>(); // Scale of defense.
	public Map<String, Integer> defenseBoosts = new HashMap<String, Integer>(); // Additional defense.
	public Map<String, Double> speedMultipliers = new HashMap<String, Double>(); // Speed of movement.
	public Map<String, Integer> speedBoosts = new HashMap<String, Integer>(); // Additional speed.
	public Map<String, Double> damageMultipliers = new HashMap<String, Double>(); // Scale of damage.
	public Map<String, Integer> damageBoosts = new HashMap<String, Integer>(); // Additional damage.
	public Map<String, Double> hasteMultipliers = new HashMap<String, Double>(); // Speed of abilities.
	public Map<String, Integer> hasteBoosts = new HashMap<String, Integer>(); // Additional ability speed.
	public Map<String, Double> effectMultipliers = new HashMap<String, Double>(); // Duration of effects.
	public Map<String, Integer> effectBoosts = new HashMap<String, Integer>(); // Additional effect duration.
	
	// Debugging:
	public Map<String, Boolean> debugBools = new HashMap<String, Boolean>();
	
	// Mob Control - General:
	public Map<String, Boolean> mobsEnabled = new HashMap<String, Boolean>();
	public Map<String, String> customDrops = new HashMap<String, String>();
	public Map<String, Boolean> defaultDrops = new HashMap<String, Boolean>();
	
	// Mob Control - Spawning
	public Map<String, Boolean> spawnEnabled = new HashMap<String, Boolean>();
	public Map<String, String> spawnTypes = new HashMap<String, String>();
	public Map<String, Boolean> mobsPeaceful = new HashMap<String, Boolean>();
	
	public Map<String, String> spawnDimensions = new HashMap<String, String>();
	public Map<String, String> spawnBiomes = new HashMap<String, String>();
	public Map<String, String> customSpawns = new HashMap<String, String>();
	
	public Map<String, Integer> spawnWeights = new HashMap<String, Integer>();
	public Map<String, Integer> spawnChances = new HashMap<String, Integer>();
	public Map<String, Integer> dungeonWeights = new HashMap<String, Integer>();
	
	public Map<String, Integer> spawnLimits = new HashMap<String, Integer>();
	public Map<String, Integer> spawnMins = new HashMap<String, Integer>();
	public Map<String, Integer> spawnMaxs = new HashMap<String, Integer>();
	public Map<String, Integer> spawnBlockCosts = new HashMap<String, Integer>();
	
	public Map<String, Boolean> despawnNaturals = new HashMap<String, Boolean>();
	public Map<String, Boolean> despawnForced = new HashMap<String, Boolean>();
	
	// Items:
	public Map<String, String> itemLists = new HashMap<String, String>();
	
	// Effect IDs:
	public Map<String, Integer> effectIDs = new HashMap<String, Integer>();
	
	
	// ==================================================
	//                 Initialize Config
	// ==================================================
	public void init(String submodID) {
		
		// ========== Create/Load Config File ==========
		String configDirPath = LycanitesMobs.proxy.getMinecraftDir() + "/config/" + LycanitesMobs.modid;
		File configDir = new File(configDirPath);
		configDir.mkdir();
		File configFile = new File(configDirPath + "/" + submodID + ".cfg");
	    try {
	    	configFile.createNewFile();
	    	System.out.println("[INFO] [LycanitesMobs-" + submodID + "] Successfully created/read configuration file.");
	    }
		catch (IOException e) {
	    	System.out.println("[SEVERE] [LycanitesMobs-" + submodID + "] Could not create configuration file:");
	    	System.out.println(e);
	    	System.out.println("Make sure the config folder isn't read only and (if using Windows) that Minecraft is not in Program Files on a non-administrator account.");
		}
	    
	    // ========== Run Config File ==========
		this.config = new Configuration(configFile);
		this.config.load();
		this.loadSettings();
		this.config.save();
	}
		
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	public void loadSettings() {
		// ========== Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "ControlVanilla", "Control Vanilla Mobs", true);
		
		// ========== Mob Stat Multipliers and Boosts ==========
		loadStatMultiplier(this.defenseMultipliers, "Mob Control", "GROUP", "Group Stat Defense Multiplier", "1.0");
		loadStatBoost(this.defenseBoosts, "Mob Control", "GROUP", "Group Stat Defense Boost", "0");
		loadStatMultiplier(this.speedMultipliers, "Mob Control", "GROUP", "Group Stat Speed Multiplier", "1.0");
		loadStatBoost(this.speedBoosts, "Mob Control", "GROUP", "Group Stat Speed Boost", "0");
		loadStatMultiplier(this.damageMultipliers, "Mob Control", "GROUP", "Group Stat Damage Multiplier", "1.0");
		loadStatBoost(this.damageBoosts, "Mob Control", "GROUP", "Group Stat Damage Boost", "0");
		loadStatMultiplier(this.hasteMultipliers, "Mob Control", "GROUP", "Group Stat Haste Multiplier", "1.0");
		loadStatBoost(this.hasteBoosts, "Mob Control", "GROUP", "Group Stat Haste Boost", "0");
		loadStatMultiplier(this.effectMultipliers, "Mob Control", "GROUP", "Group Stat Effect Multiplier", "1.0");
		loadStatBoost(this.effectBoosts, "Mob Control", "GROUP", "Group Stat Effect Boost", "0");
	}
	
	
	// ==================================================
	//                    Load Setting
	// ==================================================
	// Loads a setting from the config and saves the default if it isn't provided. SettingID should be unique. Will change DEFAULT to the default setting.
	public void loadSetting(Map<String, Boolean> settingMap, String settingCategory, String settingID, String settingName, boolean settingDefault) {
		Boolean setting = config.get(settingCategory, settingName, settingDefault).getBoolean(settingDefault);
		settingMap.put(settingID.toLowerCase(), setting);
	}
	
	public void loadSetting(Map<String, Integer> settingMap, String settingCategory, String settingID, String settingName, int settingDefault) {
		int setting = config.get(settingCategory, settingName, settingDefault).getInt(settingDefault);
		settingMap.put(settingID.toLowerCase(), setting);
	}
	
	public void loadSetting(Map<String, Double> settingMap, String settingCategory, String settingID, String settingName, double settingDefault) {
		double setting = config.get(settingCategory, settingName, settingDefault).getDouble(settingDefault);
		settingMap.put(settingID.toLowerCase(), setting);
	}
	
	public void loadSetting(Map<String, String> settingMap, String settingCategory, String settingID, String settingName, String settingDefault) {
		String setting = config.get(settingCategory, settingName, settingDefault).getString();
		if("DEFAULT".equalsIgnoreCase(setting)) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			setting = settingDefault;
			config.save();
		}
		settingMap.put(settingID.toLowerCase(), setting);
	}
	
	public void loadSettingSpawnType(String settingCategory, String settingID, String settingName, String settingDefault) {
		String typeString = config.get(settingCategory, settingName, settingDefault).getString().toUpperCase();
		if("DEFAULT".equalsIgnoreCase(typeString)) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			typeString = settingDefault;
			config.save();
		}
		
		this.spawnTypes.put(settingID.toLowerCase(), typeString);
	}
	
	// ========== Mob Settings ==========
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName, int spawnBlockCost, String spawnBiome, String spawnDimension) {
		// General:
		loadSetting(this.mobsEnabled, "Mob Control - General", mobName, mobName + " Enabled", true);
		loadSetting(this.customDrops, "Mob Control - General", mobName, mobName + " Custom Drops", "");
		loadSetting(this.defaultDrops, "Mob Control - General", mobName, mobName + " Enable Default Drops", true);
		loadSetting(this.mobsPeaceful, "Mob Control - General", mobName, mobName + " Allowed On Peaceful Difficulty", spawnTypeName.equalsIgnoreCase("CREATURE") || mobName.equalsIgnoreCase("pinky") || mobName.equalsIgnoreCase("ika"));
		
		// Spawning - Type:
		loadSetting(this.spawnEnabled, "Mob Spawning - Type", mobName, mobName + " Spawn Enabled", spawnWeight > 0);
		loadSettingSpawnType("Mob Spawning - Type", mobName, mobName + " Spawn Type", spawnTypeName);
		
		// Spawning - Location:
		loadSetting(this.spawnDimensions, "Mob Spawning - Location", mobName, mobName + " Spawn Dimensions", spawnDimension);
		loadSetting(this.spawnBiomes, "Mob Spawning - Location", mobName, mobName + " Spawn Biome Types", spawnBiome);
		
		// Spawning - Chance:
		loadSetting(this.spawnWeights, "Mob Spawning - Chance", mobName, mobName + " Spawn Weight", spawnWeight);
		loadSetting(this.spawnChances, "Mob Spawning - Chance", mobName, mobName + " Spawn Chance", 100);
		int dungeonSpawnDefault = spawnTypeName.equalsIgnoreCase("CREATURE") || spawnTypeName.equalsIgnoreCase("AMBIENT") ? 0 : spawnWeight * 25;
		loadSetting(this.dungeonWeights, "Mob Spawning - Chance", mobName, mobName + " Spawn Dungeon Weight", dungeonSpawnDefault);
		
		// Spawning - Limits:
		loadSetting(this.spawnLimits, "Mob Spawning - Limits", mobName, mobName + " Spawn Area Limit", spawnLimit);
		loadSetting(this.spawnMins, "Mob Spawning - Limits", mobName, mobName + " Spawn Group Size Min", spawnMin);
		loadSetting(this.spawnMaxs, "Mob Spawning - Limits", mobName, mobName + " Spawn Group Size Max", spawnMax);
		loadSetting(this.spawnBlockCosts, "Mob Spawning - Limits", mobName, mobName + " Spawn Required Block Amount", spawnBlockCost);
		
		// Spawning - Despawning:
		loadSetting(this.despawnNaturals, "Mob Spawning - Despawning", mobName, mobName + " Natural Despawning", !spawnTypeName.equalsIgnoreCase("CREATURE"));
		loadSetting(this.despawnForced, "Mob Spawning - Despawning", mobName, mobName + " Forced Despawning", false);
		
		// Stat Modifiers and Boosts:
		loadStatMultiplier(this.defenseMultipliers, "Mob Control - Stats", mobName, mobName + " Stat Defense Multiplier", "GROUP");
		loadStatBoost(this.defenseBoosts, "Mob Control - Stats", mobName, mobName + " Stat Defense Boost", "GROUP");
		loadStatMultiplier(this.speedMultipliers, "Mob Control - Stats", mobName, mobName + " Stat Speed Multiplier", "GROUP");
		loadStatBoost(this.speedBoosts, "Mob Control - Stats", mobName, mobName + " Stat Speed Boost", "GROUP");
		loadStatMultiplier(this.damageMultipliers, "Mob Control - Stats", mobName, mobName + " Stat Damage Multiplier", "GROUP");
		loadStatBoost(this.damageBoosts, "Mob Control - Stats", mobName, mobName + " Stat Damage Boost", "GROUP");
		loadStatMultiplier(this.hasteMultipliers, "Mob Control - Stats", mobName, mobName + " Stat Haste Multiplier", "GROUP");
		loadStatBoost(this.hasteBoosts, "Mob Control - Stats", mobName, mobName + " Stat Haste Boost", "GROUP");
		loadStatMultiplier(this.effectMultipliers, "Mob Control - Stats", mobName, mobName + " Stat Effect Multiplier", "GROUP");
		loadStatBoost(this.effectBoosts, "Mob Control - Stats", mobName, mobName + " Stat Effect Boost", "GROUP");
	}
	// Load Mob Settings - Auto Spawn Block Cost:
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName, String spawnBiome, String spawnDimension) {
		this.loadMobSettings(mobName, spawnWeight, spawnLimit, spawnMin, spawnMax, spawnTypeName, 1, spawnBiome, spawnDimension);
	}
	// Load Mob Settings - Auto Biomes and Dimensions:
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName, int spawnBlockCost) {
		this.loadMobSettings(mobName, spawnWeight, spawnLimit, spawnMin, spawnMax, spawnTypeName, spawnBlockCost, "GROUP", "GROUP");
	}
	// Load Mob Settings - Auto Spawn Block Cost, Biomes and Dimensions:
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName) {
		this.loadMobSettings(mobName, spawnWeight, spawnLimit, spawnMin, spawnMax, spawnTypeName, 1, "GROUP", "GROUP");
	}
	
	// ========== Stat Multipliers and Boosts ==========
	public void loadDifficultyMultiplier(Map<String, Double> statMap, String settingCategory, String settingID, String settingDefault) {
		String[] statIDs = {"Defense", "Speed", "Damage", "Haste", "Effect"};
		for(String statID : statIDs) {
			String settingName = settingID + " Difficulty " + statID + " Multiplier";
			if("Easy".equalsIgnoreCase(settingID) && "Speed".equalsIgnoreCase(statID))
				settingDefault = "1.0";
			String statString = config.get(settingCategory,  settingName, settingDefault).getString().toUpperCase();
			double statValue = 1.0D;
			if("DEFAULT".equalsIgnoreCase(statString) && statMap != this.difficultyMultipliers) {
				config.get(settingCategory, settingName, settingDefault).set(settingDefault);
				statString = settingDefault;
				config.save();
			}
			
			if("GROUP".equalsIgnoreCase(statString) && statMap != this.difficultyMultipliers) {
				if(statMap.containsKey("GROUP"))
					statValue = statMap.get("GROUP");
			}
			else {
				try {
					statValue = Double.parseDouble(statString);
				}
				catch(Exception e) {
					System.out.println("[WARNING] [LycanitesMobs] Invalid stat multiplier: " + statString + ". The value must be either DEFAULT, GROUP or a decimal value such as 1.0 or 1.5 or 0.2. Using 1.0.");
					statValue = 1.0D;
				}
			}
			
			statMap.put(settingID.toUpperCase() + "-" + statID.toUpperCase(), statValue);
		}
	}
	
	public void loadStatMultiplier(Map<String, Double> statMap, String settingCategory, String settingID, String settingName, String settingDefault) {
		String statString = config.get(settingCategory, settingName, settingDefault).getString().toUpperCase();
		double statValue = 1.0D;
		if("DEFAULT".equalsIgnoreCase(statString) && statMap != this.difficultyMultipliers) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			statString = settingDefault;
			config.save();
		}
		
		if("GROUP".equalsIgnoreCase(statString) && statMap != this.difficultyMultipliers) {
			if(statMap.containsKey("GROUP"))
				statValue = statMap.get("GROUP");
		}
		else {
			try {
				statValue = Double.parseDouble(statString);
			}
			catch(Exception e) {
				System.out.println("[WARNING] [LycanitesMobs] Invalid stat multiplier: " + statString + ". The value must be either DEFAULT, GROUP or a decimal value such as 1.0 or 1.5 or 0.2. Using 1.0.");
				statValue = 1.0D;
			}
		}
		
		statMap.put(settingID.toLowerCase(), statValue);
	}
	
	public void loadStatBoost(Map<String, Integer> statMap, String settingCategory, String settingID, String settingName, String settingDefault) {
		String statString = config.get(settingCategory, settingName, settingDefault).getString().toUpperCase();
		int statValue = 0;
		if("DEFAULT".equalsIgnoreCase(statString)) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			statString = settingDefault;
			config.save();
		}
		
		if("GROUP".equalsIgnoreCase(statString)) {
			if(statMap.containsKey("GROUP"))
				statValue = statMap.get("GROUP");
		}
		else {
			try {
				statValue = Integer.parseInt(statString);
			}
			catch(Exception e) {
				System.out.println("[WARNING] [LycanitesMobs] Invalid stat boost: " + statString + ". The value must be either DEFAULT, GROUP or a whole value such as 1 or 3 or 10. Using 0.");
				statValue = 0;
			}
		}
		
		statMap.put(settingID.toLowerCase(), statValue);
	}
	
	
	// ==================================================
	//                 Get Setting Values
	// ==================================================
	public boolean getFeatureBool(String key) {
		key = key.toLowerCase();
		if(this.featureBools.containsKey(key))
			return this.featureBools.get(key) != null ? this.featureBools.get(key) : false;
		else
			return false;
	}
	
	public int getFeatureInt(String key) {
		key = key.toLowerCase();
		if(this.featureInts.containsKey(key))
			return this.featureInts.get(key) != null ? this.featureInts.get(key) : 0;
		else
			return 0;
	}
	
	public double getFeatureDouble(String key) {
		key = key.toLowerCase();
		if(this.featureDoubles.containsKey(key))
			return this.featureDoubles.get(key) != null ? this.featureDoubles.get(key) : 0;
		else
			return 0;
	}
	
	public String getFeatureString(String key) {
		key = key.toLowerCase();
		if(this.featureStrings.containsKey(key))
			return this.featureStrings.get(key) != null ? this.featureStrings.get(key) : "";
		else
			return "";
	}
	
	public boolean getDebug(String key) {
		key = key.toLowerCase();
		if(this.debugBools.containsKey(key))
			return this.debugBools.get(key) != null ? this.debugBools.get(key) : false;
		else
			return false;
	}
	
	// ========== Biome Types ==========
	public BiomeGenBase[] getSpawnBiomesTypes() {
		return this.getSpawnBiomesTypes("GROUP-BIOMES-ONLY");
	}
	
	public BiomeGenBase[] getSpawnBiomesTypes(String mobName) {
		String groupTypeNames = this.getFeatureString("BiomeTypes").toUpperCase().replace(" ", "");
		String biomeTypeNames = groupTypeNames;
		if(!"GROUP-BIOMES-ONLY".equals(mobName) && this.spawnBiomes.containsKey(mobName)) {
			String mobTypeNames = this.spawnBiomes.get(mobName).toUpperCase().replace(" ", "");
			biomeTypeNames = mobTypeNames.replace("GROUP", groupTypeNames);
		}
		
		BiomeGenBase[] biomes = new BiomeGenBase[0];
		ArrayList<BiomeGenBase> customBiomes = new ArrayList<BiomeGenBase>();
		for(String biomeTypeName : biomeTypeNames.split(",")) {
			BiomeGenBase[] selectedBiomes = null;
			boolean additive = true;
			if(biomeTypeName.charAt(0) == '-' || biomeTypeName.charAt(0) == '+') {
				if(biomeTypeName.charAt(0) == '-')
					additive = false;
				biomeTypeName = biomeTypeName.substring(1);
			}
			
			if("RIVER".equals(biomeTypeName))
				selectedBiomes = new BiomeGenBase[] {BiomeGenBase.river, BiomeGenBase.frozenRiver};
			else if("ALL".equals(biomeTypeName)) {
				for(BiomeDictionary.Type biomeType : BiomeDictionary.Type.values()) {
					if(selectedBiomes == null)
						selectedBiomes = BiomeDictionary.getBiomesForType(biomeType);
					else
						selectedBiomes = ArrayUtils.addAll(selectedBiomes, BiomeDictionary.getBiomesForType(biomeType));
				}
			}
			else if(!"NONE".equals(biomeTypeName)) {
				BiomeDictionary.Type biomeType = null;
				try { biomeType = BiomeDictionary.Type.valueOf(biomeTypeName); }
				catch(Exception e) {
					biomeType = null;
					System.out.println("[WARNING] [" + LycanitesMobs.name + "] Unknown biome type " + biomeTypeName + " specified for " + mobName + " this will be ignored and treated as NONE.");
				}
				if(biomeType != null)
					selectedBiomes = BiomeDictionary.getBiomesForType(biomeType);
			}
			
			if(selectedBiomes != null) {
				for(BiomeGenBase biome : selectedBiomes)
					if(additive && !customBiomes.contains(biome)) {
						customBiomes.add(biome);
					}
					else if(!additive && customBiomes.contains(biome)) {
						customBiomes.remove(biome);
					}
			}
		}
		if(customBiomes.size() > 0)
			biomes = customBiomes.toArray(new BiomeGenBase[customBiomes.size()]);
		else
			biomes = new BiomeGenBase[0];
		return biomes;
	}
	
	// ========== Dimensions ==========
	public int[] getSpawnDimensions(String mobName) {
		String groupDimensions = this.getFeatureString("dimensions").toUpperCase().replace(" ", "");
		String spawnDimensionsString = this.spawnDimensions.get(mobName).toUpperCase().replace(" ", "").replace("GROUP", groupDimensions);
		
		int[] dimensions = new int[0];
		ArrayList<Integer> customDimensions = new ArrayList<Integer>();
		for(String dimensionID : spawnDimensionsString.split(",")) {
			if(StringUtils.isNumeric(dimensionID))
				customDimensions.add(Integer.parseInt(dimensionID.replace("+", "")));
		}
		if(customDimensions.size() > 0)
			dimensions = ArrayUtils.toPrimitive(customDimensions.toArray(new Integer[customDimensions.size()]));
		
		return dimensions;
	}
	
	public String[] getSpawnDimensionTypes(String mobName) {
		String groupDimensions = this.getFeatureString("dimensions").toUpperCase().replace(" ", "");
		String spawnDimensionsString = this.spawnDimensions.get(mobName).toUpperCase().replace(" ", "").replace("GROUP", groupDimensions);
		
		String[] dimensionTypes = new String[0];
		ArrayList<String> customDimensions = new ArrayList<String>();
		for(String dimensionType : spawnDimensionsString.split(",")) {
			if(!StringUtils.isNumeric(dimensionType))
				customDimensions.add(dimensionType);
		}
		if(customDimensions.size() > 0)
			dimensionTypes = customDimensions.toArray(new String[customDimensions.size()]);
		
		return dimensionTypes;
	}
}