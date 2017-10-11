package com.lycanitesmobs.freshwatermobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorAquaPulse;
import com.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorWaterJet;
import com.lycanitesmobs.freshwatermobs.entity.*;
import com.lycanitesmobs.freshwatermobs.item.*;
import com.lycanitesmobs.freshwatermobs.mobevent.MobEventTsunami;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = FreshwaterMobs.modid, name = FreshwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class FreshwaterMobs {
	
	public static final String modid = "freshwatermobs";
	public static final String name = "Lycanites Freshwater Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static FreshwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.freshwatermobs.ClientSubProxy", serverSide="com.lycanitesmobs.freshwatermobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Freshwater Mobs", 7)
				.setDimensionBlacklist("-1,1").setBiomes("ALL, -OCEAN, -BEACH").setDungeonThemes("WATER, DUNGEON")
                .setEggName("freshwateregg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("freshwaterspawn", new ItemFreshwaterEgg());

        Potion rawFoodEffectID = MobEffects.WEAKNESS;
        if(ObjectManager.getPotionEffect("penetration") != null)
            rawFoodEffectID = ObjectManager.getPotionEffect("penetration");
        ObjectManager.addItem("silexmeatraw", new ItemCustomFood("silexmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
        ObjectLists.addItem("rawfish", ObjectManager.getItem("silexmeatraw"));

        Potion cookedFoodEffectID = MobEffects.SPEED;
        if(ObjectManager.getPotionEffect("swiftswimming") != null)
            cookedFoodEffectID = ObjectManager.getPotionEffect("swiftswimming");
        ObjectManager.addItem("silexmeatcooked", new ItemCustomFood("silexmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(cookedFoodEffectID, 10, 2, 1.0F).setAlwaysEdible());
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("silexmeatcooked"));

        ObjectManager.addItem("lapisfishandchips", new ItemCustomFood("lapisfishandchips", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(cookedFoodEffectID, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("lapisfishandchips"));

        ObjectManager.addItem("aquapulsecharge", new ItemAquaPulseCharge());
        ObjectManager.addItem("aquapulsescepter", new ItemScepterAquaPulse(), 2, 1, 1);
		ObjectManager.addItem("waterjetcharge", new ItemWaterJetCharge());
		ObjectManager.addItem("waterjetscepter", new ItemScepterWaterJet(), 2, 1, 1);

        ObjectManager.addItem("stridertreat", new ItemTreat("stridertreat", group));
		ObjectManager.addItem("threshertreat", new ItemTreat("threshertreat", group));
		ObjectManager.addItem("ioraytreat", new ItemTreat("ioraytreat", group));
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("freshwaterspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "jengu", EntityJengu.class, 0x000099, 0x4444FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "zephyr", EntityZephyr.class, 0xFFFFDD, 0xAABBFF)
                .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("")
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "strider", EntityStrider.class, 0x2c90cd, 0x13ddf1)
                .setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
                .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("WATERFLOOR")
                .setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "silex", EntitySilex.class, 0x263abd, 0x040e75)
                .setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
                .addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("WATER").setDespawn(false)
                .setSpawnWeight(6).setAreaLimit(2).setGroupLimits(1, 6).setLightDark(true, false).setDungeonWeight(0);
        ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "thresher", EntityThresher.class, 0x77747e, 0x3f4567)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ioray", EntityIoray.class, 0x5247ca, 0xccd6ec)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER, WATERFLOOR").setBiomes("GROUP,-SWAMP")
				.setSpawnWeight(4).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "abaia", EntityAbaia.class, 0x537d41, 0xdfb752)
				.setPeaceful(false).setSummonable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER").setBiomes("GROUP,-SWAMP")
				.setSpawnWeight(6).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());
		ObjectManager.addProjectile("waterjet", EntityWaterJet.class, ObjectManager.getItem("waterjetcharge"), new DispenserBehaviorWaterJet());
		ObjectManager.addProjectile("waterjetend", EntityWaterJetEnd.class);
		
		// ========== Register Models ==========
		proxy.registerModels(this.group);
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
        // ========== Load All Mob Info from Configs ==========
        MobInfo.loadAllFromConfigs(this.group);

		// ========== Ore Dictionary ==========
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("silexmeatraw"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("silexmeatcooked"));
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("jengu") != null || MobInfo.getFromName("zephyr") != null) {
			MobEventBase mobEvent = new MobEventTsunami("tsunami", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("tsunami")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        if(MobInfo.getFromName("jengu") != null)
	        	eventSpawner.addSpawn(MobInfo.getFromName("jengu"));
	        if(MobInfo.getFromName("zephyr") != null)
	        	eventSpawner.addSpawn(MobInfo.getFromName("zephyr"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("silexmeatraw"), new ItemStack(ObjectManager.getItem("silexmeatcooked"), 1), 0.5f);
	}
}
