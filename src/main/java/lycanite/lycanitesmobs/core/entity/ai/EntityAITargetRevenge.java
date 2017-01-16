package lycanite.lycanitesmobs.core.entity.ai;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;

import java.util.Iterator;
import java.util.List;

public class EntityAITargetRevenge extends EntityAITargetAttack {
	
	// Properties:
    Class[] helpClasses = null;
    private int revengeTime;
    private boolean tameTargeting = true;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITargetRevenge(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetRevenge setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public EntityAITargetRevenge setHelpClasses(Class... setHelpClasses) {
    	this.helpClasses = setHelpClasses;
    	this.callForHelp = true;
    	return this;
    }
    public EntityAITargetRevenge setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetRevenge setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetRevenge setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
    public EntityAITargetRevenge setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
        int i = this.host.getRevengeTimer();
        if(!this.host.isAggressive() && !(this.host instanceof EntityCreatureTameable))
        	return false;
        if(!this.host.isAggressive() && this.host instanceof EntityCreatureTameable && !((EntityCreatureTameable)this.host).isTamed())
        	return false;
        return i != this.revengeTime && this.isSuitableTarget(this.host.getAITarget(), false);
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getAITarget();
        this.revengeTime = this.host.getRevengeTimer();

        try {
            if (this.callForHelp && (!(this.host instanceof EntityCreatureTameable) || (this.host instanceof EntityCreatureTameable && !((EntityCreatureTameable) this.host).isTamed()))) {
                double d0 = this.getTargetDistance();
                List allies = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().expand(d0, 4.0D, d0), this.targetSelector);
                if (this.helpClasses != null)
                    for (Class helpClass : this.helpClasses) {
                        if (helpClass != null && EntityCreatureBase.class.isAssignableFrom(helpClass) && !this.target.getClass().isAssignableFrom(helpClass)) {
                            allies.addAll(this.host.worldObj.getEntitiesWithinAABB(helpClass, this.host.getEntityBoundingBox().expand(d0, 4.0D, d0), this.targetSelector));
                        }
                    }
                Iterator possibleAllies = allies.iterator();

                while (possibleAllies.hasNext()) {
                    EntityCreatureBase possibleAlly = (EntityCreatureBase) possibleAllies.next();
                    if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target) && possibleAlly.isProtective(this.host))
                        if (!(possibleAlly instanceof EntityCreatureTameable) || (possibleAlly instanceof EntityCreatureTameable && !((EntityCreatureTameable) possibleAlly).isTamed()))
                            possibleAlly.setAttackTarget(this.target);
                }
            }
        }
        catch (Exception e) {
            LycanitesMobs.printWarning("", "An exception occurred when selecting help targets in revenge, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }

        super.startExecuting();
    }
}
