package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TickMobEventTrigger extends MobEventTrigger {
	/** The chance of this Trigger successfully firing every trigger tick. **/
	public double chance;

	/** How many ticks between trigger attempts. **/
	public double tickRate = 400;


	/** Constructor **/
	public TickMobEventTrigger(MobEvent mobEvent) {
		super(mobEvent);
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("chance"))
			this.chance = json.get("chance").getAsDouble();

		if(json.has("tickRate"))
			this.tickRate = json.get("tickRate").getAsDouble();

		super.loadFromJSON(json);
	}


	/** Called every world tick. **/
	public void onTick(World world, long ticks) {
		// Tick Rate:
		if(ticks == 0 || ticks % this.tickRate != 0) {
			return;
		}

		// Can Trigger:
		if(!this.canTrigger(world, null)) {
			return;
		}

		// Chance:
		if(this.chance < 1 && world.rand.nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, null,new BlockPos(0, 0, 0), 0);
	}
}
