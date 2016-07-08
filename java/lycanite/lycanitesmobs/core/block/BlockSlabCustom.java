package lycanite.lycanitesmobs.core.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class BlockSlabCustom extends BlockSlab {
    public static final PropertyEnum<Variant> VARIANT = PropertyEnum.<Variant>create("variant", Variant.class);
    public static enum Variant implements IStringSerializable {
        DEFAULT;
        public String getName() {
            return "default";
        }
    }

    protected Block doubleBlock;

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockSlabCustom(BlockBase block, Block doubleBlock) {
		super(block.getDefaultState().getMaterial());
        this.doubleBlock = doubleBlock;
        this.setDefaultState(this.blockState.getBaseState().withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM).withProperty(VARIANT, Variant.DEFAULT));
        String slabName = "_slab";
        this.setRegistryName(new ResourceLocation(block.group.filename, block.blockName + slabName));
        this.setUnlocalizedName(block.blockName + slabName);
        block.copyAttributesTo(this);
	}

    @Override
    public String getUnlocalizedName(int meta) {
        return super.getUnlocalizedName();
    }

    @Override
    public boolean isDouble() {
        return false; // Double slabs are defined as BlockSlabDouble.
    }

    public Block getDoubleBlock() {
        return this.doubleBlock;
    }


    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, new IProperty[] {HALF, VARIANT});
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(HALF, meta == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.BOTTOM)
            return 0;
        return 1;
    }

    @Override
    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    @Override
    public Comparable<?> getTypeForItem(ItemStack stack) {
        return Variant.DEFAULT;
    }


    // ==================================================
    //                    Harvesting
    // ==================================================
    @Nullable
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this);
    }

    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
        return new ItemStack(this);
    }
}
