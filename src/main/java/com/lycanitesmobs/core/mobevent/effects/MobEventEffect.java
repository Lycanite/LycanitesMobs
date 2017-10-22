package com.lycanitesmobs.core.mobevent.effects;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/** Mob Event Effects do various things during a mob event. **/
public abstract class MobEventEffect {

	/** The minimum time in ticks that the event must have been active for. **/
	public double eventTicksMin = 0;

	/** The maximum time in ticks that the event must have been active for. **/
	public double eventTicksMax = 0;

	/** The interval of ticks to trigger this effect. Such as every 20 ticks for every second. **/
	public double eventTicksN = 0;

	/** Creates a Mob Event Trigger from the provided JSON data. **/
	public static MobEventEffect createFromJSON(JsonObject json) {
		String type = json.get("type").getAsString();
		MobEventEffect mobEventEffect = null;

		if("world".equalsIgnoreCase(type)) {
			mobEventEffect = new WorldMobEventEffect();
		}
		else if("structure".equalsIgnoreCase(type)) {
			mobEventEffect = new StructureMobEventEffect();
		}

		mobEventEffect.loadFromJSON(json);
		return mobEventEffect;
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("eventTicksMin"))
			this.eventTicksMin = json.get("eventTicksMin").getAsDouble();

		if(json.has("eventTicksMax"))
			this.eventTicksMax = json.get("eventTicksMax").getAsDouble();

		if(json.has("eventTicksN"))
			this.eventTicksN = json.get("eventTicksN").getAsDouble();
	}

	/** Returns if this effect can activate on the given event update tick. **/
	public boolean canActivate(World world, EntityPlayer player, BlockPos pos, int level, int ticks) {
		// Check Ticks:
		if(this.eventTicksMin >= 0 && ticks < this.eventTicksMin) {
			return false;
		}
		if(this.eventTicksMax >= 0 && ticks > this.eventTicksMax) {
			return false;
		}
		if(this.eventTicksN >= 0 && ticks % this.eventTicksN != 0) {
			return false;
		}

		return true;
	}

	/**
	 * Called when the Mob Event updates.
	 * @param world The world the event is taking place in.
	 * @param player The player that triggered the event, this can be null for world based events where all player based checks will fail.
	 * @param pos Where the event origin will be. This is used by effects for generating structures as well as Mob Event Spawn Triggers and other things.
	 * @param level The level of the event.
	 * @param ticks The ticks that the event has been active for.
	 * @return Returns true if effects should be played. This is used by classes that extend this class.
	 */
	public void onUpdate(World world, EntityPlayer player, BlockPos pos, int level, int ticks) {

	}

	/**
	 * Called whenever a mob is spawned by the mob event. The Spawner must have "mobEventSpawner" set to true for this to be called by it.
	 * @param entity The spawned entity.
	 * @param world The world the event is taking place in.
	 * @param player The player that triggered the event, this can be null for world based events where all player based checks will fail.
	 * @param pos Where the event origin will be. This is used by effects for generating structures as well as Mob Event Spawn Triggers and other things.
	 * @param level The level of the event.
	 * @param ticks The ticks that the event has been active for.
	 * @return Returns true if effects should be played. This is used by classes that extend this class.
	 */
	public void onSpawn(EntityLiving entity, World world, EntityPlayer player, BlockPos pos, int level, int ticks) {

	}
}
