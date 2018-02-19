package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Biomes;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Contains default spawn conditions for a creature. **/
public class CreatureSpawn {

	// General:
	/** If false, spawning of this creature is disabled, but this wont despawn existing creatures. **/
	public boolean enabled = true;

	/** If true, this mob wont naturally spawn as a subspecies. **/
	public boolean disableSubspecies = false;


	// Spawners:
	/** A list of Spawners that this creature should use. **/
	public List<String> spawners = new ArrayList<>();

	/** A list of Vanilla Creature Types to use. **/
	public List<EnumCreatureType> creatureTypes = new ArrayList<>();


	// Dimensions:
	/** The dimension IDs that the world must or must not match depending on the list type. **/
	public int[] dimensionIds;

	/** How the dimension ID list works. Can be whitelist or blacklist. **/
	public String dimensionListType = "whitelist";


	// Biomes:
	/** The list of biome tags that this creature spawns in. Converts to a list of biomes on demand. **/
	public List<String> biomeTags = new ArrayList<>();

	/** The list of biomes that this creature spawns in. **/
	public List<Biome> biomes = null;

	/** If true, the biome check will be ignored completely by this creature. **/
	public boolean ignoreBiome = false;


	// Weights:
	/** The chance of this mob spawning over others. **/
	public int spawnWeight = 8;

	/** The chance of dungeons using this mob over others. **/
	public int dungeonWeight = 200;


	// Limits:
	/** The maximum arount of this mob allowed within the Spawn Area Search Limit. **/
	public int spawnAreaLimit = 5;

	/** The minimum number of this mob to group spawn at once. **/
	public int spawnGroupMin = 1;

	/** The maximum number of this mob to group spawn at once. **/
	public int spawnGroupMax = 3;


	// Area Conditions:
	/** Whether or not this mob can spawn in high light levels. **/
	public boolean spawnsInLight = false;

	/** Whether or not this mob can spawn in low light levels. **/
	public boolean spawnsInDark = true;

	/** The minimum world days that must have gone by, can accept fractions such as 5.25 for 5 and a quarter days. **/
	public double worldDayMin = -1;


	// Despawning:
	/** Whether this mob should despawn or not by default (some mobs can override persistence, such as once farmed). **/
	public boolean despawnNatural = true;

	/** Whether this mob should always despawn no matter what. **/
	public boolean despawnForced = false;


	/**
	 * Loads this element from a JSON object.
	 */
	public void loadFromJSON(JsonObject json) {
		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();
		if(json.has("disableSubspecies"))
			this.disableSubspecies = json.get("disableSubspecies").getAsBoolean();

		// Spawners:
		this.spawners.clear();
		this.creatureTypes.clear();
		if(json.has("spawners")) {
			this.spawners = JSONHelper.getJsonStrings(json.get("spawners").getAsJsonArray());
			for(String spawner : this.spawners) {
				if ("monster".equalsIgnoreCase(spawner))
					this.creatureTypes.add(EnumCreatureType.MONSTER);
				else if ("creature".equalsIgnoreCase(spawner))
					this.creatureTypes.add(EnumCreatureType.CREATURE);
				else if ("watercreature".equalsIgnoreCase(spawner))
					this.creatureTypes.add(EnumCreatureType.WATER_CREATURE);
				else if ("ambient".equalsIgnoreCase(spawner))
					this.creatureTypes.add(EnumCreatureType.AMBIENT);
			}
		}

		// Dimensions:
		if(json.has("dimensionIds")) {
			JsonArray jsonArray = json.get("dimensionIds").getAsJsonArray();
			this.dimensionIds = new int[jsonArray.size()];
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			int i = 0;
			while (jsonIterator.hasNext()) {
				this.dimensionIds[i] = jsonIterator.next().getAsInt();
				i++;
			}
		}
		if(json.has("dimensionListType"))
			this.dimensionListType = json.get("dimensionListType").getAsString();

		// Biomes:
		if(json.has("ignoreBiome"))
			this.ignoreBiome = json.get("ignoreBiome").getAsBoolean();
		if(json.has("biomes")) {
			this.biomeTags.clear();
			this.biomes = null;
			this.biomeTags = JSONHelper.getJsonStrings(json.get("biomes").getAsJsonArray());
		}

		if(json.has("spawnWeight"))
			this.spawnWeight = json.get("spawnWeight").getAsInt();
		if(json.has("dungeonWeight"))
			this.dungeonWeight = json.get("dungeonWeight").getAsInt();

		if(json.has("spawnAreaLimit"))
			this.spawnAreaLimit = json.get("spawnAreaLimit").getAsInt();
		if(json.has("spawnGroupMin"))
			this.spawnGroupMin = json.get("spawnGroupMin").getAsInt();
		if(json.has("spawnGroupMax"))
			this.spawnGroupMax = json.get("spawnGroupMax").getAsInt();

		if (json.has("spawnsInLight"))
			this.spawnsInLight = json.get("spawnsInLight").getAsBoolean();
		if (json.has("spawnsInDark"))
			this.spawnsInDark = json.get("spawnsInDark").getAsBoolean();
		if(json.has("worldDayMin"))
			this.worldDayMin = json.get("worldDayMin").getAsInt();

		if (json.has("despawnNatural"))
			this.despawnNatural = json.get("despawnNatural").getAsBoolean();
		if (json.has("despawnForced"))
			this.despawnForced = json.get("despawnForced").getAsBoolean();
	}


