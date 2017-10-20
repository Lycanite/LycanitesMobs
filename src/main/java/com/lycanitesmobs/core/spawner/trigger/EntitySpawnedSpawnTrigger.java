package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EntitySpawnedSpawnTrigger extends EntitySpawnTrigger {

	/** Constructor **/
	public EntitySpawnedSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		super.loadFromJSON(json);
	}


	/** Called every time an entity is spawned. **/
	public void onEntitySpawned(EntityLiving spawnedEntity) {

		// Check Entity:
		if(!this.isMatchingEntity(spawnedEntity)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && spawnedEntity.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(spawnedEntity.getEntityWorld(), null, spawnedEntity.getPosition(), 0);
	}
}
