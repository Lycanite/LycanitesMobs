package com.lycanitesmobs.freshwatermobs.entity;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import com.lycanitesmobs.core.entity.EntityProjectileLaserEnd;
import com.lycanitesmobs.freshwatermobs.FreshwaterMobs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityWaterJetEnd extends EntityProjectileLaserEnd {

    // ==================================================
 	//                   Constructors
 	// ==================================================
	public EntityWaterJetEnd(World world) {
        super(world);
    }

    public EntityWaterJetEnd(World world, double par2, double par4, double par6, EntityProjectileLaser laser) {
        super(world, par2, par4, par6, laser);
    }

    public EntityWaterJetEnd(World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
        super(world, shooter, laser);
    }
    
    // ========== Setup Projectile ==========
    public void setup() {
    	this.entityName = "waterjet";
    	this.group = FreshwaterMobs.group;
    }
    
    // ========== Stats ==========
    @Override
    public void setStats() {
		super.setStats();
        this.setSpeed(1.0D);
    }
    
	
	// ==================================================
 	//                      Visuals
 	// ==================================================
    @Override
    public ResourceLocation getTexture() {
    	if(AssetManager.getTexture(this.entityName + "End") == null)
    		AssetManager.addTexture(this.entityName + "End", this.group, "textures/items/" + this.entityName.toLowerCase() + "_end.png");
    	return AssetManager.getTexture(this.entityName + "End");
    }
    
    
    // ==================================================
 	//                      Sounds
 	// ==================================================
	@Override
	public SoundEvent getLaunchSound() {
    	return AssetManager.getSound(entityName);
	}
}
