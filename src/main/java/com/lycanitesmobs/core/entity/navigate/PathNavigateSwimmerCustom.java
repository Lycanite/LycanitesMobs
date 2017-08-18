package com.lycanitesmobs.core.entity.navigate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PathNavigateSwimmerCustom extends net.minecraft.pathfinding.PathNavigateSwimmer {
    protected PathFinder currentPathFinder;
    protected BlockPos currentTargetPos;

    public PathNavigateSwimmerCustom(EntityLiving entity, World world) {
        super(entity, world);
    }

    @Override
    protected PathFinder getPathFinder() {
        this.currentPathFinder = new PathFinder(new SwimNodeProcessorCustom());
        return this.currentPathFinder;
    }

    @Override
    public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
        return super.tryMoveToXYZ(x, y, z, speedIn);
    }

    @Override
    protected boolean canNavigate() {
        return this.theEntity.isInWater();
    }

    @Override
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.theEntity.posX, this.theEntity.getEntityBoundingBox().minY + 0.5D, this.theEntity.posZ);
    }

    @Override
    @Nullable
    public Path getPathToPos(BlockPos pos) {
        if (!this.canNavigate()) {
            return null;
        }
        else if (this.currentPath != null && !this.currentPath.isFinished() && pos.equals(this.currentTargetPos))
        {
            return this.currentPath;
        }
        else {
            this.currentTargetPos = pos;
            float f = this.getPathSearchRange();
            this.world.theProfiler.startSection("pathfind");
            BlockPos blockpos = new BlockPos(this.theEntity);
            int i = (int)(f + 8.0F);
            ChunkCache chunkcache = new ChunkCache(this.world, blockpos.add(-i, -i, -i), blockpos.add(i, i, i), 0);
            Path path = this.currentPathFinder.findPath(chunkcache, this.theEntity, this.currentTargetPos, f);
            this.world.theProfiler.endSection();
            return path;
        }
    }

    @Override
    public boolean setPath(@Nullable Path pathentityIn, double speedIn) {
        return super.setPath(pathentityIn, speedIn);
    }

    /**
     * Checks if the specified entity can safely walk to the specified location.
     */
    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        RayTraceResult raytraceresult = this.world.rayTraceBlocks(posVec31, new Vec3d(posVec32.xCoord, this.theEntity.getEntityBoundingBox().minY + 0.5D, posVec32.zCoord), false, true, false);
        return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
    }
}