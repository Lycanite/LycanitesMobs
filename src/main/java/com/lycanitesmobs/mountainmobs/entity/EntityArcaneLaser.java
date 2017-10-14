package com.lycanitesmobs.mountainmobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.mountainmobs.MountainMobs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityArcaneLaser extends EntityProjectileLaser {
    
    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityArcaneLaser(World world) {
		super(world);
	}

	public EntityArcaneLaser(World world, double par2, double par4, double par6, int setTime, int setDelay) {
		super(world, par2, par4, par6, setTime, setDelay);
	}

	public EntityArcaneLaser(World world, double par2, double par4, double par6, int setTime, int setDelay, Entity followEntity) {
		super(world, par2, par4, par6, setTime, setDelay, followEntity);
	}

	public EntityArcaneLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay) {
		super(world, entityLiving, setTime, setDelay);
	}

	public EntityArcaneLaser(World world, EntityLivingBase entityLiving, int setTime, int setDelay, Entity followEntity) {
		super(world, entityLiving, setTime, setDelay, followEntity);
	}
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "arcanelaser";
    	this.group = MountainMobs.instance.group;
    	this.setBaseDamage(4);
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
        return EntityArcaneLaserEnd.class;
    }
    
	
    // ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getBeamTexture() {
    	if(AssetManager.getTexture(this.entityName + "beam") == null)
    		AssetManager.addTexture(this.entityName + "beam", this.group, "textures/items/" + this.entityName.toLowerCase() + "_beam.png");
    	return AssetManager.getTexture(this.entityName + "beam");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
    @Override
    public SoundEvent getLaunchSound() {
    	return AssetManager.getSound(this.entityName);
    }
	
	@Override
	public SoundEvent getBeamSound() {
    	return null;
	}
}
