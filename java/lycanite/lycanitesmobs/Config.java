package lycanite.lycanitesmobs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.Configuration;

import org.apache.commons.lang3.ArrayUtils;

public class Config {
	// To read from this config use the maps.
	// Example of getting a block ID: Config.blockIDs.get("blockName");
	// A lot of settings can be added outside of this class too use the loadSetting functions and config.save();
	
	// Configuration:
	public Configuration config;
	
	// Feature Control:
	public Map<String, Boolean> featureBools = new HashMap<String, Boolean>();
	public Map<String, Integer> featureInts = new HashMap<String, Integer>();
	public Map<String, String> featureStrings = new HashMap<String, String>();
	
	// Mob Control:
	public Map<String, Boolean> mobsEnabled = new HashMap<String, Boolean>();
	public Map<String, Integer> spawnChances = new HashMap<String, Integer>();
	public Map<String, Integer> spawnWeights = new HashMap<String, Integer>();
	public Map<String, Integer> spawnLimits = new HashMap<String, Integer>();
	public Map<String, Integer> spawnMins = new HashMap<String, Integer>();
	public Map<String, Integer> spawnMaxs = new HashMap<String, Integer>();
	public Map<String, EnumCreatureType> spawnTypes = new HashMap<String, EnumCreatureType>();
	public Map<String, String> spawnBiomes = new HashMap<String, String>();
	public Map<String, String> spawnDimensions = new HashMap<String, String>();
	public Map<String, String> customDrops = new HashMap<String, String>();
	
	// Block IDs:
	public Map<String, Integer> blockIDs = new HashMap<String, Integer>();
	
	// Item IDs:
	public Map<String, Integer> itemIDs = new HashMap<String, Integer>();
	
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
		config = new Configuration(configFile);
		config.load();
		loadSettings();
		config.save();
	}
		
	
	// ==================================================
	//               Load Config Settings
	// ==================================================
	public void loadSettings() {
		// ========== Feature Control ==========
		loadSetting(this.featureBools, "Feature Control", "ControlVanilla", "Control Vanilla Mobs", true);
	}
	
	
	// ==================================================
	//                    Load Setting
	// ==================================================
	// Loads a setting from the config and saves the default if it isn't provided. SettingID should be unique. Will change DEFAULT to the default setting.
	public void loadSetting(Map<String, Boolean> settingMap, String settingCategory, String settingID, String settingName, boolean settingDefault) {
		Boolean setting = config.get(settingCategory, settingName, settingDefault).getBoolean(settingDefault);
		settingMap.put(settingID, setting);
	}
	
	public void loadSetting(Map<String, Integer> settingMap, String settingCategory, String settingID, String settingName, int settingDefault) {
		int setting = config.get(settingCategory, settingName, settingDefault).getInt(settingDefault);
		settingMap.put(settingID, setting);
	}
	
	public void loadSetting(Map<String, String> settingMap, String settingCategory, String settingID, String settingName, String settingDefault) {
		String setting = config.get(settingCategory, settingName, settingDefault).getString();
		if("DEFAULT".equalsIgnoreCase(setting)) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			setting = settingDefault;
			config.save();
		}
		settingMap.put(settingID, setting);
	}
	
	public void loadSettingSpawnType(Map<String, EnumCreatureType> settingMap, String settingCategory, String settingID, String settingName, String settingDefault) {
		String typeString = config.get(settingCategory, settingName, settingDefault).getString().toUpperCase();
		if("DEFAULT".equalsIgnoreCase(typeString)) {
			config.get(settingCategory, settingName, settingDefault).set(settingDefault);
			typeString = settingDefault;
			config.save();
		}
		
		EnumCreatureType spawnType = EnumCreatureType.monster;
		if("CREATURE".equalsIgnoreCase(typeString) || "ANIMAL".equalsIgnoreCase(typeString))
			spawnType = EnumCreatureType.creature;
		else if("WATERCREATURE".equalsIgnoreCase(typeString))
			spawnType = EnumCreatureType.waterCreature;
		else if("AMBIENT".equalsIgnoreCase(typeString))
			spawnType = EnumCreatureType.ambient;
		else if(!"MONSTER".equalsIgnoreCase(typeString))
			System.out.println("[WARNING] [LycanitesMobs] Invalid spawn type " + typeString + " given for " + settingID + " using MONSTER instead.");
		
		settingMap.put(settingID, spawnType);
	}
	
	// ========== Mob Settings ==========
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName, String spawnBiome, String spawnDimension) {
		loadSetting(this.mobsEnabled, "Mob Control", mobName, mobName + " Enabled", true);
		loadSetting(this.spawnChances, "Mob Control", mobName, mobName + " Spawn Chance", 100);
		loadSetting(this.spawnWeights, "Mob Control", mobName, mobName + " Spawn Weight", spawnWeight);
		loadSetting(this.spawnLimits, "Mob Control", mobName, mobName + " Spawn Chunk Limit", spawnLimit);
		loadSetting(this.spawnMins, "Mob Control", mobName, mobName + " Chunk Spawn Min", spawnMin);
		loadSetting(this.spawnMaxs, "Mob Control", mobName, mobName + " Chunk Spawn Max", spawnMax);
		loadSettingSpawnType(this.spawnTypes, "Mob Control", mobName, mobName + " Spawn Type", spawnTypeName);
		loadSetting(this.spawnBiomes, "Mob Control", mobName, mobName + " Spawn Biome Types", spawnBiome);
		loadSetting(this.spawnDimensions, "Mob Control", mobName, mobName + " Spawn Dimensions", spawnDimension);
		loadSetting(this.customDrops, "Mob Control", mobName, mobName + " Custom Drops", "");
	}
	public void loadMobSettings(String mobName, int spawnWeight, int spawnLimit, int spawnMin, int spawnMax, String spawnTypeName) {
		this.loadMobSettings(mobName, spawnWeight, spawnLimit, spawnMin, spawnMax, spawnTypeName, "GROUP", "GROUP");
	}
	
	
	// ==================================================
	//                 Get Setting Values
	// ==================================================
	public boolean getFeatureBool(String key) {
		if(this.featureBools.containsKey(key))
			return this.featureBools.get(key) != null ? this.featureBools.get(key) : false;
		else
			return false;
	}
	
	public int getFeatureInt(String key) {
		if(this.featureInts.containsKey(key))
			return this.featureInts.get(key) != null ? this.featureInts.get(key) : 0;
		else
			return 0;
	}
	
	public String getFeatureString(String key) {
		if(this.featureStrings.containsKey(key))
			return this.featureStrings.get(key) != null ? this.featureStrings.get(key) : "";
		else
			return "";
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
				selectedBiomes = new BiomeGenBase[] {BiomeGenBase.river};
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
					if(additive && !customBiomes.contains(biome))
						customBiomes.add(biome);
					else if(!additive && customBiomes.contains(biome))
						customBiomes.remove(biome);
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
		String groupDimensions = this.getFeatureString("Dimensions").toUpperCase().replace(" ", "");
		String spawnDimensionsString = this.spawnDimensions.get(mobName).toUpperCase().replace(" ", "").replace("GROUP", groupDimensions);
		
		int[] dimensions = new int[0];
		ArrayList<Integer> customDimensions = new ArrayList<Integer>();
		for(String dimensionID : spawnDimensionsString.split(","))
			customDimensions.add(Integer.parseInt(dimensionID.replace("+", "")));
		if(customDimensions.size() > 0)
			dimensions = ArrayUtils.toPrimitive(customDimensions.toArray(new Integer[customDimensions.size()]));
		else
			dimensions = new int[0];
		
		return dimensions;
	}
}