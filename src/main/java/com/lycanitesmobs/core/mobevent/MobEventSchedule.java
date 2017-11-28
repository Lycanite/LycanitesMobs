package com.lycanitesmobs.core.mobevent;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import net.minecraft.world.World;

public class MobEventSchedule {
	/** The Mob Event that this Schedule starts. **/
	public MobEvent mobEvent;

	/** The dimension ID of the world. **/
	public int dimensionId = 0;

	/** The day of the world. **/
	public int worldDay = 0;

	/** The day time (ticks) of the world. **/
	public int dayTime = 100;


	/** Creates a Mob Event Schedule from the provided JSON data. **/
	public static MobEventSchedule createFromJSON(JsonObject json) {
		MobEvent mobEvent = MobEventManager.getInstance().getMobEvent(json.get("eventName").getAsString());
		if(mobEvent == null) {
			return null;
		}
		MobEventSchedule mobEventSchedule = new MobEventSchedule(mobEvent);
		mobEventSchedule.loadFromJSON(json);
		return mobEventSchedule;
	}


	/** Constructor **/
	public MobEventSchedule(MobEvent mobEvent) {
		this.mobEvent = mobEvent;
	}


	/** Loads this Mob Event Trigger from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		if(json.has("dimensionId"))
			this.dimensionId = json.get("dimensionId").getAsInt();

		if(json.has("worldDay"))
			this.worldDay = json.get("worldDay").getAsInt();

		if(json.has("dayTime"))
			this.dayTime = json.get("dayTime").getAsInt();
	}


	/**
	 * Returns true if this scheduled event can start on the provided world.
	 * @return
	 */
	public boolean canStart(World world) {
		if(world == null) {
			return false;
		}

		if(world.provider.getDimension() != this.dimensionId) {
			return false;
		}

		int time = (int)Math.floor(world.getWorldTime() % 24000D);
		int day = (int)(Math.floor(world.getTotalWorldTime()) / 23999D);

		LycanitesMobs.printDebug("", "Day: " + day + "/" + this.worldDay + " Time: " + time + "/" + this.dayTime);
		if(day != this.worldDay) {
			return false;
		}

		if(time != this.dayTime) {
			return false;
		}

		return true;
	}


	/**
	 *
	 * @param worldExt
	 */
	public void start(ExtendedWorld worldExt) {
		worldExt.startWorldEvent(this.mobEvent);
	}
}
