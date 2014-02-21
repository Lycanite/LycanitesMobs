package lycanite.lycanitesmobs.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;

public class EntityAITargetAvoid extends EntityAITarget {
	// Targets:
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private int targetChance = 0;
    private EntityAITargetSorterNearest targetSorter;
    protected boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetAvoid(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(8);
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetAvoid setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAITargetAvoid setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAITargetAvoid setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetAvoid setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetAvoid setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetAvoid setSelector(IEntitySelector selector) {
    	this.targetSelector = new EntityAITargetSelector(this, selector);
    	return this;
    }
    public EntityAITargetAvoid setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAvoidTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAvoidTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
        if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.canFly()) heightDistance = distance;
        List possibleTargets = this.host.worldObj.selectEntitiesWithinAABB(this.targetClass, this.host.boundingBox.expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            return false;
        else
            this.target = (EntityLivingBase)possibleTargets.get(0);
        
        return this.target != null;
    }
}
