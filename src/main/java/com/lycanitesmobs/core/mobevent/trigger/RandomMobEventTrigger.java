package com.lycanitesmobs.core.mobevent.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEvent;
import net.minecraft.world.World;

public class RandomMobEventTrigger extends MobEventTrigger {
	/** The weight of this Trigger, higher weights means that it is most likely to get picked. **/
	public int weight = 8;

	/** The priority of this Trigger. This is usually 1 but if a higher priority Trigger has its conditions met, it will be picked first regardless of its weight. Seasonal mob events use this to override standard events. **/
	public int priority = 1;


	/** Constructor **/
	public RandomMobEventTrigger(MobEvent mobEvent) {
		super(mobEvent);
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("weight"))
			this.weight = json.get("weight").getAsInt();

		if(json.has("priority"))
			this.priority = json.get("priority").getAsInt();

		super.loadFromJSON(json);
	}
}
