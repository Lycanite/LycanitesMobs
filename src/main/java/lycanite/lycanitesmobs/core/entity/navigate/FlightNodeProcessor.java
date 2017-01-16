package lycanite.lycanitesmobs.core.entity.navigate;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

public class FlightNodeProcessor extends NodeProcessor {
    @Override
    public PathPoint getStart() {
        return this.openPoint(MathHelper.floor_double(this.entity.getEntityBoundingBox().minX), MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.entity.getEntityBoundingBox().minZ));
    }

    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        return this.openPoint(MathHelper.floor_double(x - (double)(this.entity.width / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(z - (double)(this.entity.width / 2.0F)));
    }

    @Override
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        int i = 0;

        for (EnumFacing enumfacing : EnumFacing.values()) {
            PathPoint pathpoint = this.getFlightNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());

            if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint;
            }
        }

        return i;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccess, int x, int y, int z, EntityLiving entityliving, int xSize, int ySize, int zSize, boolean canBreakDoors, boolean canEnterDoors) {
        return PathNodeType.OPEN;
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess x, int y, int z, int p_186330_4_) {
        return PathNodeType.OPEN;
    }

    protected PathPoint getFlightNode(int x, int y, int z) {
        PathNodeType pathnodetype = this.isFree(x, y, z);
        return pathnodetype == PathNodeType.OPEN ? this.openPoint(x, y, z) : null;
    }

    protected PathNodeType isFree(int p_186327_1_, int p_186327_2_, int p_186327_3_) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

        for (int i = p_186327_1_; i < p_186327_1_ + this.entitySizeX; ++i) {
            for (int j = p_186327_2_; j < p_186327_2_ + this.entitySizeY; ++j) {
                for (int k = p_186327_3_; k < p_186327_3_ + this.entitySizeZ; ++k) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    if (iblockstate.getMaterial() != Material.AIR) {
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.OPEN;
    }
}