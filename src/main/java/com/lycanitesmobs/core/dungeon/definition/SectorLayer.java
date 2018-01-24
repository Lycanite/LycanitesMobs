package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.*;

public class SectorLayer {
	/** Sector Layers make up Sector Segments. **/

	/** A 2D array list of block characters that make up this layer. **/
	public List<List<Character>> rows = new ArrayList<>();

	/** If false, this layer will not tile horizontally. The last block of the pattern will be repeated to take up the remaining space. **/
	public boolean tileHorizontal = true;

	/** If false, this layer will not tile vertically. The last block of the pattern will be repeated to take up the remaining space. **/
	public boolean tileVertical = true;

	/** If true, the pattern of this layer will be centered horizontally. **/
	public boolean centerHorizontal = false;

	/** If true, the pattern of this layer will be centered vertically. **/
	public boolean centerVertical = false;

	/** If false, any spawner blocks will not tile and will be replaced with air. **/
	public boolean tileSpawners = false;

	/** If false, any chest blocks will not tile and will be replaced with air. **/
	public boolean tileChests = false;


    /** Loads this Dungeon Sector Segment Layer from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("tileHorizontal"))
			this.tileHorizontal = json.get("tileHorizontal").getAsBoolean();

		if(json.has("tileVertical"))
			this.tileVertical = json.get("tileVertical").getAsBoolean();

		if(json.has("centerHorizontal"))
			this.centerHorizontal = json.get("centerHorizontal").getAsBoolean();

		if(json.has("centerVertical"))
			this.centerVertical = json.get("centerVertical").getAsBoolean();

		if(json.has("tileSpawners"))
			this.tileSpawners = json.get("tileSpawners").getAsBoolean();

		if(json.has("tileChests"))
			this.tileChests = json.get("tileChests").getAsBoolean();

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


	/**
	 * Returns a pattern row for the provided vertical pattern positon.
	 * @param vertical The current vertical pattern position. (Current build position relative to start position.)
	 * @param verticalLength The required vertical length of the vertical pattern. (Stop build position relative to start position.)
	 * @return A list of characters to get a column from. Returns an empty row if this layer has no rows defined.
	 */
	public List<Character> getRow(int vertical, int verticalLength) {
		if(this.rows.isEmpty()) {
			return new ArrayList<>();
		}
		int offset = 0;

		// Center:
		if(this.centerVertical) {
			offset += Math.round((float) verticalLength / 2); // Add half of the verticalLength.
			offset -= Math.round((float) this.rows.size() / 2); // Subtract half of the pattern.
		}

		List<Character> row = this.rows.get(Math.floorMod(vertical - offset, this.rows.size()));

		// No Tiling:
		if(!this.tileVertical) {
			if(vertical < offset) {
				return this.rows.get(0);
			}
			if(vertical > offset + this.rows.size()) {
				return this.rows.get(this.rows.size() - 1);
			}
		}

		return row;
	}


	/**
	 * Returns a pattern column for the provided horizontal pattern positon and row.
	 * @param vertical The current vertical pattern position. (Current build position relative to start position.)
	 * @param verticalLength The required vertical horizontalLength of the vertical pattern. (Stop build position relative to start position.)
	 * @param horizontal The current horizontal pattern position. (Current build position relative to start position.)
	 * @param horizontalLength The required horizontal length of the horizontal pattern. (Stop build position relative to start position.)
	 * @param row The row of characters to use. This cab be obtained via getRow() or will be obtained if null.
	 * @return A characters to get a block from a theme with. Returns 0 for air if the row is empty.
	 */
	public Character getColumn(int vertical, int verticalLength, int horizontal, int horizontalLength, List<Character> row) {
		if(row == null) {
			row = this.getRow(vertical, verticalLength);
		}
		if(row.isEmpty()) {
			return '0';
		}
		int verticalOffset = 0;
		int horizontalOffset = 0;

		// Center:
		if(this.centerVertical) {
			verticalOffset += Math.round((float) verticalLength / 2); // Add half of the verticalLength.
			verticalOffset -= Math.round((float) this.rows.size() / 2); // Subtract half of the pattern.
		}
		if(this.centerHorizontal) {
			horizontalOffset += Math.round((float) horizontalLength / 2); // Add half of the horizontalLength.
			horizontalOffset -= Math.round((float) row.size() / 2); // Subtract half of the pattern.
		}

		Character column = row.get(Math.floorMod(horizontal - horizontalOffset, row.size()));

		// Tiling:
		if(
			vertical < verticalOffset ||
			vertical > verticalOffset + this.rows.size() ||
			horizontal < horizontalOffset ||
			horizontal > horizontalOffset + row.size()
		) {
			if (!this.tileHorizontal) {
				if (horizontal < horizontalOffset) {
					column = row.get(0);
				}
				if (horizontal > horizontalOffset + row.size()) {
					column = row.get(row.size() - 1);
				}
			}

			// Spawners and Chests Tiling:
			if (!this.tileSpawners && column == 'S') {
				column = '0';
			}
			else if (!this.tileChests && column == 'C') {
				column = '0';
			}
		}

		return column;
	}
}
