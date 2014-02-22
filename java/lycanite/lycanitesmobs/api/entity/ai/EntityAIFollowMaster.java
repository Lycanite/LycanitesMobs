package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIFollowMaster extends EntityAIFollow {
	// Targets:
	EntityCreatureBase host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowMaster(EntityCreatureBase setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowMaster setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowMaster setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowMaster setStrayDistance(double setDist) {
    	this.strayDistance = setDist * setDist;
    	return this;
    }
    public EntityAIFollowMaster setLostDistance(double setDist) {
    	this.lostDistance = setDist * setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public EntityLivingBase getTarget() {
    	return this.host.getMasterTarget();
    }
}
