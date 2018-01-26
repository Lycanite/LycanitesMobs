package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityWhirlwind extends EntityProjectileBase {

	// Properties:
	public Entity shootingEntity;

    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityWhirlwind(World world) {
        super(world);
    }

    public EntityWhirlwind(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityWhirlwind(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "whirlwind";
    	this.group = ElementalMobs.instance.group;
    	this.setBaseDamage(2);
    	this.setProjectileScale(4F);
    	this.waterProof = false;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
		entityLiving.addPotionEffect(new PotionEffect(MobEffects.LEVITATION, this.getEffectDuration(8), 2));
        return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i) {
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.CLOUD, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    		this.getEntityWorld().spawnParticle(EnumParticleTypes.CLOUD, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
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
