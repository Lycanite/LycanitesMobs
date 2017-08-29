package com.lycanitesmobs.core.entity.navigate;

import com.google.common.collect.Sets;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.NodeProcessor;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public class CreatureNodeProcessor extends NodeProcessor implements ICreatureNodeProcessor {

    public EntityCreatureBase entityCreature;
    protected float avoidsWater;

    // ==================== Setup ====================
    @Override
    public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
        super.initProcessor(sourceIn, mob);
        this.avoidsWater = mob.getPathPriority(PathNodeType.WATER);
        if(mob instanceof EntityCreatureBase)
            this.entityCreature = (EntityCreatureBase)mob;
    }

    @Override
    public void updateEntitySize(Entity updateEntity) {
        this.entitySizeX = MathHelper.floor_double(updateEntity.width + 1.0F);
        this.entitySizeY = MathHelper.floor_double(updateEntity.height + 1.0F);
        this.entitySizeZ = MathHelper.floor_double(updateEntity.width + 1.0F);
    }

    @Override
    public void postProcess() {
        if(this.entityCreature != null)
            this.entity.setPathPriority(PathNodeType.WATER, this.avoidsWater);
        super.postProcess();
    }


    // ==================== Checks ====================
    /** Returns true if the entity is capable of pathing/moving in water at all. **/
    @Override
    public boolean getCanSwim() {
        if(this.entityCreature != null)
            return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
        return super.getCanSwim();
    }

    /** Returns true if the entity should use swimming focused pathing. **/
    public boolean swimming() {
        if(this.entityCreature == null) {
            return false;
        }
        if(this.entityCreature.isInWater()) {
            return this.entityCreature.isStrongSwimmer() || (this.entityCreature.canWade() && this.entityCreature.canDive());
        }
        return false;
    }

    /** Returns true if the entity should use flight focused pathing. **/
    public boolean flying() {
        return this.entityCreature != null && this.entityCreature.isFlying() && !this.entityCreature.isInWater();
    }


    // ==================== Start ====================
    /** Returns a PathPoint to the given coordinates. **/
    @Override
    public PathPoint getPathPointToCoords(double x, double y, double z) {
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            return this.openPoint(MathHelper.floor_double(x - (double)(this.entity.width / 2.0F)), MathHelper.floor_double(y + 0.5D), MathHelper.floor_double(z - (double)(this.entity.width / 2.0F)));
        }
        return this.openPoint(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    /** Returns the starting position to create a new path from. **/
    @Override
    public PathPoint getStart() {
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            return this.openPoint(MathHelper.floor_double(this.entity.getEntityBoundingBox().minX), MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor_double(this.entity.getEntityBoundingBox().minZ));
        }

        // Wading Through Water:
        int posY;
        if (this.getCanSwim() && this.entity.isInWater()) {
            posY = (int)this.entity.getEntityBoundingBox().minY;
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos(MathHelper.floor_double(this.entity.posX), posY, MathHelper.floor_double(this.entity.posZ));

            for (Block block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock(); block == Blocks.FLOWING_WATER || block == Blocks.WATER; block = this.blockaccess.getBlockState(blockpos$mutableblockpos).getBlock())
            {
                ++posY;
                blockpos$mutableblockpos.setPos(MathHelper.floor_double(this.entity.posX), posY, MathHelper.floor_double(this.entity.posZ));
            }
        }

        // Walking On ground:
        else if (this.entity.onGround) {
            posY = MathHelper.floor_double(this.entity.getEntityBoundingBox().minY + 0.5D);
        }

        // In Air:
        else {
            BlockPos blockpos;
            for (blockpos = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos).getMaterial() == Material.AIR || this.blockaccess.getBlockState(blockpos).getBlock().isPassable(this.blockaccess, blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down()) {}
            posY = blockpos.up().getY();
        }

        // XZ Offset:
        BlockPos offsetXZ = new BlockPos(this.entity);
        PathNodeType targetNodeType = this.getPathNodeType(this.entity, offsetXZ.getX(), posY, offsetXZ.getZ());

        if (this.entity.getPathPriority(targetNodeType) < 0.0F) {
            Set<BlockPos> set = Sets.newHashSet();
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)posY, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)posY, this.entity.getEntityBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)posY, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)posY, this.entity.getEntityBoundingBox().maxZ));

            for (BlockPos betterPos : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.entity, betterPos);
                if(this.entity.getPathPriority(pathnodetype) >= 0.0F) {
                    return this.openPoint(betterPos.getX(), betterPos.getY(), betterPos.getZ());
                }
            }
        }

        return this.openPoint(offsetXZ.getX(), posY, offsetXZ.getZ());
    }


    // ==================== Options ====================
    /** Returns the start position to use when creating a new path. **/
    public int findPathOptions(PathPoint[] pathOptions, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
        // Flying/Strong Swimming:
        if(this.flying() || this.swimming()) {
            int i = 0;
            for (EnumFacing enumfacing : EnumFacing.values()) {
                PathPoint pathpoint = null;
                if(this.flying()) {
                    pathpoint = this.getFlightNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());
                }
                else {
                    pathpoint = this.getWaterNode(currentPoint.xCoord + enumfacing.getFrontOffsetX(), currentPoint.yCoord + enumfacing.getFrontOffsetY(), currentPoint.zCoord + enumfacing.getFrontOffsetZ());
                }

                if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
                    pathOptions[i++] = pathpoint;
                }
            }
            return i;
        }

        // Walking:
        int i = 0;
        int j = 0;
        PathNodeType pathnodetype = this.getPathNodeType(this.entity, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord);

        if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
            j = MathHelper.floor_double(Math.max(1.0F, this.entity.stepHeight));
        }

        BlockPos blockpos = (new BlockPos(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord)).down();
        double d0 = (double)currentPoint.yCoord - (1.0D - this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos).maxY);
        PathPoint pathpoint = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);
        PathPoint pathpoint1 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.WEST);
        PathPoint pathpoint2 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, j, d0, EnumFacing.EAST);
        PathPoint pathpoint3 = this.getSafePoint(currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

        if (pathpoint != null && !pathpoint.visited && pathpoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint;
        }

        if (pathpoint1 != null && !pathpoint1.visited && pathpoint1.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint1;
        }

        if (pathpoint2 != null && !pathpoint2.visited && pathpoint2.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint2;
        }

        if (pathpoint3 != null && !pathpoint3.visited && pathpoint3.distanceTo(targetPoint) < maxDistance) {
            pathOptions[i++] = pathpoint3;
        }

        boolean flag = pathpoint3 == null || pathpoint3.nodeType == PathNodeType.OPEN || pathpoint3.costMalus != 0.0F;
        boolean flag1 = pathpoint == null || pathpoint.nodeType == PathNodeType.OPEN || pathpoint.costMalus != 0.0F;
        boolean flag2 = pathpoint2 == null || pathpoint2.nodeType == PathNodeType.OPEN || pathpoint2.costMalus != 0.0F;
        boolean flag3 = pathpoint1 == null || pathpoint1.nodeType == PathNodeType.OPEN || pathpoint1.costMalus != 0.0F;

        if (flag && flag3) {
            PathPoint pathpoint4 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

            if (pathpoint4 != null && !pathpoint4.visited && pathpoint4.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint4;
            }
        }

        if (flag && flag2) {
            PathPoint pathpoint5 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord - 1, j, d0, EnumFacing.NORTH);

            if (pathpoint5 != null && !pathpoint5.visited && pathpoint5.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint5;
            }
        }

        if (flag1 && flag3) {
            PathPoint pathpoint6 = this.getSafePoint(currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);

            if (pathpoint6 != null && !pathpoint6.visited && pathpoint6.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint6;
            }
        }

        if (flag1 && flag2) {
            PathPoint pathpoint7 = this.getSafePoint(currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord + 1, j, d0, EnumFacing.SOUTH);

            if (pathpoint7 != null && !pathpoint7.visited && pathpoint7.distanceTo(targetPoint) < maxDistance) {
                pathOptions[i++] = pathpoint7;
            }
        }

        return i;
    }

    /** Returns a point that the entity can move to safely, only used when walking. **/
    @Nullable
    private PathPoint getSafePoint(int x, int y, int z, int p_186332_4_, double p_186332_5_, EnumFacing facing) {
        PathPoint pathpoint = null;
        BlockPos blockpos = new BlockPos(x, y, z);
        BlockPos blockpos1 = blockpos.down();
        double d0 = (double)y - (1.0D - this.blockaccess.getBlockState(blockpos1).getBoundingBox(this.blockaccess, blockpos1).maxY);

        if (d0 - p_186332_5_ > 1.125D) {
            return null;
        }
        else {
            PathNodeType pathnodetype = this.getPathNodeType(this.entity, x, y, z);
            float f = this.entity.getPathPriority(pathnodetype);
            double d1 = (double)this.entity.width / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.openPoint(x, y, z);
                pathpoint.nodeType = pathnodetype;
                pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
            }

            if (pathnodetype == PathNodeType.WALKABLE) {
                return pathpoint;
            }
            else {
                if (pathpoint == null && p_186332_4_ > 0 && pathnodetype != PathNodeType.FENCE && pathnodetype != PathNodeType.TRAPDOOR) {
                    pathpoint = this.getSafePoint(x, y + 1, z, p_186332_4_ - 1, p_186332_5_, facing);

                    if (pathpoint != null && (pathpoint.nodeType == PathNodeType.OPEN || pathpoint.nodeType == PathNodeType.WALKABLE) && this.entity.width < 1.0F) {
                        double d2 = (double)(x - facing.getFrontOffsetX()) + 0.5D;
                        double d3 = (double)(z - facing.getFrontOffsetZ()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d2 - d1, (double)y + 0.001D, d3 - d1, d2 + d1, (double)((float)y + this.entity.height), d3 + d1);
                        AxisAlignedBB axisalignedbb1 = this.blockaccess.getBlockState(blockpos).getBoundingBox(this.blockaccess, blockpos);
                        AxisAlignedBB axisalignedbb2 = axisalignedbb.addCoord(0.0D, axisalignedbb1.maxY - 0.002D, 0.0D);

                        if (this.entity.worldObj.collidesWithAnyBlock(axisalignedbb2)) {
                            pathpoint = null;
                        }
                    }
                }

                if (pathnodetype == PathNodeType.OPEN) {
                    AxisAlignedBB axisalignedbb3 = new AxisAlignedBB((double)x - d1 + 0.5D, (double)y + 0.001D, (double)z - d1 + 0.5D, (double)x + d1 + 0.5D, (double)((float)y + this.entity.height), (double)z + d1 + 0.5D);

                    if (this.entity.worldObj.collidesWithAnyBlock(axisalignedbb3)) {
                        return null;
                    }

                    if (this.entity.width >= 1.0F) {
                        PathNodeType pathnodetype1 = this.getPathNodeType(this.entity, x, y - 1, z);

                        if (pathnodetype1 == PathNodeType.BLOCKED) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = PathNodeType.WALKABLE;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            return pathpoint;
                        }
                    }

                    int i = 0;

                    while (y > 0 && pathnodetype == PathNodeType.OPEN) {
                        --y;

                        if (i++ >= this.entity.getMaxFallHeight()) {
                            return null;
                        }

                        pathnodetype = this.getPathNodeType(this.entity, x, y, z);
                        f = this.entity.getPathPriority(pathnodetype);

                        if (pathnodetype != PathNodeType.OPEN && f >= 0.0F) {
                            pathpoint = this.openPoint(x, y, z);
                            pathpoint.nodeType = pathnodetype;
                            pathpoint.costMalus = Math.max(pathpoint.costMalus, f);
                            break;
                        }

                        if (f < 0.0F) {
                            return null;
                        }
                    }
                }

                return pathpoint;
            }
        }
    }


    // ==================== Path Nodes ====================
    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        if(this.swimming())
            return PathNodeType.WATER;

        EnumSet<PathNodeType> enumset = EnumSet.noneOf(PathNodeType.class);
        PathNodeType pathnodetype = PathNodeType.BLOCKED;
        double d0 = (double)entitylivingIn.width / 2.0D;
        BlockPos blockpos = new BlockPos(entitylivingIn);

        for (int i = 0; i < xSize; ++i) {
            for (int j = 0; j < ySize; ++j) {
                for (int k = 0; k < zSize; ++k) {
                    int l = i + x;
                    int i1 = j + y;
                    int j1 = k + z;
                    PathNodeType pathnodetype1 = this.getPathNodeType(blockaccessIn, l, i1, j1);

                    if (pathnodetype1 == PathNodeType.DOOR_WOOD_CLOSED && canBreakDoorsIn && canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.WALKABLE;
                    }

                    if (pathnodetype1 == PathNodeType.DOOR_OPEN && !canEnterDoorsIn) {
                        pathnodetype1 = PathNodeType.BLOCKED;
                    }

                    if (pathnodetype1 == PathNodeType.RAIL && !(blockaccessIn.getBlockState(blockpos).getBlock() instanceof BlockRailBase) && !(blockaccessIn.getBlockState(blockpos.down()).getBlock() instanceof BlockRailBase)) {
                        pathnodetype1 = PathNodeType.FENCE;
                    }

                    if (i == 0 && j == 0 && k == 0) {
                        pathnodetype = pathnodetype1;
                    }

                    enumset.add(pathnodetype1);
                }
            }
        }

        if (enumset.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        else {
            PathNodeType pathnodetype2 = PathNodeType.BLOCKED;

            for (PathNodeType pathnodetype3 : enumset) {
                if (entitylivingIn.getPathPriority(pathnodetype3) < 0.0F) {
                    return pathnodetype3;
                }

                if (entitylivingIn.getPathPriority(pathnodetype3) >= entitylivingIn.getPathPriority(pathnodetype2)) {
                    pathnodetype2 = pathnodetype3;
                }
            }

            if (pathnodetype == PathNodeType.OPEN && entitylivingIn.getPathPriority(pathnodetype2) == 0.0F) {
                return PathNodeType.OPEN;
            }
            else {
                return pathnodetype2;
            }
        }
    }

    public PathNodeType getPathNodeType(EntityLiving entitylivingIn, BlockPos pos) {
        if(this.swimming())
            return PathNodeType.WATER;

        return this.getPathNodeType(entitylivingIn, pos.getX(), pos.getY(), pos.getZ());
    }

    public PathNodeType getPathNodeType(EntityLiving entitylivingIn, int x, int y, int z) {
        if(this.swimming())
            return PathNodeType.WATER;

        return this.getPathNodeType(this.blockaccess, x, y, z, entitylivingIn, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.getCanBreakDoors(), this.getCanEnterDoors());
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
        if(this.swimming())
            return PathNodeType.WATER;

        PathNodeType pathnodetype = this.getPathNodeTypeRaw(blockaccessIn, x, y, z);

        if (pathnodetype == PathNodeType.OPEN && y >= 1) {
            Block block = blockaccessIn.getBlockState(new BlockPos(x, y - 1, z)).getBlock();
            PathNodeType pathnodetype1 = this.getPathNodeTypeRaw(blockaccessIn, x, y - 1, z);
            pathnodetype = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;

            if (pathnodetype1 == PathNodeType.DAMAGE_FIRE || block == Blocks.MAGMA) {
                pathnodetype = PathNodeType.DAMAGE_FIRE;
            }

            if (pathnodetype1 == PathNodeType.DAMAGE_CACTUS) {
                pathnodetype = PathNodeType.DAMAGE_CACTUS;
            }
        }

        BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

        if (pathnodetype == PathNodeType.WALKABLE) {
            for (int j = -1; j <= 1; ++j) {
                for (int i = -1; i <= 1; ++i) {
                    if (j != 0 || i != 0) {
                        Block block1 = blockaccessIn.getBlockState(blockpos$pooledmutableblockpos.setPos(j + x, y, i + z)).getBlock();

                        if (block1 == Blocks.CACTUS) {
                            pathnodetype = PathNodeType.DANGER_CACTUS;
                        }
                        else if (block1 == Blocks.FIRE) {
                            pathnodetype = PathNodeType.DANGER_FIRE;
                        }
                        else if(block1.isBurning(blockaccessIn,blockpos$pooledmutableblockpos.setPos(j +x, y, i + z))) pathnodetype = PathNodeType.DAMAGE_FIRE;
                    }
                }
            }
        }

        blockpos$pooledmutableblockpos.release();
        return pathnodetype;
    }

    protected PathNodeType getPathNodeTypeRaw(IBlockAccess blockAccess, int x, int y, int z) {
        BlockPos blockpos = new BlockPos(x, y, z);
        IBlockState iblockstate = blockAccess.getBlockState(blockpos);
        Block block = iblockstate.getBlock();
        Material material = iblockstate.getMaterial();
        /*PathNodeType type = block.getAiPathNodeType(iblockstate, blockAccess, blockpos); // 1.11.2
        if (type != null)
            return type;*/
        return material == Material.AIR ? PathNodeType.OPEN : (block != Blocks.TRAPDOOR && block != Blocks.IRON_TRAPDOOR && block != Blocks.WATERLILY ? (block == Blocks.FIRE ? PathNodeType.DAMAGE_FIRE : (block == Blocks.CACTUS ? PathNodeType.DAMAGE_CACTUS : (block instanceof BlockDoor && material == Material.WOOD && !((Boolean)iblockstate.getValue(BlockDoor.OPEN)).booleanValue() ? PathNodeType.DOOR_WOOD_CLOSED : (block instanceof BlockDoor && material == Material.IRON && !((Boolean)iblockstate.getValue(BlockDoor.OPEN)).booleanValue() ? PathNodeType.DOOR_IRON_CLOSED : (block instanceof BlockDoor && ((Boolean)iblockstate.getValue(BlockDoor.OPEN)).booleanValue() ? PathNodeType.DOOR_OPEN : (block instanceof BlockRailBase ? PathNodeType.RAIL : (!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || ((Boolean)iblockstate.getValue(BlockFenceGate.OPEN)).booleanValue()) ? (material == Material.WATER ? PathNodeType.WATER : (material == Material.LAVA ? PathNodeType.LAVA : (block.isPassable(blockAccess, blockpos) ? PathNodeType.OPEN : PathNodeType.BLOCKED))) : PathNodeType.FENCE))))))) : PathNodeType.TRAPDOOR);
    }

    protected PathPoint getFlightNode(int x, int y, int z) {
        PathNodeType pathnodetype = this.isFlyablePathNode(x, y, z);
        if(this.entityCreature != null && this.entityCreature.isStrongSwimmer()) {
            if(pathnodetype == PathNodeType.WATER)
                return this.openPoint(x, y, z);
        }
        return pathnodetype == PathNodeType.OPEN ? this.openPoint(x, y, z) : null;
    }

    protected PathNodeType isFlyablePathNode(int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + this.entitySizeY; ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    // Check For Open Air:
                    if (iblockstate.getMaterial() != Material.AIR) {
                        // If Can Swim Check For Swimmable Node:
                        if(this.entityCreature != null && this.entityCreature.isStrongSwimmer()) {
                            return this.isSwimmablePathNode(x, y, z);
                        }
                        return PathNodeType.BLOCKED;
                    }
                }
            }
        }

        return PathNodeType.OPEN;
    }

    @Nullable
    private PathPoint getWaterNode(int x, int y, int z) {
        PathNodeType pathnodetype = null;
        if(this.entityCreature != null && this.entityCreature.isFlying()) {
            pathnodetype = this.isFlyablePathNode(x, y, z);
            if(pathnodetype == PathNodeType.OPEN)
                return this.openPoint(x, y, z);
        }
        else {
            pathnodetype = this.isSwimmablePathNode(x, y, z);
        }
        return pathnodetype == PathNodeType.WATER ? this.openPoint(x, y, z) : null;
    }

    private PathNodeType isSwimmablePathNode(int x, int y, int z) {
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int i = x; i < x + this.entitySizeX; ++i) {
            for (int j = y; j < y + Math.min(this.entitySizeY, 3); ++j) {
                for (int k = z; k < z + this.entitySizeZ; ++k) {
                    IBlockState iblockstate = this.blockaccess.getBlockState(blockpos$mutableblockpos.setPos(i, j, k));

                    if(this.entityCreature == null) {
                        return PathNodeType.BLOCKED;
                    }

                    // Water Swimming:
                    if(!this.entityCreature.waterDamage()) {
                        if (iblockstate.getMaterial() != Material.WATER) {
                            // Ooze Swimming:
                            if(!this.entityCreature.canFreeze()) {
                                if (iblockstate.getBlock() != ObjectManager.getBlock("ooze")) {
                                    return PathNodeType.BLOCKED;
                                }
                            }
                            return PathNodeType.BLOCKED;
                        }
                    }

                    // Lava Swimming:
                    else if(!this.entityCreature.canBurn()) {
                        if (iblockstate.getMaterial() != Material.LAVA) {
                            return PathNodeType.BLOCKED;
                        }
                    }

                    // Ooze Swimming (With Water Damage):
                    else if(!this.entityCreature.canFreeze()) {
                        if (iblockstate.getBlock() != ObjectManager.getBlock("ooze")) {
                            return PathNodeType.BLOCKED;
                        }
                    }
                }
            }
        }
        return PathNodeType.WATER;
    }
}
