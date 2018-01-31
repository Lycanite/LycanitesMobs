package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.IFluidBlock;

import java.util.ArrayList;
import java.util.List;

public class BlockSpawnLocation extends SpawnLocation {
    /** A list of blocks to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Block> blocks = new ArrayList<>();

    /** Determines if the block list is a blacklist or whitelist. **/
    public String listType = "whitelist";

	/** If true, positions that can see the sky are allowed. **/
	public boolean surface = true;

	/** If true positions that can't see the sky are allowed. **/
	public boolean underground = true;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("blocks")) {
			this.blocks = JSONHelper.getJsonBlocks(json);
		}

		if(json.has("listType"))
			this.listType = json.get("listType").getAsString();

		if(json.has("surface"))
			this.surface = json.get("surface").getAsBoolean();

		if(json.has("underground"))
			this.underground = json.get("underground").getAsBoolean();

		super.loadFromJSON(json);
	}

    /** Returns a list of positions to spawn at. **/
    @Override
    public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
        List<BlockPos> spawnPositions = new ArrayList<>();

        for (int y = triggerPos.getY() - this.rangeMax.getY(); y <= triggerPos.getY() + this.rangeMax.getY(); y++) {
            // Y Limits:
			int yMin = 0;
			if(this.yMin >= 0) {
				yMin = this.yMin;
			}
            if (y < yMin) {
            	y = yMin;
			}
			int yMax = world.getActualHeight();
			if(this.yMax >= 0) {
				yMax = Math.min(this.yMax, yMax);
			}
            if (y >= yMax) {
                break;
            }

			for(int x = triggerPos.getX() - this.rangeMax.getX(); x <= triggerPos.getX() + this.rangeMax.getX(); x++) {
				for(int z = triggerPos.getZ() - this.rangeMax.getZ(); z <= triggerPos.getZ() + this.rangeMax.getZ(); z++) {
					BlockPos spawnPos = new BlockPos(x, y, z);
					IBlockState blockState = world.getBlockState(spawnPos);
					if(blockState == null) {
						continue;
					}

					// Ignore Flowing Liquids:
					if(blockState.getBlock() instanceof IFluidBlock) {
						float filled = ((IFluidBlock)blockState.getBlock()).getFilledPercentage(world, spawnPos);
						if (filled != 1 && filled != -1) {
							continue;
						}
					}
					if (blockState.getBlock() instanceof BlockLiquid) {
						if (blockState.getBlock().getMetaFromState(blockState) != 0) {
							continue;
						}
					}

					// Check Block:
					if(this.isValidBlock(world, spawnPos)) {
						spawnPositions.add(spawnPos);
					}
				}
			}
        }

        return this.sortSpawnPositions(spawnPositions, triggerPos);
    }

	/** Returns if the provided block position is valid. **/
    public boolean isValidBlock(World world, BlockPos blockPos) {
    	Block block = world.getBlockState(blockPos).getBlock();
    	if(block == null) {
    		return false;
		}

		if(!this.surface || !this.underground) {
			if(world.canSeeSky(blockPos)) {
				if(!this.surface) {
					return false;
				}
			}
			else {
				if(!this.underground) {
					return false;
				}
			}
		}

		if("blacklist".equalsIgnoreCase(this.listType)) {
			return !this.blocks.contains(block);
		}
		else {
			return this.blocks.contains(block);
		}
    }
}
