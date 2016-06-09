package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIFollow extends EntityAIBase {
	// Targets:
	EntityCreatureBase host;
    
    // Properties:
    double speed = 1.0D;
    Class targetClass;
    private int updateRate;
    double strayDistance = 1.0D;
    double lostDistance = 64.0D;
    double behindDistance = 0;
    
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollow(EntityCreatureBase setHost) {
        this.host = setHost;
        this.targetClass = this.host.getClass();
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    public Entity getTarget() {
    	return null;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollow setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollow setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollow setStrayDistance(double setDist) {
    	this.strayDistance = setDist;
    	return this;
    }
    public EntityAIFollow setLostDistance(double setDist) {
    	this.lostDistance = setDist;
    	return this;
    }
    public EntityAIFollow setFollowBehind(double setDist) {
    	this.behindDistance = setDist;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	Entity target = this.getTarget();
	    if(target == null)
	        return false;
        if(!target.isEntityAlive())
        	return false;

        double distance = Math.sqrt(this.host.getDistanceSqToEntity(target));
	    if(distance > this.lostDistance && this.lostDistance != 0)
	        return false;
	    if(distance <= this.strayDistance && this.strayDistance != 0)
	        return false;
	    
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean continueExecuting() {
    	Entity target = this.getTarget();
    	if(target == null)
    		return false;
        if(!target.isEntityAlive())
        	return false;
        
        double distance = Math.sqrt(this.host.getDistanceSqToEntity(target));
        if(distance > this.lostDistance && this.lostDistance != 0)
        	this.host.setMasterTarget(null);
        if(distance <= this.strayDistance && this.strayDistance != 0)
        	return false;
        
        return this.getTarget() != null;
    }
    
    
    // ==================================================
  	//                       Start
  	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 10;
            Entity target = this.getTarget();
        	if(!this.host.useDirectNavigator()) {
        		if(this.behindDistance == 0 || !(target instanceof EntityCreatureBase)) {
                    this.host.getNavigator().tryMoveToEntityLiving(target, this.speed);
                }
        		else {
        			BlockPos pos = ((EntityCreatureBase)target).getFacingPosition(-this.behindDistance);
        			this.host.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), this.speed);
        		}
        	}
        	else {
        		if(this.behindDistance == 0 || !(target instanceof EntityCreatureBase))
        			this.host.directNavigator.setTargetPosition(new BlockPos((int)target.posX, (int)target.posY, (int)target.posZ), this.speed);
        		else {
                    BlockPos pos = ((EntityCreatureBase)target).getFacingPosition(-this.behindDistance);
        			this.host.directNavigator.setTargetPosition(pos, this.speed);
        		}
        	}
        }
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.clearMovement();
    }
}
