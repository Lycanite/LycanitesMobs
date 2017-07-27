package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.core.block.BlockSummoningPedestal;
import lycanite.lycanitesmobs.core.capabilities.ExtendedEntityStorage;
import lycanite.lycanitesmobs.core.capabilities.ExtendedPlayerStorage;
import lycanite.lycanitesmobs.core.capabilities.IExtendedEntity;
import lycanite.lycanitesmobs.core.capabilities.IExtendedPlayer;
import lycanite.lycanitesmobs.core.command.CommandMain;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.entity.EntityFear;
import lycanite.lycanitesmobs.core.entity.EntityHitArea;
import lycanite.lycanitesmobs.core.entity.EntityPortal;
import lycanite.lycanitesmobs.core.info.*;
import lycanite.lycanitesmobs.core.item.*;
import lycanite.lycanitesmobs.core.mobevent.MobEventManager;
import lycanite.lycanitesmobs.core.mobevent.SharedMobEvents;
import lycanite.lycanitesmobs.core.mods.DLDungeons;
import lycanite.lycanitesmobs.core.network.PacketHandler;
import lycanite.lycanitesmobs.core.spawning.CustomSpawner;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.AchievementPage;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version, useMetadata = false)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.16.1.0 - MC 1.11.2";
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
	@SidedProxy(clientSide="lycanite.lycanitesmobs.ClientProxy", serverSide="lycanite.lycanitesmobs.CommonProxy")
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

    // Achievements:
    public static AchievementPage achievementPage;
    public static int achievementGlobalBaseID = 5500;

	// Extra Config Settings:
	public static boolean disableNausea = false;
	
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, name, achievementGlobalBaseID);
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
            ObjectManager.addPotionEffect("fallresist", config, false, 0xDDFFFF, 0, 0, false);
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
        ObjectManager.addItem("soulkey", new ItemSoulkey());
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
        SharedMobEvents.createSharedEvents(this.group);


        // ========== Seasonal Item Lists ==========
        ItemHalloweenTreat.createObjectLists();
        ItemWinterGift.createObjectLists();
		
        
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("soulgazer"), 1, 0),
                new Object[]{"GBG", "BDB", "GBG",
                        Character.valueOf('G'), Items.GOLD_INGOT,
                        Character.valueOf('D'), Items.DIAMOND,
                        Character.valueOf('B'), Items.BONE
                }));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("soulstone"), 1, 0),
				new Object[] { "DPD", "PSP", "DPD",
						Character.valueOf('D'), Items.DIAMOND,
						Character.valueOf('S'), ObjectManager.getItem("soulgazer"),
						Character.valueOf('P'), Items.ENDER_PEARL
				}));

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("soulkey"), 1, 0),
                new Object[] { "DND", "DSD", "DDD",
                        Character.valueOf('N'), Items.NETHER_STAR,
                        Character.valueOf('S'), ObjectManager.getItem("soulgazer"),
                        Character.valueOf('D'), Items.DIAMOND
                }));

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getBlock("summoningpedestal"), 1, 0),
                new Object[] { "GNG", "DSD", "GDG",
                        Character.valueOf('N'), Items.NETHER_STAR,
                        Character.valueOf('S'), ObjectManager.getItem("soulstone"),
                        Character.valueOf('D'), Blocks.DIAMOND_BLOCK,
                        Character.valueOf('G'), Blocks.GOLD_BLOCK
                }));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("summoningstaff"), 1, 0),
				new Object[] { " E ", " B ", " G ",
				Character.valueOf('E'), Items.ENDER_PEARL,
				Character.valueOf('B'), Items.BONE,
				Character.valueOf('G'), Items.GOLD_INGOT
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("stablesummoningstaff"), 1, 0),
				new Object[] { " D ", " S ", " G ",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('G'), Items.GOLD_INGOT,
				Character.valueOf('D'), Items.DIAMOND
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("bloodsummoningstaff"), 1, 0),
				new Object[] { "RRR", "BSB", "NDN",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('R'), Items.REDSTONE,
				Character.valueOf('B'), Items.BONE,
				Character.valueOf('N'), Items.NETHER_WART,
				Character.valueOf('D'), Items.DIAMOND
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("sturdysummoningstaff"), 1, 0),
				new Object[] { "III", "ISI", " O ",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('I'), Items.IRON_INGOT,
				Character.valueOf('O'), Blocks.OBSIDIAN
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("savagesummoningstaff"), 1, 0),
				new Object[] { "LLL", "BSB", "GGG",
				Character.valueOf('S'), ObjectManager.getItem("summoningstaff"),
				Character.valueOf('B'), Items.BONE,
				Character.valueOf('G'), Items.GHAST_TEAR,
				Character.valueOf('L'), new ItemStack(Items.DYE, 1, 4)
			}));
		
		// Super Food:
		if(ObjectManager.getItem("pinkymeatcooked") != null && ObjectManager.getItem("makameatcooked") != null
				&& ObjectManager.getItem("arisaurmeatcooked") != null && ObjectManager.getItem("yetimeatcooked") != null
				&& ObjectManager.getItem("aspidmeatcooked") != null) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(
					new ItemStack(ObjectManager.getItem("battleburrito"), 1, 0),
					new Object[] {
						ObjectManager.getItem("pinkymeatcooked"),
						ObjectManager.getItem("makameatcooked"),
						ObjectManager.getItem("arisaurmeatcooked"),
						ObjectManager.getItem("yetimeatcooked"),
						ObjectManager.getItem("aspidmeatcooked")
					}
				));
		}

		if(ObjectManager.getItem("joustmeatcooked") != null && ObjectManager.getItem("yalemeatcooked") != null
				&& ObjectManager.getItem("ikameatcooked") != null && ObjectManager.getItem("concapedemeatcooked") != null) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(
					new ItemStack(ObjectManager.getItem("explorersrisotto"), 1, 0),
					new Object[] {
						ObjectManager.getItem("joustmeatcooked"),
						ObjectManager.getItem("yalemeatcooked"),
						ObjectManager.getItem("ikameatcooked"),
						ObjectManager.getItem("concapedemeatcooked")
					}
				));
		}

        // Create all Recipe Makder recipes!
        RecipeMaker.createAllRecipies();


        // ========== Achievement Page ==========
        achievementPage = new AchievementPage(name, ObjectManager.achievements.values().toArray(new Achievement[ObjectManager.achievements.values().size()]));
        AchievementPage.registerAchievementPage(achievementPage);
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
