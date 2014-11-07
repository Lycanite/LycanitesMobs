package lycanite.lycanitesmobs.api.spawning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.ForgeEventFactory;
import cpw.mods.fml.common.eventhandler.Event.Result;

public class SpawnTypeBase {
	// ========== Spawn Type List ==========
	/** A static list that contains a string mapping of all spawn types. These should always be all upper case. **/
	public static Map<String, SpawnTypeBase> spawnTypeMap = new HashMap<String, SpawnTypeBase>();
	
	/** A minimum distance from the player that some spawners should enforce. **/
	public static int rangeMin = 10;
	
	// ========== Spawn Type Properties ==========
	/** The name of this spawn type. This should be the same as the name used in the spawnTypes mapping. Should be all upper case. **/
	public String typeName;
	
	/** Whether this spawner is enabled at all or not. **/
	public boolean enabled = true;
	
	/** A list of all mobs (as SpawnInfo) that use this spawn type. **/
	public List<SpawnInfo> spawnList = new ArrayList<SpawnInfo>();

    /** This is used by Mob Events to link an event with a Spawn Type. **/
    public MobEventBase mobEvent = null;
	
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
	public boolean ignoreLight = true;
	
	/** If true, mobs spawned by this spawner wont check for other nearby mobs of its kind. **/
	public boolean ignoreRangeMin = false;
	
	/** If true, mobs spawned by this spawner wont check any conditions at all except for space to fit in. **/
	public boolean ignoreMobConditions = false;
	
	/** If true, this type will ignore the mob spawn event being cancelled. This only overrides the forge event. **/
	public boolean forceSpawning = false;
	
