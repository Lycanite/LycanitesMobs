package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.ChunkCoordinates;

public class EntityAIGetItem extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
	private EntityItem target;
	
	// Properties:
    private IEntitySelector targetSelector;
    private EntityAITargetSorterNearest targetSorter;
    private double distanceMax = 32.0D * 32.0D;
    double speed = 1.0D;
    private boolean checkSight = true;
    private int cantSeeTime = 0;
    protected int cantSeeTimeMax = 60;
    private int updateRate = 0;
    public boolean tamedLooting = true;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAIGetItem(EntityCreatureBase setHost) {
        super();
        this.setMutexBits(1);
        this.host = setHost;
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIGetItem setDistanceMax(double setDouble) {
    	this.distanceMax = setDouble * setDouble;
    	return this;
    }

    public EntityAIGetItem setSpeed(double setDouble) {
    	this.speed = setDouble;
    	return this;
    }
    
    public EntityAIGetItem setCheckSight(boolean setBool) {
    	this.checkSight = setBool;
    	return this;
    }
    
    public EntityAIGetItem setTamedLooting(boolean bool) {
    	this.tamedLooting = bool;
    	return this;
    }
    
    
    // ==================================================
  	//                  Should Execute
  	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.canPickupItems())
    		return false;

    	if(!this.tamedLooting) {
    		if(this.host instanceof EntityCreatureTameable)
    			if(((EntityCreatureTameable)this.host).isTamed())
    				return false;
    	}
    	
        double heightDistance = 4.0D;
        if(this.host.useFlightNavigator()) heightDistance = this.distanceMax;
        List possibleTargets = this.host.worldObj.selectEntitiesWithinAABB(EntityItem.class, this.host.boundingBox.expand(this.distanceMax, heightDistance, this.distanceMax), this.targetSelector);
        
        if(possibleTargets.isEmpty())
            return false;
        Collections.sort(possibleTargets, this.targetSorter);
        this.target = (EntityItem)possibleTargets.get(0);
        
        return this.continueExecuting();
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	if(this.target == null)
            return false;
        if(!this.target.isEntityAlive())
            return false;
        
        double distance = this.host.getDistanceSqToEntity(target);
        if(distance > this.distanceMax)
        	return false;
        
        if(this.checkSight)
            if(this.host.getEntitySenses().canSee(this.target))
                this.cantSeeTime = 0;
            else if(++this.cantSeeTime > this.cantSeeTimeMax)
                return false;
        
        return true;
    }
    
    
    // ==================================================
 	//                      Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.target = null;
        this.host.clearMovement();
    }
    
    
    // ==================================================
  	//                       Start
  	// ==================================================
    public void startExecuting() {
        this.updateRate = 0;
    }
    
    
    // ==================================================
  	//                      Update
  	// ==================================================
    public void updateTask() {
        if(this.updateRate-- <= 0) {
            this.updateRate = 10;
        	if(!this.host.useFlightNavigator())
        		this.host.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
        	else
        		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)this.target.posX, (int)this.target.posY, (int)this.target.posZ), this.speed);
        }
    }
}
