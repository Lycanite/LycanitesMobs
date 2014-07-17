package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Collections;
import java.util.List;

import lycanite.lycanitesmobs.api.IGroupAnimal;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.monster.IMob;

import org.apache.commons.lang3.StringUtils;

public class EntityAITargetOwnerThreats extends EntityAITarget {
	// Properties:
	private EntityCreatureTameable tamedHost;
    private EntityAITargetSorterNearest targetSorter;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetOwnerThreats(EntityCreatureTameable setHost) {
        super((EntityCreatureBase)setHost);
    	this.tamedHost = setHost;
        this.setMutexBits(1);
        this.targetSelector = new EntityAITargetSelector(this, (IEntitySelector)null);
        this.targetSorter = new EntityAITargetSorterNearest(setHost);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetOwnerThreats setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    
    public EntityAITargetOwnerThreats setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    
    public EntityAITargetOwnerThreats setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    
    public EntityAITargetOwnerThreats setSelector(IEntitySelector selector) {
    	this.targetSelector = new EntityAITargetSelector(this, selector);
    	return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() { return this.host.getAttackTarget(); }
    @Override
    protected void setTarget(EntityLivingBase newTarget) { this.host.setAttackTarget(newTarget); }
    protected EntityLivingBase getOwner() { return this.host.getOwner(); }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
    	// Owner Check:
    	if(!this.tamedHost.isTamed())
    		return false;
    	
    	// Passive Check:
    	if(this.tamedHost.isPassive())
			return false;
    	
    	// Aggressive Check:
    	if(!this.host.isAggressive())
            return false;
    	
    	// Ownable Checks:
        if(this.host instanceof IEntityOwnable && StringUtils.isNotEmpty(((IEntityOwnable)this.host).func_152113_b())) { //getOwnerName()
            if(target instanceof IEntityOwnable && ((IEntityOwnable)this.host).func_152113_b().equals(((IEntityOwnable)target).func_152113_b())) // getOwnerName()
                return false;
            if(target == ((IEntityOwnable)this.host).getOwner())
                return false;
        }
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
    	this.target = null;
    	
    	// Owner Check:
    	if(!this.tamedHost.isTamed())
    		return false;
    	
    	// Passive Check:
    	if(this.tamedHost.isPassive())
			return false;
    	
    	// Aggressive Check:
    	if(!this.host.isAggressive())
            return false;
        
        double distance = this.getTargetDistance() - this.host.width;
        double heightDistance = 4.0D - this.host.height;
        if(this.host.useFlightNavigator()) heightDistance = this.getTargetDistance() - this.host.height;
        List possibleTargets = this.host.worldObj.selectEntitiesWithinAABB(EntityLivingBase.class, this.host.boundingBox.expand(distance, heightDistance, distance), this.targetSelector);
        Collections.sort(possibleTargets, this.targetSorter);
        
        if(possibleTargets.isEmpty())
            this.target = null;
        else {
        	for(Object possibleTargetObj : possibleTargets) {
        		if(possibleTargetObj instanceof EntityLivingBase) {
	        		EntityLivingBase possibleTarget = (EntityLivingBase)possibleTargetObj;
	        		if(possibleTarget instanceof IMob && !(possibleTarget instanceof IEntityOwnable) && !(possibleTarget instanceof EntityCreatureBase)) {
	        			this.target = possibleTarget;
	        		}
	        		else if(possibleTarget instanceof EntityCreatureBase && ((EntityCreatureBase)possibleTarget).isHostile() && !(possibleTarget instanceof IGroupAnimal)) {
	        			this.target = possibleTarget;
	        		}
	        		else if(possibleTarget instanceof EntityLiving && ((EntityLiving)possibleTarget).getAttackTarget() == this.getOwner()) {
	        			this.target = possibleTarget;
	        		}
	        		else if(possibleTarget.getAITarget() == this.getOwner()) {
	        			this.target = possibleTarget;
	        		}
	        		if(this.target != null)
	        			break;
        		}
        	}
        }
        
        return this.target != null;
    }
}
