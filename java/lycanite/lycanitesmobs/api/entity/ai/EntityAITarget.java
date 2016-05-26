package lycanite.lycanitesmobs.api.entity.ai;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class EntityAITarget extends EntityAIBase {
    // Targets:
    protected EntityCreatureBase host;
    protected EntityLivingBase target;
    
    // Targeting:
    protected Predicate<Entity> targetSelector;
    protected Predicate<Entity> allySelector;
    protected TargetSorterNearest nearestSorter;

    protected boolean checkSight = true;
    protected boolean nearbyOnly = false;
    protected boolean callForHelp = false;
    private int cantSeeTime;
    protected int cantSeeTimeMax = 60;
    
    private int targetSearchStatus;
    private int targetSearchDelay;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITarget(EntityCreatureBase setHost) {
        this.host = setHost;
        this.targetSelector = new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                if(!(input instanceof EntityLivingBase))
                    return false;
                return EntityAITarget.this.isSuitableTarget((EntityLivingBase)input, false);
            }
        };
        this.allySelector = new Predicate<Entity>() {
            @Override
            public boolean apply(Entity input) {
                if(!(input instanceof EntityLivingBase))
                    return false;
                return EntityAITarget.this.isAllyTarget((EntityLivingBase) input, false);
            }
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
    public boolean continueExecuting() {
        if(this.getTarget() == null)
            return false;
        if(!this.getTarget().isEntityAlive())
            return false;
        
        double distance = this.getTargetDistance();
        if(this.host.getDistanceSqToEntity(this.getTarget()) > distance * distance)
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
            List possibleTargets = this.getPossibleTargets(rangeX, rangeY, rangeZ);
            if (possibleTargets.isEmpty())
                return null;
            Collections.sort(possibleTargets, this.nearestSorter);
            newTarget = (EntityLivingBase) possibleTargets.get(0);
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
    public List getPossibleTargets(double rangeX, double rangeY, double rangeZ) {
        return this.host.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, this.host.getEntityBoundingBox().expand(rangeX, rangeY, rangeZ), Predicates.and(new Predicate[]{EntitySelectors.CAN_AI_TARGET, this.targetSelector}));
    }
    
    
    // ==================================================
 	//                 Get Target Distance
 	// ==================================================
    protected double getTargetDistance() {
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
            List allies = this.host.worldObj.getEntitiesWithinAABB(this.host.getClass(), this.host.getEntityBoundingBox().expand(d0, 4.0D, d0), this.allySelector);
            Iterator possibleAllies = allies.iterator();

            while (possibleAllies.hasNext()) {
                EntityLivingBase possibleAlly = (EntityLivingBase)possibleAllies.next();
                if(possibleAlly instanceof EntityCreatureBase) {
                    EntityCreatureBase possibleCreatureAlly = (EntityCreatureBase)possibleAlly;
                    if (possibleCreatureAlly.getAttackTarget() == null && !possibleAlly.isOnSameTeam(this.target) && possibleCreatureAlly.canAttackClass(this.target.getClass()) && possibleCreatureAlly.canAttackEntity(this.target))
                        possibleCreatureAlly.setAttackTarget(this.target);
                }
                else {
                    if (possibleAlly.getAITarget() == null && !possibleAlly.isOnSameTeam(this.target))
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
        if(checkTarget instanceof EntityPlayer && !targetCreative && ((EntityPlayer)checkTarget).capabilities.disableDamage)
            return false;
        
        // Additional Checks:
        if(!this.isValidTarget(checkTarget))
            return false;
        
        // Home Check:
        if(!this.host.positionNearHome(MathHelper.floor_double(checkTarget.posX), MathHelper.floor_double(checkTarget.posY), MathHelper.floor_double(checkTarget.posZ)))
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
        if(this.checkSight && !this.host.getEntitySenses().canSee(checkTarget))
            return false;

        return true;
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
                int i = pathpoint.xCoord - MathHelper.floor_double(target.posX);
                int j = pathpoint.zCoord - MathHelper.floor_double(target.posZ);
                return (double)(i * i + j * j) <= 2.25D;
            }
        }
    }
}
