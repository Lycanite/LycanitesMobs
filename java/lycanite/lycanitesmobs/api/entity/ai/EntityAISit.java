package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISit extends EntityAIBase {
	// Targets:
    private EntityCreatureTameable host;
    
    // Properties:
    private boolean isSitting;
    private boolean enabled = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAISit(EntityCreatureTameable setHost) {
        this.host = setHost;
        this.setMutexBits(5);
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public EntityAISit setSitting(boolean setSitting) {
        this.isSitting = setSitting;
        return this;
    }
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
        if(this.host.isInWater())
            return false;
        if(!this.host.canFly() && !this.host.onGround)
            return false;
        
        EntityLivingBase owner = this.host.getOwner();
        return owner == null ? true : (this.host.getDistanceSqToEntity(owner) < 144.0D && owner.getAITarget() != null ? false : this.isSitting);
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.clearMovement();
        this.host.setSitting(true);
    }
    
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.setSitting(false);
    }
}
