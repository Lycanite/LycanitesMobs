package com.lycanitesmobs.core.config;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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
    //		     Get All Biome Types
    // ========================================
    // Can no longer access a list of all biomes types without reflection. This is the alternative for now.
    public BiomeDictionary.Type[] getAllBiomeTypes() {
         return new BiomeDictionary.Type[] {
                BiomeDictionary.Type.HOT,
                BiomeDictionary.Type.COLD,
                BiomeDictionary.Type.SPARSE,
                BiomeDictionary.Type.DENSE,
                BiomeDictionary.Type.WET,
                BiomeDictionary.Type.DRY,
                BiomeDictionary.Type.SAVANNA,
                BiomeDictionary.Type.CONIFEROUS,
                BiomeDictionary.Type.JUNGLE,
                BiomeDictionary.Type.SPOOKY,
                BiomeDictionary.Type.DEAD,
                BiomeDictionary.Type.LUSH,
                BiomeDictionary.Type.NETHER,
                BiomeDictionary.Type.END,
                BiomeDictionary.Type.MUSHROOM,
                BiomeDictionary.Type.MAGICAL,
                BiomeDictionary.Type.RARE,
                BiomeDictionary.Type.OCEAN,
                BiomeDictionary.Type.RIVER,
                BiomeDictionary.Type.WATER,
                BiomeDictionary.Type.MESA,
                BiomeDictionary.Type.FOREST,
                BiomeDictionary.Type.PLAINS,
                BiomeDictionary.Type.MOUNTAIN,
                BiomeDictionary.Type.HILLS,
                BiomeDictionary.Type.SWAMP,
                BiomeDictionary.Type.SANDY,
                BiomeDictionary.Type.SNOWY,
                BiomeDictionary.Type.WASTELAND,
                BiomeDictionary.Type.BEACH,
                BiomeDictionary.Type.VOID
         };
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
		String biomeEntries = this.getString(category, key, defaultValue, comment);
		biomeEntries = biomeEntries.replace(" ", "");
        
        List<Biome> biomeList = new ArrayList<Biome>();
        for(String biomeEntry : biomeEntries.split(",")) {
        	if("".equals(biomeEntry))
        		continue;
            boolean additive = true;
            if(biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
                if(biomeEntry.charAt(0) == '-')
                    additive = false;
                biomeEntry = biomeEntry.substring(1);
            }

            Biome[] selectedBiomes = null;
            if("ALL".equals(biomeEntry)) {
                for(BiomeDictionary.Type biomeType : this.getAllBiomeTypes()) {
                    if(selectedBiomes == null) {
                        Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
                        selectedBiomes = selectedBiomesSet.toArray(new Biome[selectedBiomesSet.size()]);
                    }
                    else {
                        Set<Biome> typeBiomesSet = BiomeDictionary.getBiomes(biomeType);
                    	Biome[] typeBiomes = typeBiomesSet.toArray(new Biome[typeBiomesSet.size()]);
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
                try {
                    biomeType = BiomeDictionary.Type.getType(biomeEntry);
                }
                catch(Exception e) {
                    biomeType = null;
                    LycanitesMobs.printWarning("", "[Config] Unknown biome type " + biomeEntry + " specified for " + defaultValue + "this will be ignored and treated as NONE.");
                }
                if(biomeType != null) {
                    Set<Biome> selectedBiomesSet = BiomeDictionary.getBiomes(biomeType);
                    selectedBiomes = selectedBiomesSet.toArray(new Biome[selectedBiomesSet.size()]);
                }
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


    // ========== Get Spawn Biome Types ==========
    public BiomeDictionary.Type[] getBiomeTypes(boolean additive, String category, String key) {
        return this.getBiomeTypes(additive, category, key, "");
    }

    public BiomeDictionary.Type[] getBiomeTypes(boolean additive, String category, String key, String defaultValue) {
        return this.getBiomeTypes(additive, category, key, defaultValue, null);
    }

    public BiomeDictionary.Type[] getBiomeTypes(boolean additive, String category, String key, String defaultValue, String comment) {
        String biomeEntries = this.getString(category, key, defaultValue, comment);
        biomeEntries = biomeEntries.replace(" ", "");

        List<BiomeDictionary.Type> biomeTypeList = new ArrayList<BiomeDictionary.Type>();
        for(String biomeEntry : biomeEntries.split(",")) {
            if("".equals(biomeEntry))
                continue;
            if(biomeEntry.charAt(0) == '-' || biomeEntry.charAt(0) == '+') {
                if(biomeEntry.charAt(0) == '-' && additive)
                    continue;
                if(biomeEntry.charAt(0) == '+' && !additive)
                    continue;
                biomeEntry = biomeEntry.substring(1);
            }
            else if(!additive) {
                continue;
            }

            if("ALL".equals(biomeEntry)) {
                biomeTypeList.addAll(Arrays.asList(this.getAllBiomeTypes()));
            }
            else if("GROUP".equals(biomeEntry)) {
                if(additive)
                    biomeTypeList.addAll(Arrays.asList(this.group.biomeTypesAllowed));
                else
                    biomeTypeList.addAll(Arrays.asList(this.group.biomeTypesDenied));
            }
            else if(!"NONE".equals(biomeEntry)) {
                BiomeDictionary.Type biomeType;
                try {
                    biomeType = BiomeDictionary.Type.getType(biomeEntry);
                }
                catch(Exception e) {
                    biomeType = null;
                    LycanitesMobs.printWarning("", "[Config] Unknown biome type " + biomeEntry + " specified for " + defaultValue + "this will be ignored and treated as NONE.");
                }
                if(biomeType != null) {
                    biomeTypeList.add(biomeType);
                }
            }
        }

        return biomeTypeList.toArray(new BiomeDictionary.Type[biomeTypeList.size()]);
    }
}