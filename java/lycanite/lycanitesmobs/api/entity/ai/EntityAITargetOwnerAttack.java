package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITargetOwnerAttack extends EntityAITarget {
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
	private int lastAttackTime;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetOwnerAttack(EntityCreatureTameable setHost) {
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
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.isSitting())
    		return false;
    	if(this.host.getOwner() == null)
    		return false;
    	
    	this.target = this.host.getOwner().getLastAttacker();
    	if(this.target == null) {
    		return false;
    	}
    	if(lastAttackTime == this.host.getOwner().getLastAttackerTime())
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Start
  	// ==================================================
    @Override
    public void startExecuting() {
    	if(isTargetValid(target)) {
			lastAttackTime = this.host.getOwner().getLastAttackerTime();
			super.startExecuting();
		}
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean continueExecuting() {
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
