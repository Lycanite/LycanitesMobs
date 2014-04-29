package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;

public class SpawnType {
	// ========== Spawn Type List ==========
	/** A static list that contains all spawn types. **/
	public static List<SpawnType> spawnTypes = new ArrayList<SpawnType>();
	
	/** A static list that contains a string mapping of all spawn types. These should always be all upper case. **/
	public static Map<String, SpawnType> spawnTypeMap = new HashMap<String, SpawnType>();
	
	// ========== Spawn Type Properties ==========
	/** The name of this spawn type. This should be the same as the name used in the spawnTypes mapping. Should be all upper case. **/
	public String typeName;
	
	/** How many ticks per player update this spawn type should attempt to spawn anything. **/
	public boolean enabled;
	
	/** A list of all mobs (as SpawnInfo) that use this spawn type. **/
	public List<SpawnInfo> spawnList = new ArrayList<SpawnInfo>();
	
	// ========== Spawn Type Conditions ==========
	/** How many ticks per player update this spawn type should attempt to spawn anything. **/
	public int rate;
	
	/** The chance of this spawn type spawning anything at all on it's spawn tick. **/
	public double chance;
	
	/** How many blocks around each player the spawner should search. If this is set too high the spawner could become quite laggy. **/
	public int range;
	
	/** The maximum amount of blocks to use for spawning from during a spawn tick. **/
	public int blockLimit;
	
	/** The maximum amount of mobs to spawn during a spawn tick. This is not a total limit. **/
	public int mobLimit;
	
	/** An array of blocks to spawn from. Only used if materials is null. **/
	public Block[] blocks = null;
	
	/** An array of materials to spawn from. If null blocks will be used instead. **/
	public Material[] materials = null;
	
    // ==================================================
    //                  Load Spawn Types
    // ==================================================
	public static void loadSpawnTypes() {
		String[] spawnTypeNames = {"Fire", "Lava", "Portal"};
		Config config = LycanitesMobs.config;
		for(String spawnTypeName : spawnTypeNames) {
			SpawnType newSpawnType = new SpawnType(
					spawnTypeName.toUpperCase(),
					config.getFeatureInt(spawnTypeName + "SpawnTick"),
					config.getFeatureDouble(spawnTypeName + "SpawnChance"),
					config.getFeatureInt(spawnTypeName + "SpawnRange"),
					config.getFeatureInt(spawnTypeName + "SpawnBlockLimit"),
					config.getFeatureInt(spawnTypeName + "SpawnMobLimit")
					);
			newSpawnType.enabled = config.getFeatureBool(spawnTypeName + "SpawnEnabled");
			
			if("FIRE".equalsIgnoreCase(spawnTypeName))
				newSpawnType.materials = new Material[] {Material.fire};
			if("LAVA".equalsIgnoreCase(spawnTypeName))
				newSpawnType.materials = new Material[] {Material.lava};
			if("PORTAL".equalsIgnoreCase(spawnTypeName))
				newSpawnType.blocks = new Block[] {Block.portal};
			
			spawnTypes.add(newSpawnType);
			spawnTypeMap.put(spawnTypeName.toUpperCase(), newSpawnType);
			LycanitesMobs.printDebug("CustomSpawner", "Added custom spawn type: " + spawnTypeName.toUpperCase());
		}
	}
	
