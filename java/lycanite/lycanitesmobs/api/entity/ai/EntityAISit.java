package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISit extends EntityAIBase {
	// Targets:
    private EntityCreatureTameable host;
    
    // Properties:
    private boolean enabled = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAISit(EntityCreatureTameable setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public EntityAISit setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
        if(!this.host.isTamed())
            return false;
        if(this.host.isInWater() && !this.host.canBreatheUnderwater())
            return false;
        if(!this.host.onGround && !this.host.canFly())
            return false;

        EntityLivingBase owner = this.host.getOwner();
        if(owner != null && this.host.getDistanceSqToEntity(owner) < 144.0D && owner.getAITarget() != null && !this.host.isPassive())
        	return false;
        
        return this.host.isSitting();
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.clearMovement();
    }
}
