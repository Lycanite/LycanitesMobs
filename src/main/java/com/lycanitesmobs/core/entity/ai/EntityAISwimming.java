package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;

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
	    		if(!this.host.getNavigator().noPath()) {
                    targetY = this.host.getNavigator().getPath().getFinalPathPoint().yCoord;
                    if(this.host.hasAttackTarget())
                        targetY = this.host.getAttackTarget().posY;
                    else if(this.host.hasParent())
                        targetY = this.host.getParentTarget().posY;
                    else if(this.host.hasMaster())
                        targetY = this.host.getMasterTarget().posY;
                }
	    	}
	    	else {
	    		if(!this.host.directNavigator.atTargetPosition()) {
                    targetY = this.host.directNavigator.targetPosition.getY();
                }
	    	}
	    	if(this.host.posY < targetY && !this.host.isStrongSwimmer())
                this.host.getJumpHelper().setJumping();
            else if(this.host.posY > targetY && !this.host.isStrongSwimmer())
                this.host.addVelocity(0, -this.host.getAIMoveSpeed() * 0.25F, 0);
    	}
    	else if(this.host.getRNG().nextFloat() < 0.8F && !this.host.isStrongSwimmer())
            this.host.getJumpHelper().setJumping();
    }
}
