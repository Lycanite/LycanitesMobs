package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class WorldSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering after so many world ticks. **/

	/** How many ticks between trigger attempts. **/
	public double tickRate = 400;

	/** The minimum distance the player must be away from the last tick position. **/
	public double lastTickDistanceMin = -1;

	/** The maximum distance the player must be away from the last tick position. **/
	public double lastTickDistanceMax = -1;

	/** If true, the world time is used instead of a generic world tick count. **/
	public boolean useWorldTime = false;


	/** Constructor **/
	public WorldSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("tickRate"))
			this.tickRate = json.get("tickRate").getAsDouble();

		if(json.has("lastTickDistanceMin"))
			this.lastTickDistanceMin = json.get("lastTickDistanceMin").getAsDouble();

		if(json.has("lastTickDistanceMax"))
			this.lastTickDistanceMax = json.get("lastTickDistanceMax").getAsDouble();

		if(json.has("useWorldTime"))
			this.useWorldTime = json.get("useWorldTime").getAsBoolean();

		super.loadFromJSON(json);
	}


	/** Called every world tick from where players are active. **/
	public void onTick(World world, BlockPos position, long ticks) {
		// World Time:
		if(this.useWorldTime && world.getWorldTime() % 24000 != this.tickRate) {
			return;
		}

		// Tick Rate:
		else if(ticks == 0 || ticks % this.tickRate != 0) {
			return;
		}

		// Chance:
		if(this.chance < 1 && world.rand.nextDouble() > this.chance) {
			return;
		}

		this.trigger(world, null, position, 0, 0);
	}
}