    // ==================================================
    //                   Get Spawn Type
    // ==================================================
	public static SpawnType getSpawnType(String spawnTypeName) {
		if("NETHER".equalsIgnoreCase(spawnTypeName))
			spawnTypeName = "PORTAL";
		if(spawnTypeMap.containsKey(spawnTypeName))
			return spawnTypeMap.get(spawnTypeName);
		return null;
	}
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnType(String typeName, int rate, double chance, int range, int blockLimit, int mobLimit) {
		this.typeName = typeName;
		this.rate = rate;
		this.chance = chance;
		this.range = range;
		this.blockLimit = blockLimit;
		this.mobLimit = mobLimit;
	}
	
	
	// ==================================================
	//                     Add Spawn
	// ==================================================
	public void addSpawn(SpawnInfo spawnInfo) {
		if(spawnInfo != null)
			this.spawnList.add(spawnInfo);
	}
	
	
	// ==================================================
	//                    Spawn Mobs
	// ==================================================
	public void spawnMobs(long tick, World world, int x, int y, int z) {
		if(this.spawnList == null || !this.enabled)
			return;
		
		if(tick % this.rate != 0)
			return;
		
		if((this.materials == null || this.materials.length <= 0) && (this.blocks == null || this.blocks.length <= 0))
			return;
		
		if(world.rand.nextDouble() < this.chance)
			return;
		
		LycanitesMobs.printDebug("CustomSpawner", "~0==================== " + this.typeName + " Spawner ====================0~");
		LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn mobs.");
		
		// Search for Coords:
		List<int[]> coords = this.searchForCoords(world, x, y, z);
		LycanitesMobs.printDebug("CustomSpawner", "Found " + coords.size() + "/" + this.blockLimit + " coordinates for mob spawning.");
		if(coords.size() <= 0) {
			LycanitesMobs.printDebug("CustomSpawner", "No valid coordinates were found, spawning cancelled.");
			return;
		}
		if(coords.size() > this.blockLimit)
			coords = coords.subList(0, this.blockLimit);
		List<BiomeGenBase> targetBiomes = new ArrayList<BiomeGenBase>();
		for(int[] coord : coords) {
			BiomeGenBase coordBiome = world.getBiomeGenForCoords(coord[0], coord[2]);
			if(!targetBiomes.contains(coordBiome))
				targetBiomes.add(coordBiome);
		}
		
		// Choose Mobs:
		LycanitesMobs.printDebug("CustomSpawner", "Choosing mob type to spawn via spawn weights... " + this.spawnList.size() + " possible mob(s) to spawn.");
		SpawnInfo spawnInfo = this.getRandomMobType(world, coords.size(), targetBiomes);
		if(spawnInfo == null) {
			LycanitesMobs.printDebug("CustomSpawner", "No mobs are able to spawn, either not enough blocks, empty biome/dimension or all weights are 0. Spawning cancelled.");
			return;
		}
		LycanitesMobs.printDebug("CustomSpawner", "Mob type chosen: " + spawnInfo.mobInfo.name);
		
		// Spawn Chosen Mobs:
		LycanitesMobs.printDebug("CustomSpawner", "Cycling through each possible spawn coordinate and attempting to spawn a mob there. Mob limit is " + this.mobLimit + " overall.");
		int mobsSpawned = 0;
		for(int[] coord : coords) {
			EntityLiving entityLiving = null;
			try {
				entityLiving = (EntityLiving)spawnInfo.mobInfo.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
			} catch (Exception e) { e.printStackTrace(); }
			if(entityLiving != null) {
				entityLiving.setLocationAndAngles((double)coord[0] + 0.5D, (double)coord[1], (double)coord[2] + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
				LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn " + entityLiving + "...");
				LycanitesMobs.printDebug("CustomSpawner", "Coordinates: X" + coord[0] + " Y" + coord[1] + " Z" + coord[2]);
				if(entityLiving instanceof EntityCreatureBase) {
					((EntityCreatureBase)entityLiving).spawnedFromType = spawnInfo.spawnType;
				}
				Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float)coord[0], (float)coord[1], (float)coord[2]);
				if(canSpawn == Result.ALLOW || (canSpawn == Result.DEFAULT && entityLiving.getCanSpawnHere())) {
					entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
					world.spawnEntityInWorld(entityLiving);
                    if(!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float)coord[0], (float)coord[1], (float)coord[2]))
                    	entityLiving.onSpawnWithEgg(null);
                    mobsSpawned++;
                    LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Passed! Mob spawned.");
				}
				else
					LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! Unable to spawn.");
			}
			if(mobsSpawned >= this.mobLimit)
				break;
		}
		LycanitesMobs.printDebug("CustomSpawner", "Spawning finished. Spawned " + mobsSpawned + " mobs.");
	}
	
	// ========== Search for Coords ==========
	/** Searches for block coords within the defined range of the given x, y, z coordinates.
	 * This will search from the closest blocks to the farthest blocks last.
	 * world - World object to search.
	 * x, y, z - Coordinates to search from.
	 * range - How far to search from the given coordinates.
	 * blockID - The ID of the blocks to search for. An array can be taken for multiple block types.
	**/
	public List<int[]> searchForCoords(World world, int x, int y, int z) {
		List<int[]> blockCoords = new ArrayList<int[]>();
		for(int i = x - this.range; i <= x + this.range; i++) {
			for(int j = y - this.range; j <= y + this.range; j++) {
				for(int k = z - this.range; k <= z + this.range; k++) {
					if(this.materials != null && this.materials.length > 0) {
						for(Material validMaterial : this.materials) {
							if(world.getBlock(i, j, k).getMaterial() == validMaterial) {
								blockCoords.add(new int[] {i, j, k});
								break;
							}
						}
					}
					else {
						for(Block validBlock : this.blocks) {
							if(world.getBlock(i, j, k) == validBlock) {
								blockCoords.add(new int[] {i, j, k});
								break;
							}
						}
					}
				}
			}
		}
		/*Collections.sort(blockCoords, new Comparator<int[]>() {
			@Override
			public int compare(int[] currentCoord, int[] previousCoord) {
				int deltaX = currentCoord[0] - previousCoord[0];
				int deltaY = currentCoord[1] - previousCoord[1];
				int deltaZ = currentCoord[2] - previousCoord[2];
				return Math.round((float)Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ));
			}
		});*/
		Collections.shuffle(blockCoords);
		return blockCoords;
	}
	
	// ========== Get Random Mob Type ==========
	/** Gets a random valid mob type to spawn using spawn weights to influence the odds. **/
	public SpawnInfo getRandomMobType(World world, int blocksFound, List<BiomeGenBase> biomes) {
		SpawnInfo spawnInfo = null;
		List<SpawnInfo> possibleSpawns = new ArrayList<SpawnInfo>();
		int totalWeights = 0;
		for(SpawnInfo possibleSpawn : this.spawnList) {
			
			boolean enoughBlocks = true;
			if(blocksFound < possibleSpawn.spawnBlockCost) {
				LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Not enough of the required blocks available for spawning.");
				enoughBlocks = false;
			}
			
			boolean isValidBiome = "PORTAL".equalsIgnoreCase(possibleSpawn.spawnTypeName) || "NETHER".equalsIgnoreCase(possibleSpawn.spawnTypeName);
			if(enoughBlocks && !isValidBiome) {
				for(BiomeGenBase validBiome : possibleSpawn.biomes) {
					for(BiomeGenBase targetBiome : biomes) {
						if(targetBiome == validBiome) {
							isValidBiome = true;
							break;
						}
					}
					if(isValidBiome)
						break;
				}
			}
			if(!isValidBiome)
				LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": No valid spawning biomes could be found within the coordinates.");
			
			if(enoughBlocks && isValidBiome) {
				possibleSpawns.add(possibleSpawn);
				totalWeights += possibleSpawn.spawnWeight;
			}
		}
		if(totalWeights <= 0)
			return null;
		
		int randomWeight = world.rand.nextInt(totalWeights);
		LycanitesMobs.printDebug("CustomSpawner", "Generated a random weight of " + randomWeight + "/" + totalWeights);
		for(SpawnInfo possibleSpawn : possibleSpawns) {
			spawnInfo = possibleSpawn;
			if(possibleSpawn.spawnWeight > randomWeight)
				break;
		}
		return spawnInfo;
	}
}
