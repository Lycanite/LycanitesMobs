package com.lycanitesmobs.saltwatermobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.saltwatermobs.item.ItemSaltwaterEgg;
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

@Mod(modid = SaltwaterMobs.modid, name = SaltwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class SaltwaterMobs extends Submod {
	
	public static final String modid = "saltwatermobs";
	public static final String name = "Lycanites Saltwater Mobs";
	
	// Instance:
	@Instance(modid)
	public static SaltwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.saltwatermobs.ClientSubProxy", serverSide="com.lycanitesmobs.saltwatermobs.CommonSubProxy")
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
		group = new GroupInfo(this, "Saltwater Mobs", 8)
				.setDimensionBlacklist("-1,1").setBiomes("OCEAN, BEACH").setDungeonThemes("WATER, DUNGEON")
				.setEggName("saltwaterspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("saltwaterspawn", new ItemSaltwaterEgg());

		Potion rawFoodEffectID = MobEffects.BLINDNESS;
		if(ObjectManager.getPotionEffect("weight") != null)
			rawFoodEffectID = ObjectManager.getPotionEffect("weight");
		ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
		ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));

		ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.WATER_BREATHING, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));

		ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.WATER_BREATHING, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));

		ObjectManager.addItem("raikotreat", new ItemTreat("raikotreat", group));
		ObjectManager.addItem("roatreat", new ItemTreat("roatreat", group));
		ObjectManager.addItem("hermatreat", new ItemTreat("hermatreat", group));
		ObjectManager.addItem("quetzodracltreat", new ItemTreat("quetzodracltreat", group));
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("saltwaterspawn"), new DispenserBehaviorMobEggCustom());

		// No Projectiles
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("ikameatraw"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("ikameatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("ikameatraw"), new ItemStack(ObjectManager.getItem("ikameatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {

	}
}
