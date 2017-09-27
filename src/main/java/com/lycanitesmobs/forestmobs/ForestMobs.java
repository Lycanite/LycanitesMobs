package com.lycanitesmobs.forestmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.forestmobs.dispenser.DispenserBehaviorLifeDrain;
import com.lycanitesmobs.forestmobs.entity.*;
import com.lycanitesmobs.forestmobs.item.ItemForestEgg;
import com.lycanitesmobs.forestmobs.item.ItemLifeDrainCharge;
import com.lycanitesmobs.forestmobs.item.ItemScepterLifeDrain;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
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
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = ForestMobs.modid, name = ForestMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class ForestMobs {
	
	public static final String modid = "forestmobs";
	public static final String name = "Lycanites Forest Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static ForestMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.forestmobs.ClientSubProxy", serverSide="com.lycanitesmobs.forestmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Forest Mobs", 1)
				.setDimensionBlacklist("-1,1").setBiomes("FOREST, -MOUNTAIN").setDungeonThemes("FOREST, DUNGEON")
                .setEggName("forestegg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("forestspawn", new ItemForestEgg());

        ItemCustomFood rawMeat =  new ItemCustomFood("arisaurmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SATURATION, 45, 2, 0.8F);
		if(ObjectManager.getPotionEffect("paralysis") != null)
			rawMeat.setPotionEffect(ObjectManager.getPotionEffect("paralysis"), 10, 2, 0.8F);
		ObjectManager.addItem("arisaurmeatraw", rawMeat);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatraw"));
		
		ObjectManager.addItem("arisaurmeatcooked", new ItemCustomFood("arisaurmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.INSTANT_HEALTH, 1, 6, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("vegetables", ObjectManager.getItem("arisaurmeatcooked"));
		
		ObjectManager.addItem("paleosalad", new ItemCustomFood("paleosalad", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.INSTANT_HEALTH, 1, 12, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("vegetables", ObjectManager.getItem("paleosalad"));

		ObjectManager.addItem("shamblertreat", new ItemTreat("shamblertreat", group));
        ObjectManager.addItem("wargtreat", new ItemTreat("wargtreat", group));

        ObjectManager.addItem("lifedraincharge", new ItemLifeDrainCharge());
        ObjectManager.addItem("lifedrainscepter", new ItemScepterLifeDrain(), 2, 1, 1);
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("forestspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "ent", EntityEnt.class, 0x997700, 0x00FF22)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, TREE")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "trent", EntityTrent.class, 0x663300, 0x00AA11)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(1).setAreaLimit(2).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "shambler", EntityShambler.class, 0xDDFF22, 0x005511)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "arisaur", EntityArisaur.class, 0x008800, 0x00FF00)
		        .setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(10).setAreaLimit(12).setGroupLimits(1, 3).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "spriggan", EntitySpriggan.class, 0x997722, 0x008844)
                .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("CROP, SKY")
                .setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "warg", EntityWarg.class, 0x321806, 0x68523b)
                .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
                .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("MONSTER")
                .setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "calpod", EntityCalpod.class, 0x996436, 0x3d2013)
				.setPeaceful(false).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, TREE")
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("lifedrain", EntityLifeDrain.class, ObjectManager.getItem("lifedraincharge"), new DispenserBehaviorLifeDrain());
		ObjectManager.addProjectile("lifedrainend", EntityLifeDrainEnd.class);
		
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
		
		// ========== Remove Vanilla Spawns ==========
		Biome[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, biomes);
		}
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("arisaurmeatraw"), new ItemStack(ObjectManager.getItem("arisaurmeatcooked"), 1), 0.5f);
	}
}
