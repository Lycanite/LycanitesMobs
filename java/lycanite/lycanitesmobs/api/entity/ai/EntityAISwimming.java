package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAISwimming extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private boolean sink = false;
    
    // ==================================================
   	//                    Constructor
   	// ==================================================
    public EntityAISwimming(EntityCreatureBase setEntity) {
        this.host = setEntity;
        this.setMutexBits(4);
        setEntity.getNavigator().setCanSwim(true);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAISwimming setSink(boolean setSink) {
    	this.sink = setSink;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        return this.host.isInWater() || this.host.handleLavaMovement();
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
    public void updateTask() {
    	if(this.sink) {
	    	double targetY = this.host.posY;
	    	if(!this.host.useFlightNavigator()) {
	    		if(!this.host.getNavigator().noPath())
	    			targetY = this.host.getNavigator().getPath().getPosition(this.host).yCoord;
	    	}
	    	else {
	    		if(!this.host.flightNavigator.atTargetPosition())
	    		targetY = this.host.flightNavigator.targetPosition.posY;
	    	}
	    	if(this.sink && this.host.posY < targetY)
	    		this.host.getJumpHelper().setJumping();
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F)
            this.host.getJumpHelper().setJumping();
    }
}
