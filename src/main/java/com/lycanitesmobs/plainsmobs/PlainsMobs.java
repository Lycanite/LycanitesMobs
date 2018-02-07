package com.lycanitesmobs.plainsmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.plainsmobs.dispenser.DispenserBehaviorQuill;
import com.lycanitesmobs.plainsmobs.entity.EntityQuill;
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

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
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

		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.ABSORPTION, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));

		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.ABSORPTION, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
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
