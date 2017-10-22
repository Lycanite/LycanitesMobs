package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class SpawnTrigger {
    /** Spawn Triggers can respond to various events or ticks and will start a spawn. **/

    /** The Spawner using this Trigger. **/
    public Spawner spawner;

	/** How much this influences the Spawner's trigger count by, usually just 1. If 0 this Trigger will instead completely reset the count, if negative it will reduce the count. **/
	public int count = 1;

	/** The Chance of triggering. **/
	public double chance = 1;

	/** A list of Trigger specific Conditions that this Trigger will check. **/
	public List<SpawnCondition> conditions = new ArrayList<>();

	/** Determines how many Trigger specific Conditions must be met. If 0 or less all are required. **/
	public int conditionsRequired = 0;


	/** Creates a Spawn Trigger from the provided JSON data. **/
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
		else if("entitySpawned".equalsIgnoreCase(type)) {
			spawnTrigger = new EntitySpawnedSpawnTrigger(spawner);
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
		else if("mobEvent".equalsIgnoreCase(type)) {
			spawnTrigger = new MobEventSpawnTrigger(spawner);
		}

		spawnTrigger.loadFromJSON(json);
		return spawnTrigger;
	}


    /** Constructor **/
    public SpawnTrigger(Spawner spawner) {
    	this.spawner = spawner;
	}

	/** Loads this Spawn Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("count"))
			this.count = json.get("count").getAsInt();

		if(json.has("chance"))
			this.chance = json.get("chance").getAsDouble();

		if(json.has("conditionsRequired"))
			this.conditionsRequired = json.get("conditionsRequired").getAsInt();

		if(json.has("conditions")) {
			JsonArray jsonArray = json.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject conditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
				this.conditions.add(spawnCondition);
			}
		}
	}


	/** Triggers an actual spawn. **/
	public boolean trigger(World world, EntityPlayer player, BlockPos triggerPos, int level) {
		// Check Trigger Specific Conditions:
		if(!this.triggerConditionsMet(world, player)) {
			return false;
		}

		LycanitesMobs.printDebug("JSONSpawner", "Trigger Fired: " + this + " for: " + this.spawner.name);
		return this.spawner.trigger(world, player, triggerPos, level, this.count);
	}

	/** Checks all Conditions specific to this Trigger. **/
	public boolean triggerConditionsMet(World world, EntityPlayer player) {
		if(this.conditions.size() == 0) {
			return true;
		}

		int conditionsMet = 0;
		int conditionsRequired = this.conditionsRequired > 0 ? this.conditionsRequired : this.conditions.size();
		for(SpawnCondition condition : this.conditions) {
			boolean met = condition.isMet(world, player);
			if(met) {
				if(++conditionsMet >= conditionsRequired) {
					return true;
				}
			}
		}
		return false;
	}

	/** Used to apply effects, etc any mobs that have spawned because of this trigger. **/
	public void applyToEntity(EntityLiving entityLiving) {
		return;
	}
}
