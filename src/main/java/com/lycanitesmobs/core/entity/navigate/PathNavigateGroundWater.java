package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGroundWater extends PathNavigateGroundCustom {

    public PathNavigateGroundWater(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    /** Uses custom walk node processor. **/
    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessorWater();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }
}
