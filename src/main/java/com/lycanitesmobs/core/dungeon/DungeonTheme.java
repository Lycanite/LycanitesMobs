package com.lycanitesmobs.core.dungeon;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;

public class DungeonTheme {
    /** Dungeon Themes set the blocks that Dungeon Sectors and Structures should use. **/

	/** The unique name of this theme. Required. **/
	public String name = "";

    /** The pattern to use on random B blocks for floors. Can be: random (default), horizontal or vertical. **/
    public String floorPattern = "random";

	/** The pattern to use on random B blocks for walls. Can be: random (default), horizontal or vertical. **/
	public String wallPattern = "random";

	/** The pattern to use on random B blocks for ceilings. Can be: random (default), horizontal or vertical. **/
	public String ceilingPattern = "random";

	/** A list of blocks to use for floors. **/
	public List<ThemeBlock> floorBlocks;

	/** A list of blocks to use for walls. **/
	public List<ThemeBlock> wallBlocks;

	/** A list of blocks to use for ceilings. **/
	public List<ThemeBlock> ceilingBlocks;


    /** Loads this Dungeon Theme from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString().toLowerCase();

		if(json.has("floorPattern"))
			this.floorPattern = json.get("floorPattern").getAsString().toLowerCase();

		if(json.has("wallPattern"))
			this.wallPattern = json.get("wallPattern").getAsString().toLowerCase();

		if(json.has("ceilingPattern"))
			this.ceilingPattern = json.get("ceilingPattern").getAsString().toLowerCase();

		if(json.has("floorBlocks")) {
			for(JsonElement jsonElement : json.get("floorBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.floorBlocks.add(themeBlock);
			}
		}

		if(json.has("wallBlocks")) {
			for(JsonElement jsonElement : json.get("wallBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.wallBlocks.add(themeBlock);
			}
		}

		if(json.has("ceilingBlocks")) {
			for(JsonElement jsonElement : json.get("ceilingBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.ceilingBlocks.add(themeBlock);
			}
		}
	}
}
