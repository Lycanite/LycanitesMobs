package lycanite.lycanitesmobs.api.config;

import java.util.ArrayList;
import java.util.List;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.BiomeDictionary;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class ConfigSpawning extends ConfigBase {
	
	// ========== Config Collections ==========
	// Get Config:
     public static ConfigSpawning getConfig(GroupInfo group, String configName) {
         String configFileName = group.filename + "-" + configName.toLowerCase();
         if(!configs.containsKey(configFileName))
             registerConfig(new ConfigSpawning(group, configName));
         ConfigBase config = ConfigBase.configs.get(configFileName);
         if(config instanceof ConfigSpawning)
         	return (ConfigSpawning)config;
     	LycanitesMobs.printWarning("", "[Config] Tried to access a Base Config as a Spawning Config!");
     	return null;
     }
    
    
	// ========================================
	//		      Container Classes
	// ========================================
	
	// ========== Spawn Types ==========
	public class SpawnTypeSet {
		public SpawnTypeBase[] spawnTypes;
		public EnumCreatureType[] creatureTypes;
		
		public SpawnTypeSet(SpawnTypeBase[] spawnTypes, EnumCreatureType[] creatureTypes) {
			this.spawnTypes = spawnTypes;
			this.creatureTypes = creatureTypes;
		}
	}
	
	// ========== Spawn Dimensions ==========
	public class SpawnDimensionSet {
		public int[] dimensionIDs;
		public String[] dimensionTypes;
		
		public SpawnDimensionSet(int[] dimensionIDs, String[] dimensionTypes) {
			this.dimensionIDs = dimensionIDs;
			this.dimensionTypes = dimensionTypes;
		}
	}
	
	
	// ========================================
	//				 Constructor
	// ========================================
    public ConfigSpawning(GroupInfo group, String name) {
        super(group, name);
    }
	
	
	// ========================================
	//				 Get Values
	// ========================================
	
	// ========== Get Spawn Types ==========
	public SpawnTypeSet getTypes(String category, String key) {
		return this.getTypes(category, key, "");
	}
	
	public SpawnTypeSet getTypes(String category, String key, String defaultValue) {
		return this.getTypes(category, key, defaultValue, null);
	}
	
	public SpawnTypeSet getTypes(String category, String key, String defaultValue, String comment) {
		String spawnTypeEntries = this.getString(category, key, defaultValue);
		spawnTypeEntries = spawnTypeEntries.replace(" ", "");
		
		SpawnTypeBase[] spawnTypes = SpawnTypeBase.getSpawnTypes(spawnTypeEntries);
		
        List<EnumCreatureType> creatureTypeList = new ArrayList<EnumCreatureType>();
        for(String spawnTypeEntry : spawnTypeEntries.split(",")) {
            if ("MONSTER".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.monster);
            else if ("CREATURE".equalsIgnoreCase(spawnTypeEntry) || "ANIMAL".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.creature);
            else if ("WATERCREATURE".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.waterCreature);
            else if ("AMBIENT".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.ambient);
        }
        
        EnumCreatureType[] creatureTypes = creatureTypeList.toArray(new EnumCreatureType[creatureTypeList.size()]);
		return new SpawnTypeSet(spawnTypes, creatureTypes);
	}
	
	
	// ========== Get Spawn Dimensions ==========
	public SpawnDimensionSet getDimensions(String category, String key) {
		return this.getDimensions(category, key, "");
	}
	
	public SpawnDimensionSet getDimensions(String category, String key, String defaultValue) {
		return this.getDimensions(category, key, defaultValue, null);
	}
	
	public SpawnDimensionSet getDimensions(String category, String key, String defaultValue, String comment) {
		String dimensionEntries = this.getString(category, key, defaultValue);
        dimensionEntries = dimensionEntries.replace(" ", "");

        List<Integer> dimensionIDList = new ArrayList<Integer>();
        List<String> dimensionTypeList = new ArrayList<String>();
        for(String dimensionEntry : dimensionEntries.split(",")) {
            if(StringUtils.isNumeric(dimensionEntry))
                dimensionIDList.add(Integer.parseInt(dimensionEntry.replace("+", "")));
            else
                dimensionTypeList.add(dimensionEntry.replace("+", ""));
        }
        
		int[] dimensionIDs = ArrayUtils.toPrimitive(dimensionIDList.toArray(new Integer[dimensionIDList.size()]));
		String[] dimensionTypes = dimensionTypeList.toArray(new String[dimensionTypeList.size()]);
		return new SpawnDimensionSet(dimensionIDs, dimensionTypes);
	}
	
	
	// ========== Get Spawn Biomes ==========
	public BiomeGenBase[] getBiomes(String category, String key) {
		return this.getBiomes(category, key, "");
	}
	
	public BiomeGenBase[] getBiomes(String category, String key, String defaultValue) {
		return this.getBiomes(category, key, defaultValue, null);
	}
	
	public BiomeGenBase[] getBiomes(String category, String key, String defaultValue, String comment) {
		String biomeEntries = this.getString(category, key, defaultValue);
		biomeEntries = biomeEntries.replace(" ", "");
        
        List<BiomeGenBase> biomeList = new ArrayList<BiomeGenBase>();
        for(String biomeEntry : biomeEntries.split(",")) {
        	if("".equals(biomeEntry))
        		break;
            boolean additive = true;
            if(biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
                if(biomeEntry.charAt(0) == '-')
                    additive = false;
                biomeEntry = biomeEntry.substring(1);
            }

            BiomeGenBase[] selectedBiomes = null;
            if("ALL".equals(biomeEntry)) {
                for(BiomeDictionary.Type biomeType : BiomeDictionary.Type.values()) {
                    if(selectedBiomes == null)
                        selectedBiomes = BiomeDictionary.getBiomesForType(biomeType);
                    else
                    	selectedBiomes = ArrayUtils.addAll(selectedBiomes, BiomeDictionary.getBiomesForType(biomeType));
                }
            }
            else if("GROUP".equals(biomeEntry)) {
                selectedBiomes = this.group.biomes;
            }
            else if(!"NONE".equals(biomeEntry)) {
                BiomeDictionary.Type biomeType;
                try { biomeType = BiomeDictionary.Type.valueOf(biomeEntry); }
                catch(Exception e) {
                    biomeType = null;
                    LycanitesMobs.printWarning("", "[Config] Unknown biome type " + biomeEntry + " specified for " + defaultValue + "this will be ignored and treated as NONE.");
                }
                if(biomeType != null)
                    selectedBiomes = BiomeDictionary.getBiomesForType(biomeType);
            }

            if(selectedBiomes != null) {
                for(BiomeGenBase biome : selectedBiomes)
                    if(additive && !biomeList.contains(biome)) {
                        biomeList.add(biome);
                    }
                    else if(!additive && biomeList.contains(biome)) {
                        biomeList.remove(biome);
                    }
            }
        }
        
        return biomeList.toArray(new BiomeGenBase[biomeList.size()]);
	}
}