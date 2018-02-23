package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;

public class EntityAITargetDefend extends EntityAITarget {
	/** The entity class to defend. **/
	protected Class<? extends EntityLivingBase> defendClass;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetDefend(EntityCreatureBase setHost, Class<? extends EntityLivingBase> defendClass) {
        super(setHost);
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetDefend setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public EntityAITargetDefend setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public EntityAITargetDefend setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() {
    	return this.host.getAttackTarget();
    }

    @Override
    protected void setTarget(EntityLivingBase newTarget) {
    	this.host.setAttackTarget(newTarget);
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {

		// Owner Check:
		if(this.host.getOwner() != null)
			return false;

    	// Aggressive Check:
    	if(!this.host.isAggressive())
            return false;

    	// Has Target Check:
		if(target.getRevengeTarget() == null) {
			return false;
		}
    	
    	// Ownable Checks:
        if(this.host instanceof IEntityOwnable && ((IEntityOwnable)this.host).getOwner() != null) {
            if(target instanceof IEntityOwnable && ((IEntityOwnable)this.host).getOwner() == ((IEntityOwnable)target).getOwner())
                return false;
            if(target == ((IEntityOwnable)this.host).getOwner())
                return false;
        }

        // Threat Check:
        if(this.defendClass.isAssignableFrom(target.getRevengeTarget().getClass())) {
            return true;
        }
        
    	return false;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
    	// Owner Check:
    	if(this.host.getOwner() != null)
    		return false;
    	
    	// Aggressive Check:
    	if(!this.host.isAggressive())
            return false;
        
        double distance = this.getTargetDistance() - this.host.width;
        double heightDistance = 4.0D - this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = this.getTargetDistance() - this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
