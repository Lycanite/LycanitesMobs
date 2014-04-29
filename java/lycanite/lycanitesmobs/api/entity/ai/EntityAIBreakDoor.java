package lycanite.lycanitesmobs.api.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
	//Properties:
    private int breakingTime;
    private int lastBreakTime = -1;

	
	// ==================================================
 	//                    Constructor
 	// ==================================================
    public EntityAIBreakDoor(EntityLiving setHost) {
        super(setHost);
    }

	
	// ==================================================
 	//                  Should Execute
 	// ==================================================
    public boolean shouldExecute() {
    	if(!super.shouldExecute())
    		return false;
    	if(!this.host.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"))
    		return false;
    	
        return !this.targetDoor.isDoorOpen(this.host.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ);
    }

	
	// ==================================================
 	//                      Start
 	// ==================================================
    public void startExecuting() {
        super.startExecuting();
        this.breakingTime = 0;
    }

	
	// ==================================================
 	//                Continue Executing
 	// ==================================================
    public boolean continueExecuting() {
        double distance = this.host.getDistanceSq((double)this.entityPosX, (double)this.entityPosY, (double)this.entityPosZ);
        return this.breakingTime <= 240 && !this.targetDoor.isDoorOpen(this.host.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ) && distance < 4.0D;
    }

	
	// ==================================================
 	//                      Reset
 	// ==================================================
    public void resetTask() {
        super.resetTask();
        this.host.worldObj.destroyBlockInWorldPartially(this.host.getEntityId(), this.entityPosX, this.entityPosY, this.entityPosZ, -1);
    }

	
	// ==================================================
 	//                     Update
 	// ==================================================
    public void updateTask() {
        super.updateTask();

        if(this.host.getRNG().nextInt(20) == 0)
            this.host.worldObj.playAuxSFX(1010, this.entityPosX, this.entityPosY, this.entityPosZ, 0);

        ++this.breakingTime;
        int breaking = (int)((float)this.breakingTime / 240.0F * 10.0F);

        if(breaking != this.lastBreakTime) {
            this.host.worldObj.destroyBlockInWorldPartially(this.host.entityId, this.entityPosX, this.entityPosY, this.entityPosZ, breaking);
            this.lastBreakTime = breaking;
        }

        if(this.breakingTime == 240 && this.host.worldObj.difficultySetting == EnumDifficulty.HARD) {
            this.host.worldObj.setBlockToAir(this.entityPosX, this.entityPosY, this.entityPosZ);
            this.host.worldObj.playAuxSFX(1012, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
            this.host.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, this.targetDoor.blockID);
        }
    }
}
