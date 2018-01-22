package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class SectorLayer {
	/** Sector Layers make up Sector Segments. **/

	/** A 2D array list of block characters that make up this layer. **/
	public List<List<Character>> rows = new ArrayList<>();

	/** If false, this layer will not tile horizontally and will be centered along a wall, etc, useful for placing chests in the center of walls, etc. **/
	public boolean tileHorizontal = true;

	/** If false, this layer will not tile vertically, useful for placing chests, etc on the ground. **/
	public boolean tileVertical = true;


    /** Loads this Dungeon Sector Segment Layer from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("tileHorizontal"))
			this.tileHorizontal = json.get("tileHorizontal").getAsBoolean();
		if(json.has("tileVertical"))
			this.tileVertical = json.get("tileVertical").getAsBoolean();

		// Rows:
		this.rows.clear();
		for(JsonElement rowElement : json.get("pattern").getAsJsonArray()) {

			// Columns:
			List<Character> columns = new ArrayList<>();
			for(char patternChar : rowElement.getAsString().toCharArray()) {
				columns.add(patternChar);
			}
			this.rows.add(columns);
		}
		Collections.reverse(this.rows);
	}
}
