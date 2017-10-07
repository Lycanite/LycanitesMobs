package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;

public class TickSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering after so many player ticks. **/

	/** How many ticks between trigger attempts. **/
	public double tickRate = 400;


	/** Constructor **/
	public TickSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("tickRate"))
			this.tickRate = json.get("tickRate").getAsDouble();

		super.loadFromJSON(json);
	}


	/** Called every player tick. **/
	public void onTick(EntityPlayer player, long ticks) {
		// Tick Rate:
		if(ticks == 0 || ticks % this.tickRate != 0) {
			return;
		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return;
		}

		this.trigger(player.getEntityWorld(), player, player.getPosition(), 0);
	}
}
