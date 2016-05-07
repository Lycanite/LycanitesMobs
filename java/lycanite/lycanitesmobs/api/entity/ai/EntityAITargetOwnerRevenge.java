package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;

import java.util.Iterator;
import java.util.List;

public class EntityAITargetOwnerRevenge extends EntityAITargetAttack {
	
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
    boolean callForHelp = false;
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITargetOwnerRevenge(EntityCreatureTameable setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetOwnerRevenge setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public EntityAITargetOwnerRevenge setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetOwnerRevenge setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetOwnerRevenge setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.isTamed())
    		return false;
    	if(this.host.getOwner() == null)
    		return false;
        if (!(this.host.getOwner() instanceof EntityLivingBase))
            return false;
        EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        int i = owner.getRevengeTimer();
        if(i == this.revengeTime)
        	return false;
        if(!this.isSuitableTarget(owner.getAITarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        EntityLivingBase owner = (EntityLivingBase)this.host.getOwner();
        this.target = owner.getAITarget();
        this.revengeTime = owner.getRevengeTimer();
        
        if(this.callForHelp) {
            double d0 = this.getTargetDistance();
            List allies = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().expand(d0, 4.0D, d0), this.targetSelector);
            Iterator possibleAllies = allies.iterator();

            while(possibleAllies.hasNext()) {
                EntityCreatureBase possibleAlly = (EntityCreatureBase)possibleAllies.next();
                if(possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target))
                	possibleAlly.setAttackTarget(this.target);
            }
        }

        super.startExecuting();
    }
}
