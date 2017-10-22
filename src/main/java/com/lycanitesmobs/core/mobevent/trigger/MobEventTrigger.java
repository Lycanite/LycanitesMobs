package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.mobevent.MobEvent;
import com.lycanitesmobs.core.spawner.Spawner;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import com.lycanitesmobs.core.spawner.trigger.*;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/** Mob Event Triggers can respond to various events or ticks and will start a Mob Event. **/
public abstract class MobEventTrigger {

    /** The Mob Event using this Trigger. **/
    public MobEvent mobEvent;

	/** A list of Spawn Conditions that this Trigger will check. **/
	public List<SpawnCondition> conditions = new ArrayList<>();

	/** Determines how many Trigger specific Conditions must be met. If 0 or less all are required. **/
	public int conditionsRequired = 0;


	/** Creates a Mob Event Trigger from the provided JSON data. **/
	public static MobEventTrigger createFromJSON(JsonObject json, MobEvent mobEvent) {
		String type = json.get("type").getAsString();
		MobEventTrigger mobEventTrigger = null;

		if("random".equalsIgnoreCase(type)) {
			mobEventTrigger = new RandomMobEventTrigger(mobEvent);
		}
		else if("tick".equalsIgnoreCase(type)) {
			mobEventTrigger = new TickMobEventTrigger(mobEvent);
		}
		else if("altar".equalsIgnoreCase(type)) {
			mobEventTrigger = new AltarMobEventTrigger(mobEvent);
		}

		mobEventTrigger.loadFromJSON(json);
		return mobEventTrigger;
	}


    /** Constructor **/
    public MobEventTrigger(MobEvent mobEvent) {
    	this.mobEvent = mobEvent;
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
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


	/** Returns if this trigger can be triggered (checks conditions, etc). **/
	public boolean canTrigger(World world, EntityPlayer player) {
		if(!this.mobEvent.canStart(world, player))
			return false;
		return this.triggerConditionsMet(world, player);
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


	/** Triggers an actual spawn, this does not check conditions, it just triggers. **/
	public boolean trigger(World world, EntityPlayer player, BlockPos pos, int level) {
		LycanitesMobs.printDebug("MobEvents", "Trigger Fired: " + this + " for: " + this.mobEvent.name);
		return this.mobEvent.trigger(world, player, pos, level);
	}
}
