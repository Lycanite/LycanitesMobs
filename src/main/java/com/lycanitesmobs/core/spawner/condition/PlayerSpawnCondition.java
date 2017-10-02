package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class PlayerSpawnCondition extends SpawnCondition {

    /** The username of the player. **/
    public String username = "";

    /** The minimum level of the player. **/
    public int levelMin = -1;

    /** The minimum time that the player has been playing. **/
    public int timeMin = -1;


    @Override
    public boolean isMet(World world, EntityPlayer player) {
        return super.isMet(world, player);
    }

    @Override
    public void fromJSON(JsonObject json) {
        super.fromJSON(json);
    }
}
