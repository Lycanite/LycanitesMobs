package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class RandomSpawnLocation extends BlockSpawnLocation {
	/** How many random positions to select. **/
	public int limit = 32;

	/** If true positions require a solid walkable block underneath rather than using insideBlock. **/
	public boolean solidGround = false;


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("limit"))
			this.limit = json.get("limit").getAsInt();

		if(json.has("solidGround"))
			this.solidGround = json.get("solidGround").getAsBoolean();

		this.listType = "whitelist";
		this.blocks.add(Blocks.AIR);
		this.blocks.add(Blocks.TALLGRASS);
		super.loadFromJSON(json);
    }

    @Override
    public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
        List<BlockPos> spawnPositions = new ArrayList<>();
		LycanitesMobs.printDebug("JSONSpawner", "Getting " + this.limit + " Random Spawn Positions");

		for(int i = 0; i < this.limit; i++) {
			BlockPos randomPos = this.getRandomPosition(world, player, triggerPos);
			if(randomPos != null) {
				spawnPositions.add(randomPos);
			}
		}

        return this.sortSpawnPositions(spawnPositions, triggerPos);
    }

	/** Gets a random spawn position.
	 * @param world The world to search for coordinates in.
	 * @param player Player that triggered the spawn.
	 * @param triggerPos The trigger position to search around.
	 * @return Returns a BlockPos or null if no coord was found.
	 */
	public BlockPos getRandomPosition(World world, EntityPlayer player, BlockPos triggerPos) {
		int[] xz = this.getRandomXZCoord(world, triggerPos);
		int x = xz[0];
		int z = xz[1];
		int y = this.getRandomYCoord(world, new BlockPos(x, triggerPos.getY(), z));
		return y > -1 ? new BlockPos(x, y, z) : null;
	}

	/**
	 * Gets a random XZ position from the trigger position.
	 * @param world The world that the coordinates are being selected in, mainly for getting Random.
	 * @param triggerPos The trigger position to randomize around.
	 * @return An integer array containing two ints the X and Z position.
	 */
	public int[] getRandomXZCoord(World world, BlockPos triggerPos) {
		int xPos = 0;
		if(this.rangeMax.getX() > 0) {
			xPos = world.rand.nextInt(this.rangeMax.getX());
			if(world.rand.nextBoolean()) {
				xPos += this.rangeMin.getX();
			}
			else {
				xPos = -xPos - this.rangeMin.getX();
			}
		}

		int zPos = 0;
		if(this.rangeMax.getZ() > 0) {
			zPos = world.rand.nextInt(this.rangeMax.getZ());
			if(world.rand.nextBoolean()) {
				zPos += this.rangeMin.getZ();
			}
			else {
				zPos = -zPos - this.rangeMin.getZ();
			}
		}

		return new int[] {triggerPos.getX() + xPos, triggerPos.getZ() + zPos};
	}

	/**
	 * Gets a random Y position from the provided XYZ position using the provided range and range max radii.
	 * @param world The world that the coordinates are being selected in, mainly for getting Random.
	 * @param triggerPos The position to search from using XZ coords and up and down within range of the Y coord.
	 * @return The y position, -1 if a valid position could not be found.
	 */
	public int getRandomYCoord(World world, BlockPos triggerPos) {
		int originX = triggerPos.getX();
		int originY = triggerPos.getY();
		int originZ = triggerPos.getZ();
		int minY = Math.max(originY - this.rangeMax.getY(), 0); // Start from this y pos
		int maxY = Math.min(originY + this.rangeMax.getY(), world.getHeight() - 1); // Search up to this y pos
		List<Integer> yCoordsLow = new ArrayList<>();
		List<Integer> yCoordsHigh = new ArrayList<>();

		// Get Every Valid Y Pos:
		for(int nextY = minY; nextY <= maxY; nextY++) {
			// If the next y pos to check is within the min range area, move it up out of it:
			if(nextY > originY - this.rangeMin.getY() && nextY < originY + this.rangeMin.getY())
				nextY = originY + this.rangeMin.getY();

			BlockPos spawnPos = new BlockPos(originX, nextY, originZ);

			// If the pos is valid to spawn at:
			if(this.isValidBlock(world, spawnPos)) {
				boolean lastYPos = false;

				// If can see sky:
				if(world.canBlockSeeSky(spawnPos)) {
					// Get random floating position if not searching for solid ground:
					if(!this.solidGround) {
						int floatRange = maxY - nextY;
						if(floatRange > 1) {
							if(world.getBlockState(spawnPos).getBlock() != Blocks.AIR) {
								floatRange = this.getValidBlockHeight(world, spawnPos, maxY);
							}
							nextY += world.rand.nextInt(floatRange + 1) - 1;
						}
					}
					lastYPos = true;
				}

				if(super.isValidBlock(world, spawnPos.up())) {
					if(nextY <= 64) {
						yCoordsLow.add(nextY);
					}
					else {
						yCoordsHigh.add(nextY);
					}
				}

				if(lastYPos) {
					break;
				}
			}
		}

		// Pick Random Y Pos:
		int y = -1;
		if(yCoordsHigh.size() > 0 && (yCoordsLow.size() <= 0 || world.rand.nextFloat() > 0.25F)) {
			if(yCoordsHigh.size() == 1)
				y = yCoordsHigh.get(0);
			else
				y = yCoordsHigh.get(world.rand.nextInt(yCoordsHigh.size() - 1));
		}
		else if(yCoordsLow.size() > 0) {
			if(yCoordsLow.size() == 1)
				y = yCoordsLow.get(0);
			else
				y = yCoordsLow.get(world.rand.nextInt(yCoordsLow.size() - 1));
		}

		return y;
	}

	/** Returns if the provided block position is valid. **/
	@Override
	public boolean isValidBlock(World world, BlockPos blockPos) {
		if(!super.isValidBlock(world, blockPos)) {
			return false;
		}
		if(this.solidGround) {
			return this.posHasGround(world, blockPos);
		}
		return true;
	}

	/** Returns true if the specified position has a block underneath it that a mob can safely stand on. **/
	public boolean posHasGround(World world, BlockPos pos) {
		if(pos == null || pos.getY() == 0)
			return false;
		IBlockState possibleGroundBlock = world.getBlockState(pos.down());
		try {
			if(possibleGroundBlock.isNormalCube())
				return true;
		} catch(Exception e) {}
		try {
			if (possibleGroundBlock.isSideSolid(world, pos.down(), EnumFacing.UP))
				return true;
			if (possibleGroundBlock.isSideSolid(world, pos.down(), EnumFacing.DOWN))
				return true;
		} catch(Exception e) {}
		return false;
	}

	/** Returns the height of valid blocks from the starting position checking upwards until the position no longer has a valid block or maxY is reached. **/
	public int getValidBlockHeight(World world, BlockPos startPos, int maxY) {
		int y;
		for(y = startPos.getY(); y <= maxY; y++) {
			BlockPos checkPos = new BlockPos(startPos.getX(), y, startPos.getZ());
			if(!this.isValidBlock(world, checkPos)) {
				break;
			}
		}
		return y - startPos.getY();
	}
}
