package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityAquaPulse extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityAquaPulse(World world) {
        super(world);
    }

    public EntityAquaPulse(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityAquaPulse(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "aquapulse";
    	this.group = ElementalMobs.instance.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(4F);
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(ObjectManager.getPotionEffect("Penetration") != null)
            entityLiving.addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("Penetration"), this.getEffectDuration(8), 2));
        return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
    	return true;
    }

    public boolean canDestroyBlockSub(BlockPos pos) {
        Block block = this.getEntityWorld().getBlockState(pos).getBlock();
    	if(block == Blocks.SNOW_LAYER)
    		return true;
    	if(block == Blocks.TALLGRASS)
    		return true;
    	if(block == Blocks.FIRE)
    		return true;
    	if(block == Blocks.WEB)
    		return true;
    	if(ObjectManager.getBlock("PoisonCloud") != null && block == ObjectManager.getBlock("PoisonCloud"))
    		return true;
        if(ObjectManager.getBlock("PoopCloud") != null && block == ObjectManager.getBlock("PoopCloud"))
            return true;
    	if(ObjectManager.getBlock("FrostCloud") != null && block == ObjectManager.getBlock("FrostCloud"))
    		return true;
    	if(ObjectManager.getBlock("Frostweb") != null && block == ObjectManager.getBlock("Frostweb"))
    		return true;
    	if(ObjectManager.getBlock("QuickWeb") != null && block == ObjectManager.getBlock("QuickWeb"))
    		return true;
    	if(ObjectManager.getBlock("Hellfire") != null && block == ObjectManager.getBlock("Hellfire"))
    		return true;
        if(ObjectManager.getBlock("Frostfire") != null && block == ObjectManager.getBlock("Frostfire"))
            return true;
    	if(ObjectManager.getBlock("Icefire") != null && block == ObjectManager.getBlock("Icefire"))
    		return true;
        if(ObjectManager.getBlock("Scorchfire") != null && block == ObjectManager.getBlock("Scorchfire"))
            return true;
   	 	return super.canDestroyBlock(pos);
    }
    
    //========== Place Block ==========
    @Override
    public void placeBlock(World world, BlockPos pos) {
        IBlockState placedBlockBig = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 4);
        IBlockState placedBlock = Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL, 5);
        if(this.canDestroyBlockSub(pos))
            world.setBlockState(pos, placedBlockBig, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() + 1, pos.getY(), pos.getZ()), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ())))
            world.setBlockState(new BlockPos(pos.getX() - 1, pos.getY(), pos.getZ()), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() + 1), placedBlock, 3);
        if(this.canDestroyBlockSub(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1)))
            world.setBlockState(new BlockPos(pos.getX(), pos.getY(), pos.getZ() - 1), placedBlock, 3);
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    	}
    }


    // ==================================================
    //                      Visuals
    // ==================================================
    @Override
    public String getTextureName() {
        return this.entityName.toLowerCase() + "charge";
    }
}
