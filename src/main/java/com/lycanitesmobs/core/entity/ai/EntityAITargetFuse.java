package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.CreatureManager;
import net.minecraft.entity.EntityLivingBase;

public class EntityAITargetFuse extends EntityAITarget {
	// Targets:
    private Class<IFusable> targetClass = IFusable.class;

    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;

    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetFuse(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(8);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetFuse setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }

    public EntityAITargetFuse setTargetClass(Class<IFusable> setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }

    public EntityAITargetFuse setSightCheck(boolean setSightCheck) {
    	this.checkSight = setSightCheck;
    	return this;
    }

    public EntityAITargetFuse setOnlyNearby(boolean setNearby) {
    	this.nearbyOnly = setNearby;
    	return this;
    }

    public EntityAITargetFuse setCantSeeTimeMax(int setCantSeeTimeMax) {
    	this.cantSeeTimeMax = setCantSeeTimeMax;
    	return this;
    }

    public EntityAITargetFuse setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }

    public EntityAITargetFuse setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    @Override
    protected EntityLivingBase getTarget() {
    	if(this.host instanceof IFusable) {
			return (EntityLivingBase) ((IFusable) this.host).getFusionTarget();
		}
    	return null;
    }
    @Override
    protected void setTarget(EntityLivingBase newTarget) {
		if(this.host instanceof IFusable && newTarget instanceof IFusable) {
			((IFusable)this.host).setFusionTarget((IFusable)newTarget);
		}
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    @Override
    protected boolean isValidTarget(EntityLivingBase target) {
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

        // Own Class Check:
    	if(target.getClass() == this.host.getClass())
            return false;

    	// Fusable Check:
		if(!(this.host instanceof IFusable) || ((IFusable)this.host).getFusionClass((IFusable)target) == null)
			return false;

		// Owner Check:
		if(target instanceof EntityCreatureBase) {
			if(this.host.getOwner() != ((EntityCreatureBase)target).getOwner()) {
				return false;
			}
		}

    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if(!CreatureManager.getInstance().config.elementalFusion) {
			return false;
		}

		if (this.host.updateTick % 20 != 0) {
			return false;
		}
		if(this.host.isPetType("familiar")) {
			return false;
		}
		if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}

        // Check for other fusion target:
        EntityLivingBase fuseTarget = this.getTarget();
        if(fuseTarget != null && !this.isValidTarget(fuseTarget)) {
            return false;
        }

    	this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();
        return this.target != null;
    }
}
