package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;

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
        if(setEntity.getNavigator() instanceof PathNavigateGround)
            ((PathNavigateGround)setEntity.getNavigator()).setCanSwim(true);
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
        if(this.host.getControllingPassenger() != null && this.host.getControllingPassenger() instanceof EntityPlayer && this.host.canBeSteered())
            return false;
        return this.host.isInWater() || this.host.isInLava();
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
    public void updateTask() {
    	if(this.sink) {
	    	double targetY = this.host.posY;
	    	if(!this.host.useDirectNavigator()) {
	    		if(!this.host.getNavigator().noPath())
                    targetY = this.host.getNavigator().getPath().getPosition(this.host).yCoord;
	    	}
	    	else {
	    		if(!this.host.directNavigator.atTargetPosition()) {
                    targetY = this.host.directNavigator.targetPosition.getY();
                }
	    	}
	    	if(this.host.posY < targetY && !this.host.canSwim())
                this.host.getJumpHelper().setJumping();
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F && !this.host.canSwim())
            this.host.getJumpHelper().setJumping();
    }
}
