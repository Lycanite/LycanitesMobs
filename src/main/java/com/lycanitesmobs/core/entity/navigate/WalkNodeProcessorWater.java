package com.lycanitesmobs.core.entity.navigate;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;

import java.util.Set;

public class WalkNodeProcessorWater extends WalkNodeProcessorCustom {

    @Override
    public void initProcessor(IBlockAccess sourceIn, EntityLiving mob) {
        super.initProcessor(sourceIn, mob);
    }

    @Override
    public PathPoint getStart() {
        int i;

        if (this.entity.onGround || this.entity.isInWater()) {
            i = MathHelper.floor(this.entity.getEntityBoundingBox().minY + 0.5D);
        }
        else {
            BlockPos blockpos;

            for (blockpos = new BlockPos(this.entity); (this.blockaccess.getBlockState(blockpos).getMaterial() == Material.AIR || this.blockaccess.getBlockState(blockpos).getBlock().isPassable(this.blockaccess, blockpos)) && blockpos.getY() > 0; blockpos = blockpos.down())
            {
                ;
            }

            i = blockpos.up().getY();
        }

        BlockPos blockpos2 = new BlockPos(this.entity);
        PathNodeType pathnodetype1 = this.getPathNodeType(this.entity.getEntityWorld(), blockpos2.getX(), i, blockpos2.getZ());

        if (this.entity.getPathPriority(pathnodetype1) < 0.0F) {
            Set<BlockPos> set = Sets.<BlockPos>newHashSet();
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().minX, (double)i, this.entity.getEntityBoundingBox().maxZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().minZ));
            set.add(new BlockPos(this.entity.getEntityBoundingBox().maxX, (double)i, this.entity.getEntityBoundingBox().maxZ));

            for (BlockPos pos : set) {
                PathNodeType pathnodetype = this.getPathNodeType(this.entity.getEntityWorld(), pos.getX(), pos.getY(), pos.getZ());

                if (this.entity.getPathPriority(pathnodetype) >= 0.0F) {
                    return this.openPoint(pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }

        return this.openPoint(blockpos2.getX(), i, blockpos2.getZ());
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z, EntityLiving entitylivingIn, int xSize, int ySize, int zSize, boolean canBreakDoorsIn, boolean canEnterDoorsIn) {
        if(this.entity == null || this.entity.isInWater())
            return PathNodeType.WATER;
        else
            return super.getPathNodeType(blockaccessIn, x, y, z, entitylivingIn, xSize, ySize, zSize, canBreakDoorsIn, canEnterDoorsIn);
    }

    @Override
    public PathNodeType getPathNodeType(IBlockAccess blockaccessIn, int x, int y, int z) {
        if(this.entity == null || this.entity.isInWater())
            return PathNodeType.WATER;
        else
            return super.getPathNodeType(blockaccessIn, x, y, z);
    }
}
