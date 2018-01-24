package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.dungeon.instance.SectorInstance;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

	/** A list of blocks to use for floors. Required. **/
	public List<ThemeBlock> floorBlocks = new ArrayList<>();

	/** A list of blocks to use for walls. Required. **/
	public List<ThemeBlock> wallBlocks = new ArrayList<>();

	/** A list of blocks to use for ceilings. Required. **/
	public List<ThemeBlock> ceilingBlocks = new ArrayList<>();

	/** A list of full blocks to use for lights. Optional, defaults to random wall block. **/
	public List<ThemeBlock> lightBlocks = new ArrayList<>();

	/** A list of torch blocks to use for lights. Optional, defaults to air. **/
	public List<ThemeBlock> torchBlocks = new ArrayList<>();

	/** A list of blocks to use for stairs. Required. **/
	public List<ThemeBlock> stairBlocks = new ArrayList<>();

	/** A list of dangerous blocks to to fill pits with. Required. **/
	public List<ThemeBlock> pitBlocks = new ArrayList<>();


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

		if(json.has("lightBlocks")) {
			for(JsonElement jsonElement : json.get("lightBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.lightBlocks.add(themeBlock);
			}
		}
		else {
			this.lightBlocks.addAll(this.wallBlocks);
		}

		if(json.has("torchBlocks")) {
			for(JsonElement jsonElement : json.get("torchBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.torchBlocks.add(themeBlock);
			}
		}
		else {
			ThemeBlock themeBlock = new ThemeBlock();
			themeBlock.block = Blocks.AIR;
			this.torchBlocks.add(themeBlock);
		}

		if(json.has("stairBlocks")) {
			for(JsonElement jsonElement : json.get("stairBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.stairBlocks.add(themeBlock);
			}
		}

		if(json.has("pitBlocks")) {
			for(JsonElement jsonElement : json.get("pitBlocks").getAsJsonArray()) {
				ThemeBlock themeBlock = new ThemeBlock();
				themeBlock.loadFromJSON(jsonElement.getAsJsonObject());
				this.pitBlocks.add(themeBlock);
			}
		}
	}


	/**
	 * Returns a floor block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getFloor(SectorInstance sectorInstance, char patternChar, Random random) {
		return this.getBlockState(sectorInstance, patternChar, random, this.floorBlocks);
	}


	/**
	 * Returns a wall block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getWall(SectorInstance sectorInstance, char patternChar, Random random) {
		return this.getBlockState(sectorInstance, patternChar, random, this.wallBlocks);
	}


	/**
	 * Returns a ceiling block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getCeiling(SectorInstance sectorInstance, char patternChar, Random random) {
		return this.getBlockState(sectorInstance, patternChar, random, this.ceilingBlocks);
	}


	/**
	 * Returns a light block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getLight(char patternChar, Random random) {
		return this.getBlockState(null, patternChar, random, this.lightBlocks);
	}


	/**
	 * Returns a light torch state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getTorch(char patternChar, Random random) {
		return this.getBlockState(null, patternChar, random, this.torchBlocks);
	}


	/**
	 * Returns a stairs block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getStairs(char patternChar, Random random) {
		return this.getBlockState(null, patternChar, random, this.stairBlocks);
	}


	/**
	 * Returns a pit block state for the provided pattern character. Use B for random.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @return A block state for placing.
	 */
	public IBlockState getPit(char patternChar, Random random) {
		return this.getBlockState(null, patternChar, random, this.pitBlocks);
	}


	/**
	 * Returns a block state for the provided pattern character from the provided block list.
	 * @param sectorInstance The sector instance to get the block state for.
	 * @param patternChar The block character to convert to a block state.
	 * @param random The instance of random, used for characters that are random.
	 * @param blockList The list of Theme Blocks to select from.
	 * @return A block state for placing.
	 */
	public IBlockState getBlockState(SectorInstance sectorInstance, char patternChar, Random random, List<ThemeBlock> blockList) {
		// Nothing:
		if(patternChar == '0') {
			return Blocks.AIR.getDefaultState();
		}

		// Sector Instantiated:
		if(sectorInstance != null) {
			// Light:
			if (patternChar == 'L') {
				return sectorInstance.lightBlock;
			}

			// Torch:
			else if (patternChar == 'T') {
				return sectorInstance.torchBlock;
			}

			// Stairs:
			else if (patternChar == 'R') {
				return sectorInstance.stairBlock;
			}

			// Pit:
			else if (patternChar == 'P') {
				return sectorInstance.pitBlock;
			}
		}

		// Chest:
		if(patternChar == 'C') {
			return Blocks.CHEST.getDefaultState();
		}

		// Spawner:
		else if(patternChar == 'S') {
			return Blocks.MOB_SPAWNER.getStateFromMeta(1);
		}

		// List Check:
		if(blockList.isEmpty()) {
			return Blocks.AIR.getDefaultState();
		}
		if(blockList.size() == 1) {
			return blockList.get(0).getBlockState();
		}

		// Specific:
		if(NumberUtils.isCreatable("" + patternChar)) {
			int blockIndex = NumberUtils.toInt("" + patternChar, 1) - 1;
			if(blockIndex >= blockList.size())
				blockIndex = 0;
			return blockList.get(blockIndex % blockList.size()).getBlockState();
		}

		// TODO Implement horizontal and vertical patterns.

		// Get Total Weights:
		int totalWeights = 0;
		for(ThemeBlock themeBlock : blockList) {
			if(themeBlock == null || themeBlock.weight < 1) {
				continue;
			}
			totalWeights += themeBlock.weight;
		}

		// Get Weighted Block:
		int randomWeight = random.nextInt(totalWeights) + 1;
		int searchedWeight = 0;
		for(ThemeBlock themeBlock : blockList) {
			if(themeBlock == null || themeBlock.weight < 1) {
				continue;
			}
			if(randomWeight <= themeBlock.weight + searchedWeight) {
				return themeBlock.getBlockState();
			}
			searchedWeight += themeBlock.weight;
		}
		return blockList.get(blockList.size() - 1).getBlockState();
	}
}
