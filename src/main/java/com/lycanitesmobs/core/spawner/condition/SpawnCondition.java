package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
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


    /** Returns true if this Spawn Condition is met allowing the Spawner to use its Triggers. **/
    public boolean isMet(World world, EntityPlayer player) {
        return true;
    }
}
