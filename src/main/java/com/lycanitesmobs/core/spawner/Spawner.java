package com.lycanitesmobs.core.spawner;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import com.lycanitesmobs.core.spawner.location.SpawnLocation;
import com.lycanitesmobs.core.spawner.trigger.SpawnTrigger;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class Spawner {
    /** Spawners are loaded from JSON and operate around their Triggers, Conditions and Locations. **/

    /** A list of all Spawn Conditions which determine if the Triggers should be active or not. **/
    public List<SpawnCondition> conditions = new ArrayList<>();

    /** A list of all Spawn Triggers which listen to events and ticks and attempt a spawn, if conditions are met. **/
    public List<SpawnTrigger> triggers = new ArrayList<>();

    /** A list of all Spawn Locations which are used to determine where this Spawner can actually spawn the mob, some Triggers require a specific spawn location other provide an area. **/
    public List<SpawnLocation> locations = new ArrayList<>();

    /** A list of all Mobs that can be spawned by this spawner. **/
    public List<MobInfo> mobs = new ArrayList<>();

    /** Can be set to false to completely disable this Spawner. **/
    public boolean enabled = true;

    /** Determines how many Conditions must be met. If 0 or less all are required. **/
    public int conditionsRequired = 0;

    /** Determines how a Location is chosen if there are multiple. Can be: order or random. **/
    public String multipleLocations = "order";


    /** Loads this Spawner from the provided JSON data. **/
    public void fromJSON(JsonObject json) {
        // TODO Load from JSON.
    }


    /** Returns true if Triggers are allowed to operate for this Spawner. **/
    public boolean canSpawn(World world, EntityPlayer player) {
        if(!this.enabled) {
            return false;
        }

        int conditionsMet = 0;
        for(SpawnCondition condition : this.conditions) {
            if(condition.isMet(world, player)) {
                conditionsMet++;
                if(this.conditionsRequired > 0 && conditionsMet >= this.conditionsRequired) {
                    return true;
                }
            }
            else {
                if(this.conditionsRequired <= 0) {
                    return false;
                }
            }
        }

        return false;
    }


    /**
     * Starts a new spawn, usually called by Triggers.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
     * @param triggerPos The location that the spawn was triggered, usually used as the center for spawning around or on.
     * @return True on a successful spawn and false on failure.
     **/
    public boolean doSpawn(World world, EntityPlayer player, BlockPos triggerPos) {
        return false;
    }


    /**
     * Gets a weighted random mob to spawn.
     * @param world The World to spawn in.
     * @param player The player that is being spawned around.
     * @param triggerPos The location that the spawn was triggered, usually used as the center for spawning around or on.
     * @return True on a successful spawn and false on failure.
     **/
    public boolean getMob(World world, EntityPlayer player, BlockPos triggerPos) {
        return false;
    }
}
