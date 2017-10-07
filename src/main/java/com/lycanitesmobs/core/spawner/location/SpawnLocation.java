package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.CoordSorterFurthest;
import com.lycanitesmobs.core.spawner.CoordSorterNearest;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SpawnLocation {
    /** Spawn Locations define where spawns will take place, how these work can vary based on the type of Spawn Trigger. **/

    /** The minimum xyz distances in blocks from the central spawn position to spawn from. **/
    public Vec3i rangeMin = new Vec3i(0, 0, 0);

    /** The maximum xyz distances in blocks from the central spawn position to spawn from. **/
    public Vec3i rangeMax = new Vec3i(0, 0, 0);

    /** Determines the order that the returned positions should be in. Can be random, near or far (from the trigger position). **/
    public String sorting = "random";


    /** Loads this Spawn Location from the provided JSON data. **/
    public void fromJSON(JsonObject json) {
		// TODO Read SpawnLocation JSON.
    }


    /** Returns a list of positions to spawn at. **/
    public List<BlockPos> getSpawnPositions(World world, EntityPlayer player, BlockPos triggerPos) {
        List<BlockPos> spawnPositions = new ArrayList<>();
        Vec3i offset = new Vec3i(
                this.getOffset(world.rand, this.rangeMin.getX(), this.rangeMax.getX()),
                this.getOffset(world.rand, this.rangeMin.getY(), this.rangeMax.getY()),
                this.getOffset(world.rand, this.rangeMin.getZ(), this.rangeMax.getZ())
        );
        spawnPositions.add(triggerPos.add(offset));

        return this.sortSpawnPositions(spawnPositions, triggerPos);
    }


    /** Returns a random offset from the provided min and max values. **/
    public int getOffset(Random random, int min, int max) {
        if(rangeMax.getX() <= rangeMin.getX()) {
            return 0;
        }
        int offset = min + random.nextInt(max - min);
        if(random.nextBoolean()) {
            offset = -offset;
        }
        return offset;
    }


	/** Sorts a list of spawning positions. **/
	public List<BlockPos> sortSpawnPositions(List<BlockPos> spawnPositions, BlockPos triggerPos) {
		if("random".equalsIgnoreCase(this.sorting)) {
			Collections.shuffle(spawnPositions);
		}
		else if("near".equalsIgnoreCase(this.sorting)) {
			Collections.sort(spawnPositions, new CoordSorterNearest(triggerPos));
		}
		else if("far".equalsIgnoreCase(this.sorting)) {
			Collections.sort(spawnPositions, new CoordSorterFurthest(triggerPos));
		}
		return spawnPositions;
	}


	/**
	 * Returns true if the provided coordinate has space to spawn an entity at it.
	 * This works by checking if it is an air block and if the block above is also an air block.
	 * @return True if there is space else false.
	 */
	public boolean doesPositionHaveSpace(World world, BlockPos pos, Block insideBlock) {
		Block feet = world.getBlockState(pos).getBlock();
		if(feet == null) return false;
		if(feet != insideBlock) return false;

		Block head = world.getBlockState(pos.add(0, 1, 0)).getBlock();
		if(head == null) return false;
		if(head != insideBlock) return false;

		return true;
	}
}
