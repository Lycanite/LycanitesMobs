package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIStayByWater extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double strayDistance = 64.0D;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public EntityAIStayByWater(EntityCreatureBase setHost) {
    	this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIStayByWater setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }

    public EntityAIStayByWater setStrayDistance(double strayDistance) {
    	this.strayDistance = strayDistance;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
    	// Set home when in water or lava (for lava creatures).
    	if(this.host.isInWater()) {
    		this.host.setHome((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ, (float)this.strayDistance);
    		return false;
    	}
    	
    	// If we're at home but it is no longer water/lava, clear the home. It is up to the wander AI and path weights for find a new home.
    	if(!this.host.isInWater() && this.host.getDistanceFromHome() <= 0.5D) {
    		this.host.detachHome();
    		return false;
    	}
    	
    	// If it's raining and the host isn't a lava creature, then there's no need to return home, this shouldn't be set as a home though.
    	if(!this.host.isLavaCreature && this.host.waterContact())
    		return false;
    	
    	// If the host has an attack target and plenty of air, then it is allowed to stray from home.
        if(this.host.hasAttackTarget() && this.host.getAir() > -100)
        	return false;
        
        // At this point the host should return home.
        return true;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    public void startExecuting() {
    	ChunkCoordinates homePos = this.host.getHomePosition();
    	if(!host.canFly())
    		this.host.getNavigator().tryMoveToXYZ(homePos.posX, homePos.posY, homePos.posZ, this.speed);
    	else
    		host.flightNavigator.setTargetPosition(homePos, this.speed);
    }
}
