package lycanite.lycanitesmobs.api.entity.ai;

import lycanite.lycanitesmobs.api.entity.EntityCreatureBase;
import net.minecraft.entity.ai.EntityAIBase;

public class EntityAIStealth extends EntityAIBase {
	// Targets:
	private EntityCreatureBase host;
	
	// Properties:
	private int stealthTimeMax = 20;
	private int stealthTimeMaxPrev = 20;
	private int stealthTime = 0;
	private int unstealthRate = 4;
	private boolean stealthMove = false;
	private boolean stealthAttack = false;
	
	private boolean unstealth = false;
	
    // ==================================================
 	//                    Constructor
 	// ==================================================
	public EntityAIStealth(EntityCreatureBase setHost) {
		this.host = setHost;
        this.setMutexBits(0);
	}
	
	
    // ==================================================
 	//                    Properties
 	// ==================================================
	public EntityAIStealth setStealthTime(int time) {
		this.stealthTimeMax = time;
		return this;
	}
	public EntityAIStealth setUnstealthRate(int rate) {
		this.unstealthRate = rate;
		return this;
	}
	public EntityAIStealth setStealthMove(boolean flag) {
		this.stealthMove = flag;
		return this;
	}
	public EntityAIStealth setStealthAttack(boolean flag) {
		this.stealthAttack = flag;
		return this;
	}

	
    // ==================================================
 	//                   Should Execute
 	// ==================================================
	@Override
	public boolean shouldExecute() {
		this.unstealth = false;
		if(this.host.getLeashed()) this.unstealth = true;
		
		if(!this.stealthMove) {
			if(!this.host.useDirectNavigator() && !this.host.getNavigator().noPath())
				this.unstealth = true;
			if(this.host.useDirectNavigator() && !this.host.flightNavigator.atTargetPosition())
				this.unstealth = true;
		}
		
		if(!this.stealthAttack && this.host.getAttackTarget() != null)
			this.unstealth = true;
		if(!this.host.canStealth())
			this.unstealth = true;
		
		return !this.unstealth;
	}

	
    // ==================================================
 	//                 Continue Executing
 	// ==================================================
	@Override
	public boolean continueExecuting() {
		if(this.host.getLeashed()) this.unstealth = true;
		
		if(!this.stealthMove) {
			if(!this.host.useDirectNavigator() && !this.host.getNavigator().noPath())
				this.unstealth = true;
			if(this.host.useDirectNavigator() && !this.host.flightNavigator.atTargetPosition())
				this.unstealth = true;
		}
		
		if(!this.stealthAttack && this.host.getAttackTarget() != null)
			this.unstealth = true;
		if(!this.host.canStealth())
			this.unstealth = true;
		
		if(this.unstealth && this.host.getStealth() <= 0)
			return false;
		
		if(this.stealthTimeMaxPrev != this.stealthTimeMax)
			return false;
		
		return true;
	}

	
    // ==================================================
 	//                 Start Executing
 	// ==================================================
	@Override
	public void startExecuting() {
		this.host.setStealth(0F);
		this.stealthTime = 0;
		this.stealthTimeMaxPrev = this.stealthTimeMax;
	}

	
    // ==================================================
 	//                  Reset Task
 	// ==================================================
	@Override
	public void resetTask() {
		this.host.setStealth(0F);
		this.stealthTime = 0;
		this.stealthTimeMaxPrev = this.stealthTimeMax;
	}

	
    // ==================================================
 	//                  Update Task
 	// ==================================================
	@Override
	public void updateTask() {
		float nextStealth = (float)this.stealthTime / (float)this.stealthTimeMax;
		this.host.setStealth(nextStealth);
		
		if(!this.unstealth && this.stealthTime < this.stealthTimeMax)
			this.stealthTime++;
		else if(this.unstealth && this.stealthTime > 0)
			this.stealthTime -= this.unstealthRate;
		//this.stealthTime = Math.min(this.stealthTime, 1);
		//this.stealthTime = Math.max(this.stealthTime, 0);
	}
}
