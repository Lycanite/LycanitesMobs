package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PathNavigateGroundCustom extends PathNavigateGround {

    public PathNavigateGroundCustom(EntityLiving entityLiving, World world) {
        super(entityLiving, world);
    }

    /** Uses custom walk node processor. **/
    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new WalkNodeProcessorCustom();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }


    @Override
    protected Vec3d getEntityPosition() {
        return super.getEntityPosition();
    }


    /** Unchanged from PathNavigateGround, only overridden because isSafeToStandAt is private. */
    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d posVec31, Vec3d posVec32, int sizeX, int sizeY, int sizeZ) {
        int i = MathHelper.floor_double(posVec31.xCoord);
        int j = MathHelper.floor_double(posVec31.zCoord);
        double d0 = posVec32.xCoord - posVec31.xCoord;
        double d1 = posVec32.zCoord - posVec31.zCoord;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 < 1.0E-8D) {
            return false;
        }
        else {
            double d3 = 1.0D / Math.sqrt(d2);
            d0 = d0 * d3;
            d1 = d1 * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                return false;
            }
            else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(d0);
                double d5 = 1.0D / Math.abs(d1);
                double d6 = (double)i - posVec31.xCoord;
                double d7 = (double)j - posVec31.zCoord;

                if (d0 >= 0.0D) {
                    ++d6;
                }

                if (d1 >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / d0;
                d7 = d7 / d1;
                int k = d0 < 0.0D ? -1 : 1;
                int l = d1 < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor_double(posVec32.xCoord);
                int j1 = MathHelper.floor_double(posVec32.zCoord);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if (d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    }
                    else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if (!this.isSafeToStandAt(i, (int)posVec31.yCoord, j, sizeX, sizeY, sizeZ, posVec31, d0, d1)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /** Fixes lag caused by large entities. **/
    private boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d vec31, double p_179683_8_, double p_179683_10_) {
        sizeX = Math.min(sizeX, 2);
        sizeY = Math.min(sizeY, 3);
        sizeZ = Math.min(sizeZ, 2);
        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if (!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, vec31, p_179683_8_, p_179683_10_)) {
            return false;
        }
        else {
            for (int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double)k + 0.5D - vec31.xCoord;
                    double d1 = (double)l + 0.5D - vec31.zCoord;

                    if (d0 * p_179683_8_ + d1 * p_179683_10_ >= 0.0D) {
                        PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.worldObj, k, y - 1, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);

                        if (pathnodetype == PathNodeType.WATER && !this.theEntity.canBreatheUnderwater()) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.LAVA && !this.isLavaCreature()) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.OPEN) {
                            return false;
                        }

                        pathnodetype = this.nodeProcessor.getPathNodeType(this.worldObj, k, y, l, this.theEntity, sizeX, sizeY, sizeZ, true, true);
                        float f = this.theEntity.getPathPriority(pathnodetype);

                        if (f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        if (this.canBurn() && (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE)) {
                            return false;
                        }

                        if (pathnodetype == PathNodeType.DAMAGE_OTHER) {
                            return false;
                        }
                    }
                }
            }

            return true;
        }
    }

    /** Unchanged from PathNavigateGround, only overridden because isSafeToStandAt is private. */
    private boolean isPositionClear(int p_179692_1_, int p_179692_2_, int p_179692_3_, int p_179692_4_, int p_179692_5_, int p_179692_6_, Vec3d p_179692_7_, double p_179692_8_, double p_179692_10_)
    {
        for (BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(p_179692_1_, p_179692_2_, p_179692_3_), new BlockPos(p_179692_1_ + p_179692_4_ - 1, p_179692_2_ + p_179692_5_ - 1, p_179692_3_ + p_179692_6_ - 1)))
        {
            double d0 = (double)blockpos.getX() + 0.5D - p_179692_7_.xCoord;
            double d1 = (double)blockpos.getZ() + 0.5D - p_179692_7_.zCoord;

            if (d0 * p_179692_8_ + d1 * p_179692_10_ >= 0.0D)
            {
                Block block = this.worldObj.getBlockState(blockpos).getBlock();

                if (!block.isPassable(this.worldObj, blockpos))
                {
                    return false;
                }
            }
        }

        return true;
    }

    /** Returns true if the pathing entity can survive in lava. **/
    protected boolean isLavaCreature() {
        if(this.theEntity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreatureBase = (EntityCreatureBase)this.theEntity;
            return entityCreatureBase.isLavaCreature;
        }
        return false;
    }

    /** Returns true if the pathing entity can burn. **/
    protected boolean canBurn() {
        if(this.theEntity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreatureBase = (EntityCreatureBase)this.theEntity;
            return entityCreatureBase.canBurn();
        }
        return false;
    }
}
