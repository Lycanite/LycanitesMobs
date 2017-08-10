package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;

public class EntityAITargetRiderAttack extends EntityAITarget {
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
	private int lastAttackTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetRiderAttack(EntityCreatureTameable setHost) {
    	super(setHost);
        this.host = setHost;
        this.checkSight = false;
        this.setMutexBits(1);
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(this.host.isSitting())
    		return false;
    	if(this.host.getRider() == null)
    		return false;
    	
    	this.target = this.host.getRider().getLastAttacker();
    	if(this.target == null) {
    		return false;
    	}
    	if(lastAttackTime == this.host.getRider().getLastAttackerTime())
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Start
  	// ==================================================
    @Override
    public void startExecuting() {
    	if(isTargetValid(target)) {
			lastAttackTime = this.host.getRider().getLastAttackerTime();
			super.startExecuting();
		}
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean continueExecuting() {
    	if(!this.host.hasRiderTarget())
    		return false;
        if(this.host.isSitting())
            return false;
        return super.continueExecuting();
    }
    
    
    // ==================================================
  	//                    Valid Target
  	// ==================================================
    private boolean isTargetValid(EntityLivingBase target) {
    	if(target == null) return false;
    	if(!target.isEntityAlive()) return false;
		if(target == this.host) return false;
		if(!this.host.canAttackClass(target.getClass())) return false;
    	return true;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
}
