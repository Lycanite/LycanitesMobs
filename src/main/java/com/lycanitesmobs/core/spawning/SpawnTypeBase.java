package com.lycanitesmobs.core.spawning;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.SpawnInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.spawner.CoordSorterNearest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.eventhandler.Event.Result;

import java.util.*;

public class SpawnTypeBase {
	public static boolean USE_LEGACY_SPAWNER = false;

	// ========== Spawn Type List ==========
	/** A static list that contains a string mapping of all spawn types. These should always be all upper case. **/
	public static Map<String, SpawnTypeBase> spawnTypeMap = new HashMap<String, SpawnTypeBase>();
	
	/** A minimum distance from the player that some spawners should enforce. **/
	public static int rangeMin = 10;
	
	// ========== Spawn Type Properties ==========
	/** The name of this spawn type. This should be the same as the name used in the legacySpawnTypes mapping. Should be all upper case. **/
	public String typeName;
	
	/** Whether this spawner is enabled at all or not. **/
	public boolean enabled = true;
	
	/** A list of all mobs (as SpawnInfo) that use this spawn type. **/
	public List<SpawnInfo> spawnList = new ArrayList<SpawnInfo>();
	
	/** A map of spawn wave limits linked to each spawn type. **/
	public Map<SpawnInfo, Integer> spawnWaveLimits = new HashMap<SpawnInfo, Integer>();
	
	/** A map of the current mobs spawned linked to each spawn type. **/
	public Map<SpawnInfo, Integer> currentSpawnWaveCount;

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
	
	/** If true, this type will ignore the forge mob spawn event being cancelled by other mods. This only overrides the forge event. **/
	public boolean forceSpawning = false;
	
	/** If true, this type will force spawned mobs to not despawn naturally. **/
	public boolean forceNoDespawn = false;

