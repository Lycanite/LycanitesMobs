package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.player.EntityPlayer;

public class TickSpawnTrigger extends SpawnTrigger {
	/** Has a random chance of triggering after so many player ticks. **/

	/** How many ticks between trigger attempts. **/
	public double tickRate = 800;


	/** Constructor **/
	public TickSpawnTrigger(Spawner spawner) {
		super(spawner);
	}

	/** Called every player tick. **/
	public void onTick(EntityPlayer player, int ticks) {
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
