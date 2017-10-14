package com.lycanitesmobs.mountainmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.mountainmobs.entity.*;
import com.lycanitesmobs.mountainmobs.item.*;
import com.lycanitesmobs.mountainmobs.mobevent.MobEventBoulderDash;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorArcaneLaserStorm;
import com.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorBoulderBlast;
import com.lycanitesmobs.mountainmobs.entity.*;
import com.lycanitesmobs.mountainmobs.info.AltarInfoCelestialGeonach;
import com.lycanitesmobs.mountainmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = MountainMobs.modid, name = MountainMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class MountainMobs extends Submod {
	
	public static final String modid = "mountainmobs";
	public static final String name = "Lycanites Mountain Mobs";
	
	// Instance:
	@Instance(modid)
	public static MountainMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.mountainmobs.ClientSubProxy", serverSide="com.lycanitesmobs.mountainmobs.CommonSubProxy")
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
		group = new GroupInfo(this, "Mountain Mobs", 5)
				.setDimensionBlacklist("-1,1").setBiomes("MOUNTAIN").setDungeonThemes("MOUNTAIN, WASTELAND, NECRO")
				.setEggName("mountainspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("mountainspawn", new ItemMountainEgg());
		ObjectManager.addItem("soulstonemountain", new ItemSoulstoneMountain(group));

		ObjectManager.addItem("boulderblastcharge", new ItemBoulderBlastCharge());
		ObjectManager.addItem("boulderblastscepter", new ItemScepterBoulderBlast(), 2, 1, 1);
		ObjectManager.addItem("arcanelaserstormcharge", new ItemArcaneLaserStormCharge());
		ObjectManager.addItem("arcanelaserstormscepter", new ItemScepterArcaneLaserStorm(), 2, 1, 1);

		ObjectManager.addItem("yalemeatraw", new ItemCustomFood("yalemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.MINING_FATIGUE, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yalemeatraw"));

		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.HASTE, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));

		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.HASTE, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("peakskebab"));

		ObjectManager.addItem("barghesttreat", new ItemTreat("barghesttreat", group));
		ObjectManager.addItem("beholdertreat", new ItemTreat("beholdertreat", group));
		ObjectManager.addItem("wildkintreat", new ItemTreat("wildkintreat", group));
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("mountainspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "jabberwock", EntityJabberwock.class, 0x662222, 0xFFFFAA)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "troll", EntityTroll.class, 0x007711, 0xEEEEEE)
				.setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "yale", EntityYale.class, 0xFFEEAA, 0xFFDD77)
				.setPeaceful(true).setSummonCost(1).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE, ANIMAL").setDespawn(false)
				.setSpawnWeight(14).setAreaLimit(5).setGroupLimits(1, 4).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "geonach", EntityGeonach.class, 0x443333, 0xBBBBCC)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("keppel", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"))
				.addSubspecies(new Subspecies("celestial", "rare"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "beholder", EntityBeholder.class, 0x442211, 0x44AA33)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "barghest", EntityBarghest.class, 0xD08237, 0x00070A)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "wildkin", EntityWildkin.class, 0x433929, 0x766f5a)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("boulderblast", EntityBoulderBlast.class, ObjectManager.getItem("boulderblastcharge"), new DispenserBehaviorBoulderBlast());
		ObjectManager.addProjectile("arcanelaserstorm", EntityArcaneLaserStorm.class, ObjectManager.getItem("arcanelaserstormcharge"), new DispenserBehaviorArcaneLaserStorm());
		ObjectManager.addProjectile("arcanelaser", EntityArcaneLaser.class);
		ObjectManager.addProjectile("arcanelaserend", EntityArcaneLaserEnd.class);
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("yalemeatraw"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("yalemeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("yalemeatraw"), new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1), 0.5f);
	}

	@Override
	public void createMobEvents() {
		if(MobInfo.getFromName("geonach") != null) {
			MobEventBase mobEvent = new MobEventBoulderDash("boulderdash", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("boulderdash")
					.setChance(1.0D).setBlockLimit(32).setMobLimit(3);
			eventSpawner.materials = new Material[] {Material.AIR};
			eventSpawner.ignoreBiome = true;
			eventSpawner.ignoreLight = true;
			eventSpawner.forceSpawning = true;
			eventSpawner.ignoreMobConditions = true;
			eventSpawner.addSpawn(MobInfo.getFromName("geonach"));
			mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
		}

		AltarInfo celestialGeonachAltar = new AltarInfoCelestialGeonach("CelestialGeonachAltar");
		AltarInfo.addAltar(celestialGeonachAltar);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.CREATURE, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.CREATURE, this.group.biomes);
	}
}
