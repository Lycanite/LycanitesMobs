package com.lycanitesmobs.desertmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorMudshot;
import com.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorThrowingScythe;
import com.lycanitesmobs.desertmobs.entity.EntityMudshot;
import com.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import com.lycanitesmobs.desertmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
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

@Mod(modid = DesertMobs.modid, name = DesertMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class DesertMobs extends Submod {
	
	public static final String modid = "desertmobs";
	public static final String name = "Lycanites Desert Mobs";
	
	// Instance:
	@Instance(modid)
	public static DesertMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.desertmobs.ClientSubProxy", serverSide="com.lycanitesmobs.desertmobs.CommonSubProxy")
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
		group = new GroupInfo(this, "Desert Mobs", 4)
				.setDimensionBlacklist("-1,1").setBiomes("SANDY, WASTELAND, MESA, -COLD").setDungeonThemes("DESERT, WASTELAND, NECRO")
				.setEggName("desertspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("desertspawn", new ItemDesertEgg());
		ObjectManager.addItem("throwingscythe", new ItemThrowingScythe());

		ObjectManager.addItem("joustmeatraw", new ItemCustomFood("joustmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("joustmeatraw"));

		ObjectManager.addItem("joustmeatcooked", new ItemCustomFood("joustmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.SPEED, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("joustmeatcooked"));

		ObjectManager.addItem("ambercake", new ItemCustomFood("ambercake", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.SPEED, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("ambercake"));

		ObjectManager.addItem("crusktreat", new ItemTreat("crusktreat", group));
		ObjectManager.addItem("erepedetreat", new ItemTreat("erepedetreat", group));

		ObjectManager.addItem("mudshotcharge", new ItemMudshotCharge());
		ObjectManager.addItem("scythescepter", new ItemScepterScythe(), 2, 1, 1);
		ObjectManager.addItem("mudshotscepter", new ItemScepterMudshot(), 2, 1, 1);
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("desertspawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("throwingscythe", EntityThrowingScythe.class, ObjectManager.getItem("throwingscythe"), new DispenserBehaviorThrowingScythe());
		ObjectManager.addProjectile("mudshot", EntityMudshot.class, ObjectManager.getItem("mudshotcharge"), new DispenserBehaviorMudshot());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("joustmeatraw"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("joustmeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("joustmeatraw"), new ItemStack(ObjectManager.getItem("joustmeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
	}
}
