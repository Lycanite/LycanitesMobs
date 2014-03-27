package lycanite.lycanitesmobs.api;

import lycanite.lycanitesmobs.Config;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class SpawnInfo {
	// Mob Info:
	/** The Mob Info of the mob this Spawn Info belongs to. **/
	public MobInfo mobInfo;
	
	/** Mob spawning enabled. **/
	public boolean enabled;
	
	// ========== Spawn Type ==========
	/** The method that this mob should with. Can be MONSTER, CREATURE, WATERCREATURE, AMBIENT or BLOCK. **/
	public String spawnType;
	
	/** The CreatureType this mob should spawn as, null when using custom spawn types such as BLOCK. **/
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
	public boolean persistent = false;
	
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
		Config config = this.mobInfo.mod.getConfig();
		
		// Is mob spawning for this mob enabled?
		this.enabled = config.spawnEnabled.containsKey(name) ? config.spawnEnabled.get(name) : false;
		if(!this.enabled)
			return;
		
		// Spawn Type:
		this.spawnType = config.spawnTypes.get(name);
		this.creatureType = config.creatureTypes.get(name);
		
		// Spawn Location:
		this.dimensionIDs = config.getSpawnDimensions(name);
		this.biomes = config.getSpawnBiomesTypes(name);
		
		// Spawn Chance:
		this.spawnWeight = config.spawnWeights.get(name);
		this.spawnChance = config.spawnChances.get(name);
		
		// Spawn limits:
		this.spawnAreaLimit = config.spawnLimits.get(name);
		this.spawnGroupMin = config.spawnMins.get(name);
		this.spawnGroupMax = config.spawnMaxs.get(name);
		this.spawnBlockCost = config.spawnBlockCosts.get(name);
	}
}
