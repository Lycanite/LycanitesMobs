package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;

public class EntityAIFollowOwner extends EntityAIFollow {
	// Targets:
	EntityCreatureTameable host;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIFollowOwner(EntityCreatureTameable setHost) {
    	super(setHost);
        this.setMutexBits(1);
        this.host = setHost;
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIFollowOwner setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIFollowOwner setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIFollowOwner setStrayDistance(double setDist) {
    	this.strayDistance = setDist * setDist;
    	return this;
    }
    public EntityAIFollowOwner setLostDistance(double setDist) {
    	this.lostDistance = setDist * setDist;
    	return this;
    }
    
	
	// ==================================================
 	//                    Get Target
 	// ==================================================
    @Override
    public EntityLivingBase getTarget() {
    	return this.host.getOwner();
    }
    
	
	// ==================================================
 	//                      Update
 	// ==================================================
    @Override
    public void updateTask() {
    	if(!this.host.isSitting()) {
    		if(this.host.getDistanceSqToEntity(this.getTarget()) >= this.lostDistance)
    			this.teleportToOwner();
    	}
    	
    	super.updateTask();
    }
    
    // ========== Teleport to Owner ==========
    public void teleportToOwner() {
    	if(this.getTarget() != null) {
	    	int i = MathHelper.floor_double(this.getTarget().posX) - 2;
	        int j = MathHelper.floor_double(this.getTarget().boundingBox.minY);
	        int k = MathHelper.floor_double(this.getTarget().posZ) - 2;
	
	        for(int l = 0; l <= 4; ++l) {
	            for(int i1 = 0; i1 <= 4; ++i1) {
	                if((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.host.worldObj.doesBlockHaveSolidTopSurface(i + l, j - 1, k + i1) && !this.host.worldObj.isBlockNormalCube(i + l, j, k + i1) && !this.host.worldObj.isBlockNormalCube(i + l, j + 1, k + i1)) {
	                    this.host.setLocationAndAngles((double)((float)(i + l) + 0.5F), (double)j, (double)((float)(k + i1) + 0.5F), this.host.rotationYaw, this.host.rotationPitch);
	                    this.host.clearMovement();
	                    return;
	                }
	            }
	        }
    	}
    }
    
    //TODO Wait on the ChunkUnload Chunk event, if this mob is not sitting and the unloading chunk is what it's in, then teleport this mob to it's owner away from the unloaded chunk, unless it's player has disconnected.
}
