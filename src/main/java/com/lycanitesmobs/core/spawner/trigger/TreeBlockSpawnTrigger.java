package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockVine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class TreeBlockSpawnTrigger extends BlockSpawnTrigger {

	/** Constructor **/
	public TreeBlockSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	@Override
	public boolean isTriggerBlock(IBlockState blockState, World world, BlockPos blockPos) {
		return this.isTreeLogBlock(blockState.getBlock(), world, blockPos) || this.isTreeLeavesBlock(blockState.getBlock(), world, blockPos);
	}

	public boolean isTreeLogBlock(Block block, World world, BlockPos pos) {
		if(block instanceof BlockLog || ObjectLists.isInOreDictionary("logWood", block)) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY <= Math.min(world.getHeight(), y + 32); searchY++) {
						Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
						if(searchBlock != block && searchBlock != null) {
							if(ObjectLists.isInOreDictionary("treeLeaves", searchBlock))
								return true;
							if(!world.isAirBlock(new BlockPos(x, searchY, z)))
								break;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean isTreeLeavesBlock(Block block, World world, BlockPos pos) {
		if(block instanceof BlockLeaves || ObjectLists.isInOreDictionary("treeLeaves", block)) {
			int x = pos.getX();
			int y = pos.getY();
			int z = pos.getZ();
			for(int searchX = x - 1; searchX <= x + 1; searchX++) {
				for(int searchZ = z - 1; searchZ <= z + 1; searchZ++) {
					for(int searchY = y; searchY >= Math.max(0, y - 32); searchY--) {
						Block searchBlock = world.getBlockState(new BlockPos(searchX, searchY, searchZ)).getBlock();
						if(searchBlock != block && searchBlock != null) {
							if(ObjectLists.isInOreDictionary("logWood", searchBlock)) {
								return true;
							}
							if(!world.isAirBlock(new BlockPos(x, searchY, z)))
								break;
						}
					}
				}
			}
		}
		return false;
	}
}
