package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import com.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.command.CommandMain;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.*;
import com.lycanitesmobs.core.mobevent.MobEventBase;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mobevent.SharedMobEvents;
import com.lycanitesmobs.core.mods.DLDungeons;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.spawning.CustomSpawner;
import com.lycanitesmobs.core.spawning.SpawnTypeBase;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version, useMetadata = false)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.17.1.2 - MC 1.12.2";
	public static final String website = "http://lycanitesmobs.com";
	public static final String websiteAPI = "http://api.lycanitesmobs.com";
	public static final String websitePatreon = "https://www.patreon.com/lycanite";
	
	public static final PacketHandler packetHandler = new PacketHandler();

    public static GroupInfo group;
    public static ConfigBase config;
	
	// Instance:
	@Mod.Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.ClientProxy", serverSide="com.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;

    // Capabilities:
    @CapabilityInject(IExtendedEntity.class)
    public static final Capability<IExtendedEntity> EXTENDED_ENTITY = null;
    @CapabilityInject(IExtendedPlayer.class)
    public static final Capability<IExtendedPlayer> EXTENDED_PLAYER = null;
	
	// Spawning:
	public static CustomSpawner customSpawner;
	public static MobEventManager mobEventManager;
	
	// Creative Tab:
    public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), modid + ".items");
    public static final CreativeTabs blocksTab = new CreativeTabBlocks(CreativeTabs.getNextID(), modid + ".blocks");
	public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), modid + ".creatures");
	
	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";

	// Extra Config Settings:
	public static boolean disableNausea = false;
	
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, name, 1000);
        ConfigBase.versionCheck("1.14.2.1", version);
		group.loadFromConfig();
		config = ConfigBase.getConfig(group, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		config.setCategoryComment("Extras", "Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");
		disableNausea = config.getBool("Extras", "Disable Nausea Debuff", disableNausea, "Set to true to disable the nausea debuff on players.");

        config.setCategoryComment("Admin", "Special tools for server admins.");
        ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = config.getStringList("Admin", "Force Remove Entity Names", new String[0], "Here you can add a list of entity IDs for entity that you want to be forcefully removed.");
        if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
            printInfo("", "Lycanites Mobs will forcefully remove the following entities based on their registered IDs:");
            for (String removeEntityID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS)
                printInfo("", removeEntityID);
        }
        ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = config.getInt("Admin", "Force Remove Entity Ticks", 40, "How many ticks it takes for an entity to be forcefully removed (1 second = 20 ticks). This only applies to EntityLiving, other entities are instantly removed.");

        // Register Rendering Factories:
        proxy.registerRenders(this.group);

		this.packetHandler.init();
		
		
		// ========== Custom Potion Effects ==========
		config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
		if(config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
			PotionBase.reserveEffectIDSpace();
			ObjectManager.addPotionEffect("paralysis", config, true, 0xFFFF00, 1, 0, false);
			ObjectManager.addPotionEffect("leech", config, false, 0x00FF99, 7, 0, true);
			ObjectManager.addPotionEffect("penetration", config, true, 0x222222, 7, 1, false);
			ObjectManager.addPotionEffect("recklessness", config, true, 0xFF0044, 4, 0, false);
			ObjectManager.addPotionEffect("rage", config, true, 0xFF4400, 4, 0, false);
			ObjectManager.addPotionEffect("weight", config, true, 0x000022, 1, 0, false);
			ObjectManager.addPotionEffect("swiftswimming", config, false, 0x0000FF, 0, 2, true);
            ObjectManager.addPotionEffect("fear", config, false, 0x220022, 7, 0, false);
            ObjectManager.addPotionEffect("fallresist", config, false, 0xDDFFFF, 0, 0, true);
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
		FMLCommonHandler.instance().bus().register(mobEventManager);


        // ========== Item Info ==========
        ItemInfo.loadGlobalSettings();


        // ========== Altar Info ==========
        AltarInfo.loadGlobalSettings();



        // ========== Register Capabilities ==========
        CapabilityManager.INSTANCE.register(IExtendedPlayer.class, new ExtendedPlayerStorage(), ExtendedPlayer.class);
        CapabilityManager.INSTANCE.register(IExtendedEntity.class, new ExtendedEntityStorage(), ExtendedEntity.class);
		
		
		// ========== Register Event Listeners ==========
		MinecraftForge.EVENT_BUS.register(new EventListener());
        proxy.registerEvents();
		
        
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentGroup(group);


        // ========== Create Blocks ==========
        ObjectManager.addBlock("summoningpedestal", new BlockSummoningPedestal(group));
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("soulstone", new ItemSoulstone());
		ObjectManager.addItem("soulkey", new ItemSoulkey("soulkey", 0));
		ObjectManager.addItem("soulkeydiamond", new ItemSoulkey("soulkeydiamond", 1));
		ObjectManager.addItem("soulkeyemerald", new ItemSoulkey("soulkeyemerald", 2));
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning());
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable());
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood());
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy());
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage());
		
		// Super Foods:
		ObjectManager.addItem("battleburrito", new ItemFoodBattleBurrito("battleburrito", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));
		ObjectManager.addItem("explorersrisotto", new ItemFoodExplorersRisotto("explorersrisotto", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));
		
		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat());
        ObjectManager.addItem("wintergift", new ItemWinterGift());
        ObjectManager.addItem("wintergiftlarge", new ItemWinterGiftLarge());

        ObjectManager.addItem("mobtoken", new ItemMobToken(group));


        // ========== Create Tile Entities ==========
        ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class);


        // ========== Call Object Lists Setup ==========
        ObjectLists.createCustomItems();
		ObjectLists.createLists();


		// ========== Mob Events ==========
		MobEventBase.loadGlobalSettings();

		
		// ========== Mod Support ==========
		DLDungeons.init();
	}
	
	
	// ==================================================
	//                  Initialization
	// ==================================================
	@Mod.EventHandler
    public void load(FMLInitializationEvent event) {
		// ========== Register and Initialize Handlers ==========
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		
		// ========== Register Special Entities ==========
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "summoningportal"), EntityPortal.class, "summoningportal", specialEntityID++, instance, 64, 1, true);
		MobInfo newMob = new MobInfo(group, "fear", EntityFear.class, 0x000000, 0x000000)
			.setPeaceful(true).setSummonable(false).setSummonCost(0).setDungeonLevel(0).setDummy(true);
		EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "fear"), EntityFear.class, "fear", specialEntityID++, instance, 64, 1, true);
		AssetManager.addSound("effect_fear", group, "effect.fear");
        EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "hitarea"), EntityHitArea.class, "hitarea", specialEntityID++, instance, 64, 1, true);


        // ========== Load All Mob Info from Configs ==========
        MobInfo.loadAllFromConfigs(this.group);
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        // ========== Assign Mob Spawning ==========
        GroupInfo.loadAllSpawningFromConfigs();
        MobInfo.loadAllSpawningFromConfigs();


		// ========== Register and Initialize Handlers/Objects ==========
		proxy.registerAssets();
        proxy.registerTileEntities();
		
		
		// ========== Mob Events ==========
        SharedMobEvents.createSharedEvents(group);


        // ========== Seasonal Item Lists ==========
        ItemHalloweenTreat.createObjectLists();
        ItemWinterGift.createObjectLists();
    }
	
	
    // ==================================================
    //                    Server Load
    // ==================================================
	@Mod.EventHandler
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
