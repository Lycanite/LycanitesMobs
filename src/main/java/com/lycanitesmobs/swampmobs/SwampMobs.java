package com.lycanitesmobs.swampmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.plainsmobs.mobevent.MobEventWindStorm;
import com.lycanitesmobs.swampmobs.block.BlockPoisonCloud;
import com.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorPoisonRay;
import com.lycanitesmobs.swampmobs.entity.*;
import com.lycanitesmobs.swampmobs.item.*;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorVenomShot;
import com.lycanitesmobs.swampmobs.entity.*;
import com.lycanitesmobs.swampmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
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

@Mod(modid = SwampMobs.modid, name = SwampMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class SwampMobs {
	
	public static final String modid = "swampmobs";
	public static final String name = "Lycanites Swamp Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static SwampMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.swampmobs.ClientSubProxy", serverSide="com.lycanitesmobs.swampmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Swamp Mobs", 2)
				.setDimensionBlacklist("").setBiomes("SWAMP, SPOOKY").setDungeonThemes("SWAMP, NECRO")
                .setEggName("swampspawn");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("swampspawn", new ItemSwampEgg());
		
		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.POISON, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("aspidmeatraw"));
		
		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.REGENERATION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("aspidmeatcooked"));
		
		ObjectManager.addItem("mosspie", new ItemCustomFood("mosspie", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.REGENERATION, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));

		ObjectManager.addItem("lurkertreat", new ItemTreat("lurkertreat", group));
		ObjectManager.addItem("eyewigtreat", new ItemTreat("eyewigtreat", group));
		
		ObjectManager.addItem("poisongland", new ItemPoisonGland());
		ObjectManager.addItem("poisonrayscepter", new ItemScepterPoisonRay(), 2, 1, 1);
		ObjectManager.addItem("venomshotscepter", new ItemScepterVenomShot(), 2, 1, 1);
		ObjectManager.addItem("venomaxeblade", new ItemSwordVenomAxeblade("venomaxeblade", "swordvenomaxeblade"), 2, 1, 1);
        ObjectManager.addItem("goldenvenomaxeblade", new ItemSwordVenomAxebladeGolden("goldenvenomaxeblade", "swordvenomaxebladegolden"));
        ObjectManager.addItem("verdantvenomaxeblade", new ItemSwordVenomAxebladeVerdant("verdantvenomaxeblade", "swordvenomaxebladeverdant"));
		
		// ========== Create Blocks ==========
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("swampspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "ghoulzombie", EntityGhoulZombie.class, 0x009966, 0xAAFFDD)
		        .setPeaceful(false).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "aglebemu", EntityAglebemu.class, 0x047f0a, 0xc3a85b)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("GROUP, WATER")
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, WATER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "dweller", EntityDweller.class, 0x009922, 0x994499)
		        .setPeaceful(false).setSummonable(true).setSummonCost(1).setDungeonLevel(1).setDungeonThemes("GROUP, WATER")
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ettin", EntityEttin.class, 0x669900, 0xFF6600)
		        .setPeaceful(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(3).setAreaLimit(3).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "lurker", EntityLurker.class, 0x009900, 0x99FF00)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "eyewig", EntityEyewig.class, 0x000000, 0x009900)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(3).setAreaLimit(5).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "aspid", EntityAspid.class, 0x009944, 0x446600)
		        .setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(12).setAreaLimit(10).setGroupLimits(1, 5).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "triffid", EntityTriffid.class, 0xe60f05, 0x09e30d)
				.setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(3).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "remobra", EntityRemobra.class, 0x440066, 0xDD00FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("poisonray", EntityPoisonRay.class, Items.FERMENTED_SPIDER_EYE, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("poisonrayend", EntityPoisonRayEnd.class);
		ObjectManager.addProjectile("venomshot", EntityVenomShot.class, ObjectManager.getItem("poisongland"), new DispenserBehaviorVenomShot());
		
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
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("remobra") != null) {
			MobEventBase mobEvent = new MobEventWindStorm("wingedvenom", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("wingedvenom")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("remobra"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Remove Vanilla Spawns ==========
		Biome[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.CREATURE, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.CREATURE, biomes);
		}
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("aspidmeatraw"), new ItemStack(ObjectManager.getItem("aspidmeatcooked"), 1), 0.5f);
	}
}
