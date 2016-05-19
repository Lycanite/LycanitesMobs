package lycanite.lycanitesmobs.junglemobs.block;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.junglemobs.JungleMobs;
import lycanite.lycanitesmobs.junglemobs.entity.EntityVespidQueen;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPropolis extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPropolis() {
		super(Material.clay);
        this.setCreativeTab(LycanitesMobs.itemsTab);
		
		// Properties:
		this.group = JungleMobs.group;
		this.blockName = "propolis";
		this.setup();
		
		// Stats:
		this.setHardness(0.6F);
		this.setHarvestLevel("shovel", 0);
		this.setStepSound(SoundType.GROUND);
		this.tickRate = 100;
		this.removeOnTick = true;
	}


    // ==================================================
    //                   Block States
    // ==================================================
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BlockVeswax.HIVE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockVeswax.HIVE);
    }

    @Override
    public BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockVeswax.HIVE);
    }
    
    
	// ==================================================
	//                   Placement
	// ==================================================
    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack itemStack) {
        int orientationMeta = BlockPistonBase.getFacingFromEntity(pos, placer).getIndex();
        orientationMeta += 8;
        world.setBlockState(pos, state.withProperty(BlockVeswax.HIVE, orientationMeta), 2);
        super.onBlockPlacedBy(world, pos, state, placer, itemStack);
    }
    
    
	// ==================================================
	//                     Ticking
	// ==================================================
    // ========== Tick Rate ==========
    @Override
    public int tickRate(World world) {
    	return this.tickRate + world.rand.nextInt(100);
    }

    // ========== Tick Update ==========
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
		if(world.isRemote)
			return;
		if(this.getMetaFromState(state) >= 8)
			return;
		double range = 32D;
		if(!world.getEntitiesWithinAABB(EntityVespidQueen.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range)).isEmpty())
			return;
        super.updateTick(world, pos, state, random);
    }
}
