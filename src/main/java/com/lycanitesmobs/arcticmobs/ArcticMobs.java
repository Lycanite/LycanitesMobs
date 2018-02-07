package com.lycanitesmobs.arcticmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.arcticmobs.block.*;
import com.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorBlizzard;
import com.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorFrostweb;
import com.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorIcefire;
import com.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorTundra;
import com.lycanitesmobs.arcticmobs.entity.EntityBlizzard;
import com.lycanitesmobs.arcticmobs.entity.EntityFrostweb;
import com.lycanitesmobs.arcticmobs.entity.EntityIcefireball;
import com.lycanitesmobs.arcticmobs.entity.EntityTundra;
import com.lycanitesmobs.arcticmobs.item.*;
import com.lycanitesmobs.arcticmobs.worldgen.WorldGeneratorArctic;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
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

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
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

		ObjectManager.addItem("yetimeatcooked", new ItemCustomFood("yetimeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.RESISTANCE, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yetimeatcooked"));

		ObjectManager.addItem("palesoup", new ItemCustomFood("palesoup", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.RESISTANCE, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("palesoup"));

		ObjectManager.addItem("frostyfur", new ItemFrostyFur());

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

		// Projectiles
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
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
	}
}
