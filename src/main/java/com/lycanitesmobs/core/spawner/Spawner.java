package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.SpawnInfo;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import com.lycanitesmobs.core.spawner.location.SpawnLocation;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.*;

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
    public List<MobSpawn> mobSpawns = new ArrayList<>();

    /** Can be set to false to completely disable this Spawner. **/
    public boolean enabled = true;

    /** Determines how many Conditions must be met. If 0 or less all are required. **/
    public int conditionsRequired = 0;

	/** Sets how many times any Trigger must activate (per player) before a wave is spawned. **/
	public int triggersRequired = 1;

	/** Stores how many times each player has triggered any of this spawners Spawn Triggers, the count is reset when a wave is spawned. **/
	protected Map<EntityPlayer, Integer> triggerCounts = new HashMap<>();

	/** A list of messages to send to the player whenever the associated count is reached. **/
	protected Map<Integer, String> triggerCountMessages = new HashMap<>();

    /** Determines how a Location is chosen if there are multiple. Can be: order, random or combine. **/
    public String multipleLocations = "combine";

	/** How many mobs to spawn each wave. **/
	public int mobCount = 1;

	/** If true, this Spawner will ignore all biome checks, this bypasses the biome checks of MobSpawns and SpawnInfos. **/
	public boolean ignoreBiomes = false;

    /** If true, mobs spawned by this Spawner will not naturally despawn. **/
    public boolean forceNoDespawn = false;

	/** If true, this Spawner will pass the current Mob Event to any mobs it spawns meaning the spawned mobs will despawn on the Mob Event despawn time. **/
	public boolean mobEventSpawner = false;


	/** Loads (or reloads) all JSON Spawners. **/
	public static void loadAllFromJSON() {
		// TODO Load JSON Spawners
	}


    /** Loads this Spawner from the provided JSON data. **/
    public void fromJSON(JsonObject json) {
        // TODO Load from JSON.
    }


    /** Returns true if Triggers are allowed to operate for this Spawner. **/
    public boolean canSpawn(World world, EntityPlayer player) {
        if(!this.enabled || SpawnInfo.disableAllSpawning) {
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
	 * @param countAmount How much this trigger affects the trigger count by.
	 * @return True on a successful spawn and false on failure.
	 **/
	public boolean trigger(World world, EntityPlayer player, BlockPos triggerPos, int level, int countAmount) {
		// Only One Trigger Required:
		if(this.triggersRequired <= 1) {
			return this.doSpawn(world, player, triggerPos, level);
		}

		// Get Current Count:
		if(!this.triggerCounts.containsKey(player)) {
			this.triggerCounts.put(player, Math.max(countAmount, 0));
		}
		int currentCount = this.triggerCounts.get(player);
		int lastCount = currentCount;

		// Change Count:
		if(countAmount == 0) {
			currentCount = 0;
		}
		else {
			currentCount += countAmount;
		}
		if(currentCount != lastCount) {
			if(this.triggerCountMessages.containsKey(currentCount)) {
				String message = I18n.translateToLocal(this.triggerCountMessages.get(currentCount));
				player.sendMessage(new TextComponentString(message));
			}
		}

		// Required Count Met:
		if(currentCount >= this.triggersRequired) {
			this.triggerCounts.put(player, 0);
			return this.doSpawn(world, player, triggerPos, level);
		}

		this.triggerCounts.put(player, currentCount);
		return false;
	}


    /**
     * Starts a new spawn, usually called from trigger() when the count is sufficient.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
     * @param triggerPos The location that the spawn was triggered, usually used as the center for spawning around or on.
	 * @param level The level of the spawn trigger, higher levels are from rarer spawn conditions and can result in tougher mobs being spawned.
     * @return True on a successful spawn and false on failure.
     **/
    public boolean doSpawn(World world, EntityPlayer player, BlockPos triggerPos, int level) {
		ExtendedWorld worldExt = ExtendedWorld.getForWorld(world);
		LycanitesMobs.printDebug("JSONSpawner", "~O==================== Spawner Activated: " + this.name + " ====================O~");
		LycanitesMobs.printDebug("JSONSpawner", "Trigger World: " + world);
		LycanitesMobs.printDebug("JSONSpawner", "Trigger Player: " + player);
		LycanitesMobs.printDebug("JSONSpawner", "Trigger Position: " + triggerPos);

    	// Get Positions:
		List<BlockPos> spawnPositions = this.getSpawnPos(world, player, triggerPos);
		if(spawnPositions.isEmpty()) {
			return false;
		}
		Collections.shuffle(spawnPositions);
		LycanitesMobs.printDebug("JSONSpawner", "Positions Found: " + spawnPositions.size());

		// Get Biomes
		List<Biome> biomes = null;
		if(!this.ignoreBiomes) {
			biomes = new ArrayList<>();
			for(BlockPos spawnPos : spawnPositions) {
				Biome biome = world.getBiome(spawnPos);
				if(!biomes.contains(biome))
					biomes.add(biome);
			}
			LycanitesMobs.printDebug("JSONSpawner", "Biomes Found: " + biomes.size());
		}
		else {
			LycanitesMobs.printDebug("JSONSpawner", "All biome checks are ignored by this Spawner.");
		}

		// Get Mobs:
		List<MobSpawn> mobSpawns = this.getMobSpawns(world, player, spawnPositions.size(), biomes);
		LycanitesMobs.printDebug("JSONSpawner", "Mobs Found: " + mobSpawns.size());
		if(mobSpawns.size() == 0) {
			return false;
		}

		int mobsSpawned = 0;
		for(BlockPos spawnPos : spawnPositions) {
			if(mobsSpawned >= this.mobCount) {
				break;
			}
			LycanitesMobs.printDebug("JSONSpawner", "---------- Spawn Iteration: " + mobsSpawned + " ----------");
			LycanitesMobs.printDebug("JSONSpawner", "Spawn Position: " + spawnPos);

			// Choose Mob To Spawn:
			MobSpawn mobSpawn = this.chooseMobToSpawn(world, mobSpawns);
			if(mobSpawn == null) {
				LycanitesMobs.printDebug("JSONSpawner", "No Mob Spawn Chosen");
				continue;
			}
			LycanitesMobs.printDebug("JSONSpawner", "Spawn Mob: " + mobSpawn.mobInfo.name);

			// Create Entity:
			EntityLiving entityLiving = null;
			try {
				entityLiving = (EntityLiving) mobSpawn.mobInfo.entityClass.getConstructor(new Class[]{World.class}).newInstance(new Object[]{world});
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (entityLiving == null) {
				LycanitesMobs.printWarning("JSONSpawner", "Unable to instantiate an entity. Class: " + mobSpawn.mobInfo.entityClass);
				continue;
			}
			EntityCreatureBase entityCreature = null;
			if (entityLiving instanceof EntityCreatureBase) {
				entityCreature = (EntityCreatureBase)entityLiving;
				LycanitesMobs.printDebug("JSONSpawner", "Mob is a Lycanites Mob");
			}
			else {
				LycanitesMobs.printDebug("JSONSpawner", "Mob is not a Lycanites Mob");
			}

			// Attempt To Spawn EntityLiving:
			LycanitesMobs.printDebug("JSONSpawner", "Attempting to spawn " + entityLiving + "...");
			entityLiving.setLocationAndAngles((double) spawnPos.getX() + 0.5D, (double) spawnPos.getY(), (double) spawnPos.getZ() + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);

			// Forge Can Spawn Event:
			Event.Result canSpawn = ForgeEventFactory.canEntitySpawn(entityLiving, world, (float) spawnPos.getX() + 0.5F, (float) spawnPos.getY(), (float) spawnPos.getZ() + 0.5F, false);
			if (canSpawn == Event.Result.DENY && !mobSpawn.ignoreForgeCanSpawnEvent) {
				LycanitesMobs.printDebug("JSONSpawner", "Spawn Check Failed! Spawning blocked by Forge Can Spawn Event, this is caused another mod.");
				continue;
			}

			// Mob Instance Spawn Check:
			if(!this.mobInstanceSpawnCheck(entityLiving, mobSpawn, world, player, triggerPos, level, canSpawn == Event.Result.ALLOW)) {
				LycanitesMobs.printDebug("JSONSpawner", "Mob Instance Spawn Check Failed!");
				continue;
			}

			// Spawn The Mob:
			entityLiving.timeUntilPortal = entityLiving.getPortalCooldown();
			if (entityCreature != null) {
				entityCreature.forceNoDespawn = this.forceNoDespawn;
				entityCreature.spawnedRare = level > 0;
				if (this.mobEventSpawner && worldExt != null && worldExt.getWorldEvent() != null) {
					entityCreature.spawnEventType = worldExt.getWorldEventName();
					entityCreature.spawnEventCount = worldExt.getWorldEventCount();
				}
			}
			this.spawnEntity(world, worldExt, entityLiving, level);
			if(MobEventBase.aggressiveEvents && this.mobEventSpawner && worldExt != null && worldExt.getWorldEvent() != null && player != null) {
				entityLiving.setAttackTarget(player);
			}

			// Call Entity's Initial Spawn:
			if (!ForgeEventFactory.doSpecialSpawn(entityLiving, world, (float) spawnPos.getX() + 0.5F, (float) spawnPos.getY(), (float) spawnPos.getZ() + 0.5F)) {
				if (entityCreature != null) {
					entityLiving.onInitialSpawn(world.getDifficultyForLocation(spawnPos), null);
				}
			}

			LycanitesMobs.printDebug("JSONSpawner", "Spawn Checks Passed! Mob Spawned!");
			mobsSpawned++;
		}

        return mobsSpawned > 0;
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
     * Returns all viable mobs that can be spawned within the spawn conditions. Spawn block costs, chances and biomes are checked here.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
	 * @param blockCount The total number of possible spawn positions found.
	 * @param biomes A list of all biomes within the spawning area. If null, biome checks will be ignored.
     * @return The MobSpawn of the mob to spawn or null if no mob can be spawned at the position.
     **/
    public List<MobSpawn> getMobSpawns(World world, EntityPlayer player, int blockCount, List<Biome> biomes) {
    	List<MobSpawn> allMobSpawns = new ArrayList<>();

    	// Global Spawns:
		Collection<MobSpawn> globalSpawns = SpawnerMobRegistry.getMobSpawns(this.name);
		if(globalSpawns != null) {
			allMobSpawns.addAll(globalSpawns);
		}

		// Local Spawns:
		allMobSpawns.addAll(this.mobSpawns);

		// Get Viable Spawns:
		List<MobSpawn> viableMobSpawns = new ArrayList<>();
		for(MobSpawn possibleMobSpawn : allMobSpawns) {
			if(possibleMobSpawn.canSpawn(world, blockCount, biomes)) {
				viableMobSpawns.add(possibleMobSpawn);
			}
		}

        return viableMobSpawns;
    }


	/**
	 * Gets a weighted random mob to spawn.
	 * @param world The World to spawn in.
	 * @param mobSpawns A list of MobSpawns to choose from.
	 * @return The MobSpawn of the mob to spawn or null if no mob can be spawned.
	 **/
	public MobSpawn chooseMobToSpawn(World world, List<MobSpawn> mobSpawns) {
		// Get Weights:
		int totalWeights = 0;
		for(MobSpawn mobSpawn : mobSpawns) {
			totalWeights += mobSpawn.getWeight();
		}
		if(totalWeights <= 0) {
			return null;
		}

		// Pick Random Spawn Using Weights:
		int randomWeight = 1;
		if(totalWeights > 1) {
			randomWeight = world.rand.nextInt(totalWeights - 1) + 1;
		}
		int searchWeight = 0;
		MobSpawn chosenMobSpawn = null;
		for(MobSpawn mobSpawn : mobSpawns) {
			chosenMobSpawn = mobSpawn;
			if(mobSpawn.getWeight() + searchWeight > randomWeight) {
				break;
			}
			searchWeight += mobSpawn.getWeight();
		}
		return chosenMobSpawn;
	}

	/**
	 * Performs a check from the Mob Instance, this mob has not yet been spawned into the world, but has been instantiated and is able to do per mob checks.
	 * @param entityLiving The to be spawned mob instance.
	 * @param mobSpawn The MobSpawn that is controlling the conditions of the spawn.
	 * @param world The World to spawn in.
	 * @param player The Player that triggered the spawn.
	 * @param spawnPos The position to spawn at.
	 * @param level The level of the spawn.
	 * @param forgeForced If true, the Forge Can Spawn Event wants to force this mob to spawn.
	 * @return True if the check has passed.
	 **/
	public boolean mobInstanceSpawnCheck(EntityLiving entityLiving, MobSpawn mobSpawn, World world, EntityPlayer player, BlockPos spawnPos, int level, boolean forgeForced) {
		// Lycanites Mobs:
		if (entityLiving instanceof EntityCreatureBase) {
			EntityCreatureBase entityCreature = (EntityCreatureBase)entityLiving;

			LycanitesMobs.printDebug("JSONSpawner", "Checking Mob Collision...");
			if(!entityCreature.checkSpawnCollision(world, spawnPos)) {
				return false;
			}

			LycanitesMobs.printDebug("JSONSpawner", "Checking For Nearby Boss...");
			if(!entityCreature.checkSpawnBoss(world, spawnPos)) {
				return false;
			}

			if(mobSpawn.ignoreMobInstanceConditions) {
				LycanitesMobs.printDebug("JSONSpawner", "All Mob Instance Checks Ignored");
				return true;
			}

			if(!mobSpawn.ignoreLightLevel) {
				LycanitesMobs.printDebug("JSONSpawner", "Checking Light Level...");
				if(!entityCreature.checkSpawnLightLevel(world, spawnPos)) {
					return false;
				}
			}

			if(!mobSpawn.ignoreGroupLimit) {
				LycanitesMobs.printDebug("JSONSpawner", "Checking Group Limit...");
				if(!entityCreature.checkSpawnGroupLimit(world, spawnPos)) {
					return false;
				}
			}

			return true;
		}

		// Vanilla or Other Mod Mob:
		if(!mobSpawn.ignoreMobInstanceConditions) {
			LycanitesMobs.printDebug("JSONSpawner", "None Lycanites Mobs Checks...");
			return entityLiving.getCanSpawnHere();
		}
		LycanitesMobs.printDebug("JSONSpawner", "All Mob Instance Checks Ignored");
		return true;
	}

	/**
	 * Spawns an entity into the world. The mob instance should have already been positioned.
	 * @param world The world to spawn in.
	 * @param entityLiving The entity to spawn.
	 */
	public void spawnEntity(World world, ExtendedWorld worldExt, EntityLiving entityLiving, int level) {
		world.spawnEntity(entityLiving);
		if(this.mobEventSpawner && worldExt != null && worldExt.getWorldEvent() != null && entityLiving != null) {
			MobEventBase mobEvent = worldExt.getWorldEvent();
			mobEvent.onSpawn(entityLiving, level);
		}
	}
}
