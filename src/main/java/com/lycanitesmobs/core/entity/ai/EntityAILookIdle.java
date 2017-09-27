package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAILookIdle extends EntityAIBase {
    // Targets:
    private EntityCreatureBase host;

    // Properties:
    private int idleTime;
    private int idleTimeMin = 20;
    private int idleTimeRange = 20;
    private double lookX;
    private double lookZ;
    
    // ==================================================
   	//                    Constructor
   	// ==================================================
    public EntityAILookIdle(EntityCreatureBase setHost) {
        this.host = setHost;
        this.setMutexBits(3);
    }
    
    
    // ==================================================
  	//                  Set Properties
  	// ==================================================
    public EntityAILookIdle setTimeMin(int setTimeMin) {
    	this.idleTimeMin = setTimeMin;
    	return this;
    }
    public EntityAILookIdle setTimeRange(int setTimeRange) {
    	this.idleTimeRange = setTimeRange;
    	return this;
    }
    
    
    // ==================================================
   	//                  Should Execute
   	// ==================================================
    public boolean shouldExecute() {
        return this.host.getRNG().nextFloat() < 0.02F;
    }
    
    
    // ==================================================
   	//                Continue Executing
   	// ==================================================
    public boolean shouldContinueExecuting() {
        return this.idleTime >= 0;
    }
    
    
    // ==================================================
   	//                     Start
   	// ==================================================
    public void startExecuting() {
        double d0 = (Math.PI * 2D) * this.host.getRNG().nextDouble();
        this.lookX = Math.cos(d0);
        this.lookZ = Math.sin(d0);
        this.idleTime = idleTimeMin + this.host.getRNG().nextInt(idleTimeRange);
    }
    
    
    // ==================================================
   	//                     Update
   	// ==================================================
    public void updateTask() {
        this.idleTime--;
        this.host.getLookHelper().setLookPosition(
        		this.host.posX + this.lookX,
        		this.host.posY + (double)this.host.getEyeHeight(),
        		this.host.posZ + this.lookZ, 10.0F,
        		(float)this.host.getVerticalFaceSpeed()
        		);
    }
}
