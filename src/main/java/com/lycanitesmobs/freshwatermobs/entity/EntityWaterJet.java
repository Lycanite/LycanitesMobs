package com.lycanitesmobs.freshwatermobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityWaterJet extends EntityProjectileLaser {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityWaterJet(World par1World) {
		super(par1World);
	}

	public EntityWaterJet(World par1World, double par2, double par4, double par6, int setTime, int setDelay) {
		super(par1World, par2, par4, par6, setTime, setDelay);
	}

	public EntityWaterJet(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityWaterJet(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay) {
		super(par1World, par2EntityLivingBase, setTime, setDelay);
	}

	public EntityWaterJet(World par1World, EntityLivingBase par2EntityLivingBase, int setTime, int setDelay, Entity followEntity) {
		super(par1World, par2EntityLivingBase, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "waterjet";
    	this.group = FreshwaterMobs.group;
    	this.setBaseDamage(3);
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
        return EntityWaterJetEnd.class;
    }
    
    
    // ==================================================
 	//                      Damage
 	// ==================================================
    @Override
    public boolean updateDamage(Entity target) {
    	boolean damageDealt = super.updateDamage(target);
        if(this.getThrower() != null && damageDealt) {
        	if(target instanceof EntityLivingBase && ObjectManager.getPotionEffect("penetration") != null)
    			((EntityLivingBase)target).addPotionEffect(new PotionEffect(ObjectManager.getPotionEffect("penetration"), this.getEffectDuration(5), 0));
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
