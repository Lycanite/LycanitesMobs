package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class EntityAIChase extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    private EntityLivingBase target;
    
    // Properties:
    private double speed = 1.0D;
    private float maxTargetDistance = 8.0F;
    
    private BlockPos movePos;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIChase(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIChase setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIChase setMaxDistance(float setDist) {
    	this.maxTargetDistance = setDist;
    	return this;
    }
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        this.target = this.host.getAttackTarget();
        if(this.target == null)
            return false;
        //else if(this.host.getDistanceSqToEntity(this.target) > (double)(this.maxTargetDistance * this.maxTargetDistance))
            //return false;
        
        Vec3d vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, new Vec3d(this.target.posX, this.target.posY, this.target.posZ));
        if(vec3 == null)
            return false;
        
        this.movePos = new BlockPos(vec3.xCoord, vec3.yCoord, vec3.zCoord);
        return true;
    }
	
    
	// ==================================================
 	//                 Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	return !this.host.getNavigator().noPath() && this.host.isEntityAlive() && this.target.getDistanceSqToEntity(this.host) < (double)(this.maxTargetDistance * this.maxTargetDistance);
    }
	
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useFlightNavigator())
    		this.host.getNavigator().tryMoveToXYZ(this.movePos.getX(), this.movePos.getY(), this.movePos.getZ(), this.speed);
    	else
    		this.host.flightNavigator.setTargetPosition(this.movePos, speed);
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.target = null;
        this.host.flightNavigator.clearTargetPosition(1.0D);
    }
}
