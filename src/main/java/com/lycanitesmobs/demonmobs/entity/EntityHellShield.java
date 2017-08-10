package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityHellShield extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityHellShield(World world) {
        super(world);
    }

    public EntityHellShield(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityHellShield(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "hellshield";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(0);
    	this.setProjectileScale(1F);
        this.knockbackChance = 0D;
        this.noClip = true;
    }
	
    
    // ==================================================
 	//                   Update
 	// ==================================================
    @Override
    public void onUpdate() {
    	super.onUpdate();
    	if(this.posY > this.getEntityWorld().getHeight() + 20)
    		this.setDead();
    }
	
    
    // ==================================================
 	//                   Movement
 	// ==================================================
    // ========== Gravity ==========
    @Override
    protected float getGravityVelocity() {
        return 0F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.REDSTONE, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }

    @Override
    public boolean canDamage(EntityLivingBase entity) {
        return false;
    }


    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return null;
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
