package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.OldConfig;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.spawning.SpawnType;
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
	public boolean enabled;
	
	// ========== Spawn Type ==========
	/** The method that this mob should with. Can be MONSTER, CREATURE, WATERCREATURE, AMBIENT, PORTAL, NETHER, FIRE or LAVA. **/
	public String spawnTypeName;
	
	/** The custom spawn type instance that this mob should use, null when using vanilla spawn types such as MONSTER. **/
	public SpawnType spawnType = null;
	
	/** The CreatureType this mob should spawn as, null when using custom spawn types such as FIRE. **/
	public EnumCreatureType creatureType = null;
	
	// ========== Spawn Location ==========
	/** The list of dimensions that this mob spawns in. **/
	public int[] dimensionIDs;
	
	/** Extra dimension types info, can contain values such as ALL or VANILLA. **/
	public String dimensionTypes; // NYI
	
	/** The list of biomes that this mob spawns in. Use this to get the biomes not biomeTypes. **/
	public BiomeGenBase[] biomes;
	
	/** The list of Biome Types that this mob spawns in. Includes some custom types such as RIVER or GROUP. See BiomeDictionary.Type for non-custom values. **/
	public String[] biomeTypes;
	
	/** The block IDs to search for when using the BLOCK spawn type. **/
	public int[] blockIDs;
	
	// ========== Spawn Chance ==========
	/** The chance of this mob spawning over others. **/
	public int spawnWeight;

	/** The success chance of this mob being able to spawn. **/
	public double spawnChance = 1.0D;
	
	/** The chance of dungeons using this mob over others. **/
	public int dungeonWeight;
	
	// ========== Spawn Limit ==========
	/** The maximum arount of this mob allowed within the Spawn Area Search Limit. **/
	public int spawnAreaLimit;
	
	/** The minimum number of this mob to spawn at once. **/
	public int spawnGroupMin;

	/** The maximum number of this mob to spawn at once. **/
	public int spawnGroupMax;
	
	/** Used for the BLOCK spawn type. How many blocks that must be within the Spawn Block Search Range. **/
	public int spawnBlockCost;
	
	// ========== Despawning ==========
	/** Whether this mob should despawn or not by default (some mobs can override persistence, such as once farmed). **/
	public boolean despawnNatural = true;
	
	/** Whether this mob should always despawn no matter what. **/
	public boolean despawnForced = false;
	
	// ==================================================
    //        Load Global Settings From Config
    // ==================================================
	public static void loadGlobalSettings() {
		OldConfig config = LycanitesMobs.config;
		spawnLimitRange = (double)LycanitesMobs.config.getFeatureInt("SpawnLimitRange");
		disableAllSpawning = LycanitesMobs.config.getFeatureBool("DisableAllSpawning");
		disableDungeonSpawners = LycanitesMobs.config.getFeatureBool("DisableDungeonSpawners");
		enforceBlockCost = LycanitesMobs.config.getFeatureBool("EnforceBlockCost");
		spawnWeightScale = LycanitesMobs.config.getFeatureDouble("SpawnWeightScale");
		dungeonSpawnerWeightScale = LycanitesMobs.config.getFeatureDouble("DungeonSpawnerWeightScale");
	}
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnInfo(MobInfo setMobInfo) {
		this.mobInfo = setMobInfo;
		
		this.loadFromConfig();
	}

	
	// ==================================================
    //                 Load from Config
    // ==================================================
	public void loadFromConfig() {
		String name = this.mobInfo.name;
		OldConfig config = this.mobInfo.mod.getConfig();
		
		// Is mob spawning for this mob enabled?
		this.enabled = config.spawnEnabled.containsKey(name) ? config.spawnEnabled.get(name) : false;
		if(!this.enabled)
			return;
		
		// Spawn Type:
		this.spawnTypeName = config.spawnTypes.get(name);
		this.spawnType = SpawnType.getSpawnType(this.spawnTypeName);
		if("MONSTER".equalsIgnoreCase(this.spawnTypeName) || "NETHER".equalsIgnoreCase(this.spawnTypeName))
			this.creatureType = EnumCreatureType.monster;
		else if("CREATURE".equalsIgnoreCase(this.spawnTypeName) || "ANIMAL".equalsIgnoreCase(this.spawnTypeName))
			this.creatureType = EnumCreatureType.creature;
		else if("WATERCREATURE".equalsIgnoreCase(this.spawnTypeName))
			this.creatureType = EnumCreatureType.waterCreature;
		else if("AMBIENT".equalsIgnoreCase(this.spawnTypeName))
			this.creatureType = EnumCreatureType.ambient;
		
		// Spawn Location:
		this.dimensionIDs = config.getSpawnDimensions(name);
		this.biomes = config.getSpawnBiomesTypes(name);
		
		// Spawn Chance:
		this.spawnWeight = Math.round((float)config.spawnWeights.get(name) * (float)spawnWeightScale);
		this.spawnChance = config.spawnChances.get(name);
		this.dungeonWeight = Math.round((float)config.dungeonWeights.get(name) * (float)dungeonSpawnerWeightScale);
		
		// Spawn limits:
		this.spawnAreaLimit = config.spawnLimits.get(name);
		this.spawnGroupMin = config.spawnMins.get(name);
		this.spawnGroupMax = config.spawnMaxs.get(name);
		this.spawnBlockCost = config.spawnBlockCosts.get(name);
		
		// Despawning:
		this.despawnNatural = config.despawnNaturals.get(name);
		this.despawnForced = config.despawnForced.get(name);
	}
}
