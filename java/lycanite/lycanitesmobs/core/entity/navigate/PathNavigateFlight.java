package lycanite.lycanitesmobs.core.entity.navigate;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateFlight extends PathNavigate {

    public PathNavigateFlight(EntityLiving entity, World world) {
        super(entity, world);
    }

    /**
     * Returns a new Path Finder.
     */
    @Override
    protected PathFinder getPathFinder() {
        return new PathFinder(new FlightNodeProcessor());
    }

    /**
     * Returns true if able to navigate.
     */
    @Override
    protected boolean canNavigate() {
        return true;
    }

    @Override
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.theEntity.posX, this.theEntity.posY + (double)this.theEntity.height * 0.5D, this.theEntity.posZ);
    }

    @Override
    protected void pathFollow() {
        Vec3d entityVector = this.getEntityPosition();
        float entitySize = this.theEntity.width * this.theEntity.width;

        if (entityVector.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < (double)entitySize) {
            this.currentPath.incrementPathIndex();
        }

        int pathIndexRange = 6;
        for (int pathIndex = Math.min(this.currentPath.getCurrentPathIndex() + pathIndexRange, this.currentPath.getCurrentPathLength() - 1); pathIndex > this.currentPath.getCurrentPathIndex(); --pathIndex) {
            Vec3d pathVector = this.currentPath.getVectorFromIndex(this.theEntity, pathIndex);

            if (pathVector.squareDistanceTo(entityVector) <= 36.0D && this.isDirectPathBetweenPoints(entityVector, pathVector, 0, 0, 0)) {
                this.currentPath.setCurrentPathIndex(pathIndex);
                break;
            }
        }

        this.checkForStuck(entityVector);
    }

    /**
     * Trims path data from the end to the first sun covered block
     */
    @Override
    protected void removeSunnyPath() {
        super.removeSunnyPath();
    }

    /**
     * Returns true when an entity of specified size could safely walk in a straight line between the two points. Args:
     * pos1, pos2, entityXSize, entityYSize, entityZSize
     */
    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        RayTraceResult raytraceresult = this.getEntityWorld().rayTraceBlocks(posVec31, new Vec3d(posVec32.xCoord, posVec32.yCoord + (double)this.theEntity.height * 0.5D, posVec32.zCoord), false, true, false);
        return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
    }

    /**
     * Returns true if the provided position is a valid position to randomly wander to.
     */
    @Override
    public boolean canEntityStandOnPos(BlockPos pos) {
        return !this.getEntityWorld().getBlockState(pos).isFullBlock();
    }
}