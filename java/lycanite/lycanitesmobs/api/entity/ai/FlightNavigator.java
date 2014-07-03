package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;

public class FlightNavigator {
	// Targets:
	EntityCreatureBase host;
	public ChunkCoordinates targetPosition;
	
	// Properties:
	public double flyingSpeed = 1.0D;
	public boolean faceMovement = true;
	public double speedModifier = 1.0D;
	
	// ==================================================
 	//                    Constructor
 	// ==================================================
	public FlightNavigator(EntityCreatureBase setHost) {
		this.host = setHost;
	}
	
	// ==================================================
 	//                   Set Properties
 	// ==================================================
	public FlightNavigator setSpeed(double setSpeed) {
		this.flyingSpeed = setSpeed;
		return this;
	}
	public FlightNavigator setFacing(boolean facing) {
		this.faceMovement = facing;
		return this;
	}
	
	
	// ==================================================
  	//                    Navigation
  	// ==================================================
	// ========== Set Target Position ===========
	public boolean setTargetPosition(ChunkCoordinates targetPosition, double setSpeedMod) {
		if(isTargetPositionValid(targetPosition)) {
			this.targetPosition = targetPosition;
			this.speedModifier = setSpeedMod;
			return true;
		}
		return false;
	}
	
	public boolean setTargetPosition(Entity targetEntity, double setSpeedMod) {
		return this.setTargetPosition(new ChunkCoordinates((int)targetEntity.posX, (int)targetEntity.posY, (int)targetEntity.posZ), setSpeedMod);
	}

	// ========== Clear Target Position ===========
	public boolean clearTargetPosition(double setSpeedMod) {
		return this.setTargetPosition((ChunkCoordinates)null, setSpeedMod);
	}
	
    // ========== Position Valid ==========
    public boolean isTargetPositionValid() {
		return isTargetPositionValid(this.targetPosition);
	}
    
	public boolean isTargetPositionValid(ChunkCoordinates targetPosition) {
		if(targetPosition == null)
			return true;
		if(this.host.canSwim() && this.host.isSwimmable(targetPosition.posX, targetPosition.posY, targetPosition.posZ))
			return true;
		if(!this.host.canFly())
			return false;
		if(!this.host.worldObj.isAirBlock(targetPosition.posX, targetPosition.posY, targetPosition.posZ))
			return false;
		if(targetPosition.posY < 3)
			return false;
		return true;
	}
	
	// ========== Is At Target Position ==========
	public boolean atTargetPosition(){
		if(targetPosition != null)
			return this.host.getDistance(targetPosition.posX, targetPosition.posY, targetPosition.posZ) < 2;
		return true;
	}
	
	
	// ==================================================
  	//                      Update
  	// ==================================================
	public void updateFlight() {
		if(this.targetPosition == null) return;
		double dirX = (double)this.targetPosition.posX + 0.5D - this.host.posX;
		double dirY = (double)this.targetPosition.posY + 0.1D - this.host.posY;
		double dirZ = (double)this.targetPosition.posZ + 0.5D - this.host.posZ;
		
		double speed = this.host.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() * 2;
		this.host.motionX += ((Math.signum(dirX) * speed - this.host.motionX) * 0.10000000149011612D*0.3D) * speedModifier;
		this.host.motionY += ((Math.signum(dirY) * speed - this.host.motionY) * 0.10000000149011612D*0.3D) * speedModifier;
		this.host.motionZ += ((Math.signum(dirZ) * speed - this.host.motionZ) * 0.10000000149011612D*0.3D) * speedModifier;
		float fullAngle = (float)(Math.atan2(this.host.motionZ, this.host.motionX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.host.rotationYaw);
		this.host.moveForward = 0.5F;
		if(this.faceMovement && this.host.getAttackTarget() != null && (this.host.motionX > 0.05F || this.host.motionZ > 0.05F))
			this.host.rotationYaw += angle;
	}
	
	
	// ==================================================
  	//                      Movement
  	// ==================================================
	public void flightMovement(float moveStrafe, float moveForward) {
		if(this.host.isInWater() && !host.canSwim()) {
            this.host.moveFlying(moveStrafe, moveForward, 0.02F);
            this.host.moveEntity(this.host.motionX, this.host.motionY, this.host.motionZ);
            this.host.motionX *= 0.800000011920929D;
            this.host.motionY *= 0.800000011920929D;
            this.host.motionZ *= 0.800000011920929D;
        }
        else if(this.host.handleLavaMovement() && !host.canSwim()) {
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
                Block block = this.host.worldObj.getBlock(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.boundingBox.minY) - 1, MathHelper.floor_double(this.host.posZ));
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            float flyingMotion = 0.16277136F / (motion * motion * motion);
            this.host.moveFlying(moveStrafe, moveForward, this.host.onGround ? 0.1F * flyingMotion : (float)(0.02F * this.speedModifier));
            
            motion = 0.91F;
            if(this.host.onGround) {
            	motion = 0.54600006F;
                Block block = this.host.worldObj.getBlock(MathHelper.floor_double(this.host.posX), MathHelper.floor_double(this.host.boundingBox.minY) - 1, MathHelper.floor_double(this.host.posZ));
                if(block != null)
                	motion = block.slipperiness * 0.91F;
            }
            
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
		double distX = targetPosition.posX - this.host.posX;
		double distZ = targetPosition.posZ - this.host.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI);// - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.host.rotationYaw);
		if(angle > 30.0F) angle = 30.0F;
		if(angle < -30.0F) angle = -30.0F;
		this.host.renderYawOffset = this.host.rotationYaw += angle;
	}

	// ========== Rotate to Target ==========
    public void adjustRotationToTarget(ChunkCoordinates target) {
		double distX = target.posX - this.host.posX;
		double distZ = target.posZ - this.host.posZ;
		float fullAngle = (float)(Math.atan2(distZ, distX) * 180.0D / Math.PI) - 90.0F;
		float angle = MathHelper.wrapAngleTo180_float(fullAngle - this.host.rotationYaw);
		this.host.rotationYaw += angle; 
    }
}
