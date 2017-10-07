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

	/** How much this influences the Spawner's trigger count by, usually just 1. If 0 this Trigger will instead completely reset the count, if negative it will reduce the count. **/
	public int count = 1;

	/** The Chance of triggering. **/
	public double chance = 1;


    /** Constructor **/
    public SpawnTrigger(Spawner spawner) {
    	this.spawner = spawner;
	}

	/** Loads this Spawn Condition from the provided JSON data. **/
	public void fromJSON(JsonObject json) {
		// TODO Read SpawnTrigger JSON.
	}

	/** Triggers an actual spawn. **/
	public boolean trigger(World world, EntityPlayer player, BlockPos triggerPos, int level) {
		return this.spawner.trigger(world, player, triggerPos, level, this.count);
	}

	/** Used to apply effects, etc any mobs that have spawned because of this trigger. **/
	public void applyToEntity(EntityLiving entityLiving) {
		return;
	}
}
