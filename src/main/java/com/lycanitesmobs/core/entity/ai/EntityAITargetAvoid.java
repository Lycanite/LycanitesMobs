package com.lycanitesmobs.core.entity.ai;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

public class EntityAITargetAvoid extends EntityAITarget {
	// Targets:
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetAvoid(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(8);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetAvoid setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public EntityAITargetAvoid setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }

    public EntityAITargetAvoid setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public EntityAITargetAvoid setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public EntityAITargetAvoid setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public EntityAITargetAvoid setSelector(Predicate<Entity> selector) {
    	this.targetSelector = selector;
    	return this;
    }

    public EntityAITargetAvoid setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public EntityAITargetAvoid setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAvoidTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAvoidTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        // Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
        // Check for other avoid target AIs:
        EntityLivingBase avoidTarget = this.getTarget();
        if(avoidTarget != null && !this.isValidTarget(avoidTarget))
            return false;

    	this.target = null;
    	
        if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
