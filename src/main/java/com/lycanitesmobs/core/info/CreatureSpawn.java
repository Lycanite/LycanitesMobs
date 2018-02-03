package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

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


	// Dimensions:
	/** The dimension IDs that the world must or must not match depending on the list type. **/
	public int[] dimensionIds;

	/** How the dimension ID list works. Can be whitelist or blacklist. **/
	public String dimensionListType = "whitelist";


	// Biomes:
	/** The list of biomes that this creature spawns in. **/
	public List<Biome> biomes = new ArrayList<>();

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


	/** Loads this element from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();
		if(json.has("disableSubspecies"))
			this.disableSubspecies = json.get("disableSubspecies").getAsBoolean();

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
		if(json.has("biomes"))
			this.biomes = JSONHelper.getJsonBiomes(json.get("biomes").getAsJsonArray());

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


	/**
	 * Returns if this creature is allowed to spawn in the provided world dimension.
	 * @param world The world to check.
	 * @return True if allowed, false if disallowed.
	 */
	public boolean isAllowedDimension(World world) {
		if(world == null || world.provider == null || this.dimensionIds.length == 0) {
			LycanitesMobs.printDebug("MobSpawns", "No world or dimension spawn settings were found, defaulting to valid.");
			return true;
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
		for(Biome validBiome : this.biomes) {
			if(biomes.contains(validBiome)) {
				return true;
			}
		}
		return false;
	}
}
