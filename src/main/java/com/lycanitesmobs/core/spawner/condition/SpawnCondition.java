package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class SpawnCondition {
    /** Spawn Conditions determine if the Spawner is allowed to be triggered. **/


    /** Loads this Spawn Condition from the provided JSON data. **/
    public static SpawnCondition createFromJSON(JsonObject json) {
		String type = json.get("type").getAsString();
		SpawnCondition spawnCondition = null;

		if("world".equalsIgnoreCase(type)) {
			spawnCondition = new WorldSpawnCondition();
		}
		else if("player".equalsIgnoreCase(type)) {
			spawnCondition = new PlayerSpawnCondition();
		}
		else if("event".equalsIgnoreCase(type)) {
			spawnCondition = new EventSpawnCondition();
		}
		else if("date".equalsIgnoreCase(type)) {
			spawnCondition = new DateSpawnCondition();
		}
		else if("group".equalsIgnoreCase(type)) {
			spawnCondition = new GroupSpawnCondition();
		}

		spawnCondition.loadFromJSON(json);
		return spawnCondition;
    }


	/** Loads this Spawn Condition from the provided JSON data. **/
	public void loadFromJSON(JsonObject json) {
		// No base properties to load currently.
	}


    /**
	 * Returns true if this Spawn Condition is met allowing the Spawner to use its Triggers.
	 * @param world The world to check the conditions of or in.
	 * @param player The player to check the conditions of, can be null for some types.
	 * @param position The positon to check the conditions from, can be null for some types.
	 * @return True if conditions are met.
	 */
    public boolean isMet(World world, EntityPlayer player, BlockPos position) {
        return true;
    }
}
