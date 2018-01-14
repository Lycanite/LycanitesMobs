package com.lycanitesmobs.core.dungeon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.SpawnerJSONUtilities;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class DungeonSector {
    /** Dungeon sectors can be corridors, rooms, entrances, etc that make up a dungeon. **/

	/** The unique name of this sector. Required. **/
	public String name = "";

    /** The type of sector that this is. Can be: room (default), corridor, stairs or entrance. **/
    public String type = "room";

	/** If true, this sector will use a new theme selected from the dungeon. **/
	public boolean changeTheme = false;

	/** Defines the minimum size of this sector. **/
	public Vec3i sizeMin = new Vec3i(8, 8, 8);

	/** Defines the maximum size of this sector. **/
	public Vec3i sizeMax = new Vec3i(10, 10, 10);

    /** A list of Structures used by this sector. **/
    public List<String> structures = new ArrayList<>();

	/** The floor segment of this sector. **/
	public SectorSegment floor;

	/** The wall segment of this sector. **/
	public SectorSegment wall;

	/** The ceiling segment of this sector. **/
	public SectorSegment ceiling;


    /** Loads this Dungeon Sector from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("type"))
			this.type = json.get("type").getAsString().toLowerCase();

		if(json.has("changeTheme"))
			this.changeTheme = json.get("changeTheme").getAsBoolean();

		this.sizeMin = SpawnerJSONUtilities.getVec3i(json, "sizeMin");

		this.sizeMax = SpawnerJSONUtilities.getVec3i(json, "sizeMax");

		if(json.has("structures")) {
			for(JsonElement jsonElement : json.get("structures").getAsJsonArray()) {
				String structureName = jsonElement.getAsString();
				if(!this.structures.contains(structureName))
					this.structures.add(structureName);
			}
		}

		if(json.has("floor")) {
			this.floor = new SectorSegment();
			this.floor.loadFromJSON(json.get("floor").getAsJsonObject());
		}

		if(json.has("wall")) {
			this.wall = new SectorSegment();
			this.wall.loadFromJSON(json.get("wall").getAsJsonObject());
		}

		if(json.has("ceiling")) {
			this.ceiling = new SectorSegment();
			this.ceiling.loadFromJSON(json.get("ceiling").getAsJsonObject());
		}
	}
}
