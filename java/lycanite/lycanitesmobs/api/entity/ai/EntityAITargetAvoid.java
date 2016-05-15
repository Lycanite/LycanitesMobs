package lycanite.lycanitesmobs.api.entity.ai;

import com.google.common.base.Predicate;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.Collections;
import java.util.List;

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
        this.targetSelector = new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                if(!(input instanceof EntityLivingBase))
                    return false;
                return EntityAITargetAvoid.this.isSuitableTarget((EntityLivingBase)input, false);
            }
        };
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
    public EntityAITargetAvoid setSelector(Predicate<Entity> selector) {
    	this.targetSelector = selector;
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
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

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
        // Check for other avoid target AIs:
        EntityLivingBase avoidTarget = this.getTarget();
        if(avoidTarget != null && !this.isValidTarget(avoidTarget))
            return false;

    	this.target = null;
    	
        if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0)
            return false;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useFlightNavigator()) heightDistance = distance;
        List possibleTargets = this.host.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.host.getEntityBoundingBox().expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            return false;
        else
            this.target = (EntityLivingBase)possibleTargets.get(0);
        
        return this.target != null;
    }
}
