package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.SpawnerJSONUtilities;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonSector {
    /** Dungeon sectors can be corridors, rooms, entrances, etc that make up a dungeon. **/

	/** The unique name of this sector. Required. **/
	public String name = "";

    /** The type of sector that this is. Can be: room (default), corridor, stairs or entrance. **/
    public String type = "room";

	/** The weight to use for this sector when selecting randomly. **/
	public int weight = 8;

	/** If true, this sector will use a new theme selected from the dungeon. **/
	public boolean changeTheme = false;

	/** Defines the minimum size of this sector. **/
	public Vec3i sizeMin = new Vec3i(8, 8, 8);

	/** Defines the maximum size of this sector. **/
	public Vec3i sizeMax = new Vec3i(10, 10, 10);

	/** Sets a padding around this sector to count towards collision. This is automatically increased by negative segment layers as needed. **/
	public Vec3i padding = new Vec3i(0, 0, 0);

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

		this.padding = SpawnerJSONUtilities.getVec3i(json, "padding");

		if(json.has("weight"))
			this.weight = json.get("weight").getAsInt();

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


	/**
	 * Returns a random room size to use based on the size min max values.
	 * @param random The instance of Random to use.
	 * @return A random size to use.
	 */
	public Vec3i getRandomSize(Random random) {
		return new Vec3i(
				this.sizeMin.getX() + random.nextInt(this.sizeMax.getX() - this.sizeMin.getX() + 1),
				this.sizeMin.getY() + random.nextInt(this.sizeMax.getY() - this.sizeMin.getY() + 1),
				this.sizeMin.getZ() + random.nextInt(this.sizeMax.getZ() - this.sizeMin.getZ() + 1)
		);
	}
}
