package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.api.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityOwnable;
import net.minecraft.util.AxisAlignedBB;

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
        int i = this.host.getOwner().func_142015_aE(); // Get Owners Revenge Timer
        if(i == this.revengeTime)
        	return false;
        if(!this.isSuitableTarget(this.host.getOwner().getAITarget(), false))
        	return false;
        return true;
    }
	
    
	// ==================================================
 	//                 Start Executing
 	// ==================================================
    public void startExecuting() {
        this.target = this.host.getOwner().getAITarget();
        this.revengeTime = this.host.getOwner().func_142015_aE(); // Get Owners Revenge Timer
        
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
