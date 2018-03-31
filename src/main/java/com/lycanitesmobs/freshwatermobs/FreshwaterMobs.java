package com.lycanitesmobs.freshwatermobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorWaterJet;
import com.lycanitesmobs.freshwatermobs.entity.EntityWaterJet;
import com.lycanitesmobs.freshwatermobs.entity.EntityWaterJetEnd;
import com.lycanitesmobs.freshwatermobs.item.ItemFreshwaterEgg;
import com.lycanitesmobs.freshwatermobs.item.ItemScepterWaterJet;
import com.lycanitesmobs.freshwatermobs.item.ItemSoulstoneFreshwater;
import com.lycanitesmobs.freshwatermobs.item.ItemWaterJetCharge;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
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

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
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
		ObjectManager.addItem("silexmeatcooked", new ItemCustomFood("silexmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(cookedFoodEffectID, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("silexmeatcooked"));

		ObjectManager.addItem("lapisfishandchips", new ItemCustomFood("lapisfishandchips", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(cookedFoodEffectID, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("lapisfishandchips"));

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

		// Projectiles:
		ObjectManager.addProjectile("waterjet", EntityWaterJet.class, ObjectManager.getItem("waterjetcharge"), new DispenserBehaviorWaterJet());
		ObjectManager.addProjectile("waterjetend", EntityWaterJetEnd.class, false);
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
