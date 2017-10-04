package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpawnTrigger {
    /** Spawn Triggers can respond to various events or ticks and will start a spawn. **/

    /** The Spawner using this Trigger. **/
    public Spawner spawner;


    /** Constructor **/
    public SpawnTrigger(Spawner spawner) {
    	this.spawner = spawner;
	}

	/** Loads this Spawn Condition from the provided JSON data. **/
	public void fromJSON(JsonObject json) {
		// TODO Read SpawnTrigger JSON.
	}

	/** Triggers an actual spawn. **/
	public void trigger(World world, EntityPlayer player, BlockPos triggerPos, int level) {
		this.spawner.doSpawn(world, player, triggerPos, level);
	}

	/** Used to apply effects, etc any mobs that have spawned because of this trigger. **/
	public void applyToEntity(EntityLiving entityLiving) {
		return;
	}
}
