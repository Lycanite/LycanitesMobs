package com.lycanitesmobs.swampmobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.swampmobs.block.BlockPoisonCloud;
import com.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorPoisonRay;
import com.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorVenomShot;
import com.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import com.lycanitesmobs.swampmobs.entity.EntityPoisonRayEnd;
import com.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import com.lycanitesmobs.swampmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
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

@Mod(modid = SwampMobs.modid, name = SwampMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class SwampMobs extends Submod {
	
	public static final String modid = "swampmobs";
	public static final String name = "Lycanites Swamp Mobs";
	
	// Instance:
	@Instance(modid)
	public static SwampMobs instance;

	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.swampmobs.ClientSubProxy", serverSide="com.lycanitesmobs.swampmobs.CommonSubProxy")
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
		this.group = new GroupInfo(this, "Swamp Mobs", 2)
				.setDimensionBlacklist("").setBiomes("SWAMP, SPOOKY").setDungeonThemes("SWAMP, NECRO")
				.setEggName("swampspawn");
		this.group.loadFromConfig();
		
		ObjectManager.setCurrentGroup(this.group);
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("swampspawn", new ItemSwampEgg());

		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.POISON, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));

		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.REGENERATION, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));

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
	}

	@Override
	public void createBlocks() {
		AssetManager.addSound("poisoncloud", group, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("swampspawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("poisonray", EntityPoisonRay.class, Items.FERMENTED_SPIDER_EYE, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("poisonrayend", EntityPoisonRayEnd.class, false);
		ObjectManager.addProjectile("venomshot", EntityVenomShot.class, ObjectManager.getItem("poisongland"), new DispenserBehaviorVenomShot());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("aspidmeatraw"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("aspidmeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("aspidmeatraw"), new ItemStack(ObjectManager.getItem("aspidmeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.CREATURE, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.CREATURE, this.group.biomes);
	}
}
