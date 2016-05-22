package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.block.BlockBase;
import lycanite.lycanitesmobs.api.block.BlockSoulcube;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.*;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.demonmobs.block.BlockDoomfire;
import lycanite.lycanitesmobs.demonmobs.block.BlockHellfire;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDevilstar;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDoomfireball;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorHellfireball;
import lycanite.lycanitesmobs.demonmobs.entity.*;
import lycanite.lycanitesmobs.demonmobs.info.AltarInfoEbonCacodemon;
import lycanite.lycanitesmobs.demonmobs.info.AltarInfoRahovart;
import lycanite.lycanitesmobs.demonmobs.item.*;
import lycanite.lycanitesmobs.demonmobs.mobevent.MobEventHellsFury;
import lycanite.lycanitesmobs.demonmobs.mobevent.MobEventRahovart;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Biomes;
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

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class DemonMobs {
	
	public static final String modid = "demonmobs";
	public static final String name = "Lycanites Demon Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static DemonMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.demonmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.demonmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		// ========== Config ==========
		group = new GroupInfo(this, "Demon Mobs", 11)
                .setDimensionBlacklist("-1").setDimensionWhitelist(true).setBiomes("NETHER").setDungeonThemes("NETHER, NECRO")
                .setEggName("demonspawn");
		group.loadFromConfig();


		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);


		// ========== Create Items ==========
		ObjectManager.addItem("demonspawn", new ItemDemonEgg());
		ObjectManager.addItem("soulstonedemonic", new ItemSoulstoneDemonic(group));

		ObjectManager.addItem("doomfirecharge", new ItemDoomfireCharge());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireCharge());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstarCharge());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightningCharge());
		
		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.WITHER, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("pinkymeatraw"));
		
		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.STRENGTH, 10, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("pinkymeatcooked"));
		
		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.STRENGTH, 60, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("pinkytreat", new ItemTreat("pinkytreat", group));
		ObjectManager.addItem("cacodemontreat", new ItemTreat("cacodemontreat", group));
		
		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire(), 2, 1, 1);
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire(), 2, 1, 1);
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar(), 2, 1, 1);
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning(), 2, 1, 1);


		// ========== Create Blocks ==========
        ObjectManager.addBlock("soulcubedemonic", new BlockSoulcube(group, "soulcubedemonic"));
		AssetManager.addSound("hellfire", group, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
        AssetManager.addSound("doomfire", group, "block.doomfire");
        ObjectManager.addBlock("doomfire", new BlockDoomfire());
        ObjectManager.addBlock("demonstone", new BlockBase(Material.ROCK, group, "demonstone").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.itemsTab));
        ObjectManager.addBlock("demonbrick", new BlockBase(Material.ROCK, group, "demonbrick").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.itemsTab));
        ObjectManager.addBlock("demontile", new BlockBase(Material.ROCK, group, "demontile").setHardness(10.0F).setResistance(2000.0F).setCreativeTab(LycanitesMobs.itemsTab));
        ObjectManager.addBlock("demoncrystal", new BlockBase(Material.GLASS, group, "demoncrystal").setBlockStepSound(SoundType.GLASS).setHardness(5.0F).setResistance(2000.0F).setLightLevel(1.0F).setCreativeTab(LycanitesMobs.itemsTab));


		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("demonspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "belph", EntityBelph.class, 0x992222, 0x000000)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 4).setLightDark(true, true).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "behemoth", EntityBehemoth.class, 0xFF2222, 0xFF9900)
		        .setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(2).setAreaLimit(5).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "pinky", EntityPinky.class, 0xFF0099, 0x990000)
		        .setPeaceful(true).setTameable(true).setSummonCost(4).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setLightDark(true, true).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "trite", EntityTrite.class, 0xFFFF88, 0x000000)
		        .setPeaceful(false).setSummonCost(1).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(10).setAreaLimit(40).setGroupLimits(1, 10).setLightDark(true, true).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "astaroth", EntityAstaroth.class, 0x999944, 0x0000FF)
		        .setPeaceful(false).setSummonCost(8).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "nethersoul", EntityNetherSoul.class, 0xFF9900, 0xFF0000)
		        .setPeaceful(false).setSummonCost(1).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL, SKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 4).setLightDark(true, true).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cacodemon", EntityCacodemon.class, 0xFF0000, 0x000099)
		        .setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"))
				.addSubspecies(new Subspecies("ebon", "rare"));
		newMob.spawnInfo.setSpawnTypes("PORTAL, SKY")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(80);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "rahovart", EntityRahovart.class, 0x000000, 0xFF0000)
                .setPeaceful(false).setSummonCost(100).setDungeonLevel(4).setBoss(true);
        newMob.spawnInfo.setSpawnTypes("")
                .setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(true, true).setDungeonWeight(0);
        ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class);
        ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
        ObjectManager.addProjectile("hellfirewall", EntityHellfireWall.class);
        ObjectManager.addProjectile("hellfireorb", EntityHellfireOrb.class);
        ObjectManager.addProjectile("hellfirewave", EntityHellfireWave.class);
        ObjectManager.addProjectile("hellfirebarrier", EntityHellfireBarrier.class);


        // ========== Register Models ==========
		proxy.registerModels(this.group);
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");


		// ========== World Events ==========
        if(MobInfo.getFromName("nethersoul") != null) {
			MobEventBase mobEvent = new MobEventHellsFury("hellsfury", this.group).setDimensions("1");
			SpawnTypeBase eventSpawner = new SpawnTypeSky("hellsfury")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("nethersoul"));
	        eventSpawner.addSpawn(MobInfo.getFromName("cacodemon"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }


        // ========== Boss Events ==========
        MobEventBase mobEvent = new MobEventRahovart("rahovart", this.group).setDimensions("");
        MobEventManager.instance.addMobEvent(mobEvent);


		// ========== Edit Vanilla Spawns ==========
		Biome[] biomes = {Biomes.HELL };
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.MONSTER, biomes);
			EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.MONSTER, biomes);
		}


        // ========== Altars ==========
		AltarInfo ebonCacodemonAltar = new AltarInfoEbonCacodemon("EbonCacodemonAltar");
		AltarInfo.addAltar(ebonCacodemonAltar);
        AltarInfo rahovartAltar = new AltarInfoRahovart("RahovartAltar");
        AltarInfo.addAltar(rahovartAltar);


		// ========== Crafting ==========
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock("soulcubedemonic"), 1, 0),
                new Object[] { "DDD", "DSD", "DDD",
                        Character.valueOf('S'), ObjectManager.getItem("soulstonedemonic"),
                        Character.valueOf('D'), Blocks.DIAMOND_BLOCK
                }));

        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(ObjectManager.getBlock("demonstone"), 1, 0),
                new Object[] {
                        Items.NETHER_WART,
                        Items.NETHER_WART,
                        Blocks.COBBLESTONE
                }
        ));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock("demonbrick"), 4, 0),
                new Object[]{"BB", "BB",
                        Character.valueOf('B'), ObjectManager.getBlock("demonstone")
                }));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock("demontile"), 4, 0),
                new Object[] { "BB", "BB",
                        Character.valueOf('B'), ObjectManager.getBlock("demonbrick")
                }));
        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock("demonstone"), 4, 0),
                new Object[] { "BB", "BB",
                        Character.valueOf('B'), ObjectManager.getBlock("demontile")
                }));
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(ObjectManager.getBlock("demoncrystal"), 1, 0),
                new Object[] {
                        Items.NETHER_WART,
                        Items.NETHER_WART,
                        Blocks.GLOWSTONE
                }
        ));

        if(ItemInfo.enableWeaponRecipes) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("doomfirescepter"), 1, 0),
                    new Object[]{" C ", " R ", " R ",
                            Character.valueOf('C'), ObjectManager.getItem("doomfirecharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("hellfirescepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("hellfirecharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("devilstarscepter"), 1, 0),
                    new Object[]{" C ", " R ", " R ",
                            Character.valueOf('C'), ObjectManager.getItem("devilstarcharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("demoniclightningscepter"), 1, 0),
                    new Object[]{" C ", " R ", " R ",
                            Character.valueOf('C'), ObjectManager.getItem("demoniclightningcharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));
        }

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("devillasagna"), 1, 0),
				new Object[] {
					Items.NETHER_WART,
					Items.WHEAT,
					ObjectManager.getItem("pinkymeatcooked")
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("devillasagna") }
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("pinkytreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("hellfirecharge"),
				Character.valueOf('B'), Items.BONE
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("cacodemontreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("pinkymeatcooked"),
				Character.valueOf('B'), Items.BONE
			}));


		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);
	}
}
