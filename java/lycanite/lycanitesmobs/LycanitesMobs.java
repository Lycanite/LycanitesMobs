package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.command.CommandMain;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityFear;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.item.CreativeTabCreatures;
import lycanite.lycanitesmobs.api.item.CreativeTabItems;
import lycanite.lycanitesmobs.api.item.ItemHalloweenTreat;
import lycanite.lycanitesmobs.api.item.ItemSoulgazer;
import lycanite.lycanitesmobs.api.item.ItemStaffBlood;
import lycanite.lycanitesmobs.api.item.ItemStaffSavage;
import lycanite.lycanitesmobs.api.item.ItemStaffStable;
import lycanite.lycanitesmobs.api.item.ItemStaffSturdy;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.api.mobevent.MobEventBamstorm;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventHalloween;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.mods.DLDungeons;
import lycanite.lycanitesmobs.api.network.PacketHandler;
import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.10.6.0 - MC 1.7.10";
	
	public static final PacketHandler packetHandler = new PacketHandler();

    public static GroupInfo group;
    public static ConfigBase config;
	
	// Instance:
	@Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.ClientProxy", serverSide="lycanite.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;
	
	// Spawning:
	public static CustomSpawner customSpawner;
	public static MobEventManager mobEventManager;
	
	// Creative Tab:
	public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), modid + ".items");
	public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), modid + ".creatures");
	
	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";
	
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, name);
        ConfigBase.versionCheck("1.10.6.0", version);
		group.loadFromConfig();
		config = ConfigBase.getConfig(group, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		this.packetHandler.init();
		
		
		// ========== Custom Potion Effects ==========
		config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
		if(config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
			PotionBase.reserveEffectIDSpace();
			ObjectManager.addPotionEffect("Paralysis", config, true, 0xFFFF00, 1, 0, false);
			ObjectManager.addPotionEffect("Leech", config, false, 0x00FF99, 7, 0, true);
			ObjectManager.addPotionEffect("Penetration", config, true, 0x222222, 7, 1, false);
			ObjectManager.addPotionEffect("Recklessness", config, true, 0xFF0044, 4, 0, false);
			ObjectManager.addPotionEffect("Rage", config, true, 0xFF4400, 4, 0, false);
			ObjectManager.addPotionEffect("Weight", config, true, 0x000022, 1, 0, false);
			ObjectManager.addPotionEffect("Swiftswimming", config, false, 0x0000FF, 0, 2, true);
            ObjectManager.addPotionEffect("Fear", config, false, 0x220022, 7, 0, false);
			MinecraftForge.EVENT_BUS.register(new PotionEffects());
		}
		
		
		// ========== Mob Info ==========
		MobInfo.loadGlobalSettings();
		
		
		// ========== Spawning ==========
		customSpawner = new CustomSpawner();
		SpawnTypeBase.loadSpawnTypes();
		MinecraftForge.EVENT_BUS.register(customSpawner);
		FMLCommonHandler.instance().bus().register(customSpawner);
		
		SpawnInfo.loadGlobalSettings();
		
		mobEventManager = new MobEventManager();
		mobEventManager.loadMobEvents();
		//MinecraftForge.EVENT_BUS.register(mobEventManager);
		FMLCommonHandler.instance().bus().register(mobEventManager);
		
		
		// ========== Register Event Listeners ==========
		MinecraftForge.EVENT_BUS.register(new EventListener());
        proxy.registerEvents();
		
        
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentGroup(group);
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning());
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable());
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood());
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy());
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage());
		
		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat());
		
		
		// ========== Call Object Lists Setup ==========
		ObjectLists.createLists();
		
		
		// ========== Mod Support ==========
		DLDungeons.init();
	}
	
	
	// ==================================================
	//                  Initialization
	// ==================================================
	@EventHandler
    public void load(FMLInitializationEvent event) {
		// ========== Register and Initialize Handlers ==========
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		
		// ========== Register Entities ==========
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(EntityPortal.class, "summoningportal", specialEntityID++, instance, 64, 1, true);
		MobInfo newMob = new MobInfo(group, "fear", EntityFear.class, 0x000000, 0x000000)
			.setPeaceful(true).setSummonable(false).setSummonCost(0).setDungeonLevel(0).setDummy(true);
		EntityRegistry.registerModEntity(EntityFear.class, "fear", specialEntityID++, instance, 64, 1, true);
		AssetManager.addSound("effect_fear", group, "effect.fear");
		
		
		// ========== Load All Mob Info and Spawn Infos from Configs ==========
		GroupInfo.loadAllSpawningFromConfigs();
		MobInfo.loadAllFromConfigs();
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		// ========== Register and Initialize Handlers/Objects ==========
		proxy.registerAssets();
		proxy.registerTileEntities();
		proxy.registerRenders();
		
		
		// ========== Mob Events ==========
		// Bamstorm:
		MobEventBase bamstormEvent = new MobEventBamstorm("bamstorm", this.group);
        
		SpawnTypeBase bamLandSpawner = new SpawnTypeLand("bamstorm_land")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		bamLandSpawner.materials = new Material[] {Material.air};
		bamLandSpawner.ignoreBiome = true;
		bamLandSpawner.ignoreLight = true;
		bamLandSpawner.forceSpawning = true;
		bamLandSpawner.ignoreMobConditions = true;
		bamLandSpawner.addSpawn(MobInfo.getFromName("kobold"));
		bamLandSpawner.addSpawn(MobInfo.getFromName("conba"));
		bamLandSpawner.addSpawn(MobInfo.getFromName("belph"));
		bamLandSpawner.addSpawn(MobInfo.getFromName("geken"));
        if(bamLandSpawner.hasSpawns())
        	bamstormEvent.addSpawner(bamLandSpawner);
        
		SpawnTypeBase bamSkySpawner = new SpawnTypeSky("bamstorm_sky")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		bamSkySpawner.materials = new Material[] {Material.air};
		bamSkySpawner.ignoreBiome = true;
		bamSkySpawner.ignoreLight = true;
		bamSkySpawner.forceSpawning = true;
		bamSkySpawner.ignoreMobConditions = true;
		bamSkySpawner.addSpawn(MobInfo.getFromName("zephyr"));
		bamSkySpawner.addSpawn(MobInfo.getFromName("manticore"));
        if(bamSkySpawner.hasSpawns())
        	bamstormEvent.addSpawner(bamSkySpawner);
        
        if(bamstormEvent.hasSpawners())
        	MobEventManager.instance.addWorldEvent(bamstormEvent);
        

		// Halloween:
		MobEventBase halloweenEvent = new MobEventHalloween("halloween", this.group);
        
		SpawnTypeBase halloweenLandSpawner = new SpawnTypeLand("halloween_land")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		halloweenLandSpawner.materials = new Material[] {Material.air};
		halloweenLandSpawner.ignoreBiome = true;
		halloweenLandSpawner.ignoreLight = true;
		halloweenLandSpawner.forceSpawning = true;
		halloweenLandSpawner.ignoreMobConditions = true;
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("ghoulzombie"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("cryptzombie"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("belph"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("behemoth"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("ent"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("trent"));
		halloweenLandSpawner.addSpawn(MobInfo.getFromName("lurker"));
        if(halloweenLandSpawner.hasSpawns())
        	halloweenEvent.addSpawner(halloweenLandSpawner);
        
		SpawnTypeBase halloweenSkySpawner = new SpawnTypeSky("halloween_sky")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
		halloweenSkySpawner.materials = new Material[] {Material.air};
		halloweenSkySpawner.ignoreBiome = true;
		halloweenSkySpawner.ignoreLight = true;
		halloweenSkySpawner.forceSpawning = true;
		halloweenSkySpawner.ignoreMobConditions = true;
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("nethersoul"));
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("cacodemon"));
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("afrit"));
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("grue"));
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("phantom"));
		halloweenSkySpawner.addSpawn(MobInfo.getFromName("epion"));
        if(halloweenSkySpawner.hasSpawns())
        	halloweenEvent.addSpawner(halloweenSkySpawner);
        
        if(halloweenEvent.hasSpawners())
        	MobEventManager.instance.addWorldEvent(halloweenEvent);
		
		// Halloween Treats:
		ObjectLists.addItem("halloween_treats", Items.diamond);
		ObjectLists.addItem("halloween_treats", Items.gold_ingot);
		ObjectLists.addItem("halloween_treats", Items.emerald);
		ObjectLists.addItem("halloween_treats", Blocks.iron_block);
		ObjectLists.addItem("halloween_treats", Items.ender_pearl);
		ObjectLists.addItem("halloween_treats", Items.blaze_rod);
		ObjectLists.addItem("halloween_treats", Items.glowstone_dust);
		ObjectLists.addItem("halloween_treats", ObjectManager.getItem("mosspie"));
		ObjectLists.addItem("halloween_treats", ObjectManager.getItem("bulwarkburger"));
		ObjectLists.addItem("halloween_treats", ObjectManager.getItem("paleosalad"));
		ObjectLists.addItem("halloween_treats", ObjectManager.getItem("searingtaco"));
		ObjectLists.addItem("halloween_treats", ObjectManager.getItem("devillasagna"));
		ObjectLists.addFromConfig("halloween_treats");
		
		// Halloween Mobs:
		ObjectLists.addEntity("halloween_tricks", "ghoulzombie");
		ObjectLists.addEntity("halloween_tricks", "cryptzombie");
		ObjectLists.addEntity("halloween_tricks", "belph");
		ObjectLists.addEntity("halloween_tricks", "behemoth");
		ObjectLists.addEntity("halloween_tricks", "ent");
		ObjectLists.addEntity("halloween_tricks", "trent");
		ObjectLists.addEntity("halloween_tricks", "nethersoul");
		ObjectLists.addEntity("halloween_tricks", "cacodemon");
		ObjectLists.addEntity("halloween_tricks", "grue");
		ObjectLists.addEntity("halloween_tricks", "phantom");
		ObjectLists.addEntity("halloween_tricks", "epion");
		
        
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("soulgazer"), 1, 0),
				new Object[] { "GBG", "BDB", "GBG",
				Character.valueOf('G'), Items.gold_ingot,
				Character.valueOf('D'), Items.diamond,
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("summoningstaff"), 1, 0),
				new Object[] { " E ", " B ", " G ",
				Character.valueOf('E'), Items.ender_pearl,
				Character.valueOf('B'), Items.bone,
				Character.valueOf('G'), Items.gold_ingot
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("stablesummoningstaff"), 1, 0),
				new Object[] { " D ", " S ", " G ",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('G'), Items.gold_ingot,
				Character.valueOf('D'), Items.diamond
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("bloodsummoningstaff"), 1, 0),
				new Object[] { "RRR", "BSB", "NDN",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('R'), Items.redstone,
				Character.valueOf('B'), Items.bone,
				Character.valueOf('N'), Items.nether_wart,
				Character.valueOf('D'), Items.diamond
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("sturdysummoningstaff"), 1, 0),
				new Object[] { "III", "ISI", " O ",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('I'), Items.iron_ingot,
				Character.valueOf('O'), Blocks.obsidian
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("savagesummoningstaff"), 1, 0),
				new Object[] { "LLL", "BSB", "GGG",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('B'), Items.bone,
				Character.valueOf('G'), Items.ghast_tear,
				Character.valueOf('L'), new ItemStack(Items.dye, 1, 4)
			}));
    }
	
	
    // ==================================================
    //                    Server Load
    // ==================================================
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		// ========== Commands ==========
		event.registerServerCommand(new CommandMain());
	}
	
	
	// ==================================================
	//                     Debugging
	// ==================================================
    public static void printInfo(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Info] " + message);
        }
    }

    public static void printDebug(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Debug] " + message);
        }
    }

    public static void printWarning(String key, String message) {
		if("".equals(key) || config.getBool("Debug", key, false)) {
			System.err.println("[LycanitesMobs] [WARNING] " + message);
		}
	}
}
