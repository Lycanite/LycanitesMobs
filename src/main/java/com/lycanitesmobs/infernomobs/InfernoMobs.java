package com.lycanitesmobs.infernomobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.infernomobs.block.BlockFluidPureLava;
import com.lycanitesmobs.infernomobs.block.BlockScorchfire;
import com.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorMagma;
import com.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorScorchfire;
import com.lycanitesmobs.infernomobs.entity.*;
import com.lycanitesmobs.infernomobs.info.AltarInfoUmberLobber;
import com.lycanitesmobs.infernomobs.item.*;
import com.lycanitesmobs.infernomobs.worldgen.WorldGeneratorInferno;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
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

@Mod(modid = InfernoMobs.modid, name = InfernoMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class InfernoMobs extends Submod {
	
	public static final String modid = "infernomobs";
	public static final String name = "Lycanites Inferno Mobs";
	
	// Instance:
	@Instance(modid)
	public static InfernoMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.infernomobs.ClientSubProxy", serverSide="com.lycanitesmobs.infernomobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);

		AltarInfo umberLobberAltar = new AltarInfoUmberLobber("UmberLobberAltar");
		AltarInfo.addAltar(umberLobberAltar);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
        GameRegistry.registerWorldGenerator(new WorldGeneratorInferno(), 0);
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Inferno Mobs", 9)
				.setDimensionBlacklist("").setBiomes("ALL").setDungeonThemes("FIERY, NETHER, NECRO")
				.setEggName("infernospawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		Fluid fluid = ObjectManager.addFluid("purelava");
		fluid.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluid));

		ObjectManager.addItem("infernospawn", new ItemInfernoEgg());
		ObjectManager.addItem("soulstoneinferno", new ItemSoulstoneInferno(group));

		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.FIRE_RESISTANCE, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("cephignismeatcooked"));

		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.FIRE_RESISTANCE, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));

		ObjectManager.addItem("magmacharge", new ItemMagmaCharge());
		ObjectManager.addItem("magmascepter", new ItemScepterMagma(), 2, 1, 1);
		ObjectManager.addItem("scorchfirecharge", new ItemScorchfireCharge());
		ObjectManager.addItem("scorchfirescepter", new ItemScepterScorchfire(), 2, 1, 1);

		ObjectManager.addItem("afrittreat", new ItemTreat("afrittreat", group));
		ObjectManager.addItem("salamandertreat", new ItemTreat("salamandertreat", group));
		ObjectManager.addItem("gorgertreat", new ItemTreat("gorgertreat", group));
		ObjectManager.addItem("ignibustreat", new ItemTreat("ignibustreat", group));

		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava(fluid).setContainerItem(Items.BUCKET));
	}

	@Override
	public void createBlocks() {
		AssetManager.addSound("scorchfire", group, "block.scorchfire");
		ObjectManager.addBlock("scorchfire", new BlockScorchfire());
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("infernospawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

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


		// Projectiles:
		ObjectManager.addProjectile("magma", EntityMagma.class, ObjectManager.getItem("magmacharge"), new DispenserBehaviorMagma());
		ObjectManager.addProjectile("scorchfireball", EntityScorchfireball.class, ObjectManager.getItem("scorchfirecharge"), new DispenserBehaviorScorchfire());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cephignismeatcooked"));
	}

	@Override
	public void addRecipes() {

	}

	@Override
	public void editVanillaSpawns() {

	}
}
