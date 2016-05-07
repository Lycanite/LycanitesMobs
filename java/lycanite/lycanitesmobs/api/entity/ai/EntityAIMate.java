package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

import java.util.Iterator;
import java.util.List;

public class EntityAIMate extends EntityAIBase {
	// Targets:
    private EntityCreatureAgeable host;
    private EntityCreatureAgeable partner;
    
    // Properties:
    private double speed = 1.0D;
    private Class targetClass;
    private int mateTime;
    private int mateTimeMax = 60;
    private double mateDistance = 9.0D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIMate(EntityCreatureAgeable setHost) {
        this.host = setHost;
        this.targetClass = this.host.getClass();
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIMate setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIMate setMateDistance(double setDouble) {
    	this.mateDistance = setDouble * setDouble;
    	return this;
    }
    public EntityAIMate setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIMate setMateTime(int setTime) {
    	this.mateTimeMax = setTime;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
        if(!this.host.isInLove())
            return false;
        this.partner = this.getPartner();
        return this.partner != null;
    }
    
    
    // ==================================================
  	//                Continue Executing
  	// ==================================================
    public boolean continueExecuting() {
        return this.partner != null && this.partner.isEntityAlive() && this.partner.isInLove() && this.mateTime < mateTimeMax;
    }
    
    
    // ==================================================
  	//                      Reset
  	// ==================================================
    public void resetTask() {
        this.partner = null;
        this.mateTime = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        this.host.getLookHelper().setLookPositionWithEntity(this.partner, 10.0F, (float)this.host.getVerticalFaceSpeed());
        if(!this.host.useFlightNavigator())
        	this.host.getNavigator().tryMoveToEntityLiving(this.partner, this.speed);
        else
        	this.host.flightNavigator.setTargetPosition(new BlockPos((int)this.partner.posX, (int)this.partner.posY, (int)this.partner.posZ), speed);
        if(this.host.getDistanceSqToEntity(this.partner) < this.mateDistance)
	        ++this.mateTime;
	        if(this.mateTime >= mateTimeMax)
	            this.host.procreate(this.partner);
    }
    
    
    // ==================================================
  	//                    Get Partner
  	// ==================================================
    private EntityCreatureAgeable getPartner() {
        float distance = 8.0F;
        List possibleMates = this.host.worldObj.getEntitiesWithinAABB(this.targetClass, this.host.getEntityBoundingBox().expand((double)distance, (double)distance, (double)distance));
        double closestDistance = Double.MAX_VALUE;
        EntityCreatureAgeable newMate = null;
        Iterator possibleMate = possibleMates.iterator();
        
        while(possibleMate.hasNext())  {
        	EntityLivingBase nextEntity = (EntityLivingBase)possibleMate.next();
        	if(nextEntity instanceof EntityCreatureAgeable) {
	        	EntityCreatureAgeable testMate = (EntityCreatureAgeable)nextEntity;
	            if(this.host.canBreedWith(testMate) && this.host.getDistanceSqToEntity(testMate) < closestDistance) {
	            	newMate = testMate;
	            	closestDistance = this.host.getDistanceSqToEntity(testMate);
	            }
        	}
        }
        return newMate;
    }
}
