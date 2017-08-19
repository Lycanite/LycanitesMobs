package com.lycanitesmobs.junglemobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.junglemobs.block.BlockPropolis;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.junglemobs.entity.*;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.spawning.SpawnTypeLand;
import com.lycanitesmobs.core.spawning.SpawnTypeSky;
import com.lycanitesmobs.junglemobs.block.BlockPoopCloud;
import com.lycanitesmobs.junglemobs.block.BlockQuickWeb;
import com.lycanitesmobs.junglemobs.block.BlockVeswax;
import com.lycanitesmobs.junglemobs.dispenser.DispenserBehaviorPoop;
import com.lycanitesmobs.junglemobs.entity.*;
import com.lycanitesmobs.junglemobs.item.ItemJungleEgg;
import com.lycanitesmobs.junglemobs.item.ItemPoopCharge;
import com.lycanitesmobs.junglemobs.item.ItemScepterPoop;
import com.lycanitesmobs.junglemobs.mobevent.MobEventPoopParty;
import com.lycanitesmobs.junglemobs.mobevent.MobEventTheSwarm;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
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

@Mod(modid = JungleMobs.modid, name = JungleMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class JungleMobs {
	
	public static final String modid = "junglemobs";
	public static final String name = "Lycanites Jungle Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static JungleMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.junglemobs.ClientSubProxy", serverSide="com.lycanitesmobs.junglemobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Jungle Mobs", 3)
				.setDimensionBlacklist("-1,1").setBiomes("JUNGLE").setDungeonThemes("JUNGLE, DUNGEON")
                .setEggName("junglespawn");
		group.loadFromConfig();
		
		
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("junglespawn", new ItemJungleEgg());
		
		ObjectManager.addItem("concapedemeatraw", new ItemCustomFood("concapedemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("concapedemeatraw"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("concapedemeatraw"));
		
		ObjectManager.addItem("concapedemeatcooked", new ItemCustomFood("concapedemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.JUMP_BOOST, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("concapedemeatcooked"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("concapedemeatcooked"));
		
		ObjectManager.addItem("tropicalcurry", new ItemCustomFood("tropicalcurry", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.JUMP_BOOST, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("tropicalcurry"));
		
		ObjectManager.addItem("poopcharge", new ItemPoopCharge());
		ObjectManager.addItem("poopscepter", new ItemScepterPoop(), 2, 1, 1);

		ObjectManager.addItem("uvaraptortreat", new ItemTreat("uvaraptortreat", group));
		ObjectManager.addItem("dawontreat", new ItemTreat("dawontreat", group));

		
		// ========== Create Blocks ==========
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
		AssetManager.addSound("poopcloud", group, "block.poopcloud");
		ObjectManager.addBlock("poopcloud", new BlockPoopCloud());
		ObjectManager.addBlock("propolis", new BlockPropolis());
		ObjectManager.addBlock("veswax", new BlockVeswax());
		
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("junglespawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "geken", EntityGeken.class, 0x00AA00, 0xFFFF00)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "uvaraptor", EntityUvaraptor.class, 0x00FF33, 0xFF00FF)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "concapede", EntityConcapedeHead.class, 0x111144, 0xDD0000)
		        .setPeaceful(true).setSummonCost(2).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(18).setAreaLimit(10).setGroupLimits(1, 1).setLightDark(true, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "concapedesegment", EntityConcapedeSegment.class, 0x000022, 0x990000)
		        .setPeaceful(true).setSummonCost(1).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE")
				.setSpawnWeight(0).setAreaLimit(0).setGroupLimits(0, 0).setLightDark(false, false).setEnabled(false);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "tarantula", EntityTarantula.class, 0x008800, 0xDD0000)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);
        
        newMob = new MobInfo(group, "conba", EntityConba.class, 0x665500, 0xCC99BB)
		        .setPeaceful(false).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(10).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);
        
        newMob = new MobInfo(group, "vespid", EntityVespid.class, 0x112200, 0x998800)
		        .setPeaceful(false).setSummonCost(2).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(10).setAreaLimit(10).setGroupLimits(1, 6).setLightDark(false, true);
		ObjectManager.addMob(newMob);
        
        newMob = new MobInfo(group, "vespidqueen", EntityVespidQueen.class, 0x223300, 0xFFCC00)
		        .setPeaceful(false).setSummonCost(2).setDungeonLevel(3)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "dawon", EntityDawon.class, 0x49e554, 0x030601)
				.setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(1)
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);
		
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("poop", EntityPoop.class, ObjectManager.getItem("poopcharge"), new DispenserBehaviorPoop());
		
		
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
		// Poop Party:
        if(MobInfo.getFromName("conba") != null) {
			MobEventBase mobEvent = new MobEventPoopParty("poopparty", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeLand("poopparty")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("conba"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
	     // The Swarm:
		MobEventBase theSwarmEvent = new MobEventTheSwarm("theswarm", this.group);
		theSwarmEvent.minDay = 10;
	     
		SpawnTypeBase theSwarmLandSpawner = new SpawnTypeLand("theswarm_land")
	         .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		theSwarmLandSpawner.materials = new Material[] {Material.AIR};
		theSwarmLandSpawner.ignoreBiome = true;
		theSwarmLandSpawner.ignoreLight = true;
		theSwarmLandSpawner.forceSpawning = true;
		theSwarmLandSpawner.ignoreMobConditions = true;
		theSwarmLandSpawner.addSpawn(MobInfo.getFromName("conba"));
	     if(theSwarmLandSpawner.hasSpawns())
	    	 theSwarmEvent.addSpawner(theSwarmLandSpawner);
	     
		SpawnTypeBase theSwarmSkySpawner = new SpawnTypeSky("theswarm_sky")
	         .setChance(1.0D).setBlockLimit(32).setMobLimit(8);
		theSwarmSkySpawner.materials = new Material[] {Material.AIR};
		theSwarmSkySpawner.ignoreBiome = true;
		theSwarmSkySpawner.ignoreLight = true;
		theSwarmSkySpawner.forceSpawning = true;
		theSwarmSkySpawner.ignoreMobConditions = true;
		theSwarmSkySpawner.addSpawn(MobInfo.getFromName("vespid"));
		theSwarmSkySpawner.addSpawn(MobInfo.getFromName("vespidqueen"), 1);
	     if(theSwarmSkySpawner.hasSpawns())
	    	 theSwarmEvent.addSpawner(theSwarmSkySpawner);
	     
	     if(theSwarmEvent.hasSpawners())
	     	MobEventManager.instance.addWorldEvent(theSwarmEvent);
		
        
		// ========== Remove Vanilla Spawns ==========
		Biome[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.MONSTER, biomes);
		}
		
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("tropicalcurry"), 1, 0),
				new Object[] {
					Items.BOWL,
					new ItemStack(Items.DYE, 1, 3),
					Blocks.VINE,
					ObjectManager.getItem("concapedemeatcooked")
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("concapedemeatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("tropicalcurry") }
			));

        if(ItemInfo.enableWeaponRecipes) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("poopscepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("poopcharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));
        }
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("uvaraptortreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("concapedemeatcooked"),
				Character.valueOf('B'), Items.BONE
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("dawontreat"), 4, 0),
				new Object[] { "   ", "BBT", "   ",
						Character.valueOf('T'), Items.EMERALD,
						Character.valueOf('B'), Items.BONE
				}));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(Items.CLAY_BALL, 4, 0),
				new Object[] {
					ObjectManager.getBlock("propolis")
				}
			));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(Blocks.MYCELIUM, 2, 0),
				new Object[] {
					ObjectManager.getBlock("propolis"),
					Blocks.DIRT
				}
			));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(Items.SUGAR, 4, 0),
				new Object[] {
					ObjectManager.getBlock("veswax")
				}
			));
		
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("concapedemeatraw"), new ItemStack(ObjectManager.getItem("concapedemeatcooked"), 1), 0.5f);
		GameRegistry.addSmelting(ObjectManager.getBlock("propolis"), new ItemStack(Blocks.HARDENED_CLAY, 1), 0.5f);
		GameRegistry.addSmelting(ObjectManager.getBlock("veswax"), new ItemStack(Items.SUGAR, 6), 0.5f);
	}
}
