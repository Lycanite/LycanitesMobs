package com.lycanitesmobs.core.entity.ai;

import com.google.common.base.Predicate;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class EntityAITarget extends EntityAIBase {
    // Targets:
    protected EntityCreatureBase host;
    protected EntityLivingBase target;
    
    // Targeting:
    protected Predicate<EntityLivingBase> targetSelector;
    protected Predicate<EntityLivingBase> allySelector;
    protected TargetSorterNearest nearestSorter;

    protected boolean checkSight = true;
    protected boolean nearbyOnly = false;
    protected boolean callForHelp = false;
    private int cantSeeTime;
    protected int cantSeeTimeMax = 60;
    protected double targetingRange = 0;
    
    private int targetSearchStatus;
    private int targetSearchDelay;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITarget(EntityCreatureBase setHost) {
        this.host = setHost;

        this.targetSelector = entity -> {
            double d0 = EntityAITarget.this.getTargetDistance();
            if (entity.isSneaking()) {
                d0 *= 0.800000011920929D;
            }
            return !entity.isInvisible() && (!((double) entity.getDistanceToEntity(EntityAITarget.this.host) > d0) && EntityAITarget.this.isSuitableTarget(entity, false));
        };

        this.allySelector = entity -> {
            double d0 = EntityAITarget.this.getTargetDistance();
            if (entity.isSneaking()) {
                d0 *= 0.800000011920929D;
            }
            return !entity.isInvisible() && (!((double) entity.getDistanceToEntity(EntityAITarget.this.host) > d0) && EntityAITarget.this.isAllyTarget(entity, false));
        };
        this.nearestSorter = new TargetSorterNearest(setHost);
    }
    
    
    // ==================================================
 	//                  Start Executing
 	// ==================================================
    @Override
    public void startExecuting() {
    	this.setTarget(this.target);
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.cantSeeTime = 0;
    }
    
    
    // ==================================================
 	//                  Continue Executing
 	// ==================================================
    @Override
    public boolean shouldContinueExecuting() {
        if(this.getTarget() == null)
            return false;
        if(!this.getTarget().isEntityAlive())
            return false;

        // Target Out of Range:
        double distance = this.getTargetDistance() + 2;
        if(Math.sqrt(this.host.getDistanceSqToEntity(this.getTarget())) > distance)
            return false;
        
        if(this.checkSight)
            if(this.host.getEntitySenses().canSee(this.getTarget()))
                this.cantSeeTime = 0;
            else if(++this.cantSeeTime > this.cantSeeTimeMax)
                return false;
        
        return true;
    }
    
    
    // ==================================================
 	//                      Reset
 	// ==================================================
    @Override
    public void resetTask() {
        this.setTarget(null);
    }
    
    
    // ==================================================
 	//                    Host Target
 	// ==================================================
    protected EntityLivingBase getTarget() { return null; }
    protected void setTarget(EntityLivingBase newTarget) {}


    // ==================================================
    //                  Get New Target
    // ==================================================
    public EntityLivingBase getNewTarget(double rangeX, double rangeY, double rangeZ) {
        EntityLivingBase newTarget = null;
        try {
            List<EntityLivingBase> possibleTargets = this.getPossibleTargets(EntityLivingBase.class, rangeX, rangeY, rangeZ);

            if (possibleTargets.isEmpty())
                return null;
            Collections.sort(possibleTargets, this.nearestSorter);
            newTarget = possibleTargets.get(0);
        }
        catch (Exception e) {
            LycanitesMobs.printWarning("", "An exception occurred when target selecting, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }
        return newTarget;
    }


    // ==================================================
    //               Get Possible Targets
    // ==================================================
    public <T extends EntityLivingBase> List<T> getPossibleTargets(Class <? extends T > clazz, double rangeX, double rangeY, double rangeZ) {
        return this.host.getEntityWorld().getEntitiesWithinAABB(clazz, this.host.getEntityBoundingBox().grow(rangeX, rangeY, rangeZ), this.targetSelector);
    }
    
    
    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    protected double getTargetDistance() {
        if(this.targetingRange > 0)
            return this.targetingRange;
    	IAttributeInstance attributeInstance = this.host.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE);
        return attributeInstance == null ? 16.0D : attributeInstance.getAttributeValue();
    }


    // ==================================================
    //              Call Nearby For Help
    // ==================================================
    public void callNearbyForHelp() {
        if(this.allySelector == null || this.target == null)
            return;
        try {
            double d0 = this.getTargetDistance();
            List allies = this.host.getEntityWorld().getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().grow(d0, 4.0D, d0), this.allySelector);
            Iterator possibleAllies = allies.iterator();

            while (possibleAllies.hasNext()) {
                EntityLivingBase possibleAlly = (EntityLivingBase)possibleAllies.next();
                if(possibleAlly instanceof EntityCreatureBase) {
                    EntityCreatureBase possibleCreatureAlly = (EntityCreatureBase)possibleAlly;
                    if (possibleCreatureAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target) && possibleCreatureAlly.canAttackClass(this.target.getClass()) && possibleCreatureAlly.canAttackEntity(this.target))
                        possibleCreatureAlly.setAttackTarget(this.target);
                }
                else {
                    if (possibleAlly.getRevengeTarget() == null && !possibleAlly.isOnSameTeam(this.target))
                        possibleAlly.setRevengeTarget(this.target);
                }
            }
        }
        catch (Exception e) {
            LycanitesMobs.printWarning("", "An exception occurred when calling for help, this has been skipped to prevent a crash.");
            e.printStackTrace();
        }
    }
    
    
    // ==================================================
 	//                 Valid Target Check
 	// ==================================================
    // ========== Common Checks ==========
    protected boolean isSuitableTarget(EntityLivingBase checkTarget, boolean targetCreative) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isEntityAlive())
            return false;

        // Creative Check:
        if(checkTarget instanceof EntityPlayer && !targetCreative && (((EntityPlayer)checkTarget).isCreative() || ((EntityPlayer)checkTarget).isSpectator()))
            return false;
        
        // Additional Checks:
        if(!this.isValidTarget(checkTarget))
            return false;
        
        // Home Check:
        if(!this.host.positionNearHome(MathHelper.floor(checkTarget.posX), MathHelper.floor(checkTarget.posY), MathHelper.floor(checkTarget.posZ)))
            return false;
        
        // Sight Check:
        if(this.checkSight && !this.host.getEntitySenses().canSee(checkTarget))
            return false;
        
        // Nearby Check:
        if(this.nearbyOnly) {
            if(--this.targetSearchDelay <= 0)
                this.targetSearchStatus = 0;
            if(this.targetSearchStatus == 0)
                this.targetSearchStatus = this.isNearby(checkTarget) ? 1 : 2;
            if(this.targetSearchStatus == 2)
                return false;
        }
        
        return true;
    }
    
    // ========== Additional Checks ==========
    protected boolean isValidTarget(EntityLivingBase target) {
    	return true;
    }

    // ========== Ally Checks ==========
    protected boolean isAllyTarget(EntityLivingBase checkTarget, boolean targetCreative) {
        if(checkTarget == null)
            return false;
        if(checkTarget == this.host)
            return false;
        if(!checkTarget.isEntityAlive())
            return false;
        if(checkTarget.getClass() != this.host.getClass() && (!this.host.isOnSameTeam(checkTarget) || !checkTarget.isOnSameTeam(this.host)))
            return false;

        // Creative Check:
        if(checkTarget instanceof EntityPlayer)
            return false;

        // Sight Check:
        return !this.checkSight || this.host.getEntitySenses().canSee(checkTarget);
    }
    
    
    // ==================================================
 	//                     Is Nearby
 	// ==================================================
    private boolean isNearby(EntityLivingBase target) {
        this.targetSearchDelay = 10 + this.host.getRNG().nextInt(5);
        Path path = this.host.getNavigator().getPathToEntityLiving(target);

        if(path == null)
            return false;
        else {
            PathPoint pathpoint = path.getFinalPathPoint();
            if(pathpoint == null)
                return false;
            else {
                int i = pathpoint.x - MathHelper.floor(target.posX);
                int j = pathpoint.z - MathHelper.floor(target.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