	/** If true, this type will force spawned mobs to not despawn naturally. **/
	public boolean forceNoDespawn = false;
	
	
    // ==================================================
    //                  Load Spawn Types
    // ==================================================
	public static void loadSpawnTypes() {
		List<SpawnTypeBase> spawnTypes = new ArrayList<SpawnTypeBase>();
		
		// Fire Spawner:
		SpawnTypeBase fireBlockSpawner = new SpawnTypeBlock("Fire")
				.setRate(400).setChance(0.5D).setRange(32).setBlockLimit(32).setMobLimit(32);
		fireBlockSpawner.blocks = new Block[] {Blocks.fire};
		fireBlockSpawner.ignoreBiome = true;
		fireBlockSpawner.ignoreLight = true;
		fireBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(fireBlockSpawner);
        spawnTypes.add(fireBlockSpawner);
		
		// Frostfire Spawner:
		SpawnTypeBase frostfireBlockSpawner = new SpawnTypeBlock("Frostfire")
				.setRate(400).setChance(0.5D).setRange(32).setBlockLimit(32).setMobLimit(32);
		frostfireBlockSpawner.blockStrings = new String[] {"frostfire"};
		frostfireBlockSpawner.ignoreBiome = true;
		frostfireBlockSpawner.ignoreLight = true;
		frostfireBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(frostfireBlockSpawner);
        spawnTypes.add(frostfireBlockSpawner);
		
		// Sky Spawner:
		SpawnTypeBase skySpawner = new SpawnTypeSky("Sky")
				.setRate(400).setChance(0.75D).setRange(48).setBlockLimit(32).setMobLimit(32);
		skySpawner.materials = new Material[] {Material.air};
		skySpawner.ignoreBiome = false;
		skySpawner.ignoreLight = false;
		skySpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(skySpawner);
        spawnTypes.add(skySpawner);
		
		// Water Spawner:
		SpawnTypeBase waterSpawner = new SpawnTypeWater("Water")
				.setRate(200).setChance(0.75D).setRange(32).setBlockLimit(64).setMobLimit(32);
		waterSpawner.blocks = new Block[] {Blocks.water};
		waterSpawner.ignoreBiome = false;
		waterSpawner.ignoreLight = false;
		waterSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(waterSpawner);
        spawnTypes.add(waterSpawner);
		
		// Lava Spawner:
		SpawnTypeBase lavaBlockSpawner = new SpawnTypeBlock("Lava")
				.setRate(400).setChance(0.25D).setRange(32).setBlockLimit(64).setMobLimit(32);
		lavaBlockSpawner.blocks = new Block[] {Blocks.lava};
		lavaBlockSpawner.ignoreBiome = true;
		lavaBlockSpawner.ignoreLight = true;
		lavaBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(lavaBlockSpawner);
        spawnTypes.add(lavaBlockSpawner);
		
		// Portal Spawner:
		SpawnTypeBase portalBlockSpawner = new SpawnTypeBlock("Portal")
				.setRate(1200).setChance(0.25D).setRange(32).setBlockLimit(32).setMobLimit(1);
		portalBlockSpawner.blocks = new Block[] {Blocks.portal};
		portalBlockSpawner.ignoreBiome = true;
		portalBlockSpawner.ignoreDimension = true;
		portalBlockSpawner.ignoreLight = true;
		portalBlockSpawner.forceSpawning = true;
		portalBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(portalBlockSpawner);
        spawnTypes.add(portalBlockSpawner);
		
		// Rock Spawner:
		SpawnTypeBase rockSpawner = new SpawnTypeRock("Rock")
				.setRate(0).setChance(0.03D).setRange(2).setBlockLimit(32).setMobLimit(1);
		rockSpawner.materials = new Material[] {Material.air};
		rockSpawner.ignoreBiome = true;
		rockSpawner.ignoreLight = true;
		rockSpawner.forceSpawning = true;
		rockSpawner.loadFromConfig();
        spawnTypes.add(rockSpawner);
		
		// Crop Spawner:
		SpawnTypeBase cropSpawner = new SpawnTypeCrop("Crop")
				.setRate(0).setChance(0.01D).setRange(2).setBlockLimit(32).setMobLimit(1);
		cropSpawner.materials = new Material[] {Material.air};
		cropSpawner.ignoreBiome = true;
		cropSpawner.ignoreLight = true;
		cropSpawner.forceSpawning = true;
		cropSpawner.loadFromConfig();
        spawnTypes.add(cropSpawner);
		
		// Tree Spawner:
		SpawnTypeBase treeSpawner = new SpawnTypeTree("Tree")
				.setRate(0).setChance(0.03D).setRange(2).setBlockLimit(32).setMobLimit(1);
		treeSpawner.materials = new Material[] {Material.air};
		treeSpawner.ignoreBiome = true;
		treeSpawner.ignoreLight = true;
		treeSpawner.forceSpawning = true;
		treeSpawner.loadFromConfig();
        spawnTypes.add(treeSpawner);
		
		// Storm Spawner:
		SpawnTypeBase stormSpawner = new SpawnTypeStorm("Storm")
				.setRate(800).setChance(0.125D).setRange(48).setBlockLimit(32).setMobLimit(32);
		stormSpawner.materials = new Material[] {Material.air};
		stormSpawner.ignoreBiome = true;
		stormSpawner.ignoreLight = true;
		stormSpawner.forceSpawning = true;
		stormSpawner.loadFromConfig();
        spawnTypes.add(stormSpawner);
		
		// Lunar Spawner:
		SpawnTypeBase lunarSpawner = new SpawnTypeLunar("Lunar")
				.setRate(800).setChance(0.5D).setRange(48).setBlockLimit(32).setMobLimit(32);
		lunarSpawner.materials = new Material[] {Material.air};
		lunarSpawner.ignoreBiome = true;
		lunarSpawner.ignoreDimension = true;
		lunarSpawner.ignoreLight = false;
		lunarSpawner.forceSpawning = true;
		lunarSpawner.loadFromConfig();
        spawnTypes.add(lunarSpawner);
		
		// Shadow Spawner:
		SpawnTypeBase shadowSpawner = new SpawnTypeShadow("Shadow")
				.setRate(0).setChance(0.1D).setRange(2).setBlockLimit(32).setMobLimit(1);
		shadowSpawner.materials = new Material[] {Material.air};
		shadowSpawner.ignoreBiome = true;
		shadowSpawner.ignoreDimension = true;
		shadowSpawner.ignoreLight = true;
		shadowSpawner.forceSpawning = true;
		shadowSpawner.loadFromConfig();
        spawnTypes.add(shadowSpawner);
		
		// Death Spawner:
		SpawnTypeBase deathSpawner = new SpawnTypeDeath("Death")
				.setRate(0).setChance(0.03D).setRange(2).setBlockLimit(32).setMobLimit(1);
		deathSpawner.materials = new Material[] {Material.air};
		deathSpawner.ignoreBiome = true;
		deathSpawner.ignoreDimension = true;
		deathSpawner.ignoreLight = true;
		deathSpawner.forceSpawning = true;
		deathSpawner.loadFromConfig();
        spawnTypes.add(deathSpawner);
		
		// Sleep Spawner:
		SpawnTypeBase sleepSpawner = new SpawnTypeSleep("Sleep")
				.setRate(0).setChance(0.1D).setRange(2).setBlockLimit(32).setMobLimit(1);
		sleepSpawner.materials = new Material[] {Material.air};
		sleepSpawner.ignoreBiome = true;
		sleepSpawner.ignoreDimension = true;
		sleepSpawner.ignoreLight = true;
		sleepSpawner.forceSpawning = true;
		sleepSpawner.loadFromConfig();
        spawnTypes.add(sleepSpawner);
        
        // Add Spawners to Custom Spawner Map:
        for(SpawnTypeBase spawnType : spawnTypes) {
			spawnTypeMap.put(spawnType.typeName.toUpperCase(), spawnType);
			LycanitesMobs.printDebug("CustomSpawner", "Added custom spawn type: " + spawnType.typeName);
		}
	}
	
	
    // ==================================================
    //                  Get Spawn Types
    // ==================================================
	public static SpawnTypeBase getSpawnType(String spawnTypeName) {
		if(spawnTypeMap.containsKey(spawnTypeName))
			return spawnTypeMap.get(spawnTypeName);
		return null;
	}

