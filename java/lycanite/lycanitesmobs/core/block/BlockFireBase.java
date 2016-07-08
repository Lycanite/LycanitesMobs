package lycanite.lycanitesmobs.core.block;


import lycanite.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTNT;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockFireBase extends BlockBase {
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    public static final PropertyBool UPPER = PropertyBool.create("up");

    public boolean dieInRain = true;
    public boolean triggerTNT = true;
    public boolean tickRandomly = true;
    public int agingRate = 3;
    public float spreadChance = 1;

    // ==================================================
    //                   Constructor
    // ==================================================
    public BlockFireBase(Material material, GroupInfo group, String name) {
        super(material, group, name);

        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, Integer.valueOf(0)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)).withProperty(UPPER, Boolean.valueOf(false)));
        this.removeOnTick = false;
        this.loopTicks = true;
        this.canBeCrushed = true;

        this.noEntityCollision = true;
        this.noBreakCollision = false;
        this.isOpaque = false;

        this.tickRate = 30; // Default tick rate, configs can set this to 1 to remove this fire block from worlds.
        this.setLightOpacity(1);
        this.setTickRandomly(this.tickRandomly);
        this.setSoundType(SoundType.CLOTH);
        this.disableStats();
    }


    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        if (!worldIn.getBlockState(pos.down()).isSideSolid(worldIn, pos.down(), EnumFacing.UP) && !Blocks.FIRE.canCatchFire(worldIn, pos.down(), EnumFacing.UP)) {
            return state.withProperty(NORTH, this.canCatchFire(worldIn, pos.north(), EnumFacing.SOUTH))
                    .withProperty(EAST,  this.canCatchFire(worldIn, pos.east(), EnumFacing.WEST))
                    .withProperty(SOUTH, this.canCatchFire(worldIn, pos.south(), EnumFacing.NORTH))
                    .withProperty(WEST,  this.canCatchFire(worldIn, pos.west(), EnumFacing.EAST))
                    .withProperty(UPPER, this.canCatchFire(worldIn, pos.up(), EnumFacing.DOWN));
        }
        return this.getDefaultState();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(AGE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AGE);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {AGE, NORTH, EAST, SOUTH, WEST, UPPER});
    }



    // ==================================================
    //                     Ticking
    // ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(World world) {
        return this.tickRate;
    }

    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        if (!world.getGameRules().getBoolean("doFireTick"))
            return;

        // Prevent Self Replacement:
        if (!this.canPlaceBlockAt(world, pos) || this.removeOnTick)
            world.setBlockToAir(pos);

        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        boolean isOnFireSource = this.isBlockFireSource(blockBelow, world, pos.down(), EnumFacing.UP);
        int age = ((Integer)state.getValue(AGE)).intValue();

        // Environmental Extinguish:
        if (!isOnFireSource && this.canDie(world, pos) && rand.nextFloat() < 0.2F + (float)age * 0.03F) {
            world.setBlockToAir(pos);
            return;
        }

        // Increase Age:
        if (age < 15) {
            state = state.withProperty(AGE, Math.max(age + Math.round((float)rand.nextInt(this.agingRate) / 2), 15));
            world.setBlockState(pos, state, 4);
        }

        // Schedule Next Update:
        if(this.loopTicks)
            world.scheduleUpdate(pos, this, this.tickRate(world) + rand.nextInt(10));

        // Natural Extinguish:
        if (!isOnFireSource) {
            // Can't spread, old or on none solid surface:
            if (!this.canNeighborCatchFire(world, pos)) {
                if (!world.getBlockState(pos.down()).isSideSolid(world, pos.down(), EnumFacing.UP) || age > 3) {
                    world.setBlockToAir(pos);
                }
                return;
            }

            // End of life and can't spread below:
            if (!this.canCatchFire(world, pos.down(), EnumFacing.UP) && age == 15 && rand.nextInt(4) == 0) {
                world.setBlockToAir(pos);
                return;
            }
        }

        // Spread Fire:
        if(this.spreadChance <= 0)
            return;
        boolean highHumidity = world.isBlockinHighHumidity(pos);
        int humidityChance = 0;
        if (highHumidity)
            humidityChance = -50;
        this.tryCatchFire(world, pos.east(), 300 + humidityChance, rand, age, EnumFacing.WEST);
        this.tryCatchFire(world, pos.west(), 300 + humidityChance, rand, age, EnumFacing.EAST);
        this.tryCatchFire(world, pos.down(), 250 + humidityChance, rand, age, EnumFacing.UP);
        this.tryCatchFire(world, pos.up(), 250 + humidityChance, rand, age, EnumFacing.DOWN);
        this.tryCatchFire(world, pos.north(), 300 + humidityChance, rand, age, EnumFacing.SOUTH);
        this.tryCatchFire(world, pos.south(), 300 + humidityChance, rand, age, EnumFacing.NORTH);

        // Mass Spreading:
        for (int offsetX = -1; offsetX <= 1; ++offsetX) {
            for (int offsetZ = -1; offsetZ <= 1; ++offsetZ) {
                for (int offsetY = -1; offsetY <= 4; ++offsetY) {
                    if (offsetX != 0 || offsetY != 0 || offsetZ != 0) {
                        int chance = 100;
                        // Heat Rises:
                        if (offsetY > 1)
                            chance += (offsetY - 1) * 100;

                        BlockPos spreadPos = pos.add(offsetX, offsetY, offsetZ);
                        int spreadEncouragement = this.getNeighborEncouragement(world, spreadPos);
                        if (spreadEncouragement > 0) {
                            int spreadFlammability = (spreadEncouragement + 40 + world.getDifficulty().getDifficultyId() * 7) / (age + 30);
                            if (highHumidity)
                                spreadFlammability /= 2;

                            if (spreadFlammability > 0 && rand.nextInt(chance) <= spreadFlammability && (!world.isRaining() || !this.canDie(world, spreadPos))) {
                                int spreadAge = age + rand.nextInt(5) / 4;
                                if (spreadAge > 15)
                                    spreadAge = 15;
                                world.setBlockState(spreadPos, state.withProperty(AGE, Integer.valueOf(spreadAge)), 3);
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean requiresUpdates() {
        return !this.tickRandomly;
    }


    // ==================================================
    //                  Block Placement
    // ==================================================
    /** Returns true if this block can place another block at the specified location. **/
    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP) || this.canNeighborCatchFire(world, pos);
    }

    /** Called when an adjacent block changes. **/
    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        if (!world.getBlockState(pos.down()).isSideSolid(world, pos, EnumFacing.UP) && !this.canNeighborCatchFire(world, pos)) {
            world.setBlockToAir(pos);
        }
    }

    /** Called when this block is added to the world. **/
    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
    }


    // ==================================================
    //                       Fire
    // ==================================================

    /** Returns true if any adjacent blocks can catch fire. **/
    protected boolean canNeighborCatchFire(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (this.canCatchFire(worldIn, pos.offset(enumfacing), enumfacing.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    /** Gets the flammability of nearby blocks, highly flammable blocks that are near each other will help spread fire faster. **/
    protected int getNeighborEncouragement(World worldIn, BlockPos pos) {
        if (!worldIn.isAirBlock(pos))
            return 0;
        else {
            int i = 0;
            for (EnumFacing enumfacing : EnumFacing.values()) {
                i = Math.max(worldIn.getBlockState(pos.offset(enumfacing)).getBlock().getFlammability(worldIn, pos.offset(enumfacing), enumfacing.getOpposite()), i);
            }
            return i;
        }
    }

    /** Attempts to ignite the position. **/
    private void tryCatchFire(World world, BlockPos pos, int chance, Random random, int age, EnumFacing face) {
        int flammability = this.getBlockFlammability(world, pos, face);
        if (Math.round(random.nextInt(chance) / this.spreadChance) < flammability) {
            IBlockState blockState = world.getBlockState(pos);

            if (random.nextInt(age + 10) < 5 && !world.isRainingAt(pos)) {
                int newFireAge = age + random.nextInt(5) / 4;
                if (newFireAge > 15)
                    newFireAge = 15;
                world.setBlockState(pos, this.getDefaultState().withProperty(AGE, Integer.valueOf(newFireAge)), 3);
            }
            else {
                world.setBlockToAir(pos);
            }

            if (this.triggerTNT && blockState.getBlock() == Blocks.TNT) {
                Blocks.TNT.onBlockDestroyedByPlayer(world, pos, blockState.withProperty(BlockTNT.EXPLODE, Boolean.valueOf(true)));
            }
        }
    }

    /** Returns true if the block at the provided position and face can catch fire. **/
    public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return world.getBlockState(pos).getBlock().isFlammable(world, pos, face);
    }

    /** Checks if the provided block is a fire source, can be overridden for custom sources. **/
    public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
        return block.isFireSource(world, pos, side);
    }

    /** Returns how flammable the target block is. **/
    public int getBlockFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        return world.getBlockState(pos).getBlock().getFlammability(world, pos, face);
    }

    /** Returns true if this fire block should be extinguished, can check for rain and position, etc. **/
    protected boolean canDie(World world, BlockPos pos) {
        return world.isRainingAt(pos) || world.isRainingAt(pos.west()) || world.isRainingAt(pos.east()) || world.isRainingAt(pos.north()) || world.isRainingAt(pos.south());
    }


    // ==================================================
    //                      On Break
    // ==================================================
    public int quantityDropped(Random random) {
        return 0;
    }


    // ==================================================
    //                      Rendering
    // ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }
}