	/** Initialises this Creature Spawn Info, should be called after pre-init and when reloading. **/
	public void init(CreatureInfo creatureInfo) {
		for(String spawner : this.spawners) {
			LycanitesMobs.printDebug("Creature", "Adding " + creatureInfo.getName() + " to " + spawner + " global spawn list.");
			SpawnerMobRegistry.createSpawn(creatureInfo, spawner);
		}
	}


	/**
	 * Registers this mob to vanilla spawners and dungeons. Can only be done during startup.
	 */
	public void register(CreatureInfo creatureInfo) {
		// Load Biomes:
		if(this.biomes == null) {
			this.biomes = JSONHelper.getJsonBiomes(this.biomeTags);
		}

		// Add Vanilla Spawns:
		if(!CreatureManager.getInstance().spawnConfig.disableAllSpawning) {
			if(this.enabled && this.spawnWeight > 0 && this.spawnGroupMax > 0) {
				for(EnumCreatureType creatureType : this.creatureTypes) {
					EntityRegistry.addSpawn(creatureInfo.entityClass, this.spawnWeight, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, this.biomes.toArray(new Biome[this.biomes.size()]));
					for(Biome biome : this.biomes) {
						if(biome == Biomes.HELL) {
							EntityRegistry.addSpawn(creatureInfo.entityClass, this.spawnWeight * 10, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMin, CreatureManager.getInstance().spawnConfig.ignoreWorldGenSpawning ? 0 : this.spawnGroupMax, creatureType, biome);
							break;
						}
					}
				}
			}
		}

		// Dungeon Spawn:
		if(!CreatureManager.getInstance().spawnConfig.disableDungeonSpawners) {
			if(this.dungeonWeight > 0) {
				DungeonHooks.addDungeonMob(creatureInfo.getResourceLocation(), this.dungeonWeight);
				LycanitesMobs.printDebug("MobSetup", "Dungeon Spawn Added - Weight: " + this.dungeonWeight);
			}
		}
	}


	/**
	 * Returns if this creature is allowed to spawn in the provided world dimension.
	 * @param world The world to check.
	 * @return True if allowed, false if disallowed.
	 */
	public boolean isAllowedDimension(World world) {
		// Default:
		if(world == null || world.provider == null || this.dimensionIds.length == 0) {
			LycanitesMobs.printDebug("MobSpawns", "No world or dimension spawn settings were found, defaulting to valid.");
			return true;
		}

		// Global Check:
		if(!CreatureManager.getInstance().spawnConfig.isAllowedGlobal(world)) {
			return false;
		}

		// Check IDs:
		for(int dimensionId : this.dimensionIds) {
			if(world.provider.getDimension() == dimensionId) {
				LycanitesMobs.printDebug("MobSpawns", "Dimension is in " + this.dimensionListType + ".");
				return this.dimensionListType.equalsIgnoreCase("whitelist");
			}
		}
		LycanitesMobs.printDebug("MobSpawns", "Dimension was not in " + this.dimensionListType + ".");
		return this.dimensionListType.equalsIgnoreCase("blacklist");
	}


	/**
	 * Returns if any of the provided biomes are valid for this creature to spawn in.
	 * @param biomes A list of biomes to find a match in.
	 * @return True if at least one biome in the provided list is a valid biome.
	 */
	public boolean isValidBiome(List<Biome> biomes) {
		if(this.ignoreBiome) {
			return true;
		}
		if(this.biomes == null) {
			this.biomes = JSONHelper.getJsonBiomes(this.biomeTags);
		}
		for(Biome validBiome : this.biomes) {
			if(biomes.contains(validBiome)) {
				return true;
			}
		}
		return false;
	}
}
