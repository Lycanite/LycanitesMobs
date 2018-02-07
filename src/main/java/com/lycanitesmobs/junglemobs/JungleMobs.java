package com.lycanitesmobs.junglemobs;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.junglemobs.block.BlockPoopCloud;
import com.lycanitesmobs.junglemobs.block.BlockPropolis;
import com.lycanitesmobs.junglemobs.block.BlockQuickWeb;
import com.lycanitesmobs.junglemobs.block.BlockVeswax;
import com.lycanitesmobs.junglemobs.dispenser.DispenserBehaviorPoop;
import com.lycanitesmobs.junglemobs.entity.EntityPoop;
import com.lycanitesmobs.junglemobs.item.ItemJungleEgg;
import com.lycanitesmobs.junglemobs.item.ItemPoopCharge;
import com.lycanitesmobs.junglemobs.item.ItemScepterPoop;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
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

@Mod(modid = JungleMobs.modid, name = JungleMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class JungleMobs extends Submod {
	
	public static final String modid = "junglemobs";
	public static final String name = "Lycanites Jungle Mobs";
	
	// Instance:
	@Instance(modid)
	public static JungleMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.junglemobs.ClientSubProxy", serverSide="com.lycanitesmobs.junglemobs.CommonSubProxy")
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
		group = new GroupInfo(this, "Jungle Mobs", 3)
				.setDimensionBlacklist("-1,1").setBiomes("JUNGLE").setDungeonThemes("JUNGLE, DUNGEON")
				.setEggName("junglespawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("junglespawn", new ItemJungleEgg());

		ObjectManager.addItem("concapedemeatraw", new ItemCustomFood("concapedemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("concapedemeatraw"));

		ObjectManager.addItem("concapedemeatcooked", new ItemCustomFood("concapedemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.JUMP_BOOST, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("concapedemeatcooked"));

		ObjectManager.addItem("tropicalcurry", new ItemCustomFood("tropicalcurry", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.JUMP_BOOST, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("tropicalcurry"));

		ObjectManager.addItem("poopcharge", new ItemPoopCharge());
		ObjectManager.addItem("poopscepter", new ItemScepterPoop(), 2, 1, 1);

		ObjectManager.addItem("uvaraptortreat", new ItemTreat("uvaraptortreat", group));
		ObjectManager.addItem("dawontreat", new ItemTreat("dawontreat", group));
	}

	@Override
	public void createBlocks() {
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
		AssetManager.addSound("poopcloud", group, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud());
		ObjectManager.addBlock("propolis", new BlockPropolis());
		ObjectManager.addBlock("veswax", new BlockVeswax());
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("junglespawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("poop", EntityPoop.class, ObjectManager.getItem("poopcharge"), new DispenserBehaviorPoop());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("concapedemeatraw"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("concapedemeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("concapedemeatraw"), new ItemStack(ObjectManager.getItem("concapedemeatcooked"), 1), 0.5f);
		GameRegistry.addSmelting(ObjectManager.getBlock("propolis"), new ItemStack(Blocks.HARDENED_CLAY, 1), 0.5f);
		GameRegistry.addSmelting(ObjectManager.getBlock("veswax"), new ItemStack(Items.SUGAR, 6), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.MONSTER, this.group.biomes);
	}
}
