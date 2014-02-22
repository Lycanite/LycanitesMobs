package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureRideable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIPlayerControl extends EntityAIBase {
    // Targets:
    private EntityCreatureRideable host;
    
    // Properties:
    private double speed = 1.0D;
    private double sprintSpeed = 1.5D;
    private double flightSpeed = 1.0D;
    public boolean enabled = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIPlayerControl(EntityCreatureRideable setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIPlayerControl setSpeed(double set) {
    	this.speed = set;
    	return this;
    }

    public EntityAIPlayerControl setSprintSpeed(double set) {
    	this.sprintSpeed = set;
    	return this;
    }

    public EntityAIPlayerControl setFlightSpeed(double set) {
    	this.flightSpeed = set;
    	return this;
    }
    
    public EntityAIPlayerControl setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.isTamed())
    		return false;
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(!(this.host.getRiderTarget() instanceof EntityLivingBase))
    		return false;
    	return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	return this.shouldExecute();
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	
    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
    	
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {
    	
    }
}
