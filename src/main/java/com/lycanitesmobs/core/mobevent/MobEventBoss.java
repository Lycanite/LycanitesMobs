package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import com.lycanitesmobs.core.info.GroupInfo;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.World;

public class MobEventBoss extends MobEventBase {


    // ==================================================
    //                     Constructor
    // ==================================================
	public MobEventBoss(String name, GroupInfo group) {
		super(name, group);
	}


    // ==================================================
    //                       Config
    // ==================================================
    /** Makes this event read the config. **/
    @Override
    public void loadFromConfig() {
        ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        this.duration = 30 * 20;
        this.forceSpawning = true;
        this.forceNoDespawn = true;
        this.minDay = config.getInt("Boss Event Day Minimums", this.name, this.minDay);

        // Event Dimensions:
        ConfigSpawning.SpawnDimensionSet eventDimensions = config.getDimensions("Boss Event Dimensions", this.name + " Dimensions", "");
        this.dimensionBlacklist = eventDimensions.dimensionIDs;
        this.dimensionTypes = eventDimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("Boss Event Dimensions", this.name + " Dimensions Whitelist Mode", false);
    }


    // ==================================================
    //                      Enabled
    // ==================================================
    @Override
    public boolean isEnabled() {
        ConfigSpawning config = ConfigSpawning.getConfig(LycanitesMobs.group, "mobevents");
        return config.getBool("Boss Events Enabled", this.name, true);
    }


    // ==================================================
    //                      Can Start
    // ==================================================
	/*
	 * Returns true if this event is able to start on the provided extended world.
	 */
    @Override
    public boolean canStart(World world, ExtendedWorld worldExt) {
        if(world.provider == null)
            return false;

        boolean validDimension = false;
        // Check Types:
        for(String eventDimensionType : this.dimensionTypes) {
            if("ALL".equalsIgnoreCase(eventDimensionType)) {
                validDimension = true;
            }
            else if("VANILLA".equalsIgnoreCase(eventDimensionType)) {
                validDimension = world.provider.getDimension() > -2 && world.provider.getDimension() < 2;
            }
        }

        // Check IDs:
        if(!validDimension) {
            validDimension =  !this.dimensionWhitelist;
            for(int eventDimension : this.dimensionBlacklist) {
                if(world.provider.getDimension() == eventDimension) {
                    validDimension = this.dimensionWhitelist;
                    break;
                }
            }
        }

        return validDimension;
    }


    // ==================================================
    //                       Start
    // ==================================================
    @Override
    public void onStart(World world, int rank) {
        super.onStart(world, rank);
    }


    // ==================================================
    //                     Boss Setup
    // ==================================================
    /** This is the main boss setup, this will create the arena, decorate it, move players and finally, summon the boss. The time value is used to determine what to do. **/
    public void bossSetup(int time, World world, int originX, int originY, int originZ, int rank) {

    }


    // ==================================================
    //                       Finish
    // ==================================================
    @Override
    public void onFinish(World world, int rank) {
        super.onFinish(world, rank);
    }
	
	
    // ==================================================
    //                   Spawn Effects
    // ==================================================
    @Override
	public void onSpawn(EntityLiving entity, int rank) {
        if(entity instanceof EntityCreatureBase) {
            EntityCreatureBase entityCreature = (EntityCreatureBase)entity;
            entityCreature.setSubspecies(rank, true);
        }
	}


    // ==================================================
    //                  Get Event Players
    // ==================================================
    public MobEventServer getServerEvent(World world) {
        return new MobEventServerBoss(this, world);
    }
    public MobEventClient getClientEvent(World world) {
        return new MobEventClientBoss(this, world);
    }
}
