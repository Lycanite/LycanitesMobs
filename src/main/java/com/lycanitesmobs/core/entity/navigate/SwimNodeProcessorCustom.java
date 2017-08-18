package com.lycanitesmobs.core.entity.navigate;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.SwimNodeProcessor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class SwimNodeProcessorCustom extends SwimNodeProcessor {

    @Override
    public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
        super.initProcessor(sourceIn, mob);
        this.entitySizeX = MathHelper.floor(Math.min(mob.width, 3));
        this.entitySizeZ = MathHelper.floor(Math.min(mob.width, 3));
        this.entitySizeY = MathHelper.floor(Math.min(mob.height, 3));
    }

    @Override
    public PathPoint getStart() {
        PathPoint pp = this.openPoint(MathHelper.floor(this.entity.posX), MathHelper.floor(this.entity.posY), MathHelper.floor(this.entity.posZ));
        return pp;
    }

    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        PathPoint pp = this.openPoint(MathHelper.floor(x - (double)(this.entity.width / 2.0F)), MathHelper.floor(y), MathHelper.floor(z - (double)(this.entity.width / 2.0F)));
        return pp;
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;

        for (EnumFacing enumfacing : EnumFacing.values())
        {
            PathPoint pathpoint = this.getWaterNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance)
            {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }

    @Nullable
    protected PathPoint getWaterNode(int p_186328_1_, int p_186328_2_, int p_186328_3_) {
        PathNodeType pathnodetype = this.isFree(p_186328_1_, p_186328_2_, p_186328_3_);
        return pathnodetype == PathNodeType.WATER ? this.openPoint(p_186328_1_, p_186328_2_, p_186328_3_) : null;
    }

    protected PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
            for (int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
                for (int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    if (iblockstate.getMaterial() != Material.WATER) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.WATER;
    }
}
