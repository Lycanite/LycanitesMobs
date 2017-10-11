package com.lycanitesmobs.core.spawner.trigger;

import com.google.gson.JsonObject;
import com.lycanitesmobs.ExtendedPlayer;
import com.lycanitesmobs.core.spawner.Spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class SleepSpawnTrigger extends SpawnTrigger {

	/** How long (in ticks) until this trigger can be started again, this is done per player. **/
	public int cooldown = 1200;


	/** Stores the age tick of each player when they last attempted to sleep. **/
	protected Map<EntityPlayer, Integer> playerUsedTicks = new HashMap<>();


	/** Constructor **/
	public SleepSpawnTrigger(Spawner spawner) {
		super(spawner);
	}


	@Override
	public void loadFromJSON(JsonObject json) {
		if(json.has("cooldown"))
			this.cooldown = json.get("cooldown").getAsInt();

		super.loadFromJSON(json);
	}



	/** Called every time a player attempts to use a bed. **/
	public boolean onSleep(World world, EntityPlayer player, BlockPos spawnPos) {
		// Cooldown:
		if(this.cooldown > -1) {
			ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(player);
			if(playerExt != null) {
				int lastUsedTicks = 0;
				if (!this.playerUsedTicks.containsKey(player)) {
					this.playerUsedTicks.put(player, lastUsedTicks);
				} else {
					lastUsedTicks = this.playerUsedTicks.get(player);
				}
				if(playerExt.timePlayed - lastUsedTicks < this.cooldown) {
					return false;
				}
			}

		}

		// Chance:
		if(this.chance < 1 && player.getRNG().nextDouble() > this.chance) {
			return false;
		}

		return this.trigger(world, player, spawnPos, 0);
	}
}
