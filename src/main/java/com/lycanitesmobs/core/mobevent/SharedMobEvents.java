package com.lycanitesmobs.core.mobevent;

import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.spawning.SpawnTypeLand;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import net.minecraft.block.material.Material;

public class SharedMobEvents {

    // ==================================================
    //              Create Shared Mob Events
    // ==================================================
    public static void createSharedEvents(GroupInfo group) {
        // ========== Generic ==========
        // Bamstorm:
        MobEventBase bamstormEvent = new MobEventBamstorm("bamstorm", group);

        SpawnTypeBase landSpawner = new SpawnTypeLand("bamstorm_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        landSpawner.materials = new Material[] {Material.AIR};
        landSpawner.ignoreBiome = true;
        landSpawner.ignoreLight = true;
        landSpawner.forceSpawning = true;
        landSpawner.ignoreMobConditions = true;
        landSpawner.addSpawn(MobInfo.getFromName("kobold"));
        landSpawner.addSpawn(MobInfo.getFromName("conba"));
        landSpawner.addSpawn(MobInfo.getFromName("belph"));
        landSpawner.addSpawn(MobInfo.getFromName("geken"));
        landSpawner.addSpawn(MobInfo.getFromName("aglebemu"));
        bamstormEvent.addSpawner(landSpawner);

        SpawnTypeBase skySpawner = new SpawnTypeSky("bamstorm_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        skySpawner.materials = new Material[] {Material.AIR};
        skySpawner.ignoreBiome = true;
        skySpawner.ignoreLight = true;
        skySpawner.forceSpawning = true;
        skySpawner.ignoreMobConditions = true;
        skySpawner.addSpawn(MobInfo.getFromName("zephyr"));
        skySpawner.addSpawn(MobInfo.getFromName("manticore"));
        bamstormEvent.addSpawner(skySpawner);

        MobEventManager.INSTANCE.addWorldEvent(bamstormEvent);


        // Raptor Rampage:
        MobEventBase event = new MobEventBase("raptorrampage", group);

        SpawnTypeBase spawner = new SpawnTypeLand("raptorrampage_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        spawner.materials = new Material[] {Material.AIR};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("ventoraptor"));
        spawner.addSpawn(MobInfo.getFromName("uvaraptor"));
        event.addSpawner(spawner);

        spawner = new SpawnTypeSky("raptorrampage_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        spawner.materials = new Material[] {Material.AIR};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("ventoraptor"));
        spawner.addSpawn(MobInfo.getFromName("uvaraptor"));
        event.addSpawner(spawner);

        MobEventManager.INSTANCE.addWorldEvent(event);


        // Arachnophobia:
        event = new MobEventBase("arachnophobia", group);

        spawner = new SpawnTypeLand("arachnophobia_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        spawner.materials = new Material[] {Material.AIR};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("tarantula"));
        spawner.addSpawn(MobInfo.getFromName("frostweaver"));
        spawner.addSpawn(MobInfo.getFromName("sutiramu"));
        spawner.addSpawn(MobInfo.getFromName("trite"));
        event.addSpawner(spawner);

        MobEventManager.INSTANCE.addWorldEvent(event);


        // Blade Flurry:
        event = new MobEventBladeFlurry("bladeflurry", group);
        spawner = new SpawnTypeLand("bladeflurry")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(8);
        spawner.materials = new Material[] {Material.AIR};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("clink"));
        spawner.addSpawn(MobInfo.getFromName("quillbeast"));
        event.addSpawner(spawner);

        MobEventManager.INSTANCE.addWorldEvent(event);



        // Root Riot:
        event = new MobEventRootRiot("rootriot", group);

        landSpawner = new SpawnTypeLand("rootriot_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        landSpawner.materials = new Material[] {Material.AIR};
        landSpawner.ignoreBiome = true;
        landSpawner.ignoreLight = true;
        landSpawner.forceSpawning = true;
        landSpawner.ignoreMobConditions = true;
        landSpawner.addSpawn(MobInfo.getFromName("shambler"));
        landSpawner.addSpawn(MobInfo.getFromName("triffid"));
        if(landSpawner.hasSpawns())
            event.addSpawner(landSpawner);

        skySpawner = new SpawnTypeSky("rootriot_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        skySpawner.materials = new Material[] {Material.AIR};
        skySpawner.ignoreBiome = true;
        skySpawner.ignoreLight = true;
        skySpawner.forceSpawning = true;
        skySpawner.ignoreMobConditions = true;
        skySpawner.addSpawn(MobInfo.getFromName("spriggan"));
        if(skySpawner.hasSpawns())
            event.addSpawner(skySpawner);

        if(event.hasSpawners())
            MobEventManager.INSTANCE.addWorldEvent(event);



        // Primal Fury:
        event = new MobEventPrimalFury("primalfury", group);

        landSpawner = new SpawnTypeLand("primalfury_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        landSpawner.materials = new Material[] {Material.AIR};
        landSpawner.ignoreBiome = true;
        landSpawner.ignoreLight = true;
        landSpawner.forceSpawning = true;
        landSpawner.ignoreMobConditions = true;
        landSpawner.addSpawn(MobInfo.getFromName("warg"));
        landSpawner.addSpawn(MobInfo.getFromName("barghest"));
        landSpawner.addSpawn(MobInfo.getFromName("maug"));
        landSpawner.addSpawn(MobInfo.getFromName("feradon"));
        landSpawner.addSpawn(MobInfo.getFromName("dawon"));
        if(landSpawner.hasSpawns())
            event.addSpawner(landSpawner);

        if(event.hasSpawners())
            MobEventManager.INSTANCE.addWorldEvent(event);



        // Dragons Roar:
        event = new MobEventDragonsRoar("dragonsroar", group);

        landSpawner = new SpawnTypeLand("dragonsroar_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(1);
        landSpawner.materials = new Material[] {Material.AIR};
        landSpawner.ignoreBiome = true;
        landSpawner.ignoreLight = true;
        landSpawner.forceSpawning = true;
        landSpawner.ignoreMobConditions = true;
        landSpawner.addSpawn(MobInfo.getFromName("ignibus"));
        landSpawner.addSpawn(MobInfo.getFromName("morock"));
        landSpawner.addSpawn(MobInfo.getFromName("quetzodracl"));
        if(landSpawner.hasSpawns())
            event.addSpawner(landSpawner);

        skySpawner = new SpawnTypeSky("dragonsroar_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(1);
        skySpawner.materials = new Material[] {Material.AIR};
        skySpawner.ignoreBiome = true;
        skySpawner.ignoreLight = true;
        skySpawner.forceSpawning = true;
        skySpawner.ignoreMobConditions = true;
        skySpawner.addSpawn(MobInfo.getFromName("quetzodracl"));
        skySpawner.addSpawn(MobInfo.getFromName("morock"));
        skySpawner.addSpawn(MobInfo.getFromName("ignibus"));
        if(skySpawner.hasSpawns())
            event.addSpawner(skySpawner);

        if(event.hasSpawners())
            MobEventManager.INSTANCE.addWorldEvent(event);




        // ========== Halloween ==========
        // Halloween:
        MobEventBase halloweenEvent = new MobEventHalloween("halloween", group);

        SpawnTypeBase halloweenLandSpawner = new SpawnTypeLand("halloween_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        halloweenLandSpawner.materials = new Material[] {Material.AIR};
        halloweenLandSpawner.ignoreBiome = true;
        halloweenLandSpawner.ignoreLight = true;
        halloweenLandSpawner.forceSpawning = true;
        halloweenLandSpawner.ignoreMobConditions = true;
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("ghoulzombie"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("cryptzombie"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("belph"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("behemoth"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("ent"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("trent"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("lurker"));
        halloweenLandSpawner.addSpawn(MobInfo.getFromName("triffid"));
        halloweenEvent.addSpawner(halloweenLandSpawner);

        SpawnTypeBase halloweenSkySpawner = new SpawnTypeSky("halloween_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        halloweenSkySpawner.materials = new Material[] {Material.AIR};
        halloweenSkySpawner.ignoreBiome = true;
        halloweenSkySpawner.ignoreLight = true;
        halloweenSkySpawner.forceSpawning = true;
        halloweenSkySpawner.ignoreMobConditions = true;
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("nethersoul"));
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("cacodemon"));
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("afrit"));
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("grue"));
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("phantom"));
        halloweenSkySpawner.addSpawn(MobInfo.getFromName("epion"));
        halloweenEvent.addSpawner(halloweenSkySpawner);

        MobEventManager.INSTANCE.addWorldEvent(halloweenEvent);




        // ========== Yuletide ==========
        // Roasting:
        MobEventBase roastingEvent = new MobEventRoasting("roasting", group);

        SpawnTypeBase roastingLandSpawner = new SpawnTypeLand("roasting_land")
                .setChance(0.5D).setBlockLimit(32).setMobLimit(3);
        roastingLandSpawner.materials = new Material[] {Material.AIR};
        roastingLandSpawner.ignoreBiome = true;
        roastingLandSpawner.ignoreLight = true;
        roastingLandSpawner.forceSpawning = true;
        roastingLandSpawner.ignoreMobConditions = true;
        roastingLandSpawner.addSpawn(MobInfo.getFromName("lobber"));
        roastingLandSpawner.addSpawn(MobInfo.getFromName("wendigo"));
        roastingEvent.addSpawner(roastingLandSpawner);

        SpawnTypeBase roastingSkySpawner = new SpawnTypeSky("roasting_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(6);
        roastingSkySpawner.materials = new Material[] {Material.AIR};
        roastingSkySpawner.ignoreBiome = true;
        roastingSkySpawner.ignoreLight = true;
        roastingSkySpawner.forceSpawning = true;
        roastingSkySpawner.ignoreMobConditions = true;
        roastingSkySpawner.addSpawn(MobInfo.getFromName("cinder"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("afrit"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("reiver"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("arix"));
        roastingEvent.addSpawner(roastingSkySpawner);

        MobEventManager.INSTANCE.addWorldEvent(roastingEvent, "yule");


        // Rudolph:
        MobEventBase rudolphEvent = new MobEventRudolph("rudolph", group);

        SpawnTypeBase rudolphLandSpawner = new SpawnTypeLand("rudolph_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        rudolphLandSpawner.materials = new Material[] {Material.AIR};
        rudolphLandSpawner.ignoreBiome = true;
        rudolphLandSpawner.ignoreLight = true;
        rudolphLandSpawner.forceSpawning = true;
        rudolphLandSpawner.ignoreMobConditions = true;
        rudolphLandSpawner.addSpawn(MobInfo.getFromName("jabberwock"));
        rudolphEvent.addSpawner(rudolphLandSpawner);

        MobEventManager.INSTANCE.addWorldEvent(rudolphEvent, "yule");


        // Salty Tree:
        MobEventBase saltytreeEvent = new MobEventSaltyTree("saltytree", group);

        SpawnTypeBase saltytreeLandSpawner = new SpawnTypeLand("saltytree_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        saltytreeLandSpawner.materials = new Material[] {Material.AIR};
        saltytreeLandSpawner.ignoreBiome = true;
        saltytreeLandSpawner.ignoreLight = true;
        saltytreeLandSpawner.forceSpawning = true;
        saltytreeLandSpawner.ignoreMobConditions = true;
        saltytreeLandSpawner.addSpawn(MobInfo.getFromName("ent"));
        saltytreeLandSpawner.addSpawn(MobInfo.getFromName("trent"));
        saltytreeEvent.addSpawner(saltytreeLandSpawner);

        MobEventManager.INSTANCE.addWorldEvent(saltytreeEvent, "yule");


        // Satan Claws:
        MobEventBase satanclawsEvent = new MobEventSatanClaws("satanclaws", group);

        SpawnTypeBase satanclawsSkySpawner = new SpawnTypeSky("satanclaws_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        satanclawsSkySpawner.materials = new Material[] {Material.AIR};
        satanclawsSkySpawner.ignoreBiome = true;
        satanclawsSkySpawner.ignoreLight = true;
        satanclawsSkySpawner.forceSpawning = true;
        satanclawsSkySpawner.ignoreMobConditions = true;
        satanclawsSkySpawner.addSpawn(MobInfo.getFromName("phantom"));
        satanclawsEvent.addSpawner(satanclawsSkySpawner);

        MobEventManager.INSTANCE.addWorldEvent(satanclawsEvent, "yule");
    }
}