    public static SpawnTypeBase[] getSpawnTypes(String spawnTypeNames) {
        List<SpawnTypeBase> spawnTypeList = new ArrayList<SpawnTypeBase>();
        for(String spawnTypeName : spawnTypeNames.split(",")) {
            SpawnTypeBase spawnType = getSpawnType(spawnTypeName);
            if(spawnType != null)
                spawnTypeList.add(spawnType);
        }
        return spawnTypeList.toArray(new SpawnTypeBase[spawnTypeList.size()]);
    }
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public SpawnTypeBase(String typeName) {
		this.typeName = typeName.toUpperCase();
	}


    // ==================================================
    //                 Load from Config
    // ==================================================
    public void loadFromConfig() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");
		
		config.setCategoryComment("Spawners Enabled", "Here you can turn each special spawner on or off.");
		this.enabled = config.getBool("Spawners Enabled", this.getCfgName("Spawn Enabled"), this.enabled);
		
		config.setCategoryComment("Spawner Ticks", "Here you can set the interval that a spawner will try and spawn in ticks. (20 ticks = 1 second). Increase this and lower and the chance if you are having lag issues, this will make the spawner less frequent but more predicatable.");
		this.rate = config.getInt("Spawner Ticks", this.getCfgName("Spawn Tick"), this.rate);
		
		config.setCategoryComment("Spawner Chances", "Here you can set the chance that a spawner will try and spawn mobs, this chance is tested after every interval.");
		this.chance = config.getDouble("Spawner Chances", this.getCfgName("Spawn Chance"), this.chance);

		config.setCategoryComment("Spawner Ranges", "Here you can set how far from the player or event location that mobs can be spawned. Lower this is you are having major lag issues.");
		this.range = config.getInt("Spawner Ranges", this.getCfgName("Spawn Range"), this.range);

