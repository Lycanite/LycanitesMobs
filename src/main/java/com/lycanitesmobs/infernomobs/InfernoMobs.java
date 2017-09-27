package com.lycanitesmobs.infernomobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorEmber;
import com.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorMagma;
import com.lycanitesmobs.infernomobs.entity.*;
import com.lycanitesmobs.infernomobs.item.*;
import com.lycanitesmobs.infernomobs.mobevent.MobEventEruption;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeLand;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.infernomobs.block.BlockFluidPureLava;
import com.lycanitesmobs.infernomobs.block.BlockScorchfire;
import com.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorScorchfire;
import com.lycanitesmobs.infernomobs.entity.*;
import com.lycanitesmobs.infernomobs.info.AltarInfoUmberLobber;
import com.lycanitesmobs.infernomobs.item.*;
import com.lycanitesmobs.infernomobs.mobevent.MobEventCinderfall;
import com.lycanitesmobs.infernomobs.worldgen.WorldGeneratorInferno;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
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

@Mod(modid = InfernoMobs.modid, name = InfernoMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class InfernoMobs {
	
	public static final String modid = "infernomobs";
	public static final String name = "Lycanites Inferno Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static InfernoMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.infernomobs.ClientSubProxy", serverSide="com.lycanitesmobs.infernomobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Inferno Mobs", 9)
				.setDimensionBlacklist("").setBiomes("ALL").setDungeonThemes("FIERY, NETHER, NECRO")
                .setEggName("infernospawn");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Fluids ==========
		Fluid fluid = ObjectManager.addFluid("purelava");
		fluid.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluid));
		
		// ========== Create Items ==========
		ObjectManager.addItem("infernospawn", new ItemInfernoEgg());
        ObjectManager.addItem("soulstoneinferno", new ItemSoulstoneInferno(group));
		
		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.FIRE_RESISTANCE, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("cephignismeatcooked"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cephignismeatcooked"));
		
		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.FIRE_RESISTANCE, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));
		
		ObjectManager.addItem("embercharge", new ItemEmberCharge());
		ObjectManager.addItem("emberscepter", new ItemScepterEmber(), 2, 1, 1);
		ObjectManager.addItem("cinderfallsword", new ItemSwordCinderfall("cinderfallsword", "swordcinderfall"), 2, 1, 1);
        ObjectManager.addItem("azurecinderfallsword", new ItemSwordCinderfallAzure("azurecinderfallsword", "swordcinderfallazure"));
        ObjectManager.addItem("verdantcinderfallsword", new ItemSwordCinderfallVerdant("verdantcinderfallsword", "swordcinderfallverdant"));
		ObjectManager.addItem("magmacharge", new ItemMagmaCharge());
		ObjectManager.addItem("magmascepter", new ItemScepterMagma(), 2, 1, 1);
        ObjectManager.addItem("scorchfirecharge", new ItemScorchfireCharge());
        ObjectManager.addItem("scorchfirescepter", new ItemScepterScorchfire(), 2, 1, 1);

		ObjectManager.addItem("afrittreat", new ItemTreat("afrittreat", group));
        ObjectManager.addItem("salamandertreat", new ItemTreat("salamandertreat", group));
		ObjectManager.addItem("gorgertreat", new ItemTreat("gorgertreat", group));
		ObjectManager.addItem("ignibustreat", new ItemTreat("ignibustreat", group));

		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava(fluid).setContainerItem(Items.BUCKET));

        // ========== Create Blocks ==========
        AssetManager.addSound("scorchfire", group, "block.scorchfire");
        ObjectManager.addBlock("scorchfire", new BlockScorchfire());
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("infernospawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "cinder", EntityCinder.class, 0xFF9900, 0xFFFF00)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("FIRE").setBlockCost(8)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "lobber", EntityLobber.class, 0x330011, 0xFF5500)
		        .setPeaceful(false).setSummonCost(8).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"))
				.addSubspecies(new Subspecies("umber", "rare"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cephignis", EntityCephignis.class, 0xFFBB00, 0xDD00FF)
		        .setPeaceful(true).setSummonCost(1).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16).setDespawn(false)
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 3).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "afrit", EntityAfrit.class, 0x110000, 0x773300)
                .setPeaceful(false).setTameable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(8)
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "khalk", EntityKhalk.class, 0x442200, 0xFFAA22)
                .setPeaceful(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
                .setSpawnWeight(1).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "salamander", EntitySalamander.class, 0xAACC00, 0xDDFF22)
                .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
                .addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(8)
                .setSpawnWeight(6).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "gorger", EntityGorger.class, 0xDE2A00, 0x200905)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(3)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ignibus", EntityIgnibus.class, 0xBB0000, 0x00DD00)
				.setPeaceful(false).setTameable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());
        ObjectManager.addProjectile("magma", EntityMagma.class, ObjectManager.getItem("magmacharge"), new DispenserBehaviorMagma());
        ObjectManager.addProjectile("scorchfireball", EntityScorchfireball.class, ObjectManager.getItem("scorchfirecharge"), new DispenserBehaviorScorchfire());

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
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(this.group);


        // ========== World Generation ==========
        GameRegistry.registerWorldGenerator(new WorldGeneratorInferno(), 0);


		// ========== Mob Events ==========
        MobEventBase mobEvent = new MobEventCinderfall("cinderfall", this.group).setDimensions("1");
        SpawnTypeBase eventSpawner = new SpawnTypeSky("cinderfall")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.AIR};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("cinder"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);

        mobEvent = new MobEventEruption("eruption", this.group).setDimensions("1");
        mobEvent.minDay = 10;
        eventSpawner = new SpawnTypeLand("eruption")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.AIR};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("lobber"));
        eventSpawner.addSpawn(MobInfo.getFromName("khalk"), 2);
        eventSpawner.addSpawn(MobInfo.getFromName("gorger"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);


        // ========== Altars ==========
        AltarInfo umberLobberAltar = new AltarInfoUmberLobber("UmberLobberAltar");
        AltarInfo.addAltar(umberLobberAltar);


		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("cephignismeatraw").itemID, new ItemStack(ObjectManager.getItem("cephignismeatcooked"), 1), 0.5f); Cephignis live in lava, cooking is redundant!
	}
}
