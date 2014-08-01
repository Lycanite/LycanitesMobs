package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.config.ConfigSpawning.SpawnDimensionSet;
import lycanite.lycanitesmobs.api.config.ConfigSpawning.SpawnTypeSet;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class SpawnInfo {
	// ========== Global Spawn Settings ==========
	public static double spawnLimitRange = 32D;
	public static boolean disableAllSpawning = false;
	public static boolean disableDungeonSpawners = false;
	public static boolean enforceBlockCost = true;
	public static double spawnWeightScale = 1.0D;
	public static double dungeonSpawnerWeightScale = 1.0D;

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

    /** A list of Spawn Types to use. **/
    public EnumCreatureType[] creatureTypes = new EnumCreatureType[0];
	
	// ========== Spawn Dimensions ==========
    /** A comma separated list of dimensions that this mob spawns in. As read from the config **/
    public String dimensionEntries = "GROUP";

	/** The list of dimension IDs that this mob spawns in. **/
	public int[] dimensionIDs;
	
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes;

    // ========== Spawn Biomes ==========
    /** The list of biomes that this mob spawns in. As read from the config. **/
    public String biomeEntries = "GROUP";
	
	/** The list of biomes that this mob spawns in. Use this to get the biomes not biomeTypes. **/
	public BiomeGenBase[] biomes;
	
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

        spawnLimitRange = config.getDouble("Global Spawning", "Mob Limit Search Range", spawnLimitRange, "How far a mob should search from in blocks when checking how many of its kind have already spawned.");
        disableAllSpawning = config.getBool("Global Spawning", "Disable Spawning", disableAllSpawning, "If true, all mobs from this mod will not spawn at all.");
        enforceBlockCost = config.getBool("Global Spawning", "Enforce Block Costs", enforceBlockCost, "If true, mobs will double check if their required blocks are nearby, such as Cinders needing so many blocks of fire.");
        spawnWeightScale = config.getDouble("Global Spawning", "Weight Scale", spawnWeightScale, "Scales the spawn weights of all mobs from this mod. For example, you can use this to quickly half the spawn rates of mobs from this mod compared to vanilla/other mod mobs by setting it to 0.5.");

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
		String name = this.mobInfo.name;
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
        config.setCategoryComment("Spawn Dimensions", "Sets which dimensions mobs spawn in. You may enter dimension IDs or tags such as: ALL, VANILLA or GROUP. Multiple entries should be comma separated.");
        SpawnDimensionSet spawnDimensions = config.getDimensions("Spawn Dimensions", this.getCfgName("Spawn Dimensions"), this.dimensionEntries);
        this.dimensionIDs = spawnDimensions.dimensionIDs;
        this.dimensionTypes = spawnDimensions.dimensionTypes;

        // Spawn Biomes:
		config.setCategoryComment("Spawn Biomes", "Sets which biomes this mob spawns in using Biome Tags. Multiple entries should be comma separated and can be subtractive if provided with a - in front.");
		this.biomes = config.getBiomes("Spawn Biomes", this.getCfgName("Spawn Biomes"), this.biomeEntries);
		
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
		
		// Natural Despawning:
        config.setCategoryComment("Despawning", "Sets whether or not each mob will despawn over time. Most farmable mobs don't despawn naturally.");
        this.despawnNatural = config.getBool("Despawning", this.getCfgName("Natural Despawning"), this.despawnNatural);
        
        // Forced Despawning:
        config.setCategoryComment("Despawning", "Forces a mob to despawn naturally (unless tamed). Some farmable mobs such as Pinkies ignore their Natural Despawning setting once they've been fed or moved out of their home dimension.");
        this.despawnForced = config.getBool("Despawning", this.getCfgName("Forced Despawning"), this.despawnForced);
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
