package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class DirectNavigator {
	// Targets:
	EntityCreatureBase host;
	public BlockPos targetPosition;
	
	// Properties:
	public double flyingSpeed = 1.0D;
	public boolean faceMovement = true;
	public double speedModifier = 1.0D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
	public DirectNavigator(EntityCreatureBase setHost) {
		this.host = setHost;
	}
	
	// ==================================================
 	//                   Set Properties
 	// ==================================================
	public DirectNavigator setSpeed(double setSpeed) {
		this.flyingSpeed = setSpeed;
		return this;
	}
	public DirectNavigator setFacing(boolean facing) {
		this.faceMovement = facing;
		return this;
	}
	
	
	// ==================================================
  	//                    Navigation
  	// ==================================================
	// ========== Set Target Position ===========
	public boolean setTargetPosition(BlockPos targetPosition, double setSpeedMod) {
		if(isTargetPositionValid(targetPosition)) {
			this.targetPosition = targetPosition;
			this.speedModifier = setSpeedMod;
			return true;
		}
		return false;
	}
	
	public boolean setTargetPosition(Entity targetEntity, double setSpeedMod) {
		return this.setTargetPosition(new BlockPos((int)targetEntity.posX, (int)targetEntity.posY, (int)targetEntity.posZ), setSpeedMod);
	}

	// ========== Clear Target Position ===========
	public boolean clearTargetPosition(double setSpeedMod) {
		return this.setTargetPosition((BlockPos)null, setSpeedMod);
	}
	
    // ========== Position Valid ==========
    public boolean isTargetPositionValid() {
		return isTargetPositionValid(this.targetPosition);
	}
    
	public boolean isTargetPositionValid(BlockPos targetPosition) {
		if(targetPosition == null)
			return true;
		if(this.host.isStrongSwimmer() && this.host.isSwimmable(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ()))
			return true;
		if(!this.host.isFlying())
			return false;
		if(!this.host.worldObj.isAirBlock(new BlockPos(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ())) && !this.host.noClip)
			return false;
		if(targetPosition.getY() < 3)
			return false;
		return true;
	}

    // ========== DistanceTo Target Position ==========
    public double distanceToTargetPosition(){
        return this.host.getDistance(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
    }
	
	// ========== Is At Target Position ==========
	public boolean atTargetPosition(){
		if(targetPosition != null)
			return distanceToTargetPosition() < (this.host.width / 2);
		return true;
	}
	
	
	// ==================================================
  	//                      Update
  	// ==================================================
    private double randomStrafeAngle = 0;
	public void updateFlight() {
		if(this.targetPosition == null)
            return;
        if(this.randomStrafeAngle <= 0 && this.host.getRNG().nextDouble() <= 0.25D)
            this.randomStrafeAngle = this.host.getRNG().nextBoolean() ? 90D : -90D;
        if(this.randomStrafeAngle > 0)
            this.randomStrafeAngle -= 0.5D;

        BlockPos pos = this.host.getFacingPosition(this.targetPosition.getX(), this.targetPosition.getY(), this.targetPosition.getZ(), 1.0D, this.randomStrafeAngle);
        //double dirX = (double)this.targetPosition.getX() + 0.5D - this.host.posX;
        double dirX = pos.getX() - this.host.posX;
        double dirY = (double)this.targetPosition.getY() + 0.1D - this.host.posY;
        //double dirZ = (double)this.targetPosition.getZ() + 0.5D - this.host.posZ;
        double dirZ = pos.getZ() - this.host.posZ;

        double speed = this.host.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getAttributeValue() * 2;
		this.host.motionX += ((Math.signum(dirX) * speed - this.host.motionX) * 0.10000000149011612D*0.3D) * this.speedModifier;
		this.host.motionY += ((Math.signum(dirY) * speed - this.host.motionY) * 0.10000000149011612D*0.3D) * this.speedModifier;
		this.host.motionZ += ((Math.signum(dirZ) * speed - this.host.motionZ) * 0.10000000149011612D*0.3D) * this.speedModifier;
		float fullAngle = (float)(Math.atan2(this.host.motionZ, this.host.motionX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
		this.host.moveForward = 0.5F;
		if(this.faceMovement && this.host.getAttackTarget() != null && (this.host.motionX > 0.025F || this.host.motionZ > 0.025F))
			this.host.rotationYaw += angle;
	}
	
	
	// ==================================================
  	//                      Movement
  	// ==================================================
	public void flightMovement(float moveStrafe, float moveForward) {
		if(this.host.isInWater() && !host.isStrongSwimmer()) {
            this.host.moveFlying(moveStrafe, moveForward, 0.02F);
            this.host.moveEntity(this.host.motionX, this.host.motionY, this.host.motionZ);
            this.host.motionX *= 0.800000011920929D;
            this.host.motionY *= 0.800000011920929D;
            this.host.motionZ *= 0.800000011920929D;
        }
        else if(this.host.lavaContact() && !host.isStrongSwimmer()) {
            this.host.moveFlying(moveStrafe, moveForward, 0.02F);
            this.host.moveEntity(this.host.motionX, this.host.motionY, this.host.motionZ);
            this.host.motionX *= 0.5D;
            this.host.motionY *= 0.5D;
            this.host.motionZ *= 0.5D;
        }
        else {
        	float motion = 0.91F;
            if(this.host.onGround) {
            	motion = 0.54600006F;
                Block block = this.host.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.host.posZ))).getBlock();
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            float flyingMotion = 0.16277136F / (motion * motion * motion);
            this.host.moveFlying(moveStrafe, moveForward, this.host.onGround ? 0.1F * flyingMotion : (float)(0.02F * this.speedModifier));
            
            motion = 0.91F;
            if(this.host.onGround) {
            	motion = 0.54600006F;
                Block block = this.host.worldObj.getBlockState(new BlockPos(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(this.host.posZ))).getBlock();
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            
            if(this.host != null && this.host.getEntityBoundingBox() != null)
            	this.host.moveEntity(this.host.motionX, this.host.motionY, this.host.motionZ);
            this.host.motionX *= (double)motion;
            this.host.motionY *= (double)motion;
            this.host.motionZ *= (double)motion;
        }
		
        this.host.prevLimbSwingAmount = this.host.limbSwingAmount;
        double deltaX = this.host.posX - this.host.prevPosX;
        double deltaZ = this.host.posZ - this.host.prevPosZ;
        float var7 = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ) * 4.0F;
        if(var7 > 1.0F) var7 = 1.0F;
        this.host.limbSwingAmount += (var7 - this.host.limbSwingAmount) * 0.4F;
        this.host.limbSwing += this.host.limbSwingAmount;
	}
	
	
	// ==================================================
  	//                      Rotate
  	// ==================================================
	// ========== Rotate to Waypoint ==========
    protected void adjustRotationToWaypoint() {		
		double distX = targetPosition.getX() - this.host.posX;
		double distZ = targetPosition.getZ() - this.host.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI);// - 90.0F;
		float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
		if(angle > 30.0F) angle = 30.0F;
		if(angle < -30.0F) angle = -30.0F;
		this.host.renderYawOffset = this.host.rotationYaw += angle;
	}

	// ========== Rotate to Target ==========
    public void adjustRotationToTarget(BlockPos target) {
		double distX = target.getX() - this.host.posX;
		double distZ = target.getZ() - this.host.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapDegrees(fullAngle - this.host.rotationYaw);
		this.host.rotationYaw += angle; 
    }
}
