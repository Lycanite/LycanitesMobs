package com.lycanitesmobs.mountainmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorArcaneLaserStorm;
import com.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorBoulderBlast;
import com.lycanitesmobs.mountainmobs.entity.EntityArcaneLaser;
import com.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserEnd;
import com.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import com.lycanitesmobs.mountainmobs.entity.EntityBoulderBlast;
import com.lycanitesmobs.mountainmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
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

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
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

		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.HASTE, 60, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));

		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.HASTE, 600, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
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
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.CREATURE, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.CREATURE, this.group.biomes);
	}
}
