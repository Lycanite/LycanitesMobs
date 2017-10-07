package com.lycanitesmobs.core.spawner.trigger;

import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SleepSpawnTrigger extends SpawnTrigger {

	/** Constructor **/
	public SleepSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	/** Called every time a player attempts to use a bed. **/
	public boolean onSleep(World world, EntityPlayer player, BlockPos spawnPos) {
		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return false;
		}

		return this.trigger(world, player, spawnPos, 0);
	}
}
