package com.lycanitesmobs.elementalmobs.entity;

import com.lycanitesmobs.core.entity.EntityProjectileBase;
import com.lycanitesmobs.elementalmobs.ElementalMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFrostbolt extends EntityProjectileBase {
	
	// Properties:
	public Entity shootingEntity;
	
    // ==================================================
 	//                   Constructors
 	// ==================================================
    public EntityFrostbolt(World world) {
        super(world);
    }

    public EntityFrostbolt(World world, EntityLivingBase entityLivingBase) {
        super(world, entityLivingBase);
    }

    public EntityFrostbolt(World world, double x, double y, double z) {
        super(world, x, y, z);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "frostbolt";
    	this.group = ElementalMobs.instance.group;
    	this.setBaseDamage(5);
    	
    	this.waterProof = true;
    }
    
    
    // ==================================================
 	//                     Impact
 	// ==================================================
    //========== Entity Living Collision ==========
    @Override
    public boolean entityLivingCollision(EntityLivingBase entityLiving) {
        entityLiving.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, this.getEffectDuration(8), 0));
    	return true;
    }
    
    //========== On Impact Particles/Sounds ==========
    @Override
    public void onImpactVisuals() {
    	for(int i = 0; i < 8; ++i)
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOW_SHOVEL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
    }
}
