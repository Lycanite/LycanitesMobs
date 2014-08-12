package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.item.CreativeTabCreatures;
import lycanite.lycanitesmobs.api.item.CreativeTabItems;
import lycanite.lycanitesmobs.api.item.ItemSoulgazer;
import lycanite.lycanitesmobs.api.item.ItemStaffBlood;
import lycanite.lycanitesmobs.api.item.ItemStaffSavage;
import lycanite.lycanitesmobs.api.item.ItemStaffStable;
import lycanite.lycanitesmobs.api.item.ItemStaffSturdy;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.api.mods.DLDungeons;
import lycanite.lycanitesmobs.api.network.PacketHandler;
import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version)
public class LycanitesMobs {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.8.1e- MC 1.7.10";
	
	public static final PacketHandler packetHandler = new PacketHandler();

    public static GroupInfo group;
    public static ConfigBase config;
	
	// Instance:
	@Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.ClientProxy", serverSide="lycanite.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;
	
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
        ConfigBase.versionCheck("1.8.1", version);
		group.loadFromConfig();
		config = ConfigBase.getConfig(group, "general");
		config.setCategoryComment("Debug", "Set debug options to true to show extra debugging information in the console.");
		this.packetHandler.init();
		
		// ========== Custom Potion Effects ==========
		config.setCategoryComment("Potion Effects", "Here you can override each potion effect ID from the automatic ID, use 0 if you want it to stay automatic. Overrides should only be needed if you are running a lot of mods that add custom effects.");
		if(config.getBool("Potion Effects", "Enable Custom Effects", true, "Set to false to disable the custom potion effects.")) {
			PotionBase.reserveEffectIDSpace();
			ObjectManager.addPotionEffect("Paralysis", config, true, 0xFFFF00, 1, 0);
			ObjectManager.addPotionEffect("Leech", config, false, 0x00FF99, 7, 0);
			ObjectManager.addPotionEffect("Penetration", config, true, 0x222222, 6, 1);
			ObjectManager.addPotionEffect("Recklessness", config, true, 0xFF0044, 4, 0);
			ObjectManager.addPotionEffect("Rage", config, true, 0xFF4400, 4, 0);
			ObjectManager.addPotionEffect("Weight", config, true, 0x000022, 1, 0);
			ObjectManager.addPotionEffect("Swiftswimming", config, false, 0x0000FF, 0, 2);
			MinecraftForge.EVENT_BUS.register(new PotionEffects());
		}
		
		// ========== Register Event Listeners ==========
		MinecraftForge.EVENT_BUS.register(new EventListener());
        proxy.registerEvents();
		
		// ========== Mob Info ==========
		MobInfo.loadGlobalSettings();
		
		// ========== Custom Mob Spawning ==========
		MinecraftForge.EVENT_BUS.register(new CustomSpawner());
		SpawnTypeBase.loadSpawnTypes();
		
		// ========== Spawn Info ==========
		SpawnInfo.loadGlobalSettings();
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("summoningstaff", new ItemStaffSummoning());
		ObjectManager.addItem("stablesummoningstaff", new ItemStaffStable());
		ObjectManager.addItem("bloodsummoningstaff", new ItemStaffBlood());
		ObjectManager.addItem("sturdysummoningstaff", new ItemStaffSturdy());
		ObjectManager.addItem("savagesummoningstaff", new ItemStaffSavage());
		
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
