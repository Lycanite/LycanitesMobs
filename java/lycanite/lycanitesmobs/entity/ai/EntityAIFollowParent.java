package lycanite.lycanitesmobs.entity.ai;

import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.entity.EntityCreatureAgeable;
import lycanite.lycanitesmobs.entity.EntityCreatureBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIFollowParent extends EntityAIFollow {
	// Targets:
	EntityCreatureAgeable host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowParent(EntityCreatureAgeable setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowParent setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowParent setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowParent setStrayDistance(double setDist) {
    	this.strayDistance = setDist * setDist;
    	return this;
    }
    public EntityAIFollowParent setLostDistance(double setDist) {
    	this.lostDistance = setDist * setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public EntityLivingBase getTarget() {
    	return this.host.getParentTarget();
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	if(this.host.getGrowingAge() >= 0)
    		return false;
    	return super.shouldExecute();
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    @Override
    public boolean continueExecuting() {
    	if(this.host.getGrowingAge() >= 0)
    		return false;
    	return super.continueExecuting();
    }
}
