package com.lycanitesmobs.plainsmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.plainsmobs.dispenser.DispenserBehaviorQuill;
import com.lycanitesmobs.plainsmobs.entity.*;
import com.lycanitesmobs.plainsmobs.item.ItemPlainsEgg;
import com.lycanitesmobs.plainsmobs.item.ItemQuill;
import com.lycanitesmobs.plainsmobs.item.ItemScepterQuill;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = PlainsMobs.modid, name = PlainsMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class PlainsMobs extends Submod {
	
	public static final String modid = "plainsmobs";
	public static final String name = "Lycanites Plains Mobs";
	
	// Instance:
	@Instance(modid)
	public static PlainsMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.plainsmobs.ClientSubProxy", serverSide="com.lycanitesmobs.plainsmobs.CommonSubProxy")
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
		group = new GroupInfo(this, "Plains Mobs", 0)
				.setDimensionBlacklist("-1,1").setBiomes("PLAINS, SAVANNA, -SNOWY").setDungeonThemes("PLAINS, DUNGEON")
				.setEggName("plainsspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("plainsspawn", new ItemPlainsEgg());

		ObjectManager.addItem("quill", new ItemQuill());
		ObjectManager.addItem("quillscepter", new ItemScepterQuill(), 2, 1, 1);

		ObjectManager.addItem("makameatraw", new ItemCustomFood("makameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WEAKNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("makameatraw"));

		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.ABSORPTION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));

		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.ABSORPTION, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bulwarkburger"));

		ObjectManager.addItem("ventoraptortreat", new ItemTreat("ventoraptortreat", group));
		ObjectManager.addItem("roctreat", new ItemTreat("roctreat", group));
		ObjectManager.addItem("feradontreat", new ItemTreat("feradontreat", group));
		ObjectManager.addItem("quillbeasttreat", new ItemTreat("quillbeasttreat", group));
		ObjectManager.addItem("morocktreat", new ItemTreat("morocktreat", group));
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("plainsspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "kobold", EntityKobold.class, 0x996633, 0xFF7777)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ventoraptor", EntityVentoraptor.class, 0x99BBFF, 0x0033FF)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(0)
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "maka", EntityMaka.class, 0xAA8855, 0x221100)
				.setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE, ANIMAL").setDespawn(false)
				.setSpawnWeight(10).setAreaLimit(10).setGroupLimits(2, 5).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "makaalpha", EntityMakaAlpha.class, 0x663300, 0x000000)
				.setPeaceful(false).setSummonCost(4).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("").setDespawn(false)
				.setSpawnWeight(1).setAreaLimit(4).setGroupLimits(1, 2).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "zoataur", EntityZoataur.class, 0x442200, 0xFFDDBB)
				.setPeaceful(false).setSummonable(true).setSummonCost(4).setDungeonLevel(2)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
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
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "quillbeast", EntityQuillbeast.class, 0x642a12, 0x6051fe)
				.setPeaceful(false).setTameable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "morock", EntityMorock.class, 0x222211, 0x554444)
				.setPeaceful(false).setTameable(true).setSummonCost(8).setDungeonLevel(3)
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("quill", EntityQuill.class, ObjectManager.getItem("quill"), new DispenserBehaviorQuill());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("makameatraw"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("makameatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("makameatraw"), new ItemStack(ObjectManager.getItem("makameatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.CREATURE, this.group.biomes);
		EntityRegistry.removeSpawn(EntityChicken.class, EnumCreatureType.CREATURE, this.group.biomes);
	}
}
