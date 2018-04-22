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
import scala.Int;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BlockSpawnLocation extends SpawnLocation {
    /** A list of blocks to either spawn in or not spawn in depending on if it is a blacklist or whitelist. **/
    public List<Block> blocks = new ArrayList<>();

    /** Determines if the block list is a blacklist or whitelist. **/
    public String listType = "whitelist";

	/** If true, positions that can see the sky are allowed. **/
	public boolean surface = true;

	/** If true positions that can't see the sky are allowed. **/
	public boolean underground = true;

	/** The minimum amount of blocks that must be found in an area for the location to return any positions. Default -1 (ignore). **/
	public int blockCost = -1;

	/** If set (above 0), tthe amount of each block in the blocks list must be found. Default: 0 (disabled) **/
	public int requiredBlockTypes = 0;


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

		if(json.has("blockCost"))
			this.blockCost = json.get("blockCost").getAsInt();

		if(json.has("requiredBlockTypes"))
			this.requiredBlockTypes = json.get("requiredBlockTypes").getAsInt();

		super.loadFromJSON(json);
	}

    /** Returns a list of positions to spawn at. **/
    @Override
    public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
        List<BlockPos> spawnPositions = new ArrayList<>();
		Map<Block, Integer> validBlocksFound = new HashMap<>();

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

						// Require All:
						if(this.requiredBlockTypes > 0) {
							Block block = world.getBlockState(spawnPos).getBlock();
							if(!validBlocksFound.containsKey(block)) {
								validBlocksFound.put(block, 1);
							}
							else {
								validBlocksFound.put(block, validBlocksFound.get(block) + 1);
							}
						}
					}
				}
			}
        }

        // Block Cost:
		if(this.blockCost > 0) {
			if (spawnPositions.size() < this.blockCost) {
				return new ArrayList<>();
			}
		}

		// Require All Block Types:
		if(this.requiredBlockTypes > 0) {
        	if(validBlocksFound.size() < this.blocks.size()) {
        		return new ArrayList<>();
			}
			for(int blocksFoundOfType : validBlocksFound.values()) {
        		if(blocksFoundOfType < this.requiredBlockTypes) {
					return new ArrayList<>();
				}
			}
		}

        return this.sortSpawnPositions(spawnPositions, world, triggerPos);
    }

	/** Returns if the provided block position is valid. **/
    public boolean isValidBlock(World world, BlockPos blockPos) {
    	Block block = world.getBlockState(blockPos).getBlock();
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
