package com.lycanitesmobs.demonmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.demonmobs.DemonMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityHellLaser extends EntityProjectileLaser {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityHellLaser(World world) {
		super(world);
	}

	public EntityHellLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(world, par2, par4, par6, setTime, setDelay);
	}

	public EntityHellLaser(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityHellLaser(World world, EntityLivingBase entityShooter, int setTime, int setDelay) {
		super(world, entityShooter, setTime, setDelay);
	}

	public EntityHellLaser(World world, EntityLivingBase entityShooter, int setTime, int setDelay, Entity followEntity) {
		super(world, entityShooter, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "helllaser";
    	this.group = DemonMobs.group;
    	this.setBaseDamage(1);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(4.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityHellLaserEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
        	if(target instanceof EntityLivingBase)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.WITHER, this.getEffectDuration(5), 0));
        }
        return damageDealt;
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "Beam") == null)
    		AssetManager.addTexture(this.entityName + "Beam", this.group, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
    	return AssetManager.getTexture(this.entityName + "Beam");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound(entityName);
    }
	
	@Override
	public SoundEvent getBeamSound() {
    	return AssetManager.getSound(entityName);
	}
}
