package lycanite.lycanitesmobs.swampmobs.block;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockPoisonCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockPoisonCloud() {
		super(Material.plants);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));

        // Properties:
		this.group = SwampMobs.group;
		this.blockName = "poisoncloud";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Poison Clouds", true) ? 200 : 1;
		this.removeOnTick = true;
		this.loopTicks = false;
		this.canBeCrushed = true;
		
		this.noEntityCollision = true;
		this.noBreakCollision = true;
		this.isOpaque = false;
		
		this.setBlockUnbreakable();
		this.setLightOpacity(1);
	}


    // ==================================================
    //                   Block States
    // ==================================================
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
        return new BlockStateContainer(this, AGE);
    }


	// ==================================================
	//                     Break
	// ==================================================
	@Override
	public Item getItemDropped(IBlockState blockState, Random random, int fortune) {
		return ObjectManager.getItem("poisongland");
	}
	
	@Override
	public int damageDropped(IBlockState blockState) {
		return 0;
	}
    
	@Override
	public int quantityDropped(Random random) {
        return 1;
    }
    
    
	// ==================================================
	//                Collision Effects
	// ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity) {
		super.onEntityCollidedWithBlock(world, pos, entity);
		if(entity instanceof EntityLivingBase) {
			((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.poison, 5 * 20, 0));
		}
	}
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState blockState, World world, BlockPos pos, Random random) {
        int x = pos.getX();
        int y = pos.getX();
        int z = pos.getX();
    	if(random.nextInt(24) == 0)
        	world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("poisoncloud"), SoundCategory.AMBIENT, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        int l;
        float f; 
        float f1;
        float f2;

        for(l = 0; l < 12; ++l) {
            f = (float)x + random.nextFloat();
            f1 = (float)y + random.nextFloat() * 0.5F;
            f2 = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.PORTAL, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
            world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)f, (double)f1, (double)f2, 0.0D, 0.0D, 0.0D);
        }
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
