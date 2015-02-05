package lycanite.lycanitesmobs.api.mobevent;

import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import net.minecraft.block.material.Material;

public class SharedMobEvents {

    // ==================================================
    //              Create Shared Mob Events
    // ==================================================
    public static void createSharedEvents(GroupInfo group) {
        // ========== Generic ==========
        // Bamstorm:
        MobEventBase bamstormEvent = new MobEventBamstorm("bamstorm", group);

        SpawnTypeBase bamLandSpawner = new SpawnTypeLand("bamstorm_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        bamLandSpawner.materials = new Material[] {Material.air};
        bamLandSpawner.ignoreBiome = true;
        bamLandSpawner.ignoreLight = true;
        bamLandSpawner.forceSpawning = true;
        bamLandSpawner.ignoreMobConditions = true;
        bamLandSpawner.addSpawn(MobInfo.getFromName("kobold"));
        bamLandSpawner.addSpawn(MobInfo.getFromName("conba"));
        bamLandSpawner.addSpawn(MobInfo.getFromName("belph"));
        bamLandSpawner.addSpawn(MobInfo.getFromName("geken"));
        bamstormEvent.addSpawner(bamLandSpawner);

        SpawnTypeBase bamSkySpawner = new SpawnTypeSky("bamstorm_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        bamSkySpawner.materials = new Material[] {Material.air};
        bamSkySpawner.ignoreBiome = true;
        bamSkySpawner.ignoreLight = true;
        bamSkySpawner.forceSpawning = true;
        bamSkySpawner.ignoreMobConditions = true;
        bamSkySpawner.addSpawn(MobInfo.getFromName("zephyr"));
        bamSkySpawner.addSpawn(MobInfo.getFromName("manticore"));
        bamstormEvent.addSpawner(bamSkySpawner);

        MobEventManager.instance.addWorldEvent(bamstormEvent);


        // Raptor Rampage:
        MobEventBase event = new MobEventBase("raptorrampage", group);

        SpawnTypeBase spawner = new SpawnTypeLand("raptorrampage_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        spawner.materials = new Material[] {Material.air};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("ventoraptor"));
        spawner.addSpawn(MobInfo.getFromName("uvaraptor"));
        event.addSpawner(spawner);

        spawner = new SpawnTypeSky("raptorrampage_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        spawner.materials = new Material[] {Material.air};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("ventoraptor"));
        spawner.addSpawn(MobInfo.getFromName("uvaraptor"));
        event.addSpawner(spawner);

        MobEventManager.instance.addWorldEvent(event);


        // Arachnophobia:
        event = new MobEventBase("arachnophobia", group);

        spawner = new SpawnTypeLand("arachnophobia_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(10);
        spawner.materials = new Material[] {Material.air};
        spawner.ignoreBiome = true;
        spawner.ignoreLight = true;
        spawner.forceSpawning = true;
        spawner.ignoreMobConditions = true;
        spawner.addSpawn(MobInfo.getFromName("tarantula"));
        spawner.addSpawn(MobInfo.getFromName("frostweaver"));
        spawner.addSpawn(MobInfo.getFromName("trite"));
        event.addSpawner(spawner);

        MobEventManager.instance.addWorldEvent(event);




        // ========== Halloween ==========
        // Halloween:
        MobEventBase halloweenEvent = new MobEventHalloween("halloween", group);

        SpawnTypeBase halloweenLandSpawner = new SpawnTypeLand("halloween_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        halloweenLandSpawner.materials = new Material[] {Material.air};
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
        halloweenEvent.addSpawner(halloweenLandSpawner);

        SpawnTypeBase halloweenSkySpawner = new SpawnTypeSky("halloween_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        halloweenSkySpawner.materials = new Material[] {Material.air};
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

        MobEventManager.instance.addWorldEvent(halloweenEvent);




        // ========== Yuletide ==========
        // Roasting:
        MobEventBase roastingEvent = new MobEventRoasting("roasting", group);

        SpawnTypeBase roastingLandSpawner = new SpawnTypeLand("roasting_land")
                .setChance(0.5D).setBlockLimit(32).setMobLimit(3);
        roastingLandSpawner.materials = new Material[] {Material.air};
        roastingLandSpawner.ignoreBiome = true;
        roastingLandSpawner.ignoreLight = true;
        roastingLandSpawner.forceSpawning = true;
        roastingLandSpawner.ignoreMobConditions = true;
        roastingLandSpawner.addSpawn(MobInfo.getFromName("lobber"));
        roastingLandSpawner.addSpawn(MobInfo.getFromName("wendigo"));
        roastingEvent.addSpawner(roastingLandSpawner);

        SpawnTypeBase roastingSkySpawner = new SpawnTypeSky("roasting_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(6);
        roastingSkySpawner.materials = new Material[] {Material.air};
        roastingSkySpawner.ignoreBiome = true;
        roastingSkySpawner.ignoreLight = true;
        roastingSkySpawner.forceSpawning = true;
        roastingSkySpawner.ignoreMobConditions = true;
        roastingSkySpawner.addSpawn(MobInfo.getFromName("cinder"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("afrit"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("reiver"));
        roastingSkySpawner.addSpawn(MobInfo.getFromName("arix"));
        roastingEvent.addSpawner(roastingSkySpawner);

        MobEventManager.instance.addWorldEvent(roastingEvent, "yule");


        // Rudolph:
        MobEventBase rudolphEvent = new MobEventRudolph("rudolph", group);

        SpawnTypeBase rudolphLandSpawner = new SpawnTypeLand("rudolph_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        rudolphLandSpawner.materials = new Material[] {Material.air};
        rudolphLandSpawner.ignoreBiome = true;
        rudolphLandSpawner.ignoreLight = true;
        rudolphLandSpawner.forceSpawning = true;
        rudolphLandSpawner.ignoreMobConditions = true;
        rudolphLandSpawner.addSpawn(MobInfo.getFromName("jabberwock"));
        rudolphEvent.addSpawner(rudolphLandSpawner);

        MobEventManager.instance.addWorldEvent(rudolphEvent, "yule");


        // Salty Tree:
        MobEventBase saltytreeEvent = new MobEventSaltyTree("saltytree", group);

        SpawnTypeBase saltytreeLandSpawner = new SpawnTypeLand("saltytree_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
        saltytreeLandSpawner.materials = new Material[] {Material.air};
        saltytreeLandSpawner.ignoreBiome = true;
        saltytreeLandSpawner.ignoreLight = true;
        saltytreeLandSpawner.forceSpawning = true;
        saltytreeLandSpawner.ignoreMobConditions = true;
        saltytreeLandSpawner.addSpawn(MobInfo.getFromName("ent"));
        saltytreeLandSpawner.addSpawn(MobInfo.getFromName("trent"));
        saltytreeEvent.addSpawner(saltytreeLandSpawner);

        MobEventManager.instance.addWorldEvent(saltytreeEvent, "yule");


        // Satan Claws:
        MobEventBase satanclawsEvent = new MobEventSatanClaws("satanclaws", group);

        SpawnTypeBase satanclawsSkySpawner = new SpawnTypeSky("satanclaws_sky")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        satanclawsSkySpawner.materials = new Material[] {Material.air};
        satanclawsSkySpawner.ignoreBiome = true;
        satanclawsSkySpawner.ignoreLight = true;
        satanclawsSkySpawner.forceSpawning = true;
        satanclawsSkySpawner.ignoreMobConditions = true;
        satanclawsSkySpawner.addSpawn(MobInfo.getFromName("phantom"));
        satanclawsEvent.addSpawner(satanclawsSkySpawner);

        MobEventManager.instance.addWorldEvent(satanclawsEvent, "yule");
    }
}
