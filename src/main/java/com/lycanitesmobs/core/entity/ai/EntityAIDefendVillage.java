package com.lycanitesmobs.core.entity.ai;

import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.village.Village;

public class EntityAIDefendVillage extends EntityAITargetAttack {

	protected Village village;

	public EntityAIDefendVillage(EntityCreatureBase setHost)
	{
		super(setHost);
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{
		if(this.host.getOwner() != null) {
			return false;
		}

		if(this.village == null) {
			this.village = this.host.getEntityWorld().getVillageCollection().getNearestVillage(new BlockPos(this.host), 32);
		}
		if(this.village == null) {
			return false;
		}

		this.target = this.village.findNearestVillageAggressor(this.host);

		if (this.target instanceof EntityCreeper) {
			return false;
		}
		else if (this.isSuitableTarget(this.target, false)) {
			return true;
		}
		else if (this.host.getRNG().nextInt(20) == 0) {
			this.target = village.getNearestTargetPlayer(this.host);
			return this.isSuitableTarget(this.target, false);
		}
		else {
			return false;
		}
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		super.startExecuting();
	}

}
