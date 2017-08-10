package com.lycanitesmobs.core.info;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.config.ConfigSpawning.SpawnDimensionSet;
import com.lycanitesmobs.core.config.ConfigSpawning.SpawnTypeSet;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class SpawnInfo {
	// ========== Global Spawn Settings ==========
	public static double spawnLimitRange = 32D;
	public static boolean disableAllSpawning = false;
	public static boolean disableDungeonSpawners = false;
	public static boolean enforceBlockCost = true;
	public static boolean useSurfaceLightLevel = true;
	public static double spawnWeightScale = 1.0D;
	public static double dungeonSpawnerWeightScale = 1.0D;
	public static boolean ignoreWorldGenSpawning = false;

	// ========== Spawn General ==========
	/** The Mob Info of the mob this Spawn Info belongs to. **/
	public MobInfo mobInfo;
	
	/** Mob spawning enabled. **/
	public boolean enabled = true;
	
	// ========== Spawn Type ==========
    /** A comma separated list of Spawn Types Entries to use. Can be: MONSTER, CREATURE, WATERCREATURE, AMBIENT, PORTAL, NETHER, FIRE, LAVA, etc. **/
    public String spawnTypeEntries = "";

    /** A list of Spawn Types to use. **/
    public SpawnTypeBase[] spawnTypes = new SpawnTypeBase[0];

    /** A list of Vanilla Creature Types to use. **/
    public EnumCreatureType[] creatureTypes = new EnumCreatureType[0];
	
	// ========== Spawn Dimensions ==========
    /** A comma separated list of dimensions that this mob spawns in. As read from the config **/
    public String dimensionEntries = "GROUP";

	/** A blacklist of dimension IDs (changes to whitelist if dimensionWhitelist is true) that this mob spawns in. **/
	public int[] dimensionBlacklist;
	
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes;
	
	/** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
	public boolean dimensionWhitelist = false;

    // ========== Spawn Biomes ==========
    /** The list of biomes that this mob spawns in. As read from the config. **/
    public String biomeEntries = "GROUP";
	
	/** The list of biomes that this mob spawns in. Use this to get the biomes not biomeTypes. **/
	public Biome[] biomes;

	/** The list of biome types that this mob can spawn in. **/
	//public BiomeDictionary.Type[] biomeTypesAllowed;

	/** The list of biome types that this mob cannot spawn in. **/
	//public BiomeDictionary.Type[] biomeTypesDenied;

    /** If true, the biome check will be ignored completely, essnetially using ALL does the same thing but it doesn't always work with some mods. **/
    public boolean ignoreBiome = false;
	
	// ========== Spawn Chance ==========
	/** The chance of this mob spawning over others. **/
	public int spawnWeight = 8;

	/** The success chance of this mob being able to spawn. **/
	public double spawnChance = 1.0D;
	
	/** The chance of dungeons using this mob over others. **/
	public int dungeonWeight = 200;
	
	// ========== Spawn Limit ==========
	/** The maximum arount of this mob allowed within the Spawn Area Search Limit. **/
	public int spawnAreaLimit = 5;
	
	/** The minimum number of this mob to spawn at once. **/
	public int spawnGroupMin = 1;

	/** The maximum number of this mob to spawn at once. **/
	public int spawnGroupMax = 3;
	
	/** Used for the BLOCK spawn type. How many blocks that must be within the Spawn Block Search Range. **/
	public int spawnBlockCost = 1;

    // ========== Spawn Conditions ==========
    /** Whether or not this mob can spawn in high light levels. **/
    public boolean spawnsInLight = false;

    /** Whether or not this mob can spawn in low light levels. **/
    public boolean spawnsInDark = true;

	/** How many days old the world must at least be for this mob to be able to naturally spawn. **/
	public int spawnMinDay = 0;

    // ========== Despawning ==========
	/** Whether this mob should despawn or not by default (some mobs can override persistence, such as once farmed). **/
	public boolean despawnNatural = true;
	
	/** Whether this mob should always despawn no matter what. **/
	public boolean despawnForced = false;
	
	// ==================================================
    //        Load Global Settings From Config
    // ==================================================
	public static void loadGlobalSettings() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");
		
		config.setCategoryComment("Global Spawning", "These settings are used by everything. It is recommended to leave them as they are however low end machines might benefit from a few tweaks here.");
        spawnLimitRange = config.getDouble("Global Spawning", "Mob Limit Search Range", spawnLimitRange, "How far a mob should search from in blocks when checking how many of its kind have already spawned.");
        disableAllSpawning = config.getBool("Global Spawning", "Disable Spawning", disableAllSpawning, "If true, all mobs from this mod will not spawn at all.");
        enforceBlockCost = config.getBool("Global Spawning", "Enforce Block Costs", enforceBlockCost, "If true, mobs will double check if their required blocks are nearby, such as Cinders needing so many blocks of fire.");
        spawnWeightScale = config.getDouble("Global Spawning", "Weight Scale", spawnWeightScale, "Scales the spawn weights of all mobs from this mod. For example, you can use this to quickly half the spawn rates of mobs from this mod compared to vanilla/other mod mobs by setting it to 0.5.");
		useSurfaceLightLevel = config.getBool("Global Spawning", "Use Surface Light Level", useSurfaceLightLevel, "If true, when water mobs spawn, instead of checking the light level of the block the mob is spawning at, the light level of the surface (if possible) is checked. This stops mobs like Jengus from spawning at the bottom of deep rivers during the day, set to false for the old way.");
		ignoreWorldGenSpawning = config.getBool("Global Spawning", "Ignore WorldGen Spawning", ignoreWorldGenSpawning, "If true, when new world chunks are generated, no mobs from this mod will pre-spawn (mobs will still attempt to spawn randomly afterwards). Set this to true if you are removing mobs from vanilla dimensions as the vanilla WorldGen spawning ignores mob spawn conditions.");

		config.setCategoryComment("Dungeon Features", "Here you can set special features used in dungeon generation.");
        disableDungeonSpawners = config.getBool("Dungeon Features", "Disable Dungeon Spawners", disableDungeonSpawners, "If true, newly generated dungeons wont create spawners with mobs from this mod.");
		dungeonSpawnerWeightScale = config.getDouble("Dungeon Features", "Dungeon Spawner Weight Scale", dungeonSpawnerWeightScale, "Scales the weight of dungeons using spawners from this mod. For example, you can half the chances all dungeons having spawners with mobs from this mod in them by setting this to 0.5.");
	}
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnInfo(MobInfo setMobInfo) {
		this.mobInfo = setMobInfo;
	}

	
	// ==================================================
    //                 Load from Config
    // ==================================================
	public void loadFromConfig() {
        if(this.mobInfo.dummy) return;
		ConfigSpawning config = ConfigSpawning.getConfig(this.mobInfo.group, "spawning");
		
		// Enabled:
        config.setCategoryComment("Enabled Spawns", "Set to false to prevent mobs from spawning naturally at all.");
		this.enabled = config.getBool("Enabled Spawns", this.getCfgName("Spawning Enabled"), this.enabled);
		if(!this.enabled)
			return;
		
		// Spawn Type:
        config.setCategoryComment("Spawn Types", "Specifies how this mob spawns, multiple entries should be comma separated. Valid types are: MONSTER, CREATURE, WATERCREATURE, FIRE, FROSTFIRE, LAVA, ROCK, STORM. More will likely be added too.");
        SpawnTypeSet spawnTypeSet = config.getTypes("Spawn Types", this.getCfgName("Spawn Types"), this.spawnTypeEntries);
		this.spawnTypes = spawnTypeSet.spawnTypes;
		this.creatureTypes = spawnTypeSet.creatureTypes;
        
		// Spawn Dimensions:
        config.setCategoryComment("Spawn Dimensions", "Sets which dimensions (by ID) that mobs WILL NOT spawn in. However if 'Spawn Dimensions Whitelist Mode' is set to true, it will instead set which dimensions they WILL ONLY spawn in. You may enter dimension IDs and/or GROUP to use the group settings. Multiple entries should be comma separated. Note that some Spawn Types ignore this such as the PORTAL type.");
        SpawnDimensionSet spawnDimensions = config.getDimensions("Spawn Dimensions", this.getCfgName("Spawn Dimensions"), this.dimensionEntries);
        this.dimensionBlacklist = spawnDimensions.dimensionIDs;
        this.dimensionTypes = spawnDimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("Spawn Dimensions", this.getCfgName("Spawn Dimensions Whitelist Mode"), this.dimensionWhitelist);

        // Spawn Biomes:
		config.setCategoryComment("Spawn Biomes", "Sets which biomes this mob spawns in using Biome Tags. Multiple entries should be comma separated and can be subtractive if provided with a - in front. You can also have a mob skip the biome check completely if ALL is not working correctly for modded biomes.");
		this.biomes = config.getBiomes("Spawn Biomes", this.getCfgName("Spawn Biomes"), this.biomeEntries);
        //this.biomeTypesAllowed = config.getBiomeTypes(true, "Spawn Biomes", this.getCfgName("Spawn Biomes"), this.biomeEntries);
		//this.biomeTypesDenied = config.getBiomeTypes(false, "Spawn Biomes", this.getCfgName("Spawn Biomes"), this.biomeEntries);
		this.ignoreBiome = config.getBool("Spawn Biomes", this.getCfgName("Spawn Ignores Biome Check"), this.ignoreBiome);
		
		// Spawn Weight:
		config.setCategoryComment("Spawn Weights", "The higher the weight, the more likely the mob will spawn randomly instead of others. Vanilla Zombies have a weight of 8.");
        this.spawnWeight = Math.round((float)config.getDouble("Spawn Weights", this.getCfgName("Spawn Weight"), this.spawnWeight) * (float)spawnWeightScale);

        // Spawn Chance:
        config.setCategoryComment("Spawn Chances", "Adds a forced spawn fail chance. A chance of 1.0 (100%) means that the mob will always spawn. A chance of 0.5 means it will have a 50% chance of spawning. Use this as a last resort as a failed spawn still takes up a spawning oppotunity.");
        this.spawnChance = config.getDouble("Spawn Chances", this.getCfgName("Spawn Chance"), this.spawnChance);

        // Dungeon Weight:
        config.setCategoryComment("Dungeon Weights", "The higher the weight, the more likely this mob will appear in random dungeon spawners. Vanilla Zombie have a dungeon weight of 200.");
        this.dungeonWeight = Math.round((float)config.getDouble("Dungeon Weights", this.getCfgName("Dungeon Weight"), this.dungeonWeight) * (float)dungeonSpawnerWeightScale);
		
		// Spawn Area Limits:
        config.setCategoryComment("Area limits", "Sets how many of each mob is allowed to naturally spawn near each other.");
		this.spawnAreaLimit = config.getInt("Area limits", this.getCfgName("Area Limit"), this.spawnAreaLimit);
		
		// Spawn Group Sizes:
        config.setCategoryComment("Group Sizes", "Sets the minimum and maximum random size of a group spawned. Note with the vanilla spawn, large groups can reduce the spawning odds in biomes with lots trees, etc.");
        this.spawnGroupMin = config.getInt("Group Sizes", this.getCfgName("Group Min"), this.spawnGroupMin);
		this.spawnGroupMax = config.getInt("Group Sizes", this.getCfgName("Group Max"), this.spawnGroupMax);
		
		// Spawn Block Cost:
        config.setCategoryComment("Block Costs", "Only used by certain spawners. Sets how many blocks are required for spawning, such as how many blocks of Fire a Cinder requires.");
        this.spawnBlockCost = config.getInt("Block Costs", this.getCfgName("Block Cost"), this.spawnBlockCost);

        // Spawning Conditions:
        config.setCategoryComment("Spawn Conditions", "Various conditions that are checked at a spawn location when spawning mobs, such as light level.");
        this.spawnsInLight = config.getBool("Spawn Conditions", this.getCfgName("Spawns In Light"), this.spawnsInLight);
        this.spawnsInDark = config.getBool("Spawn Conditions", this.getCfgName("Spawns In Dark"), this.spawnsInDark);
		this.spawnMinDay = config.getInt("Spawn Conditions", this.getCfgName("World Day Minimum"), this.spawnMinDay, "How many days old the world must at least be for this mob to be able to naturally spawn.");

        // Natural Despawning:
        config.setCategoryComment("Despawning", "Sets whether or not each mob will despawn over time. Most farmable mobs don't despawn naturally.");
        this.despawnNatural = config.getBool("Despawning", this.getCfgName("Natural Despawning"), this.despawnNatural);

        // Forced Despawning:
        config.setCategoryComment("Despawning", "Forces a mob to despawn naturally (unless tamed). Some farmable mobs such as Pinkies ignore their Natural Despawning setting once they've been fed or moved out of their home dimension.");
        this.despawnForced = config.getBool("Despawning", this.getCfgName("Forced Despawning"), this.despawnForced);
        
        // Register Spawn:
        this.registerSpawn();
	}


    // ==================================================
    //                    Register Spawn
    // ==================================================
    /** Registers this mob to vanilla and custom spawners as well as dungeons. **/
    public void registerSpawn() {
    	// Add Spawn (Vanilla):
		boolean spawnAdded = false;
		if(!disableAllSpawning) {
			if(this.enabled && this.mobInfo.mobEnabled && this.spawnWeight > 0 && this.spawnGroupMax > 0) {
				for(EnumCreatureType creatureType : this.creatureTypes) {
					EntityRegistry.addSpawn(mobInfo.entityClass, this.spawnWeight, ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, this.biomes);
					for(Biome biome : this.biomes) {
						if(biome == Biomes.HELL) {
							EntityRegistry.addSpawn(mobInfo.entityClass, this.spawnWeight * 10, ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, biome);
							break;
						}
					}
				}
				spawnAdded = true;
			}
		}
		
		// Add Spawn (Custom):
		// Still added if disabled as the Custom Spawner can check the disabled booleans and ignores 0 weight/group max entries.
		for(SpawnTypeBase spawnType : this.spawnTypes) {
			spawnType.addSpawn(this.mobInfo);
		}
		
		// Debug Message - Spawn Added:
		if(spawnAdded) {
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Added - Weight: " + this.spawnWeight + " Min: " + this.spawnGroupMin + " Max: " + this.spawnGroupMax);
			for(EnumCreatureType creatureType : this.creatureTypes)
				LycanitesMobs.printDebug("MobSetup", "Vanilla Spawn Type: " + creatureType);
			for(SpawnTypeBase spawnType : this.spawnTypes)
				LycanitesMobs.printDebug("MobSetup", "Custom Spawn Type: " + spawnType != null ? spawnType.typeName : "NULL");
			String biomesList = "";
			if(LycanitesMobs.config.getBool("Debug", "MobSetup")) {
				for(Biome biome : this.biomes) {
					if(!"".equals(biomesList))
						biomesList += ", ";
					biomesList += biome.getBiomeName();
				}
			}
			LycanitesMobs.printDebug("MobSetup", "Biomes: " + biomesList);
			String dimensionsList = "";
			for(int dimensionID : this.dimensionBlacklist) {
				if(!"".equals(dimensionsList))
					dimensionsList += ", ";
				dimensionsList += Integer.toString(dimensionID);
			}
			for(String dimensionType : this.dimensionTypes) {
				if(!"".equals(dimensionsList))
					dimensionsList += ", ";
				dimensionsList += dimensionType;
			}
			LycanitesMobs.printDebug("MobSetup", "Dimensions (" + (this.dimensionWhitelist ? "Whitelist" : "Blacklist") + "): " + dimensionsList);
		}
		else
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Not Added: The spawning of this mob (or all mobs) must be disabled or this mobs spawn weight or max group size is 0 or this mob is all together disabled.");
		
		// Dungeon Spawn:
		if(!disableDungeonSpawners) {
			if(this.dungeonWeight > 0) {
				DungeonHooks.addDungeonMob(this.mobInfo.getResourceLocation(), this.dungeonWeight);
				LycanitesMobs.printDebug("MobSetup", "Dungeon Spawn Added - Weight: " + this.dungeonWeight);
			}
		}
    }


    // ==================================================
    //                    Set Defaults
    // ==================================================
    /** Whether this mob should spawn at all or not. True by default. **/
    public SpawnInfo setEnabled(boolean bool) {
        this.enabled = bool;
        return this;
    }

    public SpawnInfo setSpawnTypes(String string) {
        this.spawnTypeEntries = string;
        return this;
    }

    // ========== Spawn Location ==========
    public SpawnInfo setDimensions(String string) {
        this.dimensionEntries = string;
        return this;
    }

    public SpawnInfo setDimensionWhitelist(boolean whitelist) {
        this.dimensionWhitelist = whitelist;
        return this;
    }

    public SpawnInfo setBiomes(String string) {
        this.biomeEntries = string;
        return this;
    }

    // ========== Spawn Odds ==========
    public SpawnInfo setSpawnWeight(int integer) {
        this.spawnWeight = integer;
        this.dungeonWeight = integer * 25;
        return this;
    }

    public SpawnInfo setSpawnChance(int integer) {
        this.spawnChance = integer;
        return this;
    }
    
    public SpawnInfo setDungeonWeight(int integer) {
        this.dungeonWeight = integer;
        return this;
    }

    // ========== Spawn Limits ==========
    public SpawnInfo setAreaLimit(int integer) {
        this.spawnAreaLimit = integer;
        return this;
    }

    public SpawnInfo setGroupLimits(int min, int max) {
        this.spawnGroupMin = min;
        this.spawnGroupMax = max;
        return this;
    }

    public SpawnInfo setBlockCost(int integer) {
        this.spawnBlockCost = integer;
        return this;
    }

    // ========== Spawn Conditions ==========
    public SpawnInfo setLightDark(boolean spawnsInLight, boolean spawnsInDark) {
        this.spawnsInLight = spawnsInLight;
        this.spawnsInDark = spawnsInDark;
        return this;
    }

    // ========== Despawning ==========
    public SpawnInfo setDespawn(boolean bool) {
        this.despawnNatural = bool;
        return this;
    }


    // ==================================================
    //                       Names
    // ==================================================
    public String getCfgName(String configKey) {
        return this.mobInfo.getCfgName(configKey);
    }
}
