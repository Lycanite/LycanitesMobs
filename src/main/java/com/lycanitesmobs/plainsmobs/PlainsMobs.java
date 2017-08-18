package com.lycanitesmobs.plainsmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
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
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.plainsmobs.dispenser.DispenserBehaviorQuill;
import com.lycanitesmobs.plainsmobs.entity.*;
import com.lycanitesmobs.plainsmobs.item.ItemPlainsEgg;
import com.lycanitesmobs.plainsmobs.item.ItemQuill;
import com.lycanitesmobs.plainsmobs.item.ItemScepterQuill;
import com.lycanitesmobs.plainsmobs.mobevent.MobEventWindStorm;
import com.lycanitesmobs.plainsmobs.entity.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
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

@Mod(modid = PlainsMobs.modid, name = PlainsMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class PlainsMobs {
	
	public static final String modid = "plainsmobs";
	public static final String name = "Lycanites Plains Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static PlainsMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.plainsmobs.ClientSubProxy", serverSide="com.lycanitesmobs.plainsmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Plains Mobs", 0)
				.setDimensionBlacklist("-1,1").setBiomes("PLAINS, SAVANNA, -SNOWY").setDungeonThemes("PLAINS, DUNGEON")
                .setEggName("plainsspawn");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("plainsspawn", new ItemPlainsEgg());

		ObjectManager.addItem("quill", new ItemQuill());
		ObjectManager.addItem("quillscepter", new ItemScepterQuill(), 2, 1, 1);

		ObjectManager.addItem("makameatraw", new ItemCustomFood("makameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WEAKNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("makameatraw"));
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("makameatraw"));
		
		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.ABSORPTION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("makameatcooked"));
		
		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.ABSORPTION, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bulwarkburger"));

		ObjectManager.addItem("ventoraptortreat", new ItemTreat("ventoraptortreat", group));
		ObjectManager.addItem("roctreat", new ItemTreat("roctreat", group));
		ObjectManager.addItem("feradontreat", new ItemTreat("feradontreat", group));
		ObjectManager.addItem("quillbeasttreat", new ItemTreat("quillbeasttreat", group));
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("plainsspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "kobold", EntityKobold.class, 0x996633, 0xFF7777)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ventoraptor", EntityVentoraptor.class, 0x99BBFF, 0x0033FF)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "maka", EntityMaka.class, 0xAA8855, 0x221100)
		        .setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(12).setAreaLimit(10).setGroupLimits(2, 5).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "makaalpha", EntityMakaAlpha.class, 0x663300, 0x000000)
		        .setPeaceful(false).setSummonCost(4).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(4).setGroupLimits(1, 2).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "zoataur", EntityZoataur.class, 0x442200, 0xFFDDBB)
		        .setPeaceful(false).setSummonable(true).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(4).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "roc", EntityRoc.class, 0xAA0000, 0x00DD44)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "feradon", EntityFeradon.class, 0xe4a23d, 0x552d0e)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "quillbeast", EntityQuillbeast.class, 0x642a12, 0x6051fe)
				.setPeaceful(false).setTameable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("quill", EntityQuill.class, ObjectManager.getItem("quill"), new DispenserBehaviorQuill());
		
		// ========== Register Rendering ==========
		proxy.registerRenders(this.group);
	}
	
	
	// ==================================================
	//                   Initialization
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
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("roc") != null) {
			MobEventBase mobEvent = new MobEventWindStorm("windstorm", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("windstorm")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("roc"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Remove Vanilla Spawns ==========
		Biome[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.CREATURE, biomes);
			EntityRegistry.removeSpawn(EntityChicken.class, EnumCreatureType.CREATURE, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("bulwarkburger"), 1, 0),
				new Object[] {
					Items.BREAD,
					ObjectManager.getItem("makameatcooked"),
					Items.BREAD
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("makameatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("bulwarkburger") }
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("ventoraptortreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("makameatcooked"),
				Character.valueOf('B'), Items.BONE
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("roctreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
						Character.valueOf('T'), Items.RABBIT_FOOT,
						Character.valueOf('B'), Items.BONE
				}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("makameatraw"), new ItemStack(ObjectManager.getItem("makameatcooked"), 1), 0.5f);
	}
}
