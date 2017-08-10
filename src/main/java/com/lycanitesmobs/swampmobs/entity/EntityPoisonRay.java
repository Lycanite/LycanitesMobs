package com.lycanitesmobs.swampmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.swampmobs.SwampMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityPoisonRay extends EntityProjectileLaser {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityPoisonRay(World par1World) {
		super(par1World);
	}

	public EntityPoisonRay(World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
		super(par1World, par2, par4, par6, setTime, setDelay);
	}

	public EntityPoisonRay(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityPoisonRay(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
		super(par1World, par2EntityLivingBase, setTime, setDelay);
	}

	public EntityPoisonRay(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay, Entity followEntity) {
		super(par1World, par2EntityLivingBase, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "poisonray";
    	this.group = SwampMobs.group;
    	this.setBaseDamage(1);
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setRange(16.0F);
        this.setLaserWidth(2.0F);
    }
	
    
    // ==================================================
 	//                   Get laser End
 	// ==================================================
    @Override
    public Class getLaserEndClass() {
        return EntityPoisonRayEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
        	if(target instanceof EntityLivingBase)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(MobEffects.POISON, this.getEffectDuration(5), 0));
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
