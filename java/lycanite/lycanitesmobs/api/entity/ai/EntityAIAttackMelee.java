package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class EntityAIAttackMelee extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
    private EntityLivingBase attackTarget;
    private PathEntity pathToTarget;
    
    // Properties:
    private double speed = 1.0D;
    private Class targetClass;
    private boolean longMemory = true;
    private int attackTime;
    private int attackTimeMax = 20;
    private double attackRange = 1.0D;
    private float maxChaseDistance = 40.0F * 40.0F;
    private double damage = 1.0D;
    private int failedPathFindingPenalty;
    private int failedPathFindingPenaltyMax = 0;
    public boolean enabled = true;
    
    private int repathTime;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIAttackMelee(EntityCreatureBase setHost) {
        this.host = setHost;
        this.attackRange = (double)((this.host.width * this.host.width) * 2.0F) + 1.0D;
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
    public EntityAIAttackMelee setRate(int setRate) {
    	this.attackTimeMax = setRate;
    	return this;
    }
    public EntityAIAttackMelee setRange(double range) {
    	this.attackRange = (range * range) + this.host.width;
    	return this;
    }
    public EntityAIAttackMelee scaleRange(double scale) {
    	this.attackRange *= scale;
    	return this;
    }
    public EntityAIAttackMelee setMaxChaseDistance(float distance) {
    	this.maxChaseDistance = distance * distance;
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
        if(this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(this.targetClass != null && !this.targetClass.isAssignableFrom(attackTarget.getClass()))
            return false;
        if(--this.repathTime <= 0) {
        	if(!this.host.useFlightNavigator()) {
	            this.pathToTarget = this.host.getNavigator().getPathToEntityLiving(attackTarget);
	            this.repathTime = 4 + this.host.getRNG().nextInt(7);
	            return this.pathToTarget != null;
        	}
        	else
        		return this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)attackTarget.posX, (int)attackTarget.posY, (int)attackTarget.posZ), speed);
        }
        return true;
    }
	
    
	// ==================================================
 	//                  Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
    	if(!this.enabled)
    		return false;
        attackTarget = this.host.getAttackTarget();
        if(attackTarget == null)
        	return false;
        if(!attackTarget.isEntityAlive())
        	return false;
        if(this.host.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ) > this.maxChaseDistance)
        	return false;
        if(!this.longMemory)
        	if(!this.host.useFlightNavigator() && this.host.getNavigator().noPath())
        		return false;
        	else if(this.host.useFlightNavigator() && (this.host.flightNavigator.atTargetPosition() || !this.host.flightNavigator.isTargetPositionValid()))
        		return false;
        return this.host.positionNearHome(MathHelper.floor_double(attackTarget.posX), MathHelper.floor_double(attackTarget.posY), MathHelper.floor_double(attackTarget.posZ));
    }
	
    
	// ==================================================
 	//                   Start Executing
 	// ==================================================
    public void startExecuting() {
    	if(!this.host.useFlightNavigator())
    		this.host.getNavigator().setPath(this.pathToTarget, this.speed);
    	else if(attackTarget != null)
    		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)attackTarget.posX, (int)(attackTarget.posY+1.0), (int)attackTarget.posZ), speed);
        this.repathTime = 0;
    }
	
    
	// ==================================================
 	//                       Reset
 	// ==================================================
    public void resetTask() {
        this.host.getNavigator().clearPathEntity();
        this.host.flightNavigator.clearTargetPosition(1.0D);
        this.attackTarget = null;
    }
	
    
	// ==================================================
 	//                       Update
 	// ==================================================
    public void updateTask() {
        EntityLivingBase attackTarget = this.host.getAttackTarget();
        this.host.getLookHelper().setLookPositionWithEntity(attackTarget, 30.0F, 30.0F);

        if((this.longMemory || this.host.getEntitySenses().canSee(attackTarget)) && --this.repathTime <= 0) {
	        this.repathTime = failedPathFindingPenalty + 4 + this.host.getRNG().nextInt(7);
	        
	        // Walk to Target:
        	if(!this.host.useFlightNavigator()) {
        		this.host.getNavigator().tryMoveToEntityLiving(attackTarget, this.speed);
	            if(this.host.getNavigator().getPath() != null) {
	                PathPoint finalPathPoint = this.host.getNavigator().getPath().getFinalPathPoint();
	                if(finalPathPoint != null && attackTarget.getDistanceSq(finalPathPoint.xCoord, finalPathPoint.yCoord, finalPathPoint.zCoord) < 1)
	                    failedPathFindingPenalty = 0;
	                else
	                    failedPathFindingPenalty += failedPathFindingPenaltyMax;
	            }
	            else
	                failedPathFindingPenalty += failedPathFindingPenaltyMax;
        	}
        	
        	// Fly to Target:
        	else
        		this.host.flightNavigator.setTargetPosition(new ChunkCoordinates((int)attackTarget.posX, (int)(attackTarget.posY + 1.0), (int)attackTarget.posZ), speed);
        }
        
        // Damage Target:
        this.attackTime = Math.max(this.attackTime - 1, 0);
        if(this.host.getDistanceSq(attackTarget.posX, attackTarget.boundingBox.minY, attackTarget.posZ) <= this.attackRange + (attackTarget.width * attackTarget.width)) {
            if(this.attackTime <= 0) {
                this.attackTime = Math.round((float)this.attackTimeMax + ((float)this.attackTimeMax - ((float)this.attackTimeMax * (float)this.host.getHasteMultiplier())));
                if(this.host.getHeldItem() != null)
                    this.host.swingItem();
                this.host.meleeAttack(attackTarget, damage);
            }
        }
    }
}
