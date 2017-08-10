package com.lycanitesmobs.core.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase {
    // Targets:
	private EntityLiving host;
    protected Entity closestEntity;

    // Properties
    private Class watchedClass = EntityLivingBase.class;
    private float maxDistanceForPlayer = 4.0F;
    private int lookTime;
    private int lookTimeMin = 40;
    private int lookTimeRange = 40;
    private float lookChance = 0.02F;
    
    // ==================================================
   	//                     Constructor
   	// ==================================================
    public EntityAIWatchClosest(EntityLiving setHost) {
    	this.host = setHost;
        this.setMutexBits(2);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAIWatchClosest setTargetClass(Class setTarget) {
    	this.watchedClass = setTarget;
    	return this;
    }
    public EntityAIWatchClosest setMaxDistance(float setDist) {
    	this.maxDistanceForPlayer = setDist;
    	return this;
    }
    public EntityAIWatchClosest setlookChance(float setChance) {
    	this.lookChance = setChance;
    	return this;
    }
    
    
    // ==================================================
   	//                   Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        if(this.host.getRNG().nextFloat() >= this.lookChance)
            return false;
        else {
            if(this.host.getAttackTarget() != null)
                this.closestEntity = this.host.getAttackTarget();
            if(this.watchedClass == EntityPlayer.class)
                this.closestEntity = this.host.worldObj.getClosestPlayerToEntity(this.host, (double)this.maxDistanceForPlayer);
            else
                this.closestEntity = this.host.worldObj.findNearestEntityWithinAABB(this.watchedClass, this.host.getEntityBoundingBox().expand((double)this.maxDistanceForPlayer, 3.0D, (double)this.maxDistanceForPlayer), this.host);

            return this.closestEntity != null;
        }
    }
    
    
    // ==================================================
   	//                 Continue Executing
   	// ==================================================
    public boolean continueExecuting() {
    	if(!this.closestEntity.isEntityAlive())
    		return false;
    	if(this.host.getDistanceSqToEntity(this.closestEntity) > (double)(this.maxDistanceForPlayer * this.maxDistanceForPlayer))
    		return false;
        return this.lookTime > 0;
    }
    
    
    // ==================================================
   	//                  Start Executing
   	// ==================================================
    public void startExecuting() {
        this.lookTime = lookTimeMin + this.host.getRNG().nextInt(lookTimeRange);
    }
    
    
    // ==================================================
   	//                      Reset
   	// ==================================================
    public void resetTask() {
        this.closestEntity = null;
    }
    
    
    // ==================================================
   	//                      Update
   	// ==================================================
    public void updateTask() {
        this.host.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.host.getVerticalFaceSpeed());
        this.lookTime--;
    }
}