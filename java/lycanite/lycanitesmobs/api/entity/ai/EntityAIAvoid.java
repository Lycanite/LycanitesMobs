package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.Vec3;

public class EntityAIAvoid extends EntityAIBase {
    // Targets:
    private EntityCreatureBase host;
    private EntityLivingBase avoidTarget;
    
    // Properties:
    private double farSpeed = 1.0D;
    private double nearSpeed = 1.2D;
    private double farDistance = 4096.0D;
    private double nearDistance = 49.0D;
    private Class targetClass;
    private float distanceFromEntity = 6.0F;
    private PathEntity pathEntity;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIAvoid(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIAvoid setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    public EntityAIAvoid setNearSpeed(double setSpeed) {
    	this.nearSpeed = setSpeed;
    	return this;
    }
    public EntityAIAvoid setFarDistance(double dist) {
    	this.farDistance = dist * dist;
    	return this;
    }
    public EntityAIAvoid setNearDistance(double dist) {
    	this.nearDistance = dist * dist;
    	return this;
    }
    public EntityAIAvoid setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.avoidTarget = this.host.getAvoidTarget();
        if(this.avoidTarget == null)
        	return false;
    	
        if(!this.avoidTarget.isEntityAlive())
            return false;
    	
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(this.avoidTarget.getClass()))
            return false;
        
        Vec3 vec3 = RandomPositionGenerator.findRandomTargetAwayFrom(this.host, 24, 10, Vec3.createVectorHelper(this.avoidTarget.posX, this.avoidTarget.posY, this.avoidTarget.posZ));
        if(vec3 == null)
            return false;
        
        if(this.avoidTarget.getDistanceSq(vec3.xCoord, vec3.yCoord, vec3.zCoord) < this.avoidTarget.getDistanceSqToEntity(this.host))
            return false;
        
        if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance)
        	return false;
        
        return this.pathEntity == null ? false : this.pathEntity.isDestinationSame(vec3);
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
        if(!this.host.useFlightNavigator() && this.host.getNavigator().noPath())
        	return false;
		if(this.host.useFlightNavigator() && this.host.flightNavigator.atTargetPosition())
			return false;
        if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance)
        	return false;
    	return true;
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useFlightNavigator())
    		this.host.getNavigator().setPath(this.pathEntity, this.farSpeed);
    	else
    		this.host.flightNavigator.setTargetPosition(this.avoidTarget, this.farSpeed);
    }
	
    
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
        this.avoidTarget = null;
    }
	
    
	// ==================================================
 	//                      Update
 	// ==================================================
    public void updateTask() {
        if(this.host.getDistanceSqToEntity(this.avoidTarget) < 49.0D)
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().setSpeed(this.nearSpeed);
        	else
        		this.host.flightNavigator.speedModifier = this.nearSpeed;
        else
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().setSpeed(this.farSpeed);
        	else
        		this.host.flightNavigator.speedModifier = this.farSpeed;
    }
}
