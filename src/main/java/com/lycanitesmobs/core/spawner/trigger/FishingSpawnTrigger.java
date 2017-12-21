package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FishingSpawnTrigger extends SpawnTrigger {

	/** The current hook entity to copy velocities from when spawning. **/
	protected Entity hookEntity;

	/** Constructor **/
	public FishingSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}



	/** Called every time a player fishes up an item. **/
	public void onFished(World world, EntityPlayer player, Entity hookEntity) {
		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		BlockPos spawnPos = player.getPosition().add(0, 0, 1);
		this.hookEntity = hookEntity;
		if(this.hookEntity != null) {
			spawnPos = hookEntity.getPosition();
		}
		this.trigger(world, player, spawnPos, 0, 0);
	}

	@Override
	public void applyToEntity(EntityLiving entityLiving) {
		super.applyToEntity(entityLiving);
		if(this.hookEntity != null) {
			entityLiving.setVelocity(this.hookEntity.motionX, this.hookEntity.motionY, this.hookEntity.motionZ);
		}
	}
}
