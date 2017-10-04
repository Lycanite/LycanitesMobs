package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import com.lycanitesmobs.core.spawner.location.SpawnLocation;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Spawner {
    /** Spawners are loaded from JSON and operate around their Triggers, Conditions and Locations. **/

    /** The name of this spawner, must be unique, used by creatures/groups when entering spawners. **/
    public String name;

    /** A list of all Spawn Conditions which determine if the Triggers should be active or not. **/
    public List<SpawnCondition> conditions = new ArrayList<>();

    /** A list of all Spawn Triggers which listen to events and ticks and attempt a spawn, if conditions are met. **/
    public List<SpawnTrigger> triggers = new ArrayList<>();

    /** A list of all Spawn Locations which are used to determine where this Spawner can actually spawn the mob, some Triggers require a specific spawn location other provide an area. **/
    public List<SpawnLocation> locations = new ArrayList<>();

    /** A list of all Mobs that can be spawned by this spawner. **/
    public List<MobInfo> mobs = new ArrayList<>();

    /** Can be set to false to completely disable this Spawner. **/
    public boolean enabled = true;

    /** Determines how many Conditions must be met. If 0 or less all are required. **/
    public int conditionsRequired = 0;

    /** Determines how a Location is chosen if there are multiple. Can be: order, random or combine. **/
    public String multipleLocations = "combine";

	/** How many mobs to spawn each wave. **/
	public int mobCount = 1;

    /** Sets if this Spawner should check if the spawn location is natural for mobs or not. **/
    public boolean ignoreMobConditions = false;


    /** Loads this Spawner from the provided JSON data. **/
    public void fromJSON(JsonObject json) {
        // TODO Load from JSON.
    }


    /** Returns true if Triggers are allowed to operate for this Spawner. **/
    public boolean canSpawn(World world, EntityPlayer player) {
        if(!this.enabled) {
            return false;
        }

        int conditionsMet = 0;
        for(SpawnCondition condition : this.conditions) {
            if(condition.isMet(world, player)) {
                conditionsMet++;
                if(this.conditionsRequired > 0 && conditionsMet >= this.conditionsRequired) {
                    return true;
                }
            }
            else {
                if(this.conditionsRequired <= 0) {
                    return false;
                }
            }
        }

        return false;
    }


    /**
     * Starts a new spawn, usually called by Triggers.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
     * @param triggerPos The location that the spawn was triggered, usually used as the center for spawning around or on.
	 * @param level The level of the spawn trigger, higher levels are from rarer spawn conditions and can result in tougher mobs being spawned.
     * @return True on a successful spawn and false on failure.
     **/
    public boolean doSpawn(World world, EntityPlayer player, BlockPos triggerPos, int level) {

    	// Get Positions:
		List<BlockPos> spawnPositions = this.getSpawnPos(world, player, triggerPos);
		if(spawnPositions.isEmpty()) {
			return false;
		}
		Collections.shuffle(spawnPositions);

		// Get Biomes
		List<Biome> biomes = new ArrayList<>();
		if(!this.ignoreMobConditions) {
			for(BlockPos spawnPos : spawnPositions) {
				Biome biome = world.getBiome(spawnPos);
				if(!biomes.contains(biome))
					biomes.add(biome);
			}
		}

		// Get Mobs:
		List<MobSpawn> mobSpawns = this.getMobSpawns(world, player, spawnPositions.size(), biomes);

		int spawnIteration = 0;
		for(BlockPos spawnPos : spawnPositions) {
			if(spawnIteration >= this.mobCount) {
				break;
			}

			// Choose Mob To Spawn:
			MobInfo mobInfo = this.chooseMobToSpawn(world, player, spawnPositions.size(), spawnPos, mobSpawns);
			if(mobInfo == null) {
				continue;
			}

			// TODO Spawn Mob Logic and Extra Checks

			spawnIteration++; // Only increases after each successful spawn.
		}

        return spawnIteration > 0;
    }


		/**
		 * Gets a list of BlockPos to spawn at. Returns an empty list if all Spawn Locations fail to get a spawn position. If no Spawn Locations are set, the triggerPos is used.
		 * @param world The World to spawn in.
		 * @param player The player that is being spawned around.
		 * @param triggerPos The location that the spawn was triggered, usually used as the center for spawning around or on.
		 * @return True on a successful spawn and false on failure.
		 **/
	public List<BlockPos> getSpawnPos(World world, EntityPlayer player, BlockPos triggerPos) {
		List<BlockPos> spawnPositions = new ArrayList<>();

		if(this.locations.isEmpty()) {
			spawnPositions.add(triggerPos);
			return spawnPositions;
		}
		if(this.locations.size() == 1) {
			return this.locations.get(0).getSpawnPositions(world, player, triggerPos);
		}

		if("order".equals(this.multipleLocations)) {
			for(SpawnLocation location : this.locations) {
				spawnPositions = location.getSpawnPositions(world, player, triggerPos);
				if(!spawnPositions.isEmpty()) {
					return spawnPositions;
				}
			}
		}

		if("random".equals(this.multipleLocations)) {
			List<List<BlockPos>> possibleSpawnPosLists = new ArrayList<>();
			for(SpawnLocation location : this.locations) {
				spawnPositions = location.getSpawnPositions(world, player, triggerPos);
				if(!spawnPositions.isEmpty()) {
					possibleSpawnPosLists.add(spawnPositions);
				}
			}
			if(possibleSpawnPosLists.isEmpty()) {
				return spawnPositions;
			}
			if(possibleSpawnPosLists.size() == 1) {
				return possibleSpawnPosLists.get(0);
			}
			int randomIndex = world.rand.nextInt(possibleSpawnPosLists.size());
			return possibleSpawnPosLists.get(randomIndex);
		}

		if("combine".equals(this.multipleLocations)) {
			for(SpawnLocation location : this.locations) {
				spawnPositions.addAll(location.getSpawnPositions(world, player, triggerPos));
			}
			return spawnPositions;
		}

		return spawnPositions;
	}


    /**
     * Returns all viable mobs that can be spawned within the spawn conditions.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
	 * @param blockCount The total number of possible spawn positions found.
	 * @param biomes A list of all biomes within the spawning area.
     * @return The MobSpawn of the mob to spawn or null if no mob can be spawned at the position.
     **/
    public List<MobSpawn> getMobSpawns(World world, EntityPlayer player, int blockCount, List<Biome> biomes) {
    	// TODO Get MobSpawns for this Spawner and global!
        return null;
    }


	/**
	 * Gets a weighted random mob to spawn.
	 * @param world The World to spawn in.
	 * @param player The player that is being spawned around.
	 * @param blockCount The total number of possible spawn positions found.
	 * @param spawnPos The position to spawn at.
	 * @param mobSpawns A list of MobSpawns to choose from.
	 * @return The MobSpawn of the mob to spawn or null if no mob can be spawned at the position.
	 **/
	public MobInfo chooseMobToSpawn(World world, EntityPlayer player, int blockCount, BlockPos spawnPos, List<MobSpawn> mobSpawns) {
		// TODO Choose Weighted Mob!
		return null;
	}
}
