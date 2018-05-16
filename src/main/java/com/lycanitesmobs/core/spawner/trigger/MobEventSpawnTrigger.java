package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.mobevent.MobEventPlayerServer;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

import static sun.audio.AudioPlayer.player;

/** Called when a Mob Event is active. **/
public class MobEventSpawnTrigger extends SpawnTrigger {

	/** If set, this is the name of the mob event that must be active for this trigger. **/
	public String eventName = "";

	/** The current time percentage of the active Mob Event. This is a fraction of the Mob Event's duration rounded down. So at 1200 ticks (1 minute) 0.5 would be 30 seconds in. **/
	public double eventTime = 0;

	/** Constructor **/
	public MobEventSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("eventName"))
			this.eventName = json.get("eventName").getAsString();

		if(json.has("eventTime"))
			this.eventTime = json.get("eventTime").getAsDouble();

		super.loadFromJSON(json);
	}


	/** Called every world tick when an event is active. **/
	public void onTick(World world, MobEventPlayerServer mobEventPlayerServer) {
		if(mobEventPlayerServer == null) {
			return;
		}

		// Event Spawner Check:
		if(!"".equals(this.eventName) && !this.eventName.equals(mobEventPlayerServer.mobEvent.name)) {
			return;
		}

		// Trigger Tick:
		int triggerTick = Math.round((float)mobEventPlayerServer.mobEvent.duration * (float)this.eventTime);
		if(mobEventPlayerServer.ticks != triggerTick) {
			return;
		}

		this.trigger(world, mobEventPlayerServer.player, mobEventPlayerServer.origin, mobEventPlayerServer.level, 0);
	}
}
