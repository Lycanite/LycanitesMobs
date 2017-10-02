package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class WorldSpawnCondition extends SpawnCondition {

    /** How many world days must have gone by, can accept fractions such as 5.25 for 5 and a quarter days. **/
    public double worldDay = -1;

    /** The time of the current world day. **/
    public int dayTime = -1;

    /** The weather, can be: any, clear, rain, storm or rainstorm. **/
    public String weather = "any";

    /** The minimum difficulty level. **/
    public short difficultyMin = -1;


    @Override
    public boolean isMet(World world, EntityPlayer player) {
        return super.isMet(world, player);
    }

    @Override
    public void fromJSON(JsonObject json) {
        super.fromJSON(json);
    }
}
