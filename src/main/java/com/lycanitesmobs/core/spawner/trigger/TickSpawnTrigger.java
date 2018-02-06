package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;
import java.util.Map;

public class TickSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering after so many player ticks. **/

	/** How many ticks between trigger attempts. **/
	public double tickRate = 400;

	/** The minimum distance the player must be away from the last tick position. **/
	public double lastTickDistanceMin = -1;

	/** The maximum distance the player must be away from the last tick position. **/
	public double lastTickDistanceMax = -1;

	/** If true, the world time that the player is in is used instead of the player's ticks. **/
	public boolean useWorldTime = false;

	/** Stores the position of a player from the last tick for calculating distance. **/
	protected Map<EntityPlayer, BlockPos> lastTickPositions = new HashMap<>();


	/** Constructor **/
	public TickSpawnTrigger(Spawner spawner) {
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


	/** Called every player tick. **/
	public void onTick(EntityPlayer player, long ticks) {
		// World Time:
		if(this.useWorldTime && player.getEntityWorld().getWorldTime() % 24000 != this.tickRate) {
			return;
		}

		// Tick Rate:
		else if(ticks == 0 || ticks % this.tickRate != 0) {
			return;
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		// Last Tick Distance:
		if(this.lastTickDistanceMin > -1 || this.lastTickDistanceMax > -1) {
			BlockPos playerPos = player.getPosition();
			if (!this.lastTickPositions.containsKey(player)) {
				this.lastTickPositions.put(player, playerPos);
			}
			else {
				playerPos = this.lastTickPositions.get(player);
			}
			double lastTickDistance = player.getPosition().distanceSq(playerPos);
			if(this.lastTickDistanceMin > -1 && lastTickDistance < this.lastTickDistanceMin) {
				return;
			}
			if(this.lastTickDistanceMax > -1 && lastTickDistance > this.lastTickDistanceMax) {
				return;
			}
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition(), 0, 0);
	}
}
