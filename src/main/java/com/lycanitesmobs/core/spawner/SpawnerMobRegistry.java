package com.lycanitesmobs.core.spawner;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class SpawnerMobRegistry {
	/** This class globally maps Mobs to Spawners. MobSpawns created from SpawnInfo configs are handled here, MobSpawns defined in Spawner JSONs are handled by the Spawner class instead. **/

	/** A static map of each SpawnerMobRegistry by Spawner name. Note that even if a Spawner name is not found via JSON, a SpawnerMobRegistry is still created for it. **/
	public static Map<String, SpawnerMobRegistry> SPAWNER_MOB_REGISTRIES = new HashMap<>();

	/** A map of all MobSpawns added to this SpawnerMobRegistry. **/
	public Map<CreatureInfo, MobSpawn> mobSpawns = new HashMap<>();


	/**
	 * Returns a collection of global MobSpawns for the provided Spawner name.
	 * @param spawnerName The (shared) name of the Spawner to get MobSpawns for.
	 * @return A collection of MobSpawns, returns null if no global MobSpawns exists for the Spawner.
	 **/
	public static Collection<MobSpawn> getMobSpawns(String spawnerName) {
		LycanitesMobs.printDebug("JSONSpawner", "Getting Global Mobs For Spawner: " + spawnerName);
		if(SPAWNER_MOB_REGISTRIES.containsKey(spawnerName.toUpperCase())) {
			return SPAWNER_MOB_REGISTRIES.get(spawnerName.toUpperCase()).mobSpawns.values();
		}
		return null;
	}


	/**
	 * Generates a new global SpawnInfo and adds it to the provided SpawnerMobRegistry which is created if it doesn't exist.
	 * @param creatureInfo The Creature Info of the mob to create a Mob Spawn from.
	 * @param spawnerName The name of the Spawner to map the MobSpawn to.
	 **/
	public static void createSpawn(CreatureInfo creatureInfo, String spawnerName) {
		if(creatureInfo == null) {
			LycanitesMobs.printWarning("", "Tried to create a global Mob Spawn to " + spawnerName + " from a null Creature Info!");
		}
		if(spawnerName == null) {
			LycanitesMobs.printWarning("", "Tried to create a global Mob Spawn for " + creatureInfo.getName() + " with a null Spawner Name!");
		}

		MobSpawn mobSpawn = new MobSpawn(creatureInfo);

		if(!SPAWNER_MOB_REGISTRIES.containsKey(spawnerName)) {
			SPAWNER_MOB_REGISTRIES.put(spawnerName, new SpawnerMobRegistry());
		}
		if(!SPAWNER_MOB_REGISTRIES.get(spawnerName).addMobSpawn(mobSpawn)) {
			LycanitesMobs.printWarning("", "Tried to create a duplicate global MobSpawn for " + creatureInfo.getName() + " in Spawner: " + spawnerName + "!");
		}
	}


	/**
	 * Adds a new MobSpawn to this registry.
	 * @param mobSpawn The global MobSpawn to add.
	 * @return True on success, false on failure (if it already exists).
	 **/
	public boolean addMobSpawn(MobSpawn mobSpawn) {
		if(this.mobSpawns.containsKey(mobSpawn.creatureInfo)) {
			return false;
		}
		this.mobSpawns.put(mobSpawn.creatureInfo, mobSpawn);
		return true;
	}
}
