package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityAIAttackMelee extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
    private EntityLivingBase attackTarget;
    private Path pathToTarget;
    
    // Properties:
    private double speed = 1.0D;
    private Class targetClass;
    private boolean longMemory = true;
    private int attackTime;
    private double attackRange = 0.5D;
    private float maxChaseDistance = 40.0F;
    private double damage = 1.0D;
    public boolean enabled = true;

    // Pathing:
	private int failedPathFindingPenalty;
	private int failedPathFindingPenaltyMax = 0;
    private int repathTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIAttackMelee(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIAttackMelee setSpeed(double setSpeed) {
    	this.speed = setSpeed;
    	return this;
    }
    public EntityAIAttackMelee setDamage(double scale) {
    	this.damage = scale;
    	return this;
    }
    public EntityAIAttackMelee setTargetClass(Class setTargetClass) {
    	this.targetClass = setTargetClass;
    	return this;
    }
    public EntityAIAttackMelee setLongMemory(boolean setMemory) {
    	this.longMemory = setMemory;
    	return this;
    }
    public EntityAIAttackMelee setRange(double range) {
    	this.attackRange = range;
    	return this;
    }
    public EntityAIAttackMelee setMaxChaseDistance(float distance) {
    	this.maxChaseDistance = distance;
    	return this;
    }
    public EntityAIAttackMelee setMissRate(int rate) {
    	this.failedPathFindingPenaltyMax = rate;
    	return this;
    }
    public EntityAIAttackMelee setEnabled(boolean setEnabled) {
    	this.enabled = setEnabled;
    	return this;
    }
	
    
	// ==================================================
 	//                   Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!this.enabled)
    		return false;
    	
        attackTarget = this.host.getAttackTarget();
        if(attackTarget == null)
            return false;
        if(!attackTarget.isEntityAlive())
            return false;
        if(this.host.getDistance(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(attackTarget.getClass()))
            return false;

        if(--this.repathTime <= 0) {
            // Set Path:
        	if(!this.host.useDirectNavigator()) {
                this.pathToTarget = this.host.getNavigator().getPathToEntityLiving(attackTarget);//attackTarget.posX, attackTarget.posY + this.host.getFlightOffset(), attackTarget.posZ);
	            this.repathTime = 4 + this.host.getRNG().nextInt(7);
	            return this.pathToTarget != null;
        	}

            // Set Direct Target:
        	else
        		return this.host.directNavigator.setTargetPosition(new BlockPos((int)attackTarget.posX, (int)attackTarget.posY + this.host.getFlightOffset(), (int)attackTarget.posZ), this.speed);
        }
        return true;
    }
	
    
	// ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean shouldContinueExecuting() {
    	if(!this.enabled)
    		return false;
        this.attackTarget = this.host.getAttackTarget();
        if(this.attackTarget == null)
        	return false;
        if(!this.host.isEntityAlive() || !attackTarget.isEntityAlive())
        	return false;
        if(this.host.getDistance(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(!this.longMemory)
        	if(!this.host.useDirectNavigator() && this.host.getNavigator().noPath())
        		return false;
        	else if(this.host.useDirectNavigator() && (this.host.directNavigator.atTargetPosition() || !this.host.directNavigator.isTargetPositionValid()))
        		return false;
        return this.host.positionNearHome(MathHelper.floor(attackTarget.posX), MathHelper.floor(attackTarget.posY), MathHelper.floor(attackTarget.posZ));
    }
	
    
	// ==================================================
 	//                   Start Executing
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useDirectNavigator())
    		this.host.getNavigator().setPath(this.pathToTarget, this.speed);
    	else if(attackTarget != null)
    		this.host.directNavigator.setTargetPosition(new BlockPos((int)attackTarget.posX, (int)(attackTarget.posY+1.0), (int)attackTarget.posZ), speed);
        this.repathTime = 0;
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.getNavigator().clearPath();
        this.host.directNavigator.clearTargetPosition(1.0D);
        this.attackTarget = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
    public void updateTask() {
        EntityLivingBase attackTarget = this.host.getAttackTarget();
        this.host.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);

		// Path To Target:
		if((this.longMemory || this.host.getEntitySenses().canSee(attackTarget)) && --this.repathTime <= 0) {
			this.repathTime = failedPathFindingPenalty + 4 + this.host.getRNG().nextInt(7);

        	if(!this.host.useDirectNavigator()) {
        		this.host.getNavigator().tryMoveToEntityLiving(attackTarget, this.speed);
	            if(this.host.getNavigator().getPath() != null) {
	                PathPoint finalPathPoint = this.host.getNavigator().getPath().getFinalPathPoint();
	                if(finalPathPoint != null && attackTarget.getDistanceSq(finalPathPoint.x, finalPathPoint.y, finalPathPoint.z) < 1) {
						failedPathFindingPenalty = 0;
					}
	                else {
						failedPathFindingPenalty += failedPathFindingPenaltyMax;
					}
	            }
	            else {
					failedPathFindingPenalty += failedPathFindingPenaltyMax;
				}
        	}
        	else {
        		this.host.directNavigator.setTargetPosition(new BlockPos((int) attackTarget.posX, (int) (attackTarget.posY + this.host.getFlightOffset()), (int) attackTarget.posZ), speed);
        	}
        }
        
        // Damage Target:
        if(this.host.getDistanceSq(attackTarget.posX, attackTarget.getEntityBoundingBox().minY + this.host.getFlightOffset(), attackTarget.posZ) <= this.getAttackRange(attackTarget)) {
            if(--this.attackTime <= 0) {
                this.attackTime = this.host.getMeleeCooldown();
                if(this.host.getHeldItemMainhand() != null)
                    this.host.swingArm(EnumHand.MAIN_HAND);
                this.host.attackMelee(attackTarget, damage);
            }

            // Move helper won't change the Yaw if the target is already close by
            double d0 = this.host.posX - attackTarget.posX;
            double d1 = this.host.posZ - attackTarget.posZ;
            float f = (float)(Math.atan2(d1, d0) * 180.0D / Math.PI) + 90.0F;
            f = MathHelper.wrapDegrees(f - this.host.rotationYaw);
            if(f < -30f) f = -30f;
            if(f > 30f) f = 30f;
            this.host.rotationYaw = this.host.rotationYaw + f;
        }
    }

	/**
	 * Returns the required attack range.
	 * @param attackTarget The entity to attack.
	 * @return The maximum attack range.
	 */
	protected double getAttackRange(EntityLivingBase attackTarget) {
		double range = this.attackRange + (this.host.width * 2.0F * this.host.width * 2.0F + attackTarget.width);
		if(this.host.isCurrentlyFlying()) {
			range += 0.5D;
		}
		return range;
	}
}
