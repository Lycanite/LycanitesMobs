package com.lycanitesmobs.core.config;

import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;

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
     	LycanitesMobs.printWarning("", "[Config] Tried to access the Base Config: " + configName + " as a Spawning Config from group: " + group.name + "!");
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
                creatureTypeList.add(EnumCreatureType.MONSTER);
            else if ("CREATURE".equalsIgnoreCase(spawnTypeEntry) || "ANIMAL".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.CREATURE);
            else if ("WATERCREATURE".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.WATER_CREATURE);
            else if ("AMBIENT".equalsIgnoreCase(spawnTypeEntry))
                creatureTypeList.add(EnumCreatureType.AMBIENT);
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
            if(NumberUtils.isNumber(dimensionEntry))
                dimensionIDList.add(Integer.parseInt(dimensionEntry.replace("+", "")));
            else
                dimensionTypeList.add(dimensionEntry.replace("+", ""));
        }
        
		int[] dimensionIDs = ArrayUtils.toPrimitive(dimensionIDList.toArray(new Integer[dimensionIDList.size()]));
		String[] dimensionTypes = dimensionTypeList.toArray(new String[dimensionTypeList.size()]);
		return new SpawnDimensionSet(dimensionIDs, dimensionTypes);
	}
	
	
	// ========== Get Spawn Biomes ==========
	public Biome[] getBiomes(String category, String key) {
		return this.getBiomes(category, key, "");
	}
	
	public Biome[] getBiomes(String category, String key, String defaultValue) {
		return this.getBiomes(category, key, defaultValue, null);
	}
	
	public Biome[] getBiomes(String category, String key, String defaultValue, String comment) {
		String biomeEntries = this.getString(category, key, defaultValue);
		biomeEntries = biomeEntries.replace(" ", "");
        
        List<Biome> biomeList = new ArrayList<Biome>();
        for(String biomeEntry : biomeEntries.split(",")) {
        	if("".equals(biomeEntry))
        		break;
            boolean additive = true;
            if(biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
                if(biomeEntry.charAt(0) == '-')
                    additive = false;
                biomeEntry = biomeEntry.substring(1);
            }

            Biome[] selectedBiomes = null;
            if("ALL".equals(biomeEntry)) {
                for(BiomeDictionary.Type biomeType : BiomeDictionary.Type.values()) {
                    if(selectedBiomes == null)
                        selectedBiomes = BiomeDictionary.getBiomesForType(biomeType);
                    else {
                    	Biome[] typeBiomes = BiomeDictionary.getBiomesForType(biomeType);
                    	if(typeBiomes != null)
                    		selectedBiomes = ArrayUtils.addAll(selectedBiomes, typeBiomes);
                    }
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
                for(Biome biome : selectedBiomes)
                    if(additive && !biomeList.contains(biome)) {
                        biomeList.add(biome);
                    }
                    else if(!additive && biomeList.contains(biome)) {
                        biomeList.remove(biome);
                    }
            }
        }
        
        return biomeList.toArray(new Biome[biomeList.size()]);
	}
}