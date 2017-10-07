package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class OreBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public OreBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	@Override
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos) {
		Block block = blockState.getBlock();
		String blockName = block.getUnlocalizedName();
		String[] blockNameParts = blockName.split("\\.");
		boolean isOre = false;
		for(String blockNamePart : blockNameParts) {
			int blockNamePartLength = blockNamePart.length();
			if(blockNamePartLength >= 3) {
				if(blockNamePart.substring(0, 3).equalsIgnoreCase("ore") || blockNamePart.substring(blockNamePartLength - 3, blockNamePartLength).equalsIgnoreCase("ore")) {
					isOre = true;
					break;
				}
			}
		}
		return isOre || block == Blocks.MONSTER_EGG;
	}

	@Override
	public int getBlockLevel(IBlockState blockState, World world, BlockPos blockPos) {
		Block block = blockState.getBlock();
		if(block == Blocks.DIAMOND_ORE)
			return 3;
		if(block == Blocks.EMERALD_ORE)
			return 3;
		if(block == Blocks.LAPIS_ORE)
			return 2;
		if(block == Blocks.GOLD_ORE)
			return 2;
		if(block == Blocks.IRON_ORE)
			return 1;
		return 0;
	}
}
