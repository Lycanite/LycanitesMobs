package com.lycanitesmobs.core.dungeon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.MobSpawn;

import java.util.Iterator;
import java.util.List;

public class DungeonSchematic {
    /** Dungeon Schematics define actual dungeons to spawn using a collection of Sectors, Themes, etc. **/

	/** The unique name of this dungeon. Required. **/
	public String name = "";

	/** The minimum amount of sectors this dungeon should have in total. **/
	public int sectorCountMin = 30;

	/** The maximum amount of sectors this dungeon should have in total. **/
	public int sectorCountMax = 50;

	/** The chance of a corridor connecting to another corridor. **/
	public double corridorToCorridorChance = 0.1D;

	/** The chance of a room connecting to another room. **/
	public double roomToRoomChance = 0.1D;

	/** A list of themes to use. Required. **/
	public List<String> themes;

	/** A list of entrance sectors to use. Required. **/
	public List<String> entrances;

	/** A list of room sectors to use. Required. **/
	public List<String> rooms;

	/** A list of corridor sectors to use. Required. **/
	public List<String> corridors;

	/** A list of stairs sectors to use. Required. **/
	public List<String> stairs;

	/** A list of MobSpawns to use. Required. **/
	public List<MobSpawn> mobSpawns;


    /** Loads this Dungeon Theme from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("sectorCountMin"))
			this.sectorCountMin = json.get("sectorCountMin").getAsInt();

		if(json.has("sectorCountMax"))
			this.sectorCountMax = json.get("sectorCountMax").getAsInt();

		if(json.has("corridorToCorridorChance"))
			this.corridorToCorridorChance = json.get("corridorToCorridorChance").getAsDouble();

		if(json.has("roomToRoomChance"))
			this.roomToRoomChance = json.get("roomToRoomChance").getAsDouble();

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
}
