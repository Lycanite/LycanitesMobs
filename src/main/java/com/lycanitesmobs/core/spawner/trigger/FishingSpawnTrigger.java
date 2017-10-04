package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class FishingSpawnTrigger extends SpawnTrigger {

	/** The Chance of triggering. **/
	public double chance = 1;

	/** The current hook entity to copy velocities from when spawning. **/
	protected Entity hookEntity;

	/** Constructor **/
	public FishingSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	/** Called every time a player fishes up an item. **/
	public void onFished(EntityPlayer player, int ticks) {
		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition(), 0);
	}

	@Override
	public void applyToEntity(EntityLiving entityLiving) {
		super.applyToEntity(entityLiving);
		if(this.hookEntity != null) {
			entityLiving.setVelocity(this.hookEntity.motionX, this.hookEntity.motionY, this.hookEntity.motionZ);
		}
	}
}
