package com.lycanitesmobs.arcticmobs.block;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.arcticmobs.ArcticMobs;
import com.lycanitesmobs.core.block.BlockFireBase;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockIcefire extends BlockFireBase {

	// ==================================================
	//                   Constructor
	// ==================================================
	public BlockIcefire() {
		super(Material.FIRE, ArcticMobs.instance.group, "icefire");

        // Stats:
        this.tickRate = 30;
        this.dieInRain = false;
        this.triggerTNT = false;
        this.agingRate = 8;
        this.spreadChance = 2;
        this.removeOnTick = !ConfigBase.getConfig(this.group, "general").getBool("Features", "Enable Icefire", true);

        this.setLightOpacity(1);
        this.setLightLevel(0);
	}


    // ==================================================
    //                       Break
    // ==================================================
    @Override
    public Item getItemDropped(IBlockState state, Random random, int zero) {
        return ObjectManager.getItem("icefirecharge");
    }


    // ==================================================
    //                        Fire
    // ==================================================
    @Override
    public boolean canCatchFire(IBlockAccess world, BlockPos pos, EnumFacing face) {
        Block block = world.getBlockState(pos).getBlock();
        if(block ==  Blocks.ICE)
            return true;
        return false;
    }

    @Override
    public boolean isBlockFireSource(Block block, World world, BlockPos pos, EnumFacing side) {
        if(block == Blocks.SNOW)
            return true;
        return false;
    }

    @Override
    public int getBlockFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        Block block = world.getBlockState(pos).getBlock();
        if(block ==  Blocks.ICE)
            return 20;
        return 0;
    }

    @Override
    protected boolean canDie(World world, BlockPos pos) {
        return false;
    }

	@Override
	public void burnBlockReplace(World world, BlockPos pos, int newFireAge) {
		if(world.getBlockState(pos).getBlock() == Blocks.ICE) {
			world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
			return;
		}
		super.burnBlockReplace(world, pos, newFireAge);
	}

    @Override
    public void burnBlockDestroy(World world, BlockPos pos) {
	    if(world.getBlockState(pos).getBlock() == Blocks.ICE) {
	        world.setBlockState(pos, Blocks.PACKED_ICE.getDefaultState(), 3);
	        return;
        }
        super.burnBlockDestroy(world, pos);
    }


    // ==================================================
    //                Collision Effects
    // ==================================================
    @Override
    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        super.onEntityCollidedWithBlock(world, pos, state, entity);

        if(entity instanceof EntityItem) // Icefire shouldn't destroy items.
            return;

        if(entity.isBurning())
            entity.extinguish();

        PotionEffect effectSlowness = new PotionEffect(MobEffects.SLOWNESS, 5 * 20, 0);
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            if(!entityCreature.creatureInfo.element.canFreeze())
                return;
            entityCreature.addPotionEffect(effectSlowness);
        }
        if(entity instanceof EntityLivingBase) {
            EntityLivingBase entityLiving = (EntityLivingBase)entity;
            entityLiving.addPotionEffect(effectSlowness);
        }

        entity.attackEntityFrom(DamageSource.MAGIC, 2);
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
            world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), AssetManager.getSound("icefire"), SoundCategory.BLOCKS, 0.5F + random.nextFloat(), random.nextFloat() * 0.7F + 0.3F, false);

        for(int particleCount = 0; particleCount < 12; ++particleCount) {
            float particleX = (float)x + random.nextFloat();
            float particleY = (float)y + random.nextFloat() * 0.5F;
            float particleZ = (float)z + random.nextFloat();
            world.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, (double)particleX, (double)particleY, (double)particleZ, 0.0D, 0.0D, 0.0D, new int[0]);
        }
    }
}