		config.setCategoryComment("Spawner Block Limits", "Here you can set a maximum limit of how many locations the spawner is allowed to spawn mobs at.");
		this.blockLimit = config.getInt("Spawner Block Limits", this.getCfgName("Spawn Block Limit"), this.blockLimit);

		config.setCategoryComment("Spawner Mob Limits", "Here you can set the limit of how many mobs a spawner can spawn every interval. Be aware that each mob will use it's own Area Spawn Limit which will drastically decrease the number of mobs spawned overall.");
		this.mobLimit = config.getInt("Spawner Mob Limits", this.getCfgName("Spawn Mob Limit"), this.mobLimit);
    }


    // ==================================================
    //                      Names
    // ==================================================
    public String getCfgName(String configKey) {
        return this.typeName + " " + configKey;
    }
	
	
	// ==================================================
	//                     Spawn List
	// ==================================================
    /**
     * Adds a mob to this spawn type. Takes a Mob Info which will be ignored if null.
     * @param spawnInfo
     */
	public void addSpawn(MobInfo mobInfo) {
		//if(mobInfo == null)
			//LycanitesMobs.printWarning("", "Tried to add a null mob entry to a spawn type.");
		if(mobInfo != null && mobInfo.spawnInfo != null)
			this.spawnList.add(mobInfo.spawnInfo);
	}
	
    /**
     * Gets a list of all mobs that this spawner can spawn.
     * @return A list of SpawnInfos.
     */
	public List<SpawnInfo> getSpawnList() {
		return this.spawnList;
	}
	
    /**
     * Returns true if this spawn type has at least one mob to spawn.
     * @return True if the spawn list's size is > 0;
     */
	public boolean hasSpawns() {
		return this.spawnList.size() > 0;
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
    public boolean spawnMobs(long tick, World world, int x, int y, int z) {
        // Check If Able to Spawn:
        if(this.getSpawnList() == null || this.getSpawnList().size() < 1 || !this.enabled)
            return false;
        if(!this.canSpawn(tick, world, x, y, z))
            return false;
        
        LycanitesMobs.printDebug("CustomSpawner", "~0==================== " + this.typeName + " Spawner ====================0~");
        LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn mobs.");
        
        // Search for Coords:
        List<int[]> coords = this.getSpawnCoordinates(world, x, y, z);
        if(coords == null) {
            LycanitesMobs.printWarning("CustomSpawner", "Null coordinates! This spawn type might never be able to find coordinates as it has no materials or blocks set, not even air.");
            return false;
        }

        // Count Coords:
        LycanitesMobs.printDebug("CustomSpawner", "Found " + coords.size() + "/" + this.blockLimit + " coordinates for mob spawning.");
        if(coords.size() <= 0) {
            LycanitesMobs.printDebug("CustomSpawner", "No valid coordinates were found, spawning cancelled.");
            return false;
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
            return false;
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
                ((EntityCreatureBase)entityLiving).spawnedFromType = this;
            Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float)coord[0], (float)coord[1], (float)coord[2]);

            // Event Overriding:
            if(canSpawn == Result.DENY && !this.forceSpawning) {
                LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! Spawning blocked by Forge Event, this is caused another mod.");
                continue;
            }
            
            // Check if Valid Location
            boolean validLocation = true;
            if(!this.ignoreMobConditions)
                validLocation = entityLiving.getCanSpawnHere();
            else if(entityLiving instanceof EntityCreatureBase) {
                LycanitesMobs.printDebug("CustomSpawner", "Ignoring all mob spawn checks except for collision...");
                boolean ignoreLightTemp = this.ignoreLight;
                this.ignoreLight = true;
                validLocation = ((EntityCreatureBase)entityLiving).fixedSpawnCheck(world, coord[0], coord[1], coord[2]);
                this.ignoreLight = ignoreLightTemp;
            }

            if(canSpawn == Result.DEFAULT && !validLocation) {
                LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! The entity may not fit, there may be to many of it in the area, it may require specific lighting, etc.");
                continue;
            }
            
            // Spawn The Mob:
            entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
            if(entityLiving instanceof EntityCreatureBase) {
            	((EntityCreatureBase)entityLiving).forceNoDespawn = this.forceNoDespawn;
            	if(this.mobEvent != null)
            		((EntityCreatureBase)entityLiving).spawnEventType = this.mobEvent.name;
            }
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
        
        return mobsSpawned > 0;
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
        if(this.getRate(world) == 0 || tick % this.getRate(world) != 0)
            return false;
        if(world.rand.nextDouble() >= this.chance)
            return false;
        return true;
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses the block lists.
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
        for(SpawnInfo possibleSpawn : this.getSpawnList()) {
        	// Check If Enabled:
        	boolean isEnabled = true;
        	if(possibleSpawn == null || !possibleSpawn.mobInfo.mobEnabled || !possibleSpawn.enabled
					|| possibleSpawn.spawnWeight <= 0 || possibleSpawn.spawnGroupMax <= 0) {
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Not enabled, will not spawn.");
        		isEnabled = false;
        	}
        	
        	// Check Coordinate Count:
            boolean enoughCoords = true;
            if(coordsFound < possibleSpawn.spawnBlockCost) {
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Not enough of the required blocks available for spawning.");
                enoughCoords = false;
            }
            
            // Check Biomes:
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
            
            // Add If Valid:
            if(isEnabled && enoughCoords && isValidBiome) {
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
		
		// Get Weights:
		int totalWeights = 0;
		for(SpawnInfo spawnEntry : possibleSpawns) {
			totalWeights += spawnEntry.spawnWeight;
		}
		if(totalWeights <= 0)
			return null;
		
		// Pick Random Spawn Using Weights:
		int randomWeight = 1;
		if(totalWeights > 1)
			randomWeight = world.rand.nextInt(totalWeights - 1) + 1;
		int searchWeight = 0;
		SpawnInfo spawnInfo = null;
		for(SpawnInfo spawnEntry : possibleSpawns) {
			spawnInfo = spawnEntry;
			if(spawnEntry.spawnWeight + searchWeight > randomWeight)
				break;
			searchWeight += spawnEntry.spawnWeight;
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
        if(this.mobEvent != null) {
        	this.mobEvent.onSpawn(entityLiving);
        }
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
        int range = this.getRange(world);
        
        for(int i = x - range; i <= x + range; i++) {
            for(int j = y - range; j <= y + range; j++) {
                for(int k = z - range; k <= z + range; k++) {
                    if(this.materials != null && this.materials.length > 0) {
                        if(blockCoords == null) blockCoords = new ArrayList<int[]>();
                        for(Material validMaterial : this.materials) {
                            if(world.getBlock(i, j, k).getMaterial() == validMaterial && this.isValidCoord(world, i, j, k)) {
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
    //            Get Random Land Spawn Coord
    // ==================================================
    /** Gets a random spawn position from a the provided origin chunk position.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition or null if no coord was found.
     */
    public ChunkPosition getRandomLandCoord(World world, ChunkPosition originPos, int range) {
        int radius = Math.round(range * 0.5F);
        int[] xz = this.getRandomXZCoord(world, originPos.chunkPosX, originPos.chunkPosZ, rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, x, originPos.chunkPosY, z, rangeMin, range, true, Blocks.air);
        return y > -1 ? new ChunkPosition(x, y, z) : null;
    }


    // ==================================================
    //            Get Random Water Spawn Coord
    // ==================================================
    /** Gets a random spawn position from a the provided origin chunk position.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition or null if no coord was found.
     */
    public ChunkPosition getRandomWaterCoord(World world, ChunkPosition originPos, int range) {
        int radius = Math.round(range * 0.5F);
        int[] xz = this.getRandomXZCoord(world, originPos.chunkPosX, originPos.chunkPosZ, rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, x, originPos.chunkPosY, z, rangeMin, range, false, Blocks.water);
        return y > -1 ? new ChunkPosition(x, y, z) : null;
    }


    // ==================================================
    //            Get Random Sky Spawn Coord
    // ==================================================
    /** Gets a random sky spawn position from a the provided origin chunk position.
     * This checks for open air spaces.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition or null if no coord was found.
     */
    public ChunkPosition getRandomSkyCoord(World world, ChunkPosition originPos, int range) {
        int radius = Math.round(range * 0.5F);
        int[] xz = this.getRandomXZCoord(world, originPos.chunkPosX, originPos.chunkPosZ, rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, x, originPos.chunkPosY, z, rangeMin, range, false, Blocks.air);
        return y > -1 ? new ChunkPosition(x, y, z) : null;
    }


    // ==================================================
    //               Get Random XZ Coord
    // ==================================================
    /**
     * Gets a random XZ position from the provided XZ position using the provided range and range max radiuses.
     * @param world The world that the coordinates are being selected in, mainly for getting Random.
     * @param originX The origin x position.
     * @param originZ The origin z position.
     * @param rangeMax The maximum range from the origin allowed.
     * @param rangeMin The minimum range from the origin allowed.
     * @return An integer array containing two ints the X and Z position.
     */
    public int[] getRandomXZCoord(World world, int originX, int originZ, int rangeMin, int rangeMax) {
    	float xScale = world.rand.nextFloat();
    	float zScale = world.rand.nextFloat();
    	float minScale = (float)(rangeMin) / (float)(rangeMin);
    	
    	if(xScale + zScale < minScale * 2) {
    		float xShare = world.rand.nextFloat();
    		float zShare = 1.0F - xShare;
    		xScale += minScale * xShare;
    		zScale += minScale * zShare;
    	}
    	
    	int x = Math.round(rangeMax * xScale);
    	int z = Math.round(rangeMax * zScale);
    	
    	if(world.rand.nextBoolean())
    		x = originX + x;
    	else
    		x = originX - x;
    	
    	if(world.rand.nextBoolean())
    		z = originZ + z;
    	else
    		z = originZ - z;
    	
    	return new int[] {x, z};
    }


    // ==================================================
    //               Get Random Y Coord
    // ==================================================
    /**
     * Gets a random Y position from the provided XYZ position using the provided range and range max radiuses.
     * @param world The world that the coordinates are being selected in, mainly for getting Random.
     * @param originX The origin x position.
     * @param originY The origin y position.
     * @param originZ The origin z position.
     * @param rangeMax The maximum range from the origin allowed.
     * @param rangeMin The minimum range from the origin allowed.
     * @param solid If true, this will search for a block with a solid top (land), else it will search for air (sky).
     * @param insideBlock The block type to spawn in, usually air can also be water or other liquids, etc.
     * @return The y position, -1 if a valid position could not be found.
     */
    public int getRandomYCoord(World world, int originX, int originY, int originZ, int rangeMin, int rangeMax, boolean solid, Block insideBlock) {
    	int minY = Math.max(originY - rangeMax, 0);
        int maxY = originY + rangeMax;
        List<Integer> yCoordsLow = new ArrayList<Integer>();
        List<Integer> yCoordsHigh = new ArrayList<Integer>();
        for(int nextY = minY; nextY <= maxY; nextY++) {
        	
            Block block = world.getBlock(originX, nextY, originZ);
            if(block != null && (
            		(!solid && block == insideBlock) ||
            		(solid && world.isSideSolid(originX, nextY, originZ, ForgeDirection.UP))
            )) {
            	
            	if(nextY + 1 > originY - minY && nextY + 1 < originY - maxY)
            		continue;
            	
                if(world.canBlockSeeTheSky(originX, nextY, originZ)) {
                	if(!solid) {
	                    int skyCoord = nextY;
	                    int skyMax = Math.min(world.getHeight() - 1, maxY) - skyCoord;
	                    if(skyMax > 1)
	                    	nextY += world.rand.nextInt(skyMax);
	                    if(skyMax == 1)
	                    	nextY = 1;
                	}
                    if(nextY + 1 <= 64)
                        yCoordsLow.add(nextY + 1);
                    else
                        yCoordsHigh.add(nextY + 1);
                    break;
                }
                
                else if(this.doesCoordHaveSpace(world, originX, nextY + 1, originZ, insideBlock)) {
                    if(nextY + 1 <= 64)
                        yCoordsLow.add(nextY + 1);
                    else
                        yCoordsHigh.add(nextY + 1);
                    nextY += 2;
                }
            }
        }
        
        int y = -1;
        if(yCoordsHigh.size() > 0 && (yCoordsLow.size() <= 0 || world.rand.nextFloat() > 0.25F)) {
        	if(yCoordsHigh.size() == 1)
        		y = yCoordsHigh.get(0);
        	else
        		y = yCoordsHigh.get(world.rand.nextInt(yCoordsHigh.size() - 1));
        }
        else if(yCoordsLow.size() > 0) {
        	if(yCoordsLow.size() == 1)
        		y = yCoordsLow.get(0);
        	else
            	y = yCoordsLow.get(world.rand.nextInt(yCoordsLow.size() - 1));
        }
        
        return y;
    }


    // ==================================================
    //                Does Coord Have Space
    // ==================================================
    /**
     * Returns true if the provided coordinate has space to spawn an entity at it.
     * This works by checking if it is an air block and if the block above is also an air block.
     * @return True if there is space else false.
     */
    public boolean doesCoordHaveSpace(World world, int x, int y, int z, Block insideBlock) {
        Block feet = world.getBlock(x, y, z);
        if(feet == null) return false;
        if(feet != insideBlock) return false;

        Block head = world.getBlock(x, y + 1, z);
        if(head == null) return false;
        if(head != insideBlock) return false;

        return true;
    }
    
    
    // ==================================================
    //           Get Random Spawn Pos from Chunk
    // ==================================================
    /** Gets a random spawn position from a the provided chunk.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition.
     */
    public ChunkPosition getRandomChunkCoord(World world, Chunk chunk, int range) {
    	range = Math.min(range, 16);
        int x = chunk.xPosition + world.rand.nextInt(range);
        int z = chunk.zPosition + world.rand.nextInt(range);
        x += 16 - range;
        z += 16 - range;
        int y = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 1);
        return new ChunkPosition(x, y, z);
    }
    
    
    // ==================================================
    //               Coordinate Checking
    // ==================================================
    /** Checks if the provided world coordinate is valid for this spawner to use. This should not include block type/material checks as they are done elsewhere.
     * @param world The world to search for coordinates in.
     * @param x X position to check.
     * @param y Y position to check.
     * @param z Z position to check.
     * @return Returns true if it is a valid coordinate so that it can be added to the list.
     */
    public boolean isValidCoord(World world, int x, int y, int z) {
    	return true;
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


    // ==================================================
    //                    Set Values
    // ==================================================
    public SpawnTypeBase setRate(int integer) {
    	this.rate = integer;
    	return this;
    }

    public SpawnTypeBase setChance(double dbl) {
    	this.chance = dbl;
    	return this;
    }

    public SpawnTypeBase setRange(int integer) {
    	this.range = integer;
    	return this;
    }

    public SpawnTypeBase setBlockLimit(int integer) {
    	this.blockLimit = integer;
    	return this;
    }

    public SpawnTypeBase setMobLimit(int integer) {
    	this.mobLimit = integer;
    	return this;
    }

    public SpawnTypeBase setMobEvent(MobEventBase mobEvent) {
        this.mobEvent = mobEvent;
        this.forceSpawning = mobEvent.forceSpawning;
        this.forceNoDespawn = mobEvent.forceNoDespawn;
        return this;
    }


    // ==================================================
    //                    Get Values
    // ==================================================
    public int getRate(World world) {
        if(this.mobEvent != null)
            return this.mobEvent.getRate(world);
        return this.rate;
    }

    public int getRange(World world) {
        if(this.mobEvent != null)
            return this.mobEvent.getRange(world);
        return this.range;
    }
}
