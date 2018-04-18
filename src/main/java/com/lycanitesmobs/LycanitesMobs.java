package com.lycanitesmobs;

import com.lycanitesmobs.core.VersionChecker;
import com.lycanitesmobs.core.block.BlockEquipmentForge;
import com.lycanitesmobs.core.block.BlockSummoningPedestal;
import com.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import com.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import com.lycanitesmobs.core.capabilities.IExtendedEntity;
import com.lycanitesmobs.core.capabilities.IExtendedPlayer;
import com.lycanitesmobs.core.command.CommandMain;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.entity.EntityFear;
import com.lycanitesmobs.core.entity.EntityHitArea;
import com.lycanitesmobs.core.entity.EntityPortal;
import com.lycanitesmobs.core.helpers.LMReflectionHelper;
import com.lycanitesmobs.core.info.*;
import com.lycanitesmobs.core.item.*;
import com.lycanitesmobs.core.item.equipment.EquipmentPartManager;
import com.lycanitesmobs.core.mobevent.MobEventListener;
import com.lycanitesmobs.core.mobevent.MobEventManager;
import com.lycanitesmobs.core.mods.DLDungeons;
import com.lycanitesmobs.core.network.PacketHandler;
import com.lycanitesmobs.core.pets.DonationFamiliars;
import com.lycanitesmobs.core.spawner.SpawnerEventListener;
import com.lycanitesmobs.core.spawner.SpawnerManager;
import com.lycanitesmobs.core.tileentity.TileEntityEquipmentForge;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import com.lycanitesmobs.core.worldgen.WorldGeneratorDungeon;
import com.lycanitesmobs.integration.Integrations;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version,
	 useMetadata = false, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions,
	 dependencies = "required-after:targeting_api")
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String versionNumber = "1.19.5.2";
	public static final String versionMC = "1.12.2";
	public static final String version = versionNumber + " - MC " + versionMC;
	public static final String website = "http://lycanitesmobs.com";
	public static final String websiteAPI = "http://api.lycanitesmobs.com";
	public static final String websitePatreon = "https://www.patreon.com/lycanite";
	public static final String acceptedMinecraftVersions = "[1.12,1.13)";
	
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
	
	// Creative Tab:
    public static final CreativeTabs itemsTab = new CreativeTabItems(CreativeTabs.getNextID(), modid + ".items");
    public static final CreativeTabs blocksTab = new CreativeTabBlocks(CreativeTabs.getNextID(), modid + ".blocks");
	public static final CreativeTabs creaturesTab = new CreativeTabCreatures(CreativeTabs.getNextID(), modid + ".creatures");
	public static final CreativeTabs equipmentPartsTab = new CreativeTabEquipmentParts(CreativeTabs.getNextID(), modid + ".equipmentparts");
	
	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";

	// Extra Config Settings:
	public static boolean disableNausea = false;

	// Dungeon System:
	public static WorldGeneratorDungeon dungeonGenerator;

	// Universal Bucket:
	static {
		FluidRegistry.enableUniversalBucket();
	}
	
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, name, 1000);
        ConfigBase.versionCheck("1.17.3.2", version);
		group.loadFromConfig();
		config = ConfigBase.getConfig(group, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		config.setCategoryComment("Extras", "Other extra config settings, some of the aren't necessarily specific to Lycanites Mobs.");
		VersionChecker.enabled = config.getBool("Extras", "Version Checker", VersionChecker.enabled, "Set to false to disable the version checker.");
		DonationFamiliars.instance.enabled = config.getBool("Extras", "Donation Familiars", DonationFamiliars.instance.enabled, "Donation Familiars help support the development of this mod but if needed, set this to false to disable them.");

		// ========== Admin Entity Removal Tool ==========
        config.setCategoryComment("Admin", "Special tools for server admins.");
        ExtendedEntity.FORCE_REMOVE_ENTITY_IDS = config.getStringList("Admin", "Force Remove Entity Names", new String[0], "Here you can add a list of entity IDs for entity that you want to be forcefully removed.");
        if(ExtendedEntity.FORCE_REMOVE_ENTITY_IDS != null && ExtendedEntity.FORCE_REMOVE_ENTITY_IDS.length > 0) {
            printInfo("", "Lycanites Mobs will forcefully remove the following entities based on their registered IDs:");
            for (String removeEntityID : ExtendedEntity.FORCE_REMOVE_ENTITY_IDS)
                printInfo("", removeEntityID);
        }
        ExtendedEntity.FORCE_REMOVE_ENTITY_TICKS = config.getInt("Admin", "Force Remove Entity Ticks", 40, "How many ticks it takes for an entity to be forcefully removed (1 second = 20 ticks). This only applies to EntityLiving, other entities are instantly removed.");

        // ========== Register Rendering Factories ==========
        proxy.registerRenders(this.group);

        // ========== Change Health Limit ==========
		LMReflectionHelper.setPrivateFinalValue(RangedAttribute.class, (RangedAttribute)SharedMonsterAttributes.MAX_HEALTH, 100000, "maximumValue", "field_111118_b");

		// ========== Initialize Packet Handler ==========
		this.packetHandler.init();
		
		
		// ========== Custom Potion Effects ==========
		config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
		if(config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
			ObjectManager.addPotionEffect("paralysis", config, true, 0xFFFF00, false);
			ObjectManager.addPotionEffect("penetration", config, true, 0x222222, false);
			ObjectManager.addPotionEffect("recklessness", config, true, 0xFF0044, false);
			ObjectManager.addPotionEffect("rage", config, true, 0xFF4400, false);
			ObjectManager.addPotionEffect("weight", config, true, 0x000022, false);
			ObjectManager.addPotionEffect("fear", config, false, 0x220022, false);
			ObjectManager.addPotionEffect("decay", config, true, 0x110033, false);
			ObjectManager.addPotionEffect("insomnia", config, true, 0x002222, false);
			ObjectManager.addPotionEffect("instability", config, true, 0x004422, false);
			ObjectManager.addPotionEffect("lifeleak", config, true, 0x0055FF, false);
			ObjectManager.addPotionEffect("plague", config, true, 0x220066, false);
			ObjectManager.addPotionEffect("aphagia", config, true, 0xFFDDDD, false);
			ObjectManager.addPotionEffect("smited", config, true, 0xDDDDFF, false);
			ObjectManager.addPotionEffect("smouldering", config, true, 0xDD0000, false);

			ObjectManager.addPotionEffect("leech", config, false, 0x00FF99, true);
			ObjectManager.addPotionEffect("swiftswimming", config, false, 0x0000FF, true);
			ObjectManager.addPotionEffect("fallresist", config, false, 0xDDFFFF, true);
			ObjectManager.addPotionEffect("rejuvenation", config, false, 0x99FFBB, true);
			ObjectManager.addPotionEffect("immunization", config, false, 0x66FFBB, true);
			ObjectManager.addPotionEffect("cleansed", config, false, 0x66BBFF, true);
			ObjectManager.addPotionEffect("heataura", config, false, 0x996600, true); // TODO Implement
			ObjectManager.addPotionEffect("staticaura", config, false, 0xFFBB551, true); // TODO Implement
			ObjectManager.addPotionEffect("freezeaura", config, false, 0x55BBFF, true); // TODO Implement
			ObjectManager.addPotionEffect("envenom", config, false, 0x44DD66, true); // TODO Implement

			MinecraftForge.EVENT_BUS.register(new PotionEffects());
		}
		disableNausea = config.getBool("Potion Effects", "Disable Nausea Debuff", disableNausea, "Set to true to disable the vanilla nausea debuff on players.");


		// ========== Elements ==========
		ElementManager.getInstance().loadConfig();
		ElementManager.getInstance().loadAllFromJSON(group);


		// ========== Creatures ==========
		CreatureManager.getInstance().loadConfig();


		// ========== Spawners ==========
		FMLCommonHandler.instance().bus().register(SpawnerEventListener.getInstance());


		// ========== Mob Events ==========
		MobEventManager.getInstance().loadConfig();
		FMLCommonHandler.instance().bus().register(MobEventManager.getInstance());
		FMLCommonHandler.instance().bus().register(MobEventListener.getInstance());


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
		ObjectManager.addBlock("equipmentforge_wood", new BlockEquipmentForge(group, 1));
		ObjectManager.addBlock("equipmentforge_stone", new BlockEquipmentForge(group, 2));
		ObjectManager.addBlock("equipmentforge_iron", new BlockEquipmentForge(group, 3));
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("soulstone", new ItemSoulstone(group, ""));
		ObjectManager.addItem("soulkey", new ItemSoulkey("soulkey", 0));
		ObjectManager.addItem("soulkeydiamond", new ItemSoulkey("soulkeydiamond", 1));
		ObjectManager.addItem("soulkeyemerald", new ItemSoulkey("soulkeyemerald", 2));
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning("summoningstaff", "summoningstaff"));
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable("stablesummoningstaff", "staffstable"));
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood("bloodsummoningstaff", "staffblood"));
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy("sturdysummoningstaff", "staffsturdy"));
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage("savagesummoningstaff", "staffsavage"));
		
		// Super Foods:
		ObjectManager.addItem("battleburrito", new ItemFoodBattleBurrito("battleburrito", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));
		ObjectManager.addItem("explorersrisotto", new ItemFoodExplorersRisotto("explorersrisotto", group, 6, 0.7F).setAlwaysEdible().setMaxStackSize(16));

		// Buff Items:
		ObjectManager.addItem("immunizer", new ItemImmunizer());
		ObjectManager.addItem("cleansingcrystal", new ItemCleansingCrystal());

		// Seasonal Items:
		ObjectManager.addItem("halloweentreat", new ItemHalloweenTreat());
        ObjectManager.addItem("wintergift", new ItemWinterGift());
        ObjectManager.addItem("wintergiftlarge", new ItemWinterGiftLarge());

        ObjectManager.addItem("mobtoken", new ItemMobToken(group));


        // ========== Create Tile Entities ==========
        ObjectManager.addTileEntity("summoningpedestal", TileEntitySummoningPedestal.class);
		ObjectManager.addTileEntity("equipmentforge", TileEntityEquipmentForge.class);


        // ========== Call Object Lists Setup ==========
        ObjectLists.createCustomItems();
		ObjectLists.createLists();

		
		// ========== Mod Support ==========
		DLDungeons.init();
	}
	
	
	// ==================================================
	//                  Initialization
	// ==================================================
	@Mod.EventHandler
    public void init(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());

		// ========== Creatures ==========
		CreatureManager.getInstance().loadAllFromJSON(group);
		CreatureManager.getInstance().registerAll(group);


		// ========== Equipment ==========
		EquipmentPartManager.getInstance().loadAllFromJSON(group);
		
		
		// ========== Special Entities ==========
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "summoningportal"), EntityPortal.class, "summoningportal", specialEntityID++, instance, 64, 1, true);
		EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "fear"), EntityFear.class, "fear", specialEntityID++, instance, 64, 1, true);
		AssetManager.addSound("effect_fear", group, "effect.fear");
		EntityRegistry.registerModEntity(new ResourceLocation(this.group.filename, "hitarea"), EntityHitArea.class, "hitarea", specialEntityID++, instance, 64, 1, true);

		// ========== Mod Integrations API ==========
		Integrations.setup();
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {

        // ========== Assign Mob Spawning ==========
        GroupInfo.loadAllSpawningFromConfigs();


		// ========== Register and Initialize Handlers/Objects ==========
		proxy.registerAssets();
        proxy.registerTileEntities();


        // ========== Creatures ==========
		CreatureManager.getInstance().initAll();


		// ========== Spawners ==========
		SpawnerManager.getInstance().loadAllFromJSON();
		
		
		// ========== Mob Events ==========
        MobEventManager.getInstance().loadAllFromJSON(group);


        // ========== Dungeons ==========
		DungeonManager.getInstance().loadAllFromJSON();
		dungeonGenerator = new WorldGeneratorDungeon();
		GameRegistry.registerWorldGenerator(dungeonGenerator, 1000);


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
            System.out.println("[LycanitesMobs] [Info] [" + key + "] " + message);
        }
    }

    public static void printDebug(String key, String message) {
        if("".equals(key) || config.getBool("Debug", key, false)) {
            System.out.println("[LycanitesMobs] [Debug] [" + key + "] " + message);
        }
    }

    public static void printWarning(String key, String message) {
		if("".equals(key) || config.getBool("Debug", key, false)) {
			System.err.println("[LycanitesMobs] [WARNING] [" + key + "] " + message);
		}
	}
}
