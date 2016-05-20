package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

public class EntityAIStayByHome extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private boolean enabled = true;
    private double speed = 1.0D;
    private double farSpeed = 1.5D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIStayByHome(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public EntityAIStayByHome setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    public EntityAIStayByHome setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    public EntityAIStayByHome setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	if(!this.host.hasHome())
    		return false;
        if(this.host.isInWater() && !this.host.canBreatheUnderwater())
            return false;
        if(!this.host.onGround && !this.host.useDirectNavigator())
            return false;
        
        return true;
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.clearMovement();
        if(this.host.hasHome() && this.host.getDistanceFromHome() > 1.0F) {
            BlockPos homePos = this.host.getHomePosition();
        	double speed = this.speed;
        	if(this.host.getDistanceFromHome() > this.host.getHomeDistanceMax())
        		speed = this.farSpeed;
	    	if(!host.useDirectNavigator())
	    		this.host.getNavigator().tryMoveToXYZ(homePos.getX(), homePos.getY(), homePos.getZ(), this.speed);
	    	else
	    		host.flightNavigator.setTargetPosition(new BlockPos((int)homePos.getX(), (int)homePos.getY(), (int)homePos.getZ()), speed);
        }
    }
}
