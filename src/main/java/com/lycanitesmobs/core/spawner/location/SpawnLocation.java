package com.lycanitesmobs.core.spawner.location;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SpawnLocation {
    /** Spawn Locations define where spawns will take place, how these work can vary based on the type of Spawn Trigger. **/

    /** The minimum xyz distances in blocks from the central spawn position to spawn from. **/
    public Vec3i rangeMin = new Vec3i(0, 0, 0);

    /** The maximum xyz distances in blocks from the central spawn position to spawn from. **/
    public Vec3i rangeMax = new Vec3i(0, 0, 0);


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

        return spawnPositions;
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
}
