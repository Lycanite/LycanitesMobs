package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;

public class EntityAITargetOwnerRevenge extends EntityAITargetAttack {
	
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITargetOwnerRevenge(EntityCreatureTameable setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetOwnerRevenge setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public EntityAITargetOwnerRevenge setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetOwnerRevenge setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetOwnerRevenge setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.getOwner() == null)
    		return false;
        if (!(this.host.getOwner() instanceof EntityLivingBase))
            return false;
        EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        int i = owner.getRevengeTimer();
        if(i == this.revengeTime)
        	return false;
        if(!this.isSuitableTarget(owner.getAITarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        this.target = owner.getAITarget();
        this.revengeTime = owner.getRevengeTimer();
        if(this.callForHelp) {
            this.callNearbyForHelp();
        }
        super.startExecuting();
    }
}
