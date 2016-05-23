package lycanite.lycanitesmobs.api.block;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockPillar extends BlockBase {
    public static final PropertyEnum<EnumFacing.Axis> AXIS = PropertyEnum.<EnumFacing.Axis>create("axis", EnumFacing.Axis.class);

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPillar(Material material, GroupInfo group, String name) {
		super(material, group, name);
	}



    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {AXIS});
    }

    @Override
    protected ItemStack createStackedBlock(IBlockState state) {
        return new ItemStack(Item.getItemFromBlock(this));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing.Axis enumfacing$axis = EnumFacing.Axis.Y;
        int i = meta & 12;

        if (i == 4)
            enumfacing$axis = EnumFacing.Axis.X;
        else if (i == 8)
            enumfacing$axis = EnumFacing.Axis.Z;

        return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        EnumFacing.Axis enumfacing$axis = (EnumFacing.Axis)state.getValue(AXIS);

        if (enumfacing$axis == EnumFacing.Axis.X)
            i |= 4;
        else if (enumfacing$axis == EnumFacing.Axis.Z)
            i |= 8;

        return i;
    }


    // ==================================================
    //                    Placement
    // ==================================================
    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
    }


    // ==================================================
    //                     Rotation
    // ==================================================
    @Override
    public boolean rotateBlock(net.minecraft.world.World world, BlockPos pos, EnumFacing axis) {
        net.minecraft.block.state.IBlockState state = world.getBlockState(pos);
        for (net.minecraft.block.properties.IProperty<?> prop : state.getProperties().keySet()) {
            if (prop.getName().equals("axis")) {
                world.setBlockState(pos, state.cycleProperty(prop));
                return true;
            }
        }
        return false;
    }

    /** Returns a blockstate for the provided rotation. **/
    @Override
    public IBlockState withRotation(IBlockState state, Rotation rot) {
        switch (rot) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:

                switch ((EnumFacing.Axis)state.getValue(AXIS)) {
                    case X:
                        return state.withProperty(AXIS, EnumFacing.Axis.Z);
                    case Z:
                        return state.withProperty(AXIS, EnumFacing.Axis.X);
                    default:
                        return state;
                }

            default:
                return state;
        }
    }
}
