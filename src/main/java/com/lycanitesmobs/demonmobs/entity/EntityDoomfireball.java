package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityDoomfireball extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityDoomfireball(World world) {
        super(world);
    }

    public EntityDoomfireball(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityDoomfireball(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "doomfire";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(1F);
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
    	if(!entityLiving.isImmuneToFire())
    		entityLiving.setFire(this.getEffectDuration(5) / 20);
    	return true;
    }
    
    //========== Can Destroy Block ==========
    @Override
    public boolean canDestroyBlock(BlockPos pos) {
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
        Block block = ObjectManager.getBlock("doomfire");
        if(block == null)
            block = Blocks.FIRE;
	   	 world.setBlockState(pos, block.getDefaultState());
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound("doomfireball");
    }
    
    
    // ==================================================
    //                   Brightness
    // ==================================================
    public float getBrightness(float par1) {
        return 1.0F;
    }
    
    @SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float par1) {
        return 15728880;
    }
}
