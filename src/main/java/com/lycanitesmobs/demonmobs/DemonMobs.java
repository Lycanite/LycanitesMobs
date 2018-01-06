package com.lycanitesmobs.demonmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.BlockMaker;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.block.BlockSoulcube;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.effects.StructureBuilder;
import com.lycanitesmobs.demonmobs.block.BlockDoomfire;
import com.lycanitesmobs.demonmobs.block.BlockHellfire;
import com.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDemonicLightning;
import com.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDevilstar;
import com.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDoomfireball;
import com.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorHellfireball;
import com.lycanitesmobs.demonmobs.entity.*;
import com.lycanitesmobs.demonmobs.info.AltarInfoAsmodeus;
import com.lycanitesmobs.demonmobs.info.AltarInfoEbonCacodemon;
import com.lycanitesmobs.demonmobs.info.AltarInfoRahovart;
import com.lycanitesmobs.demonmobs.item.*;
import com.lycanitesmobs.demonmobs.mobevents.AsmodeusStructureBuilder;
import com.lycanitesmobs.demonmobs.mobevents.RahovartStructureBuilder;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Biomes;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class DemonMobs extends Submod {
	
	public static final String modid = "demonmobs";
	public static final String name = "Lycanites Demon Mobs";
	
	// Instance:
	@Instance(modid)
	public static DemonMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.demonmobs.ClientSubProxy", serverSide="com.lycanitesmobs.demonmobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);

		AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
		AltarInfo.addAltar(ebonCacodemonAltar);

		AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
		AltarInfo.addAltar(rahovartAltar);
		StructureBuilder.addStructureBuilder(new RahovartStructureBuilder());

		AltarInfo asmodeusAltar = new AltarInfoAsmodeus("AsmodeusAltar");
		AltarInfo.addAltar(asmodeusAltar);
		StructureBuilder.addStructureBuilder(new AsmodeusStructureBuilder());
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Demon Mobs", 11)
				.setDimensionBlacklist("-1").setDimensionWhitelist(true).setBiomes("NETHER").setDungeonThemes("NETHER, NECRO")
				.setEggName("demonspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("demonspawn", new ItemDemonEgg());
		ObjectManager.addItem("soulstonedemonic", new ItemSoulstoneDemonic(group));

		ObjectManager.addItem("doomfirecharge", new ItemDoomfireCharge());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireCharge());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstarCharge());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightningCharge());

		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WITHER, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));

		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.STRENGTH, 10, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));

		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.STRENGTH, 60, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("pinkytreat", new ItemTreat("pinkytreat", group));
		ObjectManager.addItem("cacodemontreat", new ItemTreat("cacodemontreat", group));

		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire(), 2, 1, 1);
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire(), 2, 1, 1);
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar(), 2, 1, 1);
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning(), 2, 1, 1);

		ObjectManager.addItem("wraithsigil", new ItemWraithSigil());
	}

	@Override
	public void createBlocks() {
		ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
		AssetManager.addSound("hellfire", group, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
		AssetManager.addSound("doomfire", group, "block.doomfire");
		ObjectManager.addBlock("doomfire", new BlockDoomfire());

		BlockMaker.addStoneBlocks(group, "demon", Items.NETHER_WART);
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("demonspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "belph", EntityBelph.class, 0x992222, 0x000000)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, PORTAL, NETHER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 4).setLightDark(true, true).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "behemoth", EntityBehemoth.class, 0xFF2222, 0xFF9900)
				.setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, PORTAL, NETHER")
				.setSpawnWeight(2).setAreaLimit(5).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "pinky", EntityPinky.class, 0xFF0099, 0x990000)
				.setPeaceful(true).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, PORTAL, NETHER")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setLightDark(true, true).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "trite", EntityTrite.class, 0xFFFF88, 0x000000)
				.setPeaceful(false).setSummonCost(1).setDungeonLevel(0)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, PORTAL, NETHER")
				.setSpawnWeight(10).setAreaLimit(40).setGroupLimits(1, 10).setLightDark(true, true).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "astaroth", EntityAstaroth.class, 0x999944, 0x0000FF)
				.setPeaceful(false).setSummonCost(8).setDungeonLevel(2)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST, PORTAL, NETHER")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "wraith", EntityWraith.class, 0xFF9900, 0xFF0000)
				.setPeaceful(false).setSummonCost(1).setDungeonLevel(0)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("PORTAL, SKY, NETHERSKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 4).setLightDark(true, true).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cacodemon", EntityCacodemon.class, 0xFF0000, 0x000099)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"))
				.addSubspecies(new Subspecies("ebon", "rare"));
		newMob.spawnInfo.setSpawnTypes("PORTAL, SKY, NETHERSKY")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(80);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "rahovart", EntityRahovart.class, 0x000000, 0xFF0000)
				.setPeaceful(false).setSummonCost(100).setDungeonLevel(-1).setBoss(true)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "asmodeus", EntityAsmodeus.class, 0x222222, 0x997700)
				.setPeaceful(false).setSummonCost(100).setDungeonLevel(-1).setBoss(true)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(0);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class);
		ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
		ObjectManager.addProjectile("hellfirewall", EntityHellfireWall.class);
		ObjectManager.addProjectile("hellfireorb", EntityHellfireOrb.class);
		ObjectManager.addProjectile("hellfirewave", EntityHellfireWave.class);
		ObjectManager.addProjectile("hellfirewavepart", EntityHellfireWavePart.class);
		ObjectManager.addProjectile("hellfirebarrier", EntityHellfireBarrier.class);
		ObjectManager.addProjectile("hellfirebarrierpart", EntityHellfireBarrierPart.class);
		ObjectManager.addProjectile("devilgatling", EntityDevilGatling.class);
		ObjectManager.addProjectile("hellshield", EntityHellShield.class);
		ObjectManager.addProjectile("helllaser", EntityHellLaser.class);
		ObjectManager.addProjectile("helllaserend", EntityHellLaserEnd.class);
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("pinkymeatraw"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("pinkymeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		Biome[] biomes = {Biomes.HELL };
		EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.MONSTER, biomes);
		EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.MONSTER, biomes);
	}
}
