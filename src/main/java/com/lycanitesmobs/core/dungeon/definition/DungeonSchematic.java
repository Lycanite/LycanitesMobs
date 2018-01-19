package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.spawner.MobSpawn;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class DungeonSchematic {
    /** Dungeon Schematics define actual dungeons to spawn using a collection of Sectors, Themes, etc. **/

	/** The unique name of this dungeon. Required. **/
	public String name = "";

	/** Whether this Schematic is enabled or not. **/
	public boolean enabled = true;

	/** The minimum amount of sectors this dungeon should have in total. **/
	public int sectorCountMin = 30;

	/** The maximum amount of sectors this dungeon should have in total. **/
	public int sectorCountMax = 50;

	/** The chance of a corridor connecting to another corridor. **/
	public double corridorToCorridorChance = 0.1D;

	/** The chance of a room connecting to another room. **/
	public double roomToRoomChance = 0.1D;

	/** A list of SpawnConditions to use. Optional. **/
	public List<SpawnCondition> conditions = new ArrayList<>();

	/** A list of themes to use. Required. **/
	public List<String> themes = new ArrayList<>();

	/** A list of entrance sectors to use. Required. **/
	public List<String> entrances = new ArrayList<>();

	/** A list of room sectors to use. Required. **/
	public List<String> rooms = new ArrayList<>();

	/** A list of corridor sectors to use. Required. **/
	public List<String> corridors = new ArrayList<>();

	/** A list of stairs sectors to use. Required. **/
	public List<String> stairs = new ArrayList<>();

	/** A list of MobSpawns to use. Optional. **/
	public List<MobSpawn> mobSpawns = new ArrayList<>();


    /** Loads this Dungeon Theme from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();

		if(json.has("sectorCountMin"))
			this.sectorCountMin = json.get("sectorCountMin").getAsInt();

		if(json.has("sectorCountMax"))
			this.sectorCountMax = json.get("sectorCountMax").getAsInt();

		if(json.has("corridorToCorridorChance"))
			this.corridorToCorridorChance = json.get("corridorToCorridorChance").getAsDouble();

		if(json.has("roomToRoomChance"))
			this.roomToRoomChance = json.get("roomToRoomChance").getAsDouble();

		// Conditions:
		if(json.has("conditions")) {
			JsonArray jsonArray = json.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject conditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
				this.conditions.add(spawnCondition);
			}
		}

		// Themes:
		if(json.has("themes")) {
			for(JsonElement jsonElement : json.get("themes").getAsJsonArray()) {
				String jsonString = jsonElement.getAsString().toLowerCase();
				if(!this.themes.contains(jsonString))
					this.themes.add(jsonString);
			}
		}

		// Entrances:
		if(json.has("entrances")) {
			for(JsonElement jsonElement : json.get("entrances").getAsJsonArray()) {
				String jsonString = jsonElement.getAsString().toLowerCase();
				if(!this.entrances.contains(jsonString))
					this.entrances.add(jsonString);
			}
		}

		// Rooms:
		if(json.has("rooms")) {
			for(JsonElement jsonElement : json.get("rooms").getAsJsonArray()) {
				String jsonString = jsonElement.getAsString().toLowerCase();
				if(!this.rooms.contains(jsonString))
					this.rooms.add(jsonString);
			}
		}

		// Corridors:
		if(json.has("corridors")) {
			for(JsonElement jsonElement : json.get("corridors").getAsJsonArray()) {
				String jsonString = jsonElement.getAsString().toLowerCase();
				if(!this.corridors.contains(jsonString))
					this.corridors.add(jsonString);
			}
		}

		// Stairs:
		if(json.has("stairs")) {
			for(JsonElement jsonElement : json.get("stairs").getAsJsonArray()) {
				String jsonString = jsonElement.getAsString().toLowerCase();
				if(!this.stairs.contains(jsonString))
					this.stairs.add(jsonString);
			}
		}

		// Mob Spawns:
		if(json.has("mobSpawns")) {
			JsonArray jsonArray = json.get("mobSpawns").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject mobSpawnJson = jsonIterator.next().getAsJsonObject();
				MobSpawn mobSpawn = MobSpawn.createFromJSON(mobSpawnJson);
				if(mobSpawn != null) {
					this.mobSpawns.add(mobSpawn);
				}
			}
		}
	}


	/**
	 * Returns if this Dungeon Schematic is allowed to be used for the given world and position.
	 * @param world The world to use the Schematic in.
	 * @param pos The position to build a Dungeon from with this Schematic.
	 * @return
	 */
	public boolean canBuild(World world, BlockPos pos) {
		if(!this.enabled) {
			return false;
		}

		for(SpawnCondition condition : this.conditions) {
			if(!condition.isMet(world, null)) {
				return false;
			}
		}

		// TODO Add biomes.

		return true;
	}


	/**
	 * Returns a random number of sectors to generate.
	 * @param random The random instance to use.
	 * @return A random number of sectors to generate.
	 */
	public int getRandomSectorCount(Random random) {
		if(this.sectorCountMax <= this.sectorCountMin) {
			return this.sectorCountMin;
		}
		return this.sectorCountMin + random.nextInt(this.sectorCountMax - this.sectorCountMin + 1);
	}


	/**
	 * Returns a random sector of the provided type.
	 * @param type The type of sector to get.
	 * @param random The random instance to use.
	 * @return A random Dungeon Sector.
	 */
	public DungeonSector getRandomSector(String type, Random random) {
		// Get Sector List:
		List<String> sectorList;
		if("entrance".equalsIgnoreCase(type))
			sectorList = this.entrances;
		else if("corridor".equalsIgnoreCase(type))
			sectorList = this.entrances;
		else if("stairs".equalsIgnoreCase(type))
			sectorList = this.entrances;
		else
			sectorList = this.rooms;

		// Get Sectors From List:
		List<DungeonSector> sectors = new ArrayList<>();
		int totalWeights = 0;
		for(String sectorName : sectorList) {
			DungeonSector sector = DungeonManager.getInstance().getSector(sectorName);
			if(sector == null) {
				continue;
			}
			if(sector.weight > 0 && sector.type.equalsIgnoreCase(type)) {
				sectors.add(sector);
				totalWeights += sector.weight;
			}
		}
		if(sectors.isEmpty()) {
			return null;
		}
		if(sectors.size() == 1) {
			return sectors.get(0);
		}

		// Get Weighted Sector:
		int randomWeight = random.nextInt(totalWeights) + 1;
		int searchedWeight = 0;
		for(DungeonSector sector : sectors) {
			if(randomWeight <= sector.weight + searchedWeight) {
				return sector;
			}
			searchedWeight += sector.weight;
		}
		return sectors.get(sectors.size() - 1);
	}


	/**
	 * Returns the next type of sector that should connect to the parent sector type.
	 * @param random The instance of Random to use.
	 * @return The type of sector to use next.
	 */
	public String getNextConnectingSector(String parentType, Random random) {
		if("room".equalsIgnoreCase(parentType)) {
			return random.nextDouble() <= this.roomToRoomChance ? "room" : "corridor";
		}
		if("corridor".equalsIgnoreCase(parentType)) {
			return random.nextDouble() <= this.corridorToCorridorChance ? "corridor" : "room";
		}
		if("entrance".equalsIgnoreCase(parentType)) {
			return random.nextBoolean() ? "corridor" : "room";
		}
		return "room";
	}


	/**
	 * Returns a random Dungeon Theme to generate.
	 * @param random The random instance to use.
	 * @return A random Dungeon Theme to generate.
	 */
	public DungeonTheme getRandomTheme(Random random) {
		List<DungeonTheme> themes = new ArrayList<>();
		for(String themeName : this.themes) {
			DungeonTheme dungeonTheme = DungeonManager.getInstance().getTheme(themeName);
			if(dungeonTheme != null) {
				themes.add(dungeonTheme);
			}
		}

		if(themes.isEmpty()) {
			return null;
		}

		if(themes.size() == 1) {
			return themes.get(0);
		}

		return themes.get(random.nextInt(themes.size()));
	}
}
