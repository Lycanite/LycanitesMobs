package com.lycanitesmobs.freshwatermobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorAquaPulse;
import com.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorWaterJet;
import com.lycanitesmobs.freshwatermobs.entity.*;
import com.lycanitesmobs.freshwatermobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = FreshwaterMobs.modid, name = FreshwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class FreshwaterMobs extends Submod {
	
	public static final String modid = "freshwatermobs";
	public static final String name = "Lycanites Freshwater Mobs";
	
	// Instance:
	@Instance(modid)
	public static FreshwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.freshwatermobs.ClientSubProxy", serverSide="com.lycanitesmobs.freshwatermobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Freshwater Mobs", 7)
				.setDimensionBlacklist("-1,1").setBiomes("ALL, -OCEAN, -BEACH").setDungeonThemes("WATER, DUNGEON")
				.setEggName("freshwateregg");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("freshwaterspawn", new ItemFreshwaterEgg());
		ObjectManager.addItem("soulstonefreshwater", new ItemSoulstoneFreshwater(group));

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
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
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
		newMob.spawnInfo.setSpawnTypes("WATERANIMAL").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(6).setGroupLimits(1, 6).setLightDark(true, false).setDungeonWeight(0);
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
				.addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"))
				.addSubspecies(new Subspecies("mottled", "rare"));
		newMob.spawnInfo.setSpawnTypes("WATER").setBiomes("GROUP,-SWAMP")
				.setSpawnWeight(6).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());
		ObjectManager.addProjectile("waterjet", EntityWaterJet.class, ObjectManager.getItem("waterjetcharge"), new DispenserBehaviorWaterJet());
		ObjectManager.addProjectile("waterjetend", EntityWaterJetEnd.class);
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("silexmeatraw"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("silexmeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("silexmeatraw"), new ItemStack(ObjectManager.getItem("silexmeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {

	}
}
