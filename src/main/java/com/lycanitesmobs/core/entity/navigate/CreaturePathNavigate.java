package com.lycanitesmobs.core.entity.navigate;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CreaturePathNavigate extends PathNavigate {

    public EntityCreatureBase entityCreature;
    protected BlockPos targetPosition;

    public CreaturePathNavigate(EntityCreatureBase entityCreature, World world) {
        super(entityCreature, world);
        this.entityCreature = entityCreature;
    }

    /** Create PathFinder with CreatureNodeProcessor. **/
    @Override
    protected PathFinder getPathFinder() {
        this.nodeProcessor = new CreatureNodeProcessor();
        this.nodeProcessor.setCanEnterDoors(true);
        return new PathFinder(this.nodeProcessor);
    }


    // ==================== Status ====================
    /** Returns true if the entity is capable of navigating at all. **/
    @Override
    protected boolean canNavigate() {
        if(this.entityCreature.isFlying())
            return true;
        if(this.entityCreature.isInWater())
            return this.entityCreature.canWade() || this.entityCreature.isStrongSwimmer();
        return this.entity.onGround || this.entity.isRiding();
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public void setCanOpenDoors(boolean setBreakDoors) {
        this.nodeProcessor.setCanOpenDoors(setBreakDoors);
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public boolean getCanOpenDoors() {
        return this.nodeProcessor.getCanOpenDoors();
    }

    /** Sets if the creature should navigate as though it can break doors. **/
    public void setEnterDoors(boolean setBreakDoors) {
        this.nodeProcessor.setCanEnterDoors(setBreakDoors);
    }

    /** Returns if the creature should navigate as though it can enter doors. **/
    public boolean getEnterDoors() {
        return this.nodeProcessor.getCanEnterDoors();
    }


    // ==================== Create Path ====================
    /** Returns a new path from starting path position to the provided target position. **/
    @Override
    public Path getPathToPos(BlockPos pos) {
        this.targetPosition = this.getSuitableDestination(pos);
        return super.getPathToPos(this.targetPosition);
    }

    /** Returns the path to the given EntityLiving. **/
    @Override
    public Path getPathToEntityLiving(Entity entity) {
        this.targetPosition = new BlockPos(entity);
        return super.getPathToEntityLiving(entity);
    }


    // ==================== Pathing Destination ====================
    /** Returns a suitable position close to the provided position if the position itself isn't suitable. **/
    protected BlockPos getSuitableDestination(BlockPos pos) {
        IBlockState targetBlockState = this.world.getBlockState(pos);

        // Air:
        if(targetBlockState.getMaterial() == Material.AIR) {
            // Flying:
            if (this.entityCreature.isFlying()) {
                return pos;
            }

            // Walking:
            return this.getGround(pos);
        }

        // Non-Solid:
        if(!targetBlockState.getMaterial().isSolid()) {
            return pos;
        }

        // Solid:
        return this.getSurface(pos);
    }


    // ==================== Pathing Start ====================
    /** Return the position to path from. **/
    @Override
    protected Vec3d getEntityPosition() {
        return new Vec3d(this.entity.posX, (double)this.getPathablePosY(), this.entity.posZ);
    }

    /** Return the Y position to path from. **/
    protected int getPathablePosY() {
        // If in water (or lava for lava swimmers):
        if(this.entityCreature.isInWater()) {

            // Slow swimmers (water bobbing):
            if(this.entityCreature.canFloat() && !this.entityCreature.canDive()) {
                int posY = (int)this.entity.getEntityBoundingBox().minY;
                Block block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ))).getBlock();
                int searchCount = 0;

                while (this.isSwimmableBlock(block)) { // Search up for surface.
                    ++posY;
                    block = this.world.getBlockState(new BlockPos(MathHelper.floor(this.entity.posX), posY, MathHelper.floor(this.entity.posZ))).getBlock();
                    ++searchCount;
                    if (searchCount > 16) {
                        return (int)this.entity.getEntityBoundingBox().minY;
                    }
                }

                return posY;
            }

        }

        // Path From Current Y Pos:
        return (int)(this.entity.getEntityBoundingBox().minY + 0.5D);
    }

    /** Starts pathing to target entity. **/
    @Override
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        Path path = this.getPathToEntityLiving(entityIn);

        if (path != null) {
            return this.setPath(path, speedIn);
        }

        // Climbing:
        else if(this.entityCreature.canClimb()) {
            this.targetPosition = new BlockPos(entityIn);
            this.speed = speedIn;
            return true;
        }

        return false;
    }


    // ==================== Testing ====================
    /** Returns true if the block is a block that the creature can swim in (water and lava for lava creatures). **/
    protected boolean isSwimmableBlock(Block block) {
        return this.isSwimmableBlock(block, 0);
    }

    /** Cached check skips the checking of the block type for performance. **/
    protected boolean isSwimmableBlock(Block block, int cachedCheck) {
        if(block == null || block == Blocks.AIR) {
            return false;
        }
        if(cachedCheck == 1 || this.isWaterBlock(block)) {
            return !this.entityCreature.waterDamage();
        }
        if(cachedCheck == 2 || this.isLavaBlock(block)) {
            return !this.entityCreature.canBurn();
        }
        if(cachedCheck == 3 || this.isOozeBlock(block)) {
            return !this.entityCreature.canFreeze();
        }
        return false;
    }

    /** Returns true if the block is a water block. **/
    protected boolean isWaterBlock(Block block) {
        return block == Blocks.FLOWING_WATER || block == Blocks.WATER;
    }

    /** Returns true if the block is a lava block. **/
    protected boolean isLavaBlock(Block block) {
        return block == Blocks.FLOWING_LAVA || block == Blocks.LAVA || block == ObjectManager.getBlock("purelava");
    }

    /** Returns true if the block is a ooze block. **/
    protected boolean isOozeBlock(Block block) {
        return block == ObjectManager.getBlock("ooze");
    }

    /** Returns true if the entity can move to the block position. **/
    public boolean canEntityStandOnPos(BlockPos pos) {
        // Flight/Swimming:
        if(this.entityCreature.isFlying() || (this.entityCreature.isInWater() && this.entityCreature.isStrongSwimmer())) {
            IBlockState blockState = this.world.getBlockState(pos);
            if(blockState.getMaterial().isLiquid()) {
            	return this.entityCreature.isStrongSwimmer();
			}
            return !blockState.getMaterial().isSolid();
        }
        return super.canEntityStandOnPos(pos);
    }


    // ==================== Block Searching ====================
    /** Searches for the ground from the provided position. If the void is hit then the initial position is returned. Experimental: Searched for non-solids instead of just air. **/
    public BlockPos getGround(BlockPos pos) {
        BlockPos resultPos = pos;
        for(resultPos = pos.down(); resultPos.getY() > 0 && !this.world.getBlockState(resultPos).getMaterial().isSolid(); resultPos = resultPos.down()) {}
        resultPos = resultPos.up();
        if(resultPos.getY() == 0 && !this.world.getBlockState(resultPos).getMaterial().isSolid())
            return pos;
        return resultPos;
    }

    /** Searches up for a non-solid block. If the sky limit is hit then the initial position is returned. **/
    public BlockPos getSurface(BlockPos pos) {
        BlockPos resultPos = pos;
        for(resultPos = pos.up(); resultPos.getY() < this.world.getHeight() && this.world.getBlockState(resultPos).getMaterial().isSolid(); resultPos = resultPos.up()) {}
        if(resultPos.getY() == this.world.getHeight() && this.world.getBlockState(resultPos).getMaterial().isSolid())
            return pos;
        return resultPos;
    }


    // ==================== Path Checks ====================
    /** Returns true if the path is a straight unblocked line, walking mobs also check for hazards along the line. **/
    @Override
    protected boolean isDirectPathBetweenPoints(Vec3d startVec, Vec3d endVec, int sizeX, int sizeY, int sizeZ) {
        // Flight/Swimming:
        if(this.entityCreature.isFlying() || this.entityCreature.isInWater()) {
            RayTraceResult raytraceresult = this.world.rayTraceBlocks(startVec, new Vec3d(endVec.x, endVec.y + (double)this.entity.height * 0.5D, endVec.z), false, true, false);
            return raytraceresult == null || raytraceresult.typeOfHit == RayTraceResult.Type.MISS;
        }

        int i = MathHelper.floor(startVec.x);
        int j = MathHelper.floor(startVec.z);
        double distanceX = endVec.x - startVec.x;
        double distanceY = endVec.z - startVec.z;
        double distance = distanceX * distanceX + distanceY * distanceY;

        if (distance < 1.0E-8D) {
            return false;
        }
        else {
            double d3 = 1.0D / Math.sqrt(distance);
            distanceX = distanceX * d3;
            distanceY = distanceY * d3;
            sizeX = sizeX + 2;
            sizeZ = sizeZ + 2;

            if(!this.isSafeToStandAt(i, (int)startVec.y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY)) {
                return false;
            }
            else {
                sizeX = sizeX - 2;
                sizeZ = sizeZ - 2;
                double d4 = 1.0D / Math.abs(distanceX);
                double d5 = 1.0D / Math.abs(distanceY);
                double d6 = (double)i - startVec.x;
                double d7 = (double)j - startVec.z;

                if (distanceX >= 0.0D) {
                    ++d6;
                }

                if (distanceY >= 0.0D) {
                    ++d7;
                }

                d6 = d6 / distanceX;
                d7 = d7 / distanceY;
                int k = distanceX < 0.0D ? -1 : 1;
                int l = distanceY < 0.0D ? -1 : 1;
                int i1 = MathHelper.floor(endVec.x);
                int j1 = MathHelper.floor(endVec.z);
                int k1 = i1 - i;
                int l1 = j1 - j;

                while (k1 * k > 0 || l1 * l > 0) {
                    if(d6 < d7) {
                        d6 += d4;
                        i += k;
                        k1 = i1 - i;
                    }
                    else {
                        d7 += d5;
                        j += l;
                        l1 = j1 - j;
                    }

                    if(!this.isSafeToStandAt(i, (int)startVec.y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceY)) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /** Returns true if the position is safe for the entity to stand at. **/
    protected boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d startVec, double distanceX, double distanceZ) {
        // Performance Size Cap:
        sizeX = Math.min(sizeX, 2);
        sizeY = Math.min(sizeY, 3);
        sizeZ = Math.min(sizeZ, 2);

        int i = x - sizeX / 2;
        int j = z - sizeZ / 2;

        if(!this.isPositionClear(i, y, j, sizeX, sizeY, sizeZ, startVec, distanceX, distanceZ)) {
            return false;
        }
        else {
            for(int k = i; k < i + sizeX; ++k) {
                for (int l = j; l < j + sizeZ; ++l) {
                    double d0 = (double)k + 0.5D - startVec.x;
                    double d1 = (double)l + 0.5D - startVec.z;

                    if(d0 * distanceX + d1 * distanceZ >= 0.0D) {
                        PathNodeType pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y - 1, l, this.entity, sizeX, sizeY, sizeZ, true, true);

                        if(pathnodetype == PathNodeType.WATER) {
                            return false;
                        }

                        if(pathnodetype == PathNodeType.LAVA) {
                            return false;
                        }

                        if(pathnodetype == PathNodeType.OPEN) {
                            return false;
                        }

                        pathnodetype = this.nodeProcessor.getPathNodeType(this.world, k, y, l, this.entity, sizeX, sizeY, sizeZ, true, true);
                        float f = this.entity.getPathPriority(pathnodetype);

                        if(f < 0.0F || f >= 8.0F) {
                            return false;
                        }

                        // Fire:
                        if(this.entityCreature.canBurn()) {
                            if (pathnodetype == PathNodeType.DAMAGE_FIRE || pathnodetype == PathNodeType.DANGER_FIRE || pathnodetype == PathNodeType.DAMAGE_OTHER) {
                                return false;
                            }
                        }
                    }
                }
            }

            return true;
        }
    }

    /** Returns true if the position is clear. **/
    private boolean isPositionClear(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3d startVec, double distanceX, double distanceZ) {
        for (BlockPos blockpos : BlockPos.getAllInBox(new BlockPos(x, y, z), new BlockPos(x + sizeX - 1, y + sizeY - 1, z + sizeZ - 1))) {
            double d0 = (double)blockpos.getX() + 0.5D - startVec.x;
            double d1 = (double)blockpos.getZ() + 0.5D - startVec.z;

            if (d0 * distanceX + d1 * distanceZ >= 0.0D) {
                Block block = this.world.getBlockState(blockpos).getBlock();

                if (!block.isPassable(this.world, blockpos)) {
                    return false;
                }
            }
        }

        return true;
    }


    // ==================== Path Edits ====================
    /** Trims path data from the end to the first sun covered block. **/
    @Override
    protected void removeSunnyPath() {
        super.removeSunnyPath();

        for(int i = 0; i < this.currentPath.getCurrentPathLength(); ++i) {
            PathPoint pathpoint = this.currentPath.getPathPointFromIndex(i);
            PathPoint pathpoint1 = i + 1 < this.currentPath.getCurrentPathLength() ? this.currentPath.getPathPointFromIndex(i + 1) : null;
            IBlockState iblockstate = this.world.getBlockState(new BlockPos(pathpoint.x, pathpoint.y, pathpoint.z));
            Block block = iblockstate.getBlock();

            if (block == Blocks.CAULDRON) {
                this.currentPath.setPoint(i, pathpoint.cloneMove(pathpoint.x, pathpoint.y + 1, pathpoint.z));

                if(pathpoint1 != null && pathpoint.y >= pathpoint1.y) {
                    this.currentPath.setPoint(i + 1, pathpoint1.cloneMove(pathpoint1.x, pathpoint.y + 1, pathpoint1.z));
                }
            }
        }

        if(this.entityCreature.daylightBurns()) {
            if (this.world.canSeeSky(new BlockPos(MathHelper.floor(this.entity.posX), (int)(this.entity.getEntityBoundingBox().minY + 0.5D), MathHelper.floor(this.entity.posZ)))) {
                return;
            }

            for(int j = 0; j < this.currentPath.getCurrentPathLength(); ++j) {
                PathPoint pathpoint2 = this.currentPath.getPathPointFromIndex(j);

                if(this.world.canSeeSky(new BlockPos(pathpoint2.x, pathpoint2.y, pathpoint2.z))) {
                    this.currentPath.setCurrentPathLength(j - 1);
                    return;
                }
            }
        }
    }


    // ==================== Pathing ====================
    /** Follows the path moving to the next index when needed, etc. **/
    @Override
    protected void pathFollow() {
        // Flight:
        if(this.entityCreature.isFlying() || this.entityCreature.isInWater()) {
            Vec3d entityVector = this.getEntityPosition();
            float entitySize = this.entity.width * this.entity.width;

            if (entityVector.squareDistanceTo(this.currentPath.getVectorFromIndex(this.entity, this.currentPath.getCurrentPathIndex())) < (double) entitySize) {
                this.currentPath.incrementPathIndex();
            }

            int pathIndexRange = 6;
            for (int pathIndex = Math.min(this.currentPath.getCurrentPathIndex() + pathIndexRange, this.currentPath.getCurrentPathLength() - 1); pathIndex > this.currentPath.getCurrentPathIndex(); --pathIndex) {
                Vec3d pathVector = this.currentPath.getVectorFromIndex(this.entity, pathIndex);

                if (pathVector.squareDistanceTo(entityVector) <= 36.0D && this.isDirectPathBetweenPoints(entityVector, pathVector, 0, 0, 0)) {
                    this.currentPath.setCurrentPathIndex(pathIndex);
                    break;
                }
            }

            this.checkForStuck(entityVector);
            return;
        }

        // Walking:
        super.pathFollow();
    }

    /** Called on entity update to update the navigation progress. **/
    @Override
    public void onUpdateNavigation() {
        if (!this.noPath() || !this.entityCreature.canClimb()) {
            super.onUpdateNavigation();
        }
        else {
            if (this.targetPosition != null) {
                double d0 = (double)(this.entity.width * this.entity.width);

                if (this.entity.getDistanceSqToCenter(this.targetPosition) >= d0 && (this.entity.posY <= (double)this.targetPosition.getY() || this.entity.getDistanceSqToCenter(new BlockPos(this.targetPosition.getX(), MathHelper.floor(this.entity.posY), this.targetPosition.getZ())) >= d0)) {
                    this.entity.getMoveHelper().setMoveTo((double)this.targetPosition.getX(), (double)this.targetPosition.getY(), (double)this.targetPosition.getZ(), this.speed);
                }
                else {
                    this.targetPosition = null;
                }
            }
        }
    }
}
