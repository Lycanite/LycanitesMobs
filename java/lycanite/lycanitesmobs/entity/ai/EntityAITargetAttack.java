package lycanite.lycanitesmobs.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;

import org.apache.commons.lang3.StringUtils;

public class EntityAITargetAttack extends EntityAITarget {
	// Targets:
    private Class targetClass = EntityLivingBase.class;
    
    // Properties:
    private int targetChance = 0;
    private EntityAITargetSorterNearest targetSorter;
    protected boolean tameTargeting = false;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetAttack(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(1);
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetAttack setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    public EntityAITargetAttack setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAITargetAttack setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetAttack setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetAttack setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetAttack setSelector(IEntitySelector selector) {
    	this.targetSelector = new EntityAITargetSelector(this, selector);
    	return this;
    }
    public EntityAITargetAttack setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;
    	
    	// Class Check:
    	if(!this.host.canAttackClass(target.getClass()))
            return false;

        // Entity Check:
        if(!this.host.canAttackEntity(target))
        	return false;
        
        // Ownable Checks:
        if(this.host instanceof EntityOwnable && StringUtils.isNotEmpty(((EntityOwnable)this.host).getOwnerName())) {
            if(target instanceof EntityOwnable && ((EntityOwnable)this.host).getOwnerName().equals(((EntityOwnable)target).getOwnerName()))
                return false;
            if(target == ((EntityOwnable)this.host).getOwner())
                return false;
        }
        
        // Tamed Checks:
        if(!this.tameTargeting && this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
    	if(!this.host.isAggressive())
    		return false;
    	
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
