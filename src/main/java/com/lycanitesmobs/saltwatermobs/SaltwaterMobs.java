package com.lycanitesmobs.saltwatermobs;

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
import com.lycanitesmobs.saltwatermobs.entity.*;
import com.lycanitesmobs.saltwatermobs.item.ItemSaltwaterEgg;
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

		ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.WATER_BREATHING, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));

		ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.WATER_BREATHING, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
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
		MobInfo newMob;

		newMob = new MobInfo(group, "lacedon", EntityLacedon.class, 0x000099, 0x2244FF)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATERFLOOR")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "skylus", EntitySkylus.class, 0xFFCCDD, 0xBB2299)
				.setPeaceful(false).setSummonable(true).setSummonCost(3).setDungeonLevel(1)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER, WATERFLOOR")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ika", EntityIka.class, 0x99FFBB, 0x229944)
				.setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER, WATERFLOOR").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "abtu", EntityAbtu.class, 0xFFBB00, 0x44AAFF)
				.setPeaceful(false).setSummonCost(2).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(2).setAreaLimit(32).setGroupLimits(1, 5).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "raiko", EntityRaiko.class, 0xCCCCDD, 0xFF6633)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "roa", EntityRoa.class, 0x222288, 0x222233)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(2)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "herma", EntityHerma.class, 0xe50403, 0xf1c2a1)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(0)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, WATERFLOOR")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "quetzodracl", EntityQuetzodracl.class, 0x229944, 0x99FFBB)
				.setPeaceful(false).setTameable(true).setSummonCost(8).setDungeonLevel(3)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);
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
