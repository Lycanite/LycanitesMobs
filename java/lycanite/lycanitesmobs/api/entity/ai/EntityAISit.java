package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

public class EntityAISit extends EntityAIBase {
	// Targets:
    private EntityCreatureTameable host;
    
    // Properties:
    private boolean enabled = true;
    private double speed = 1.0D;
    private double farSpeed = 1.5D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAISit(EntityCreatureTameable setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
	// ==================================================
 	//                  Set Properties
 	// ==================================================
    public EntityAISit setEnabled(boolean flag) {
        this.enabled = flag;
        return this;
    }
    
    public EntityAISit setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    public EntityAISit setFarSpeed(double setSpeed) {
    	this.farSpeed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
        if(!this.host.isTamed())
            return false;
        if(this.host.isInWater() && !this.host.canBreatheUnderwater())
            return false;
        if(!this.host.onGround && !this.host.canFly())
            return false;

        EntityLivingBase owner = this.host.getOwner();
        if(owner != null && this.host.getDistanceSqToEntity(owner) < 144.0D && owner.getAITarget() != null && !this.host.isPassive())
        	return false;
        
        return this.host.isSitting();
    }
    
    
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        this.host.clearMovement();
        if(this.host.hasHome() && this.host.getDistanceFromHome() > 1.0F) {
        	ChunkCoordinates homePos = this.host.getHomePosition();
        	double speed = this.speed;
        	if(this.host.getDistanceFromHome() > this.host.getHomeDistanceMax())
        		speed = this.farSpeed;
	    	if(!host.canFly())
	    		this.host.getNavigator().tryMoveToXYZ(homePos.posX, homePos.posY, homePos.posZ, this.speed);
	    	else
	    		host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)homePos.posX, (int)homePos.posY, (int)homePos.posZ), speed);
        }
    }
}
