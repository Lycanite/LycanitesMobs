package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
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


	/** Loads this Spawn Condition from the provided JSON data. **/
	public static SpawnTrigger createFromJSON(JsonObject json, Spawner spawner) {
		String type = json.get("type").getAsString();
		SpawnTrigger spawnTrigger = null;

		if("tick".equalsIgnoreCase(type)) {
			spawnTrigger = new TickSpawnTrigger(spawner);
		}
		else if("block".equalsIgnoreCase(type)) {
			spawnTrigger = new BlockSpawnTrigger(spawner);
		}
		else if("ore".equalsIgnoreCase(type)) {
			spawnTrigger = new OreBlockSpawnTrigger(spawner);
		}
		else if("crop".equalsIgnoreCase(type)) {
			spawnTrigger = new CropBlockSpawnTrigger(spawner);
		}
		else if("tree".equalsIgnoreCase(type)) {
			spawnTrigger = new TreeBlockSpawnTrigger(spawner);
		}
		else if("kill".equalsIgnoreCase(type)) {
			spawnTrigger = new KillSpawnTrigger(spawner);
		}
		else if("sleep".equalsIgnoreCase(type)) {
			spawnTrigger = new SleepSpawnTrigger(spawner);
		}
		else if("fishing".equalsIgnoreCase(type)) {
			spawnTrigger = new FishingSpawnTrigger(spawner);
		}

		spawnTrigger.loadFromJSON(json);
		return spawnTrigger;
	}


    /** Constructor **/
    public SpawnTrigger(Spawner spawner) {
    	this.spawner = spawner;
	}

	/** Loads this Spawn Condition from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("count"))
			this.count = json.get("count").getAsInt();

		if(json.has("chance"))
			this.chance = json.get("chance").getAsDouble();
	}


	/** Triggers an actual spawn. **/
	public boolean trigger(World world, EntityPlayer player, BlockPos triggerPos, int level) {
		LycanitesMobs.printDebug("JSONSpawner", "Trigger Fired: " + this + " for: " + this.spawner.name);
		return this.spawner.trigger(world, player, triggerPos, level, this.count);
	}

	/** Used to apply effects, etc any mobs that have spawned because of this trigger. **/
	public void applyToEntity(EntityLiving entityLiving) {
		return;
	}
}
