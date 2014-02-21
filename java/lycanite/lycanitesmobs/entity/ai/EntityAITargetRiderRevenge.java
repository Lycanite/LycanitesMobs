package lycanite.lycanitesmobs.entity.ai;

import java.util.Iterator;
import java.util.List;

import lycanite.lycanitesmobs.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.entity.EntityCreatureTameable;
import net.minecraft.util.AxisAlignedBB;

public class EntityAITargetRiderRevenge extends EntityAITargetAttack {
	
	// Targets:
	private EntityCreatureTameable host;
	
	// Properties:
    boolean callForHelp = false;
    private int revengeTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITargetRiderRevenge(EntityCreatureTameable setHost) {
        super(setHost);
    	this.host = setHost;
    	this.tameTargeting = true;
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetRiderRevenge setHelpCall(boolean setHelp) {
    	this.callForHelp = setHelp;
    	return this;
    }
    public EntityAITargetRiderRevenge setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }
    public EntityAITargetRiderRevenge setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }
    public EntityAITargetRiderRevenge setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }
	
    
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.host.hasRiderTarget())
    		return false;
    	if(this.host.getRider() == null)
    		return false;
        int i = this.host.getRider().func_142015_aE(); // Get Riders Revenge Timer
        if(i == this.revengeTime)
        	return false;
        if(!this.isSuitableTarget(this.host.getRider().getAITarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getRider().getAITarget();
        this.revengeTime = this.host.getRider().func_142015_aE(); // Get Riders Revenge Timer
        
        if(this.callForHelp) {
            double d0 = this.getTargetDistance();
            List allies = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), AxisAlignedBB.getAABBPool().getAABB(this.host.posX, this.host.posY, this.host.posZ, this.host.posX + 1.0D, this.host.posY + 1.0D, this.host.posZ + 1.0D).expand(d0, 10.0D, d0));
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
