package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.OldConfig;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class SpawnType {
	// ========== Spawn Type List ==========
	/** A static list that contains a string mapping of all spawn types. These should always be all upper case. **/
	public static Map<String, SpawnType> spawnTypeMap = new HashMap<String, SpawnType>();
	
	// ========== Spawn Type Properties ==========
	/** The name of this spawn type. This should be the same as the name used in the spawnTypes mapping. Should be all upper case. **/
	public String typeName;
	
	/** How many ticks per player update this spawn type should attempt to spawn anything. **/
	public boolean enabled = true;
	
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
	
	/** An array of blocks to spawn from. Only used if materials is null. Uses Strings to map from the ObjectManager to get the block. **/
	public String[] blockStrings = null;
	
	/** An array of materials to spawn from. If null blocks will be used instead. **/
	public Material[] materials = null;
	
	/** If true, this type will not check if a mob is allowed to spawn in the target biome. **/
	public boolean ignoreBiome = false;
	
	/** If true, this type will not check if a mob is allowed to spawn in the target dimension. **/
	public boolean ignoreDimension = false;
	
	/** If true, this type will not check if a mob is allowed to spawn in the target light level. **/
	public boolean ignoreLight = false;
	
	
    // ==================================================
    //                  Load Spawn Types
    // ==================================================
	public static void loadSpawnTypes() {
		List<SpawnType> spawnTypes = new ArrayList<SpawnType>();
		OldConfig config = LycanitesMobs.config;
		
		// Fire Spawner:
		SpawnType fireBlockSpawner = new BlockSpawner("Fire", config);
		fireBlockSpawner.blocks = new Block[] {Blocks.fire};
		fireBlockSpawner.ignoreBiome = true;
		fireBlockSpawner.ignoreLight = true;
        spawnTypes.add(fireBlockSpawner);
		
		// Frostfire Spawner:
		SpawnType frostfireBlockSpawner = new BlockSpawner("Frostfire", config);
		frostfireBlockSpawner.blockStrings = new String[] {"frostfire"};
		frostfireBlockSpawner.ignoreBiome = true;
		frostfireBlockSpawner.ignoreLight = true;
        spawnTypes.add(frostfireBlockSpawner);
		
		// Lava Spawner:
		SpawnType lavaBlockSpawner = new BlockSpawner("Lava", config);
		lavaBlockSpawner.blocks = new Block[] {Blocks.lava};
		lavaBlockSpawner.ignoreBiome = true;
        spawnTypes.add(lavaBlockSpawner);
		
		// Portal Spawner:
		SpawnType portalBlockSpawner = new BlockSpawner("Portal", config);
		portalBlockSpawner.blocks = new Block[] {Blocks.portal};
		portalBlockSpawner.ignoreBiome = true;
		portalBlockSpawner.ignoreDimension = true;
        spawnTypes.add(portalBlockSpawner);
		
		// Rock Spawner:
		SpawnType rockSpawner = new RockSpawner("Rock", config);
		rockSpawner.materials = new Material[] {Material.air};
		rockSpawner.ignoreBiome = true;
        spawnTypes.add(rockSpawner);
		
		// Storm Spawner:
		SpawnType stormSpawner = new StormSpawner("Storm", config);
		stormSpawner.materials = new Material[] {Material.air};
		stormSpawner.ignoreBiome = true;
        spawnTypes.add(stormSpawner);
        
        // Add Spawners to Map:
        for(SpawnType spawnType : spawnTypes) {
			spawnTypeMap.put(spawnType.typeName.toUpperCase(), spawnType);
			LycanitesMobs.printDebug("CustomSpawner", "Added custom spawn type: " + spawnType.typeName);
		}
	}
	
	
    // ==================================================
    //                  Get Spawn Types
    // ==================================================
	public static SpawnType getSpawnType(String spawnTypeName) {
		if(spawnTypeMap.containsKey(spawnTypeName))
			return spawnTypeMap.get(spawnTypeName);
		return null;
	}

    public static SpawnType[] getSpawnTypes(String spawnTypeNames) {
        List<SpawnType> spawnTypeList = new ArrayList<SpawnType>();
        for(String spawnTypeName : spawnTypeNames.split(",")) {
            SpawnType spawnType = getSpawnType(spawnTypeName);
            if(spawnType != null)
                spawnTypeList.add(spawnType);
        }
        return spawnTypeList.toArray(new SpawnType[spawnTypeList.size()]);
    }
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnType(String typeName, OldConfig config) {
		this.typeName = typeName.toUpperCase();

		this.rate = config.getFeatureInt(typeName + "SpawnTick");
		this.chance = config.getFeatureDouble(typeName + "SpawnChance");
		this.range = config.getFeatureInt(typeName + "SpawnRange");
		this.blockLimit = config.getFeatureInt(typeName + "SpawnBlockLimit");
		this.mobLimit = config.getFeatureInt(typeName + "SpawnMobLimit");
		
		if("PORTAL".equalsIgnoreCase(this.typeName))
			this.ignoreBiome = true;
		else if("NETHER".equalsIgnoreCase(this.typeName))
			this.ignoreBiome = true;
	}
	
	
	// ==================================================
	//                     Add Spawn
	// ==================================================
    /**
     * Adds a mob to this spawn type. Takes a Spawn Info.
     * @param spawnInfo
     */
	public void addSpawn(SpawnInfo spawnInfo) {
		if(spawnInfo != null)
			this.spawnList.add(spawnInfo);
	}
	
	
	// ==================================================
	//                    Spawn Mobs
	// ==================================================
    /**
     * Tells this spawn type to try and spawn mobs at or near the provided coordinates.
     * Usually custom spawn types shouldn't need to override this method and should instead override methods called by this method.
     * This method is usually called by the Custom Spawner class where this spawn type is added to its Spawn Type lists.
     * @param tick Used by spawn types that attempt spawn on a regular basis. Use 0 for event based spawning.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     */
    public void spawnMobs(long tick, World world, int x, int y, int z) {
        // Check If Able to Spawn:
        if(this.spawnList == null || !this.enabled)
            return;
        if(!this.canSpawn(tick, world, x, y, z))
            return;
        
        LycanitesMobs.printDebug("CustomSpawner", "~0==================== " + this.typeName + " Spawner ====================0~");
        LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn mobs.");
        
        // Search for Coords:
        List<int[]> coords = this.getSpawnCoordinates(world, x, y, z);
        if(coords == null) {
            LycanitesMobs.printWarning("CustomSpawner", "This spawn type will never be able to find coordinates as it has no materials or blocks set, not even air.");
            return;
        }

        // Count Coords:
        LycanitesMobs.printDebug("CustomSpawner", "Found " + coords.size() + "/" + this.blockLimit + " coordinates for mob spawning.");
        if(coords.size() <= 0) {
            LycanitesMobs.printDebug("CustomSpawner", "No valid coordinates were found, spawning cancelled.");
            return;
        }

        // Order Coordinates:
        coords = this.orderCoords(coords, x, y, z);

        // Apply Coordinate Limits:
        coords = this.applyCoordLimits(coords);
        LycanitesMobs.printDebug("CustomSpawner", "Applied coordinate limits. New size is " + coords.size());

        // Get Biomes from Coords:
        List<BiomeGenBase> targetBiomes = new ArrayList<BiomeGenBase>();
        if(!this.ignoreBiome) {
            for(int[] coord : coords) {
                BiomeGenBase coordBiome = world.getBiomeGenForCoords(coord[0], coord[2]);
                if(!targetBiomes.contains(coordBiome))
                    targetBiomes.add(coordBiome);
            }
        }

        // Choose Mobs:
        LycanitesMobs.printDebug("CustomSpawner", "Getting a list of all mobs that can spawn within the found coordinates.");
        List<SpawnInfo> possibleSpawns = this.getPossibleSpawns(coords.size(), targetBiomes);
        if(possibleSpawns == null || possibleSpawns.size() <= 0) {
            LycanitesMobs.printDebug("CustomSpawner", "No mobs are able to spawn, either not enough blocks, empty biome/dimension or all weights are 0. Spawning cancelled.");
            return;
        }

        // Spawn Chosen Mobs:
        LycanitesMobs.printDebug("CustomSpawner", "Cycling through each possible spawn coordinate and attempting to spawn a mob there. Mob limit is " + this.mobLimit + " overall.");
        int mobsSpawned = 0;
        for(int[] coord : coords) {
            // Get EntityLiving to Spawn:
            SpawnInfo spawnInfo = this.getRandomMob(possibleSpawns, world);
            EntityLiving entityLiving = null;
            try {
                entityLiving = (EntityLiving)spawnInfo.mobInfo.entityClass.getConstructor(new Class[] {World.class}).newInstance(new Object[] {world});
            } catch (Exception e) { e.printStackTrace(); }
            if(entityLiving == null) {
                LycanitesMobs.printWarning("CustomSpawner", "Unable to instantiate an entity from SpawnInfo: " + spawnInfo);
                continue;
            }

            // Attempt to Spawn EntityLiving:
            LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn " + entityLiving + "...");
            LycanitesMobs.printDebug("CustomSpawner", "Coordinates: X" + coord[0] + " Y" + coord[1] + " Z" + coord[2]);
            entityLiving.setLocationAndAngles((double)coord[0] + 0.5D, (double)coord[1], (double)coord[2] + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

            if(entityLiving instanceof EntityCreatureBase)
                ((EntityCreatureBase)entityLiving).spawnedFromType = spawnInfo.spawnType;
            Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float)coord[0], (float)coord[1], (float)coord[2]);
            
            if(canSpawn == Result.DENY) {
                LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! Spawning blocked by Forge Event, this is caused another mod.");
                continue;
            }
            
            if(canSpawn == Result.DEFAULT && !entityLiving.getCanSpawnHere()) {
                LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! The entity may not fit, there may be to many of it in the area, it may require specific lighting, etc.");
                continue;
            }

            entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
            this.spawnEntity(world, entityLiving);
            if(!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float)coord[0], (float)coord[1], (float)coord[2]))
                entityLiving.onSpawnWithEgg(null);
            LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Passed! Mob spawned.");
            mobsSpawned++;

            // Check Spawn Limit
            if(mobsSpawned >= this.mobLimit)
                break;
        }
        LycanitesMobs.printDebug("CustomSpawner", "Spawning finished. Spawned " + mobsSpawned + " mobs.");
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    /**
     * Returns true if this spawn type should attempt to spawn mobs on the current call.
     * The random chance of spawning and ticks are usually checked here.
     * Event based spawn types will always need to override this due to the tick check.
     * @param tick Used by spawn types that attempt spawn on a regular basis. Use 0 for event based spawning.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     * @return True if this spawn type should spawn mobs and false if it shouldn't this call.
     */
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
        if(this.rate == 0 || tick % this.rate != 0)
            return false;
        if(world.rand.nextDouble() >= this.chance)
            return false;
        return true;
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses te block lists.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
        return this.searchForBlockCoords(world, x, y, z);
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    /**
     * Organizes the list of coordinates found, this is called before the limits are applied. Usually they are shuffled.
     * @param coords
     * @return
     */
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        Collections.shuffle(coords);
        return coords;
    }
    
    
    // ==================================================
    //              Apply Coordinate Limits
    // ==================================================
    /**
     * Takes a list of coordinates and usually shortens it to match the limit. This can be used to do anything to the coordinate list.
     * @param coords The list of coordinates to alter.
     * @return An altered list of coordinates to start spawning from.
     */
    public List<int[]> applyCoordLimits(List<int[]> coords) {
        if(coords.size() > this.blockLimit)
            coords = coords.subList(0, this.blockLimit);
        return coords;
    }


    // ==================================================
    //                Get Possible Spawns
    // ==================================================
    /**
     * Returns a list of all mobs that are able to spawn within the provided biomes and number coordinates.
     * @param coordsFound The number of coordinates found, some mobs require a certain amount of blocks for example to be able to spawn.
     * @param biomes A list of all biomes found from the coordinates.
     * @return Array list of Spawn Info.
     */
    public List<SpawnInfo> getPossibleSpawns(int coordsFound, List<BiomeGenBase> biomes) {
        List<SpawnInfo> possibleSpawns = new ArrayList<SpawnInfo>();
        for(SpawnInfo possibleSpawn : this.spawnList) {
            boolean enoughCoords = true;
            if(coordsFound < possibleSpawn.spawnBlockCost) {
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Not enough of the required blocks available for spawning.");
                enoughCoords = false;
            }

            boolean isValidBiome = this.ignoreBiome;
            if(enoughCoords && !isValidBiome) {
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

            if(enoughCoords && isValidBiome) {
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Able to spawn.");
                possibleSpawns.add(possibleSpawn);
            }
        }
        return possibleSpawns;
    }


    // ==================================================
    //             Get Random Mob to Spawn
    // ==================================================
    /**
     * Get a random mob to spawn.
     * @param possibleSpawns A list of all mobs that can be spawned.
     * @param world The world object to spawn in, used mainly to RNG.
     * @return The Spawn Info of the mob to spawn.
     */
	public SpawnInfo getRandomMob(List<SpawnInfo> possibleSpawns, World world) {
		SpawnInfo spawnInfo = null;
		int totalWeights = 0;
		for(SpawnInfo possibleSpawn : possibleSpawns) {
			totalWeights += possibleSpawn.spawnWeight;
		}
		if(totalWeights <= 0)
			return null;

		int randomWeight = world.rand.nextInt(totalWeights);
		for(SpawnInfo possibleSpawn : possibleSpawns) {
			spawnInfo = possibleSpawn;
			if(possibleSpawn.spawnWeight > randomWeight)
				break;
		}
		return spawnInfo;
	}


    // ==================================================
    //                  Spawn Entity
    // ==================================================
    /**
     * Spawn an entity in the provided world. The mob should have already been positioned.
     * @param world The world to spawn in.
     * @param entityLiving The entity to spawn.
     */
    public void spawnEntity(World world, EntityLiving entityLiving) {
        world.spawnEntityInWorld(entityLiving);
    }


    // ==================================================
    //               Coordinate Searching
    // ==================================================
    /** ========== Search for Block Coordinates ==========
     * Returns all blocks around the xyz position in the given world as coordinates. Uses this Spawn Type's range.
     * @param world The world to search for coordinates in.
     * @param x X position to search near.
     * @param y Y position to search near.
     * @param z Z position to search near.
     * @return Returns a list for coordinates for spawning from.
     */
    public List<int[]> searchForBlockCoords(World world, int x, int y, int z) {
        List<int[]> blockCoords = null;
        for(int i = x - this.range; i <= x + this.range; i++) {
            for(int j = y - this.range; j <= y + this.range; j++) {
                for(int k = z - this.range; k <= z + this.range; k++) {
                    if(this.materials != null && this.materials.length > 0) {
                        if(blockCoords == null) blockCoords = new ArrayList<int[]>();
                        for(Material validMaterial : this.materials) {
                            if(world.getBlock(i, j, k).getMaterial() == validMaterial) {
                                blockCoords.add(new int[] {i, j, k});
                                break;
                            }
                        }
                    }
                    if(this.blocks != null && this.blocks.length > 0) {
                        if(blockCoords == null) blockCoords = new ArrayList<int[]>();
                        for(Block validBlock : this.blocks) {
                            if(world.getBlock(i, j, k) == validBlock) {
                                blockCoords.add(new int[] {i, j, k});
                                break;
                            }
                        }
                    }
                    if(this.blockStrings != null && this.blockStrings.length > 0) {
                        if(blockCoords == null) blockCoords = new ArrayList<int[]>();
                        for(String validBlockString : this.blockStrings) {
                            if(world.getBlock(i, j, k) == ObjectManager.getBlock(validBlockString)) {
                                blockCoords.add(new int[] {i, j, k});
                                break;
                            }
                        }
                    }
                }
            }
        }
        return blockCoords;
    }


    // ==================================================
    //               Coordinate Ordering
    // ==================================================
    /**
     * Orders the coordinates from closest to the origin to farthest.
     * @param coords
     * @return
     */
    public List<int[]> orderCoordsCloseToFar(List<int[]> coords, int x, int y, int z) {
    	Collections.sort(coords, new CoordSorterNearest(new int[] {x, y, z}));
        return coords;
    }
}
