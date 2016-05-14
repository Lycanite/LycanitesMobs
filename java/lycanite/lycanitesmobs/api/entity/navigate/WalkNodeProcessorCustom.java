package lycanite.lycanitesmobs.api.entity.navigate;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.pathfinding.WalkNodeProcessor;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import java.util.EnumSet;

public class WalkNodeProcessorCustom extends WalkNodeProcessor {

    @Override
    public PathNodeType func_186319_a(IBlockAccess world, int x, int y, int z, EntityLiving entity, int xRange, int yRange, int zRange, boolean breakDoors, boolean enterDoors) {
        EnumSet<PathNodeType> pathNodeTypes = EnumSet.<PathNodeType>noneOf(PathNodeType.class);
        PathNodeType pathNodeType = PathNodeType.BLOCKED;
        double entityRadius = (double)entity.width / 2.0D;
        BlockPos blockpos = new BlockPos(entity);

        for (int i = x; i < x + xRange; ++i) {
            for (int j = y; j < y + yRange; ++j) {
                for (int k = z; k < z + zRange; ++k) {
                    PathNodeType testPathNodeType = getPathNodeType(world, i, j, k);

                    if (testPathNodeType == PathNodeType.DOOR_WOOD_CLOSED && breakDoors && enterDoors) {
                        testPathNodeType = PathNodeType.WALKABLE;
                    }

                    if (testPathNodeType == PathNodeType.DOOR_OPEN && !enterDoors) {
                        testPathNodeType = PathNodeType.BLOCKED;
                    }

                    if (testPathNodeType == PathNodeType.RAIL && !(world.getBlockState(blockpos).getBlock() instanceof BlockRailBase) && !(world.getBlockState(blockpos.down()).getBlock() instanceof BlockRailBase)) {
                        testPathNodeType = PathNodeType.FENCE;
                    }

                    if (i == x && j == y && k == z) {
                        pathNodeType = testPathNodeType;
                    }

                    if (j > y && testPathNodeType != PathNodeType.OPEN) {
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB((double)i - entityRadius + 0.5D, (double)y + 0.001D, (double)k - entityRadius + 0.5D, (double)i + entityRadius + 0.5D, (double)((float)y + entity.height), (double)k + entityRadius + 0.5D);

                        if (!entity.worldObj.collidesWithAnyBlock(axisalignedbb)) {
                            testPathNodeType = PathNodeType.OPEN;
                        }
                    }

                    pathNodeTypes.add(testPathNodeType);
                }
            }
        }

        if (pathNodeTypes.contains(PathNodeType.FENCE)) {
            return PathNodeType.FENCE;
        }
        else {
            PathNodeType outputPathNodeType = PathNodeType.BLOCKED;

            for (PathNodeType testPathNodeType : pathNodeTypes) {
                if (entity.getPathPriority(testPathNodeType) < 0.0F) {
                    return testPathNodeType;
                }

                if (entity.getPathPriority(testPathNodeType) >= entity.getPathPriority(outputPathNodeType)) {
                    outputPathNodeType = testPathNodeType;
                }
            }

            if (pathNodeType == PathNodeType.OPEN && entity.getPathPriority(outputPathNodeType) == 0.0F) {
                return PathNodeType.OPEN;
            }
            else {
                return outputPathNodeType;
            }
        }
    }

    // ========== Get Path Node Type ==========
    public static PathNodeType getPathNodeType(IBlockAccess world, int x, int y, int z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        IBlockState blockState = world.getBlockState(blockPos);
        Block block = blockState.getBlock();
        Material material = blockState.getMaterial();
        PathNodeType pathNodeType = PathNodeType.BLOCKED;

        if (block != Blocks.trapdoor && block != Blocks.iron_trapdoor && block != Blocks.waterlily) {
            if (block == Blocks.fire) {
                return PathNodeType.DAMAGE_FIRE;
            }
            else if (block == Blocks.cactus) {
                return PathNodeType.DAMAGE_CACTUS;
            }
            else if (block instanceof BlockDoor && material == Material.wood && !((Boolean)blockState.getValue(BlockDoor.OPEN)).booleanValue()) {
                return PathNodeType.DOOR_WOOD_CLOSED;
            }
            else if (block instanceof BlockDoor && material == Material.iron && !((Boolean)blockState.getValue(BlockDoor.OPEN)).booleanValue()) {
                return PathNodeType.DOOR_IRON_CLOSED;
            }
            else if (block instanceof BlockDoor && ((Boolean)blockState.getValue(BlockDoor.OPEN)).booleanValue()) {
                return PathNodeType.DOOR_OPEN;
            }
            else if (block instanceof BlockRailBase) {
                return PathNodeType.RAIL;
            }
            else if (!(block instanceof BlockFence) && !(block instanceof BlockWall) && (!(block instanceof BlockFenceGate) || ((Boolean)blockState.getValue(BlockFenceGate.OPEN)).booleanValue())) {
                if (material == Material.air) {
                    pathNodeType = PathNodeType.OPEN;
                }
                else {
                    if (material == Material.water) {
                        return PathNodeType.WATER;
                    }

                    if (material == Material.lava) {
                        return PathNodeType.LAVA;
                    }
                }

                if (block.isPassable(world, blockPos) && pathNodeType == PathNodeType.BLOCKED) {
                    pathNodeType = PathNodeType.OPEN;
                }

                if (pathNodeType == PathNodeType.OPEN && y >= 1) {
                    PathNodeType pathnodetype1 = getPathNodeType(world, x, y - 1, z);
                    pathNodeType = pathnodetype1 != PathNodeType.WALKABLE && pathnodetype1 != PathNodeType.OPEN && pathnodetype1 != PathNodeType.WATER && pathnodetype1 != PathNodeType.LAVA ? PathNodeType.WALKABLE : PathNodeType.OPEN;
                }

                if (pathNodeType == PathNodeType.WALKABLE) {
                    for (int j = x - 1; j <= x + 1; ++j) {
                        for (int i = z - 1; i <= z + 1; ++i) {
                            if (j != x || i != z) {
                                Block block1 = world.getBlockState(new BlockPos(j, y, i)).getBlock();

                                if (block1 == Blocks.cactus) {
                                    pathNodeType = PathNodeType.DANGER_CACTUS;
                                }
                                else if (block1 == Blocks.fire) {
                                    pathNodeType = PathNodeType.DANGER_FIRE;
                                }
                            }
                        }
                    }
                }

                return pathNodeType;
            }
            else {
                return PathNodeType.FENCE;
            }
        }
        else {
            return PathNodeType.TRAPDOOR;
        }
    }
}
