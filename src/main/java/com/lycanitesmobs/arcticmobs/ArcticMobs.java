package com.lycanitesmobs.arcticmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.arcticmobs.block.*;
import com.lycanitesmobs.arcticmobs.dispenser.*;
import com.lycanitesmobs.arcticmobs.entity.*;
import com.lycanitesmobs.arcticmobs.item.*;
import com.lycanitesmobs.arcticmobs.mobevent.MobEventSubZero;
import com.lycanitesmobs.arcticmobs.mobevent.MobEventWintersGrasp;
import com.lycanitesmobs.arcticmobs.worldgen.WorldGeneratorArctic;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.spawning.SpawnTypeLand;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = ArcticMobs.modid, name = ArcticMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class ArcticMobs extends Submod {
	
	public static final String modid = "arcticmobs";
	public static final String name = "Lycanites Arctic Mobs";
	
	// Instance:
	@Instance(modid)
	public static ArcticMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.arcticmobs.ClientSubProxy", serverSide="com.lycanitesmobs.arcticmobs.CommonSubProxy")
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
		GameRegistry.registerWorldGenerator(new WorldGeneratorArctic(), 0);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Arctic Mobs", 6)
				.setDimensionBlacklist("-1,1").setBiomes("COLD, SNOWY, CONIFEROUS, -END").setDungeonThemes("FROZEN, DUNGEON")
				.setEggName("arcticspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		AssetManager.addSound("ooze", group, "block.ooze");
		ObjectManager.addDamageSource("ooze", new DamageSource("ooze"));
		Fluid fluid = ObjectManager.addFluid("ooze");
		fluid.setLuminosity(10).setDensity(3000).setViscosity(5000).setTemperature(0);
		ObjectManager.addBlock("ooze", new BlockFluidOoze(fluid));

		ObjectManager.addItem("arcticspawn", new ItemArcticEgg());

		ObjectManager.addItem("yetimeatraw", new ItemCustomFood("yetimeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yetimeatraw"));

		ObjectManager.addItem("yetimeatcooked", new ItemCustomFood("yetimeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.RESISTANCE, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yetimeatcooked"));

		ObjectManager.addItem("palesoup", new ItemCustomFood("palesoup", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.RESISTANCE, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("palesoup"));

		ObjectManager.addItem("frostyfur", new ItemFrostyFur());

		ObjectManager.addItem("frostboltcharge", new ItemFrostboltCharge());
		ObjectManager.addItem("frostboltscepter", new ItemScepterFrostbolt(), 2, 1, 1);
		ObjectManager.addItem("frostwebcharge", new ItemFrostwebCharge());
		ObjectManager.addItem("frostwebscepter", new ItemScepterFrostweb(), 2, 1, 1);
		ObjectManager.addItem("tundracharge", new ItemTundraCharge());
		ObjectManager.addItem("tundrascepter", new ItemScepterTundra(), 2, 1, 1);
		ObjectManager.addItem("icefirecharge", new ItemIcefireCharge());
		ObjectManager.addItem("icefirescepter", new ItemScepterIcefire(), 2, 1, 1);
		ObjectManager.addItem("blizzardcharge", new ItemBlizzardCharge());
		ObjectManager.addItem("blizzardscepter", new ItemScepterBlizzard(), 2, 1, 1);

		ObjectManager.addItem("arixtreat", new ItemTreat("arixtreat", group));
		ObjectManager.addItem("serpixtreat", new ItemTreat("serpixtreat", group));
		ObjectManager.addItem("maugtreat", new ItemTreat("maugtreat", group));

		ObjectManager.addItem("bucketooze", new ItemBucketOoze(fluid).setContainerItem(Items.BUCKET));
	}

	@Override
	public void createBlocks() {
		ObjectManager.addBlock("frostweb", new BlockFrostweb());

		AssetManager.addSound("frostcloud", group, "block.frostcloud");
		ObjectManager.addBlock("frostcloud", new BlockFrostCloud());

		AssetManager.addSound("frostfire", group, "block.frostfire");
		ObjectManager.addBlock("frostfire", new BlockFrostfire());

		AssetManager.addSound("icefire", group, "block.icefire");
		ObjectManager.addBlock("icefire", new BlockIcefire());
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("arcticspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "reiver", EntityReiver.class, 0xDDEEFF, 0x99DDEE)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY").setBlockCost(8)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "frostweaver", EntityFrostweaver.class, 0xAADDFF, 0x226699)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(10).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "yeti", EntityYeti.class, 0xEEEEFF, 0x000099)
				.setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE, ANIMAL").setDespawn(false)
				.setSpawnWeight(10).setAreaLimit(5).setGroupLimits(1, 4).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "wendigo", EntityWendigo.class, 0xCCCCFF, 0x0055FF)
				.setPeaceful(false).setSummonCost(8).setDungeonLevel(2)
				.addSubspecies(new Subspecies("keppel", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, OOZE").setBlockCost(16)
				.setSpawnWeight(4).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "arix", EntityArix.class, 0xDDDDFF, 0x9999FF)
				.setPeaceful(false).setTameable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY, OOZE").setBlockCost(16)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "serpix", EntitySerpix.class, 0xCCEEFF, 0x0000BB)
				.setPeaceful(false).setTameable(true).setSummonCost(8).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, OOZE").setBlockCost(32)
				.setSpawnWeight(4).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "maug", EntityMaug.class, 0xc9cccd, 0x52504e)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		// Projectiles
		ObjectManager.addProjectile("frostbolt", EntityFrostbolt.class, ObjectManager.getItem("frostboltcharge"), new DispenserBehaviorFrostbolt());
		ObjectManager.addProjectile("frostweb", EntityFrostweb.class, ObjectManager.getItem("frostwebcharge"), new DispenserBehaviorFrostweb());
		ObjectManager.addProjectile("tundra", EntityTundra.class, ObjectManager.getItem("tundracharge"), new DispenserBehaviorTundra());
		ObjectManager.addProjectile("icefireball", EntityIcefireball.class, ObjectManager.getItem("icefirecharge"), new DispenserBehaviorIcefire());
		ObjectManager.addProjectile("blizzard", EntityBlizzard.class, ObjectManager.getItem("blizzardcharge"), new DispenserBehaviorBlizzard());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("yetimeatraw"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("yetimeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("yetimeatraw"), new ItemStack(ObjectManager.getItem("yetimeatcooked"), 1), 0.5f);
	}

	@Override
	public void createMobEvents() {
		MobEventBase mobEvent = new MobEventSubZero("subzero", this.group);
		SpawnTypeBase eventSpawner = new SpawnTypeSky("subzero")
				.setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		eventSpawner.materials = new Material[] {Material.AIR};
		eventSpawner.ignoreBiome = true;
		eventSpawner.ignoreLight = true;
		eventSpawner.forceSpawning = true;
		eventSpawner.ignoreMobConditions = true;
		eventSpawner.addSpawn(MobInfo.getFromName("reiver"));
		mobEvent.addSpawner(eventSpawner);
		MobEventManager.INSTANCE.addWorldEvent(mobEvent);

		mobEvent = new MobEventWintersGrasp("wintersgrasp", this.group);
		mobEvent.minDay = 10;
		eventSpawner = new SpawnTypeLand("wintersgrasp")
				.setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		eventSpawner.materials = new Material[] {Material.AIR};
		eventSpawner.ignoreBiome = true;
		eventSpawner.ignoreLight = true;
		eventSpawner.forceSpawning = true;
		eventSpawner.ignoreMobConditions = true;
		eventSpawner.addSpawn(MobInfo.getFromName("wendigo"));
		eventSpawner.addSpawn(MobInfo.getFromName("serpix"), 2);
		mobEvent.addSpawner(eventSpawner);
		MobEventManager.INSTANCE.addWorldEvent(mobEvent);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
	}
}
