package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonObject;

public class DungeonStructure {
    /** Dungeon sectors can be corridors, rooms, entrances, etc that make up a dungeon. **/

	/** The unique name of this sector. Required. **/
	public String name = "";

    /** The type of structure that this is. Can be: decoration (default), feature, altar or arena. **/
    public String type = "decoration";

	/** The weight to use for this structure when selecting randomly. **/
	public int weight = 8;

	/** The resource location path to the nbt data for this structure. **/
	public String nbt = "";


    /** Loads this Dungeon Sector from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("type"))
			this.type = json.get("type").getAsString().toLowerCase();

		if(json.has("nbt"))
			this.nbt = json.get("nbt").getAsString().toLowerCase();
	}
}