    /** If true, when spawning in blocks, only the surface block is valid (for example a water or fire block with an air block above it). **/
    public boolean blockSurfaceOnly = false;
	
	
    // ==================================================
    //                  Load Spawn Types
    // ==================================================
	public static void loadSpawnTypes() {
		if(!USE_LEGACY_SPAWNER) {
			return;
		}

		List<SpawnTypeBase> spawnTypes = new ArrayList<SpawnTypeBase>();
		
		// Fire Spawner:
		SpawnTypeBase fireBlockSpawner = new SpawnTypeBlock("Fire")
				.setRate(400).setChance(0.5D).setRange(32).setBlockLimit(32).setMobLimit(8);
		fireBlockSpawner.blocks = new Block[] {Blocks.FIRE};
		fireBlockSpawner.ignoreBiome = true;
		fireBlockSpawner.ignoreLight = true;
        fireBlockSpawner.blockSurfaceOnly = true;
		fireBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(fireBlockSpawner);
        spawnTypes.add(fireBlockSpawner);
		
		// Frostfire Spawner:
		SpawnTypeBase frostfireBlockSpawner = new SpawnTypeBlock("Frostfire")
				.setRate(400).setChance(0.5D).setRange(32).setBlockLimit(32).setMobLimit(8);
		frostfireBlockSpawner.blockStrings = new String[] {"frostfire"};
		frostfireBlockSpawner.ignoreBiome = true;
		frostfireBlockSpawner.ignoreLight = true;
        frostfireBlockSpawner.blockSurfaceOnly = true;
		frostfireBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(frostfireBlockSpawner);
        spawnTypes.add(frostfireBlockSpawner);

        // Underground Spawner:
        SpawnTypeBase undergroundSpawner = new SpawnTypeUnderground("Underground")
                .setRate(800).setChance(0.25D).setRange(32).setBlockLimit(16).setMobLimit(8);
        undergroundSpawner.materials = new Material[] {Material.AIR};
        undergroundSpawner.ignoreBiome = false;
        undergroundSpawner.ignoreLight = false;
        undergroundSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(undergroundSpawner);
        spawnTypes.add(undergroundSpawner);
		
		// Sky Spawner:
		SpawnTypeBase skySpawner = new SpawnTypeSky("Sky")
				.setRate(800).setChance(0.5D).setRange(48).setBlockLimit(16).setMobLimit(8);
		skySpawner.materials = new Material[] {Material.AIR};
		skySpawner.ignoreBiome = false;
		skySpawner.ignoreLight = false;
		skySpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(skySpawner);
        spawnTypes.add(skySpawner);
		
		// Water Spawner:
		SpawnTypeBase waterSpawner = new SpawnTypeWater("Water")
				.setRate(400).setChance(0.75D).setRange(32).setBlockLimit(64).setMobLimit(16);
		waterSpawner.blocks = new Block[] {Blocks.WATER};
		waterSpawner.ignoreBiome = false;
		waterSpawner.ignoreLight = false;
		waterSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(waterSpawner);
        spawnTypes.add(waterSpawner);
		
		// Lava Spawner:
		SpawnTypeBase lavaBlockSpawner = new SpawnTypeBlock("Lava")
				.setRate(400).setChance(0.5D).setRange(32).setBlockLimit(64).setMobLimit(8);
		lavaBlockSpawner.blocks = new Block[] {Blocks.LAVA};
		lavaBlockSpawner.ignoreBiome = true;
		lavaBlockSpawner.ignoreLight = true;
		lavaBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(lavaBlockSpawner);
        spawnTypes.add(lavaBlockSpawner);

        // Ooze Spawner:
        SpawnTypeBase spawner = new SpawnTypeBlock("Ooze")
                .setRate(400).setChance(0.5D).setRange(32).setBlockLimit(64).setMobLimit(8);
        spawner.blockStrings = new String[] {"ooze"};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(spawner);
        spawnTypes.add(spawner);
		
		// Portal Spawner:
		SpawnTypeBase portalBlockSpawner = new SpawnTypePortal("Portal")
				.setRate(1200).setChance(0.25D).setRange(32).setBlockLimit(32).setMobLimit(1);
		portalBlockSpawner.blocks = new Block[] {Blocks.PORTAL};
		portalBlockSpawner.ignoreBiome = true;
		portalBlockSpawner.ignoreDimension = true;
		portalBlockSpawner.ignoreLight = true;
		portalBlockSpawner.forceSpawning = true;
		portalBlockSpawner.loadFromConfig();
        CustomSpawner.instance.updateSpawnTypes.add(portalBlockSpawner);
        spawnTypes.add(portalBlockSpawner);
		
		// Rock Spawner:
		SpawnTypeBase rockSpawner = new SpawnTypeRock("Rock")
				.setRate(0).setChance(0.02D).setRange(2).setBlockLimit(32).setMobLimit(1);
		rockSpawner.materials = new Material[] {Material.AIR};
		rockSpawner.ignoreBiome = true;
		rockSpawner.ignoreLight = true;
		rockSpawner.forceSpawning = true;
		rockSpawner.loadFromConfig();
        spawnTypes.add(rockSpawner);
		
		// Crop Spawner:
		SpawnTypeBase cropSpawner = new SpawnTypeCrop("Crop")
				.setRate(0).setChance(0.005D).setRange(2).setBlockLimit(32).setMobLimit(1);
		cropSpawner.materials = new Material[] {Material.AIR};
		cropSpawner.ignoreBiome = true;
		cropSpawner.ignoreLight = true;
		cropSpawner.forceSpawning = true;
		cropSpawner.loadFromConfig();
        spawnTypes.add(cropSpawner);
		
		// Tree Spawner:
		SpawnTypeBase treeSpawner = new SpawnTypeTree("Tree")
				.setRate(0).setChance(0.01D).setRange(2).setBlockLimit(32).setMobLimit(1);
		treeSpawner.materials = new Material[] {Material.AIR};
		treeSpawner.ignoreBiome = true;
		treeSpawner.ignoreLight = true;
		treeSpawner.forceSpawning = true;
		treeSpawner.loadFromConfig();
        spawnTypes.add(treeSpawner);
		
		// Storm Spawner:
		SpawnTypeBase stormSpawner = new SpawnTypeStorm("Storm")
				.setRate(800).setChance(0.125D).setRange(48).setBlockLimit(32).setMobLimit(8);
		stormSpawner.materials = new Material[] {Material.AIR};
		stormSpawner.ignoreBiome = true;
		stormSpawner.ignoreLight = true;
		stormSpawner.forceSpawning = true;
		stormSpawner.loadFromConfig();
        spawnTypes.add(stormSpawner);
		
		// Lunar Spawner:
		SpawnTypeBase lunarSpawner = new SpawnTypeLunar("Lunar")
				.setRate(800).setChance(0.125D).setRange(48).setBlockLimit(32).setMobLimit(8);
		lunarSpawner.materials = new Material[] {Material.AIR};
		lunarSpawner.ignoreBiome = true;
		lunarSpawner.ignoreDimension = false;
		lunarSpawner.ignoreLight = false;
		lunarSpawner.forceSpawning = true;
		lunarSpawner.loadFromConfig();
        spawnTypes.add(lunarSpawner);
		
		// Darkness Spawner:
		SpawnTypeBase darknessSpawner = new SpawnTypeDarkness("Darkness")
				.setRate(0).setChance(0.1D).setRange(2).setBlockLimit(32).setMobLimit(1);
        darknessSpawner.enabled = false;
		darknessSpawner.materials = new Material[] {Material.AIR};
		darknessSpawner.ignoreBiome = true;
		darknessSpawner.ignoreDimension = true;
		darknessSpawner.ignoreLight = true;
		darknessSpawner.forceSpawning = true;
		darknessSpawner.loadFromConfig();
        spawnTypes.add(darknessSpawner);
		
		/*/ Shadow Spawner: Replaced by the Darkness Spawner
		SpawnTypeBase shadowSpawner = new SpawnTypeShadow("Shadow")
				.setRate(0).setChance(0.1D).setRange(2).setBlockLimit(32).setMobLimit(1);
		shadowSpawner.materials = new Material[] {Material.air};
		shadowSpawner.ignoreBiome = true;
		shadowSpawner.ignoreDimension = true;
		shadowSpawner.ignoreLight = true;
		shadowSpawner.forceSpawning = true;
		shadowSpawner.loadFromConfig();
        legacySpawnTypes.add(shadowSpawner);*/
		
		// Death Spawner:
		SpawnTypeBase deathSpawner = new SpawnTypeDeath("Death")
				.setRate(0).setChance(0.03D).setRange(2).setBlockLimit(32).setMobLimit(1);
		deathSpawner.materials = new Material[] {Material.AIR};
		deathSpawner.ignoreBiome = true;
		deathSpawner.ignoreDimension = true;
		deathSpawner.ignoreLight = true;
		deathSpawner.forceSpawning = true;
		deathSpawner.loadFromConfig();
        spawnTypes.add(deathSpawner);

        // Undeath Spawner:
        SpawnTypeBase undeathSpawner = new SpawnTypeUndeath("Undeath")
                .setRate(0).setChance(0.03D).setRange(2).setBlockLimit(32).setMobLimit(1);
        undeathSpawner.materials = new Material[] {Material.AIR};
        undeathSpawner.ignoreBiome = true;
        undeathSpawner.ignoreDimension = true;
        undeathSpawner.ignoreLight = true;
        undeathSpawner.forceSpawning = true;
        undeathSpawner.loadFromConfig();
        spawnTypes.add(undeathSpawner);
		
		// Sleep Spawner:
		SpawnTypeBase sleepSpawner = new SpawnTypeSleep("Sleep")
				.setRate(0).setChance(0.1D).setRange(2).setBlockLimit(32).setMobLimit(1);
		sleepSpawner.materials = new Material[] {Material.AIR};
		sleepSpawner.ignoreBiome = true;
		sleepSpawner.ignoreDimension = true;
		sleepSpawner.ignoreLight = true;
		sleepSpawner.forceSpawning = true;
		sleepSpawner.loadFromConfig();
        spawnTypes.add(sleepSpawner);

        // Fishing Spawner:
        SpawnTypeBase fishingSpawner = new SpawnTypeFishing("Fishing")
                .setRate(0).setChance(0.2D).setRange(2).setBlockLimit(32).setMobLimit(1);
        fishingSpawner.materials = new Material[] {Material.AIR, Material.WATER};
        fishingSpawner.blocks = new Block[] {Blocks.WATER};
        fishingSpawner.ignoreBiome = true;
        fishingSpawner.ignoreDimension = true;
        fishingSpawner.ignoreLight = true;
        fishingSpawner.forceSpawning = true;
        fishingSpawner.loadFromConfig();
        spawnTypes.add(fishingSpawner);
        
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

        config.setCategoryComment("Spawner Checks", "Here you can set whether or not each spawn ignores certain mob checks such as dimension, biome or light level. If Ignores Event is set to true, the spawn type will ignore the mob spawn event meaning that other mods also cannot prevent mob spawns.");
        this.ignoreDimension = config.getBool("Spawner Checks", this.getCfgName("Ignores Dimension"), this.ignoreDimension);
        this.ignoreBiome = config.getBool("Spawner Checks", this.getCfgName("Ignores Biome"), this.ignoreBiome);
        this.ignoreLight = config.getBool("Spawner Checks", this.getCfgName("Ignores Light Level"), this.ignoreLight);
        this.forceSpawning = config.getBool("Spawner Checks", this.getCfgName("Ignores Event"), this.forceSpawning);
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
     * @param mobInfo The MobInfo of the mob to spawn.
     */
	public void addSpawn(MobInfo mobInfo) {
		this.addSpawn(mobInfo, 0);
	}
	
    /**
     * Adds a mob to this spawn type. Takes a Mob Info which will be ignored if null.
     * @param mobInfo The MobInfo of the mob to spawn.
     * @param spawnWaveLimit A limit of how many times this mob can be spawned per wave. Set to 0 for infinite.
     */
	public void addSpawn(MobInfo mobInfo, int spawnWaveLimit) {
		if(mobInfo == null || mobInfo.spawnInfo == null)
            return;
			//LycanitesMobs.printWarning("", "Tried to add a null mob entry to a spawn type.");
		this.spawnList.add(mobInfo.spawnInfo);
		if(spawnWaveLimit > 0)
			this.spawnWaveLimits.put(mobInfo.spawnInfo, spawnWaveLimit);
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
     * @param spawnPos Position to spawn around.
     * @param player The player or null if there is no player.
     * @param rank Higher ranks are from conditions that are rarer than usual which means special things can be done such as having a higher chance of spawning a subspecies.
     */
    public boolean spawnMobs(long tick, World world, BlockPos spawnPos, EntityPlayer player, int rank) {
        // Check If Able to Spawn:
        if(!this.enabled || SpawnInfo.disableAllSpawning || world == null || !world.isAreaLoaded(spawnPos, this.getRange(world)) || this.getSpawnList() == null || this.getSpawnList().size() < 1 || !this.hasSpawns() || !world.getGameRules().getBoolean("doMobSpawning"))
            return false;
        if(!this.canSpawn(tick, world, spawnPos, rank))
            return false;
        
        LycanitesMobs.printDebug("CustomSpawner", "~0==================== " + this.typeName + " Spawner ====================0~");
        LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn mobs.");
        this.currentSpawnWaveCount = new HashMap<SpawnInfo, Integer>();
        
        // Search for Coords:
        List<BlockPos> coords = this.getSpawnCoordinates(world, spawnPos);
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
        coords = this.orderCoords(coords, spawnPos);

        // Apply Coordinate Limits:
        coords = this.applyCoordLimits(coords);
        LycanitesMobs.printDebug("CustomSpawner", "Applied coordinate limits. New size is " + coords.size());

        // Get Biomes from Coords:
        List<Biome> targetBiomes = new ArrayList<Biome>();
        if(!this.ignoreBiome) {
            for(BlockPos coord : coords) {
                Biome coordBiome = world.getBiome(coord);
                if(!targetBiomes.contains(coordBiome))
                    targetBiomes.add(coordBiome);
            }
        }

        // Choose Mobs:
        LycanitesMobs.printDebug("CustomSpawner", "Getting a list of all mobs that can spawn within the found coordinates.");
        List<SpawnInfo> spawnList = this.getSpawnList();
        if(spawnList.isEmpty()) {
            LycanitesMobs.printDebug("CustomSpawner", "No mobs are able to spawn, from this spawn type at all. Spawning cancelled.");
            return false;
        }
        Map<Biome, List<SpawnInfo>> possibleSpawns = this.getPossibleSpawns(spawnList, coords.size(), targetBiomes);
        if(!this.ignoreBiome) {
            if (possibleSpawns == null || possibleSpawns.isEmpty()) {
                LycanitesMobs.printDebug("CustomSpawner", "No mobs are able to spawn, either not enough blocks, empty biome/dimension or all weights are 0. Spawning cancelled.");
                return false;
            }
        }

        // Spawn Chosen Mobs:
        LycanitesMobs.printDebug("CustomSpawner", "Cycling through each possible spawn coordinate and attempting to spawn a mob there. Mob limit is " + this.mobLimit + " overall.");
        int mobsSpawned = 0;
        try {
            for (BlockPos coord : coords) {

                // Get EntityLiving to Spawn:
                SpawnInfo spawnInfo = null;
                if (!this.ignoreBiome) {
                    Biome spawnBiome = world.getBiome(coord);
                    if (!possibleSpawns.containsKey(spawnBiome))
                        continue;
                    if (possibleSpawns.get(spawnBiome).isEmpty()) {
                        LycanitesMobs.printWarning("CustomSpawner", "Tried to spawn in " + spawnBiome + " but there are no possible spawns yet it has a list instantiated, skipping.");
                        continue;
                    }
                    spawnInfo = this.getRandomMob(possibleSpawns.get(spawnBiome), world);
                } else {
                    spawnInfo = this.getRandomMob(spawnList, world);
                }

                // Create Entity:
                EntityLiving entityLiving = null;
                try {
                    entityLiving = (EntityLiving) spawnInfo.mobInfo.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (entityLiving == null) {
                    LycanitesMobs.printWarning("CustomSpawner", "Unable to instantiate an entity from SpawnInfo: " + spawnInfo);
                    continue;
                }

                // Attempt to Spawn EntityLiving:
                LycanitesMobs.printDebug("CustomSpawner", "Attempting to spawn " + entityLiving + "...");
                LycanitesMobs.printDebug("CustomSpawner", "Coordinates: " + coord);
                entityLiving.setLocationAndAngles((double) coord.getX() + 0.5D, (double) coord.getY(), (double) coord.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

                if (entityLiving instanceof EntityCreatureBase)
                    ((EntityCreatureBase) entityLiving).spawnedFromType = this;
                Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float) coord.getX() + 0.5F, (float) coord.getY(), (float) coord.getZ() + 0.5F);

                // Event Overriding:
                if (canSpawn == Result.DENY && !this.forceSpawning) {
                    LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! Spawning blocked by Forge Event, this is caused another mod.");
                    continue;
                }

                // Check if Valid Location
                boolean validLocation = true;
                if (!this.ignoreMobConditions)
                    validLocation = entityLiving.getCanSpawnHere();
                else if (entityLiving instanceof EntityCreatureBase) {
                    LycanitesMobs.printDebug("CustomSpawner", "Ignoring all mob spawn checks except for collision...");
                    boolean ignoreLightTemp = this.ignoreLight;
                    this.ignoreLight = true;
                    validLocation = ((EntityCreatureBase) entityLiving).fixedSpawnCheck(world, coord);
                    this.ignoreLight = ignoreLightTemp;
                }

                if (canSpawn == Result.DEFAULT && !validLocation) {
                    LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Failed! The entity may not fit, there may be to many of it in the area, it may require specific lighting, etc.");
                    continue;
                }

                // Spawn The Mob:
                entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
                if (entityLiving instanceof EntityCreatureBase) {
                    EntityCreatureBase entityCreature = (EntityCreatureBase) entityLiving;
                    entityCreature.forceNoDespawn = this.forceNoDespawn;
                    entityCreature.spawnedRare = rank > 0;
                    ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
                    if (this.mobEvent != null && worldExt != null) {
                        entityCreature.spawnEventType = this.mobEvent.name;
                        entityCreature.spawnEventCount = worldExt.getWorldEventCount();
                    }
                }
                this.spawnEntity(world, entityLiving, rank);
                if(MobEventBase.aggressiveEvents && this.mobEvent != null && player != null) {
                    entityLiving.setAttackTarget(player);
                }

                if (!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float) coord.getX() + 0.5F, (float) coord.getY(), (float) coord.getZ() + 0.5F)) {
                    if (entityLiving instanceof EntityCreatureBase)
                        entityLiving.onInitialSpawn(world.getDifficultyForLocation(coord), null);
                }
                LycanitesMobs.printDebug("CustomSpawner", "Spawn Check Passed! Mob spawned.");
                mobsSpawned++;
                if (!this.currentSpawnWaveCount.containsKey(spawnInfo))
                    this.currentSpawnWaveCount.put(spawnInfo, 1);
                else
                    this.currentSpawnWaveCount.put(spawnInfo, this.currentSpawnWaveCount.get(spawnInfo) + 1);

                // Check Spawn Limit
                if (mobsSpawned >= this.mobLimit)
                    break;
            }
        }
        catch(Exception e) {
            LycanitesMobs.printDebug("CustomSpawner", "An exception occured when spawning " + mobsSpawned + " mobs.");
            if(LycanitesMobs.config.getBool("Debug", "CustomSpawner", false))
                e.printStackTrace();
        }
        LycanitesMobs.printDebug("CustomSpawner", "Spawning finished. Spawned " + mobsSpawned + " mobs.");
        
        return mobsSpawned > 0;
    }
    public boolean spawnMobs(long tick, World world, BlockPos spawnPos, EntityPlayer player) {
        return this.spawnMobs(tick, world, spawnPos, player, 0);
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
     * @param spawnPos The spawn position.
     * @param rank Higher ranks are from conditions that are rarer than usual which means special things can be done such as having a higher chance of spawning a subspecies.
     * @return True if this spawn type should spawn mobs and false if it shouldn't this call.
     */
    public boolean canSpawn(long tick, World world, BlockPos spawnPos, int rank) {
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
     * @param originPos The position to spawn from.
     * @return A list of int BlockPos, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    public List<BlockPos> getSpawnCoordinates(World world, BlockPos originPos) {
        return this.searchForBlockCoords(world, originPos);
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    /**
     * Organizes the list of coordinates found, this is called before the limits are applied. Usually they are shuffled.
     * @param coords
     * @return
     */
    public List<BlockPos> orderCoords(List<BlockPos> coords, BlockPos originPos) {
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
    public List<BlockPos> applyCoordLimits(List<BlockPos> coords) {
        if(coords.size() > this.blockLimit)
            coords = coords.subList(0, this.blockLimit);
        return coords;
    }


    // ==================================================
    //                Get Possible Spawns
    // ==================================================
    /**
     * Returns a list of all mobs that are able to spawn within the provided biomes and number coordinates.
     * @param spawnList A list of spawn info to choose from.
     * @param coordsFound The number of coordinates found, some mobs require a certain amount of blocks for example to be able to spawn.
     * @param biomes A list of all biomes found from the coordinates.
     * @return Map of possible spawns keyed by each provided biome type.
     */
    public Map<Biome, List<SpawnInfo>> getPossibleSpawns(List<SpawnInfo> spawnList, int coordsFound, List<Biome> biomes) {
        Map<Biome, List<SpawnInfo>> possibleSpawns = new HashMap<Biome, List<SpawnInfo>>();
        for(SpawnInfo possibleSpawn : spawnList) {
        	// Check Spawn Wave Limit:
        	boolean withinWaveLimit = true;
        	if(this.spawnWaveLimits.containsKey(possibleSpawn) && this.currentSpawnWaveCount.containsKey(possibleSpawn)) {
        		if(this.currentSpawnWaveCount.get(possibleSpawn) >= this.spawnWaveLimits.get(possibleSpawn)) {
        			withinWaveLimit = false;
                    LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Spawn Wave Limit reached for this mob.");
        		}
        	}
        	
        	// Check If Enabled:
        	boolean isEnabled = withinWaveLimit;
        	if(!isEnabled || possibleSpawn == null || !possibleSpawn.mobInfo.mobEnabled || !possibleSpawn.enabled
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
            List<Biome> spawnBiomes = new ArrayList<Biome>();
            if(enoughCoords && possibleSpawn.biomes != null) {
                for(Biome validBiome : possibleSpawn.biomes) {
                    for(Biome targetBiome : biomes) {
                        if(targetBiome == validBiome || this.ignoreBiome || possibleSpawn.ignoreBiome) {
                            spawnBiomes.add(targetBiome);
                            break;
                        }
                    }
                }
            }
            if(spawnBiomes.isEmpty())
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": No valid spawning biomes could be found within the coordinates.");
            
            // Add If Valid:
            if(isEnabled && enoughCoords && !spawnBiomes.isEmpty()) {
                LycanitesMobs.printDebug("CustomSpawner", possibleSpawn.mobInfo.name + ": Able to spawn.");
                for(Biome spawnBiome : spawnBiomes) {
                    if(!possibleSpawns.containsKey(spawnBiome))
                        possibleSpawns.put(spawnBiome, new ArrayList<SpawnInfo>());
                    possibleSpawns.get(spawnBiome).add(possibleSpawn);
                }
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
    public void spawnEntity(World world, EntityLiving entityLiving, int rank) {
        world.spawnEntity(entityLiving);
        if(this.mobEvent != null && entityLiving != null) {
        	this.mobEvent.onSpawn(entityLiving, rank);
        }
    }


    // ==================================================
    //               Coordinate Searching
    // ==================================================
    /** ========== Search for Block Coordinates ==========
     * Returns all blocks around the xyz position in the given world as coordinates. Uses this Spawn Type's range.
     * @param world The world to search for coordinates in.
     * @param searchPos The BlockPos to search around.
     * @return Returns a list for coordinates for spawning from.
     */
    public List<BlockPos> searchForBlockCoords(World world, BlockPos searchPos) {
        List<BlockPos> blockCoords = null;
        int range = this.getRange(world);

        for (int y = searchPos.getY() - range; y <= searchPos.getY() + range; y++) {
            // Y Limits:
            if (y < 0) y = 0;
            if (y >= world.getActualHeight())
                break;

            for (int x = searchPos.getX() - range; x <= searchPos.getX() + range; x++) {
                for (int z = searchPos.getZ() - range; z <= searchPos.getZ() + range; z++) {
                    BlockPos spawnPos = new BlockPos(x, y, z);
                    IBlockState blockState = world.getBlockState(spawnPos);
                    if (blockState == null)
                        continue;

                    // Ignore Flowing Liquids:
                    if (blockState.getBlock() instanceof IFluidBlock) {
                        float filled = ((IFluidBlock) blockState.getBlock()).getFilledPercentage(world, spawnPos);
                        if (filled != 1 && filled != -1) {
                            continue;
                        }
                    }
                    if (blockState.getBlock() instanceof BlockLiquid) {
                        if (blockState.getBlock().getMetaFromState(blockState) != 0) {
                            continue;
                        }
                    }

                    // Check Materials:
                    if (this.materials != null && this.materials.length > 0) {
                        if (blockCoords == null)
                            blockCoords = new ArrayList<BlockPos>();
                        for (Material validMaterial : this.materials) {
                            if (blockState.getMaterial() == validMaterial && this.isValidCoord(world, spawnPos)) {
                                if(!this.blockSurfaceOnly || (world.isAirBlock(spawnPos.up()))) {
                                    blockCoords.add(spawnPos);
                                }
                                continue;
                            }
                        }
                    }

                    // Check Blocks:
                    if (this.blocks != null && this.blocks.length > 0) {
                        if (blockCoords == null)
                            blockCoords = new ArrayList<BlockPos>();
                        for (Block validBlock : this.blocks) {
                            if (blockState.getBlock() == validBlock) {
                                if(!this.blockSurfaceOnly || (world.isAirBlock(spawnPos.up()))) {
                                    blockCoords.add(spawnPos);
                                }
                                continue;
                            }
                        }
                    }

                    // Check Object Manager Blocks:
                    if (this.blockStrings != null && this.blockStrings.length > 0) {
                        if (blockCoords == null)
                            blockCoords = new ArrayList<BlockPos>();
                        for (String validBlockString : this.blockStrings) {
                            if (blockState.getBlock() == ObjectManager.getBlock(validBlockString)) {
                                if(!this.blockSurfaceOnly || (world.isAirBlock(spawnPos.up()))) {
                                    blockCoords.add(spawnPos);
                                }
                                continue;
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
     * @return Returns a BlockPos or null if no coord was found.
     */
    public BlockPos getRandomLandCoord(World world, BlockPos originPos, int range) {
        int[] xz = this.getRandomXZCoord(world, originPos.getX(), originPos.getZ(), rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, new BlockPos(x, originPos.getY(), z), 0, range, true, Blocks.AIR, false);
        return y > -1 ? new BlockPos(x, y, z) : null;
    }


    // ==================================================
    //            Get Random Water Spawn Coord
    // ==================================================
    /** Gets a random spawn position from a the provided origin chunk position.
     * @param world The world to search for coordinates in.
     * @return Returns a BlockPos or null if no coord was found.
     */
    public BlockPos getRandomWaterCoord(World world, BlockPos originPos, int range) {
        int[] xz = this.getRandomXZCoord(world, originPos.getX(), originPos.getZ(), rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, new BlockPos(x, originPos.getY(), z), 0, range, false, Blocks.WATER, false);
        return y > -1 ? new BlockPos(x, y, z) : null;
    }


    // ==================================================
    //            Get Random Sky Spawn Coord
    // ==================================================
    /** Gets a random sky spawn position from a the provided origin chunk position.
     * This checks for open air spaces.
     * @param world The world to search for coordinates in.
     * @return Returns a BlockPos or null if no coord was found.
     */
    public BlockPos getRandomSkyCoord(World world, BlockPos originPos, int range) {
        int[] xz = this.getRandomXZCoord(world, originPos.getX(), originPos.getZ(), rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, new BlockPos(x, originPos.getY(), z), 0, range, false, Blocks.AIR, false);
        return y > -1 ? new BlockPos(x, y, z) : null;
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
     * @param originPos The position to search from using XZ coords and up and down within range of the Y coord.
     * @param rangeMin The minimum range from the origin allowed.
     * @param rangeMax The maximum range from the origin allowed.
     * @param solidSurface If true, this will search for a block with a solidSurface top (land), else it will search for air (sky).
     * @param insideBlock The block type to spawn in, usually air but can also be water or other liquids, etc.
     * @param underground If true, this spawn position must not be able to see the sky.
     * @return The y position, -1 if a valid position could not be found.
     */
    public int getRandomYCoord(World world, BlockPos originPos, int rangeMin, int rangeMax, boolean solidSurface, Block insideBlock, boolean underground) {
        int originX = originPos.getX();
        int originY = originPos.getY();
        int originZ = originPos.getZ();
    	int minY = Math.max(originY - rangeMax, 0);
        int maxY = originY + rangeMax;
        List<Integer> yCoordsLow = new ArrayList<Integer>();
        List<Integer> yCoordsHigh = new ArrayList<Integer>();

        // Get Every Valid Y Pos:
        for(int nextY = minY; nextY <= maxY; nextY++) {
            // If the next Y coord to check is not within the min range, boost it up to the min range:
            if(nextY > originY - rangeMin && nextY < originY + rangeMin)
                nextY = originY + rangeMin;

            BlockPos spawnPos = new BlockPos(originX, nextY, originZ);
            IBlockState blockState = world.getBlockState(spawnPos);
            Block block = blockState.getBlock();

            // If block is the inside block or if checking for a solid surface, if the block is a solid surface to spawn on.
            if(block != null && ((!solidSurface && block == insideBlock) || (solidSurface && this.validGroundBlock(blockState, world, spawnPos)))) {
                // Make sure the block above is within range:
            	if(nextY + 1 > originY - minY && nextY + 1 < originY - maxY)
            		continue;

                // If above ground:
                if(world.canBlockSeeSky(spawnPos)) {
                    BlockPos checkPos = spawnPos;
                    if(solidSurface)
                        checkPos = checkPos.add(0, 1, 0);
                    if(underground || world.getBlockState(checkPos).getBlock() != insideBlock)
                        break;
                	if(!solidSurface) {
	                    int skyCoord = nextY;
	                    int skyRange = Math.min(world.getHeight() - 1, maxY) - skyCoord;
                        // Get random y coord within inside block:
	                    if(skyRange > 1) {
                            if(insideBlock != Blocks.AIR)
                                skyRange = this.getInsideBlockHeight(world, checkPos, insideBlock);
                            nextY += world.rand.nextInt(skyRange);
                        }
	                    if(skyRange == 1)
	                    	nextY = 1;
                	}
                    if(nextY + 1 <= 64)
                        yCoordsLow.add(nextY + 1);
                    else
                        yCoordsHigh.add(nextY + 1);
                    break;
                }
                
                else if(this.doesCoordHaveSpace(world, spawnPos.add(0, 1, 0), insideBlock)) {
                    if(nextY + 1 <= 64)
                        yCoordsLow.add(nextY + 1);
                    else
                        yCoordsHigh.add(nextY + 1);
                    nextY += 2;
                }
            }
        }

        // Pick Random Y Pos:
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

    /** Returns the height of insideBlocks from the starting position checking upwards until the insideBlock is no longer found. **/
    public int getInsideBlockHeight(World world, BlockPos startPos, Block insideBlock) {
        int y;
        for(y = startPos.getY(); y < world.getActualHeight(); y++) {
            BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
            if(world.getBlockState(checkPos).getBlock() != insideBlock)
                break;
        }
        return y - startPos.getY();
    }

    /** Returns true if the specified block is suitable for spawning land mobs on top of. **/
    public boolean validGroundBlock(IBlockState blockState, World world, BlockPos pos) {
        if(blockState == null)
            return false;
        try {
            if(blockState.isNormalCube())
                return true;
        } catch(Exception e) {}
        try {
            if (blockState.isSideSolid(world, pos, EnumFacing.UP))
                return true;
            if (blockState.isSideSolid(world, pos, EnumFacing.DOWN))
                return true;
        } catch(Exception e) {}
        return false;
    }


    // ==================================================
    //                Does Coord Have Space
    // ==================================================
    /**
     * Returns true if the provided coordinate has space to spawn an entity at it.
     * This works by checking if it is an air block and if the block above is also an air block.
     * @return True if there is space else false.
     */
    public boolean doesCoordHaveSpace(World world, BlockPos pos, Block insideBlock) {
        Block feet = world.getBlockState(pos).getBlock();
        if(feet == null) return false;
        if(feet != insideBlock) return false;

        Block head = world.getBlockState(pos.add(0, 1, 0)).getBlock();
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
    public BlockPos getRandomChunkCoord(World world, Chunk chunk, int range) {
    	range = Math.min(range, 16);
        int x = chunk.x + world.rand.nextInt(range);
        int z = chunk.z + world.rand.nextInt(range);
        x += 16 - range;
        z += 16 - range;
        int y = world.rand.nextInt(chunk == null ? world.getActualHeight() : chunk.getTopFilledSegment() + 1);
        return new BlockPos(x, y, z);
    }
    
    
    // ==================================================
    //               Coordinate Checking
    // ==================================================
    /** Checks if the provided world coordinate is valid for this spawner to use. This should not include block type/material checks as they are done elsewhere.
     * @param world The world to search for coordinates in.
     * @param pos Position to check.
     * @return Returns true if it is a valid coordinate so that it can be added to the list.
     */
    public boolean isValidCoord(World world, BlockPos pos) {
    	return true;
    }


    // ==================================================
    //               Coordinate Ordering
    // ==================================================
    /**
     * Orders the coordinates from closest to the origin to farthest.
     * @param coords The list of coords to sort.
     * @param originPos The position to sort them from.
     * @return
     */
    public List<BlockPos> orderCoordsCloseToFar(List<BlockPos> coords, BlockPos originPos) {
    	Collections.sort(coords, new CoordSorterNearest(originPos));
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
