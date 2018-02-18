package com.lycanitesmobs.core.entity.ai;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.api.IGroupAlpha;
import com.lycanitesmobs.api.IGroupAnimal;
import com.lycanitesmobs.api.IGroupPredator;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
public class EntityAITargetAttack extends EntityAITarget {
	// Targets:
    public Class targetClass = EntityLivingBase.class;
    private List<Class> targetClasses = null;
    
    // Properties:
    private int targetChance = 0;
    protected boolean tameTargeting = false;
    private int allySize = 0;
    private int enemySize = 0;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public EntityAITargetAttack(EntityCreatureBase setHost) {
        super(setHost);
        this.setMutexBits(1);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAITargetAttack setChance(int setChance) {
    	this.targetChance = setChance;
    	return this;
    }
    
    public EntityAITargetAttack setCheckSight(boolean bool) {
    	this.checkSight = bool;
    	return this;
    }
    
    public EntityAITargetAttack setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    
    public EntityAITargetAttack setTargetClasses(List<Class> classList) {
    	this.targetClasses = classList;
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

    public EntityAITargetAttack setRange(double range) {
        this.targetingRange = range;
        return this;
    }

    public EntityAITargetAttack setHelpCall(boolean setHelp) {
        this.callForHelp = setHelp;
        return this;
    }
    
    public EntityAITargetAttack setTameTargetting(boolean setTargetting) {
    	this.tameTargeting = setTargetting;
    	return this;
    }
    
    /** If both values are above 0 then this mob will consider the size of the enemy pack and it's pack before attacking.
     * setAllySize How many of this mob vs the enemy pack.
     * setEnemySize How many of the enemy vs this mobs size.
     * For example allySize of this mob will attack up to enemySize of the enemy at once.
     * Setting either value at or below 0 will disable this functionality.
    **/
    public EntityAITargetAttack setPackHuntingScale(int setAllySize, int setEnemySize) {
    	this.allySize = setAllySize;
    	this.enemySize = setEnemySize;
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
        // Target Class Check:
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(target.getClass()))
            return false;

    	// Own Class Check:
    	if(this.targetClass != this.host.getClass() && target.getClass() == this.host.getClass())
            return false;

        // Predator Animal/Alpha Check:
        if(this.targetClass == IGroupAnimal.class || this.targetClass == IGroupAlpha.class) {
            if(target instanceof IGroupPredator)
                return false;
        }
    	
    	// Class Check:
    	if(!this.host.canAttackClass(target.getClass()))
            return false;

        // Entity Check:
        if(!this.host.canAttackEntity(target))
        	return false;
        
        // Ownable Checks:
        if(this.host instanceof IEntityOwnable && this.host.getOwner() != null) {
            if(target instanceof IEntityOwnable && this.host.getOwner() == ((IEntityOwnable)target).getOwner())
                return false;
            if(target == this.host.getOwner())
                return false;
        }
        
        // Tamed Checks:
        if(!this.tameTargeting && this.host instanceof EntityCreatureTameable && ((EntityCreatureTameable)this.host).isTamed())
        	return false;
        
        // Pack Size Check:
        if(this.allySize > 0 && this.enemySize > 0) {
            try {
                double hostPackRange = 32D;
                double hostPackSize = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().grow(hostPackRange, hostPackRange, hostPackRange)).size();
                double hostPackScale = hostPackSize / this.allySize;

                double targetPackRange = 64D;
                double targetPackSize = target.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, target.getEntityBoundingBox().grow(targetPackRange, targetPackRange, targetPackRange), new Predicate<EntityLivingBase>() {
                    @Override
                    public boolean apply(EntityLivingBase entity) {
                        return entity.getClass().isAssignableFrom(EntityAITargetAttack.this.targetClass);
                    }
                }).size();
                double targetPackScale = targetPackSize / this.enemySize;

                if (hostPackScale < targetPackScale)
                    return false;
            }
            catch (Exception e) {
                LycanitesMobs.printWarning("", "An exception occurred when assessing pack sizes, this has been skipped to prevent a crash.");
                e.printStackTrace();
            }
        }
        
    	return true;
    }
    
    
    // ==================================================
  	//                   Should Execute
  	// ==================================================
    @Override
    public boolean shouldExecute() {
		if(!this.host.isAggressive() || this.host.hasFixateTarget()) {
			return false;
		}

    	if(this.targetClass == EntityPlayer.class) {
			if (this.host.updateTick % 10 != 0) {
				return false;
			}
		}
		else {
			if (this.host.updateTick % 40 != 0) {
				return false;
			}
		}
		if(this.targetChance > 0 && this.host.getRNG().nextInt(this.targetChance) != 0) {
			return false;
		}


		this.target = null;
        
        double distance = this.getTargetDistance();
        double heightDistance = 4.0D + this.host.height;
        if(this.host.useDirectNavigator())
            heightDistance = distance;
        this.target = this.getNewTarget(distance, heightDistance, distance);
        if(this.callForHelp)
            this.callNearbyForHelp();

        return this.target != null;
    }


	// ==================================================
	//                  Get New Target
	// ==================================================
	@Override
	public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
		// Faster Player Targeting:
		if(this.targetClass == EntityPlayer.class) {
			EntityLivingBase newTarget = null;
			try {
				List<EntityPlayer> possibleTargets = this.host.getEntityWorld().getPlayers(EntityPlayer.class, this.targetSelector);
				if (possibleTargets.isEmpty())
					return null;
				Collections.sort(possibleTargets, this.nearestSorter);
				newTarget = possibleTargets.get(0);
			}
			catch (Exception e) {
				LycanitesMobs.printWarning("", "An exception occurred when player target selecting, this has been skipped to prevent a crash.");
				e.printStackTrace();
			}
			return newTarget;
		}

		return super.getNewTarget(rangeX, rangeY, rangeZ);
	}
}
