package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;

public abstract class EntityAITarget extends EntityAIBase {
    // Targets:
    protected EntityCreatureBase host;
    protected EntityLivingBase target;
    
    // Properties:
    protected IEntitySelector targetSelector;
    
    protected boolean checkSight = true;
    protected boolean nearbyOnly = false;
    private int cantSeeTime;
    protected int cantSeeTimeMax = 60;
    
    private int targetSearchStatus;
    private int targetSearchDelay;
    
    // ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAITarget(EntityCreatureBase setHost) {
        this.host = setHost;
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
 	//                 Get Target Distance
 	// ==================================================
    protected double getTargetDistance() {
    	IAttributeInstance attributeinstance = this.host.getEntityAttribute(SharedMonsterAttributes.followRange);
        return attributeinstance == null ? 16.0D : attributeinstance.getAttributeValue();
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
        
        // Additional Checks:
        if(!this.isValidTarget(checkTarget))
            return false;
        
        // Creative Check:
        if(checkTarget instanceof EntityPlayer && !targetCreative && ((EntityPlayer)checkTarget).capabilities.disableDamage)
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
    
    
    // ==================================================
 	//                     Is Nearby
 	// ==================================================
    private boolean isNearby(EntityLivingBase target) {
        this.targetSearchDelay = 10 + this.host.getRNG().nextInt(5);
        PathEntity pathentity = this.host.getNavigator().getPathToEntityLiving(target);

        if(pathentity == null)
            return false;
        else {
            PathPoint pathpoint = pathentity.getFinalPathPoint();
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
