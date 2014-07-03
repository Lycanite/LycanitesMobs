package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Vec3;

public class EntityAIMoveRestriction extends EntityAIBase {
	// Targets:
    private EntityCreatureBase host;
    
    // Properties:
    private double speed = 1.0D;
    private double movePosX;
    private double movePosY;
    private double movePosZ;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIMoveRestriction(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIMoveRestriction setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
        if(this.host.hasHome())
            return false;
        ChunkCoordinates chunkcoordinates = this.host.getHomePosition();
        Vec3 vec3 = RandomPositionGenerator.findRandomTargetTowards(this.host, 16, 7, this.host.worldObj.getWorldVec3Pool().getVecFromPool((double)chunkcoordinates.posX, (double)chunkcoordinates.posY, (double)chunkcoordinates.posZ));
        if(vec3 == null)
            return false;
        
        this.movePosX = vec3.xCoord;
        this.movePosY = vec3.yCoord;
        this.movePosZ = vec3.zCoord;
        return true;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean continueExecuting() {
        return !this.host.getNavigator().noPath();
    }
    
    
    // ==================================================
  	//                     Start
  	// ==================================================
    public void startExecuting() {
        this.host.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.speed);
    }
}
