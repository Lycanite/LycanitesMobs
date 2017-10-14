package com.lycanitesmobs.arcticmobs.block;

import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.block.BlockBase;
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

public class BlockFrostCloud extends BlockBase {
	
	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockFrostCloud() {
		super(Material.PLANTS);
        this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
		
		// Properties:
		this.group = ArcticMobs.instance.group;
		this.blockName = "frostcloud";
		this.setup();
		
		// Stats:
		this.tickRate = ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Frostclouds", true) ? 200 : 1;
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
        return ObjectManager.getItem("frostyfur");
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
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);
        if(entity instanceof EntityLivingBase) {
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 5 * 20, 0));
            ((EntityLivingBase)entity).addPotionEffect(new PotionEffect(MobEffects.HUNGER, 2 * 20, 0));
        }
    }
    
    
	// ==================================================
	//                      Particles
	// ==================================================
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState state, World world, BlockPos pos, Random random) {
        super.randomDisplayTick(state, world, pos, random);

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        if(random.nextInt(24) == 0)
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("frostcloud"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
            world.spawnParticle(EnumParticleTypes.CLOUD, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
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
