package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public abstract class SpawnCondition {
    /** Spawn Conditions determine if the Spawner is allowed to be triggered. **/

    /** Returns true if this Spawn Condition is met allowing the Spawner to use its Triggers. **/
    public boolean isMet(World world, EntityPlayer player) {
        return true;
    }

    /** Loads this Spawn Condition from the provided JSON data. **/
    public void fromJSON(JsonObject json) {

    }
}
