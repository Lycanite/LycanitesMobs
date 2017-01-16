package lycanite.lycanitesmobs.core.entity.ai;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAITargetParentAttack extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
	
	// Properties:
    private boolean tameTargeting = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetParentAttack(EntityCreatureBase setHost) {
        host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetParentAttack setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(this.host.getAttackTarget() != null) {
    		if(!this.host.getAttackTarget().isEntityAlive())
    			return false;
    	}
    	if(this.host.getParentAttackTarget() == null)
    		return false;
    	return true;
    }

    
    // ==================================================
  	//                       Update
  	// ==================================================
    @Override
    public void updateTask() {
    	if(this.host.getAttackTarget() == null) {
    		EntityLivingBase target = this.host.getParentAttackTarget();
    		if(this.isTargetValid(target))
    			this.host.setAttackTarget(target);
    	}
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
    	return true;
    }
    
    
    // ==================================================
 	//                       Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.host.setAttackTarget(null);
    }
}
