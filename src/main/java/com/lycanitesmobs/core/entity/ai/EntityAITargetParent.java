package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import com.lycanitesmobs.core.entity.EntityCreatureAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityAnimal;

public class EntityAITargetParent extends EntityAITarget {
	// Targets:
	private EntityCreatureAgeable host;
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private boolean tameTargeting = true;
    private int targetChance = 0;
    private double targetDistance = -1D;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetParent(EntityCreatureAgeable setHost) {
        super(setHost);
        this.setMutexBits(2);
        this.host = setHost;
        this.targetClass = this.host.getClass();
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetParent setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAITargetParent setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAITargetParent setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetParent setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetParent setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetParent setDistance(double setDist) {
    	this.targetDistance = setDist;
    	return this;
    }
    public EntityAITargetParent setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getParentTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setParentTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        if(target instanceof EntityAnimal && ((EntityAnimal)target).getGrowingAge() < 0)
            return false;
    	if(target instanceof EntityCreatureAgeable && ((EntityCreatureAgeable)target).getGrowingAge() < 0)
            return false;
        
        // Tamed Checks:
        if(!this.tameTargeting && this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
    	return true;
    }
    
    
    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    @Override
    protected double getTargetDistance() {
    	if(this.targetDistance > -1)
    		return this.targetDistance;
        IAttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return attributeinstance == null ? 16.0D : attributeinstance.getAttributeValue();
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if (this.host.updateTick % 20 != 0) {
			return false;
		}

    	this.target = null;
    	if(this.host.getGrowingAge() >= 0) {
    		this.host.setParentTarget(null);
    		return false;
    	}
    	
        if(this.host.updateTick % 20 != 0 && this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        return this.target != null;
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
    	if(this.host.getGrowingAge() >= 0) {
    		this.host.setParentTarget(null);
    		return false;
    	}
    	
    	return super.shouldContinueExecuting();
    }
}
