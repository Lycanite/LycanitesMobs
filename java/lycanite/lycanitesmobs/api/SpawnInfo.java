package lycanite.lycanitesmobs.api;

import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.biome.BiomeGenBase;

public class SpawnInfo {
	// Mob Info:
	/** The Mob Info of the mob this Spawn Info belongs to. **/
	public MobInfo hostInfo;
	
	// Spawn Location:
	/** The list of biomes that this mob spawns in. Use this to get the biomes not biomeTypes. **/
	BiomeGenBase[] biomes;
	
	/** The list of Biome Types that this mob spawns in. Includes some custom types such as RIVER or GROUP. See BiomeDictionary.Type for non-custom values. **/
	String[] biomeTypes;
	
	/** The block IDs to search for when using the BLOCK spawn type. **/
	int[] blockIDs;
	
	// Spawn Method:
	/** The method that this mob should with. Can be MONSTER, CREATURE, WATERCREATURE, AMBIENT or BLOCK. **/
	String spawnType;
	
	/** The CreatureType this mob should spawn as, null when using custom spawn types such as BLOCK. **/
	EnumCreatureType creatureType = null;
	
	// Spawn Chance:
	/** The chance of this mob spawning over others. **/
	int spawnWeight;
	
	/** The maximum arount of this mob allowed within the Spawn Area Search Limit. **/
	int spawnAreaLimit;
	
	/** The minimum number of this mob to spawn at once. **/
	int spawnGroupMin;

	/** The maximum number of this mob to spawn at once. **/
	int spawnGroupMax;

	/** The success chance of this mob being able to spawn. **/
	double spawnChance = 1.0D;
	
	/** Used for the BLOCK spawn type. How many blocks that must be within the Spawn Block Search Range. **/
	int spawnBlockCost;
	
	// Despawning:
	/** Whether this mob should despawn or not by default (some mobs can override persistence, such as once farmed). **/
	boolean persistent = false;
}
