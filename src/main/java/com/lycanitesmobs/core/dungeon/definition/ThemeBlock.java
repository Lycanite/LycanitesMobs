package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ThemeBlock {
	/** Dungeon Theme Blocks define a block to be used in the theme along with other information. **/

	/** The block to use. **/
	public Block block = Blocks.STONE;

	/** The metadata of the block. **/
	public int metadata = 0;

	/** The weight for randomly using this block. **/
	public int weight = 8;


	/** Loads this Dungeon Theme from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("blockId"))
			this.block = GameRegistry.findRegistry(Block.class).getValue(new ResourceLocation(json.get("blockId").getAsString().toLowerCase()));

		if(json.has("metadata"))
			this.metadata = json.get("metadata").getAsInt();

		if(json.has("weight"))
			this.weight = json.get("weight").getAsInt();
	}


	/**
	 * Returns a block state for this theme block entry.
	 * @return A new block state.
	 */
	public IBlockState getBlockState() {
		if(this.metadata <= 0) {
			return this.block.getDefaultState();
		}
		return this.block.getStateFromMeta(this.metadata);
	}
}
