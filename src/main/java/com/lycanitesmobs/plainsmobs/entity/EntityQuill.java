package com.lycanitesmobs.plainsmobs.entity;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.plainsmobs.PlainsMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityQuill extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityQuill(World world) {
        super(world);
    }

    public EntityQuill(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityQuill(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "quill";
    	this.group = PlainsMobs.group;
    	this.setBaseDamage(3);
    	this.setProjectileScale(0.5F);
        this.knockbackChance = 0.25D;
    	
    	this.waterProof = true;
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
        return 0.01F;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
}
