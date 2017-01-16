package lycanite.lycanitesmobs.core.entity.ai;

import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.Vec3d;

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
    private Path pathEntity;
	
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
        if(this.avoidTarget == null) {
        	return false;
        }
    	
        if(!this.avoidTarget.isEntityAlive())
            return false;
    	
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(this.avoidTarget.getClass()))
            return false;

        /*if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance) {
        	return false;
        }*/
        
        Vec3d avoidVector = RandomPositionGenerator.findRandomTargetAwayFrom(this.host, 16, 7, new Vec3d(this.avoidTarget.posX, this.avoidTarget.posY, this.avoidTarget.posZ));
        if(avoidVector == null)
            return false;
        
        if(this.avoidTarget.getDistanceSq(avoidVector.xCoord, avoidVector.yCoord, avoidVector.zCoord) < this.avoidTarget.getDistanceSqToEntity(this.host))
            return false;

        if(!this.host.useDirectNavigator()) {
            this.pathEntity = this.host.getNavigator().getPathToXYZ(avoidVector.xCoord, avoidVector.yCoord, avoidVector.zCoord);
            if(this.pathEntity == null)// || !this.pathEntity.isDestinationSame(avoidVector))
                return false;
        }
        
        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
        if(!this.host.useDirectNavigator() && this.host.getNavigator().noPath())
        	return false;
		if(this.host.useDirectNavigator() && this.host.directNavigator.atTargetPosition())
			return false;

        /*if(this.host.getDistanceSqToEntity(this.avoidTarget) >= this.farDistance)
        	return false;*/
    	return true;
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useDirectNavigator())
    		this.host.getNavigator().setPath(this.pathEntity, this.farSpeed);
    	else
    		this.host.directNavigator.setTargetPosition(this.avoidTarget, this.farSpeed);
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
        if(this.host.getDistanceSqToEntity(this.avoidTarget) < this.nearDistance)
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().setSpeed(this.nearSpeed);
        	else
        		this.host.directNavigator.speedModifier = this.nearSpeed;
        else
        	if(!this.host.useDirectNavigator())
        		this.host.getNavigator().setSpeed(this.farSpeed);
        	else
        		this.host.directNavigator.speedModifier = this.farSpeed;
    }
}
