package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;

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

        if (!(this.host.getOwner() instanceof EntityLivingBase))
            return false;
        EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
    	this.target = owner.getLastAttacker();
    	if(this.target == null) {
    		return false;
    	}
    	if(lastAttackTime == owner.getLastAttackerTime())
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Start
  	// ==================================================
    @Override
    public void startExecuting() {
    	if(this.isTargetValid(target)) {
			lastAttackTime = ((EntityLivingBase)this.host.getOwner()).getLastAttackerTime();
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
    	if(target == null)
            return false;
    	if(!target.isEntityAlive())
            return false;
		if(target == this.host)
            return false;
		if(!this.host.canAttackClass(target.getClass()))
            return false;
		if(!this.host.canAttackEntity(target))
            return false;
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
