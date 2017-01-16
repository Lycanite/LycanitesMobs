package lycanite.lycanitesmobs.core.entity.ai;

import com.google.common.base.Predicate;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.core.entity.EntityCreatureBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.util.Iterator;
import java.util.List;

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
        int i = this.host.getRider().getRevengeTimer();
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
        this.revengeTime = this.host.getRider().getRevengeTimer();

        try {
            if (this.callForHelp) {
                double d0 = this.getTargetDistance();
                List allies = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().expand(d0, 4.0D, d0), new Predicate<Entity>() {
                    @Override
                    public boolean apply(Entity input) {
                        return input instanceof EntityLivingBase;
                    }
                });
                Iterator possibleAllies = allies.iterator();

                while (possibleAllies.hasNext()) {
                    EntityCreatureBase possibleAlly = (EntityCreatureBase) possibleAllies.next();
                    if (possibleAlly != this.host && possibleAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target))
                        possibleAlly.setAttackTarget(this.target);
                }
            }
        }
        catch(Exception e) {
            LycanitesMobs.printWarning("", "An exception occurred when selecting help targets in rider revenge, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }

        super.startExecuting();
    }
}
