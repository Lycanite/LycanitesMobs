package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import lycanite.lycanitesmobs.api.spawning.SpawnType;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {LycanitesMobs.modid}, packetHandler = PacketHandler.class)
public class LycanitesMobs {
	
	public static final String modid = "LycanitesMobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.5.0 - MC 1.6.4";
	public static final String domain = modid.toLowerCase();
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.ClientProxy", serverSide="lycanite.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;
	
	// Creative Tab:
	public static final CreativeTabs creativeTab = new CreativeTab(CreativeTabs.getNextID(), modid);
	
	// Managers and Handlers:
	public static Hooks hooks = new Hooks();
	
	// Texture Path:
	public static String texturePath = "mods/lycanitesmobs/";
	
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		config.init(modid);
		
		// ========== Mob Info ==========
		MobInfo.loadGlobalSettings();
		
		// ========== Spawn Type ==========
		SpawnType.loadSpawnTypes();
		
		// ========== Spawn Info ==========
		SpawnInfo.loadGlobalSettings();
		
		// ========== Add Custom Potion Effects ==========
		PotionBase.reserveEffectIDSpace();
		ObjectManager.addPotionEffect("Paralysis", config, true, 0xFFFF00, 1, 0);
		ObjectManager.addPotionEffect("Leech", config, true, 0x00FF99, 7, 0);
		ObjectManager.addPotionEffect("Penetration", config, true, 0x222222, 6, 1);
		ObjectManager.addPotionEffect("Recklessness", config, true, 0xFF0044, 4, 0);
		ObjectManager.addPotionEffect("Rage", config, true, 0xFF4400, 4, 0);
		ObjectManager.addPotionEffect("Weight", config, true, 0x000022, 1, 0);
		ObjectManager.addPotionEffect("Swiftswimming", config, true, 0x0000FF, 0, 2);
		
		// ========== Register Event Listeners ==========
		MinecraftForge.EVENT_BUS.register(new PotionEffects());
		MinecraftForge.EVENT_BUS.register(new EventListener());
		MinecraftForge.EVENT_BUS.register(new CustomSpawner());
	}
	
	
	// ==================================================
	//                  Initialization
	// ==================================================
	@EventHandler
    public void load(FMLInitializationEvent event) {
		// ========== Custom Keys and Events ==========
		proxy.registerEvents();
		
		// ========== Handlers ==========
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		
		// ========== Set Current Mod ==========
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(EntityPortal.class, "SummoningPortal", specialEntityID++, instance, 64, 1, true);
		LanguageRegistry.instance().addStringLocalization("entity." + "SummoningPortal" + ".name", "en_US", "Summoning Portal");
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		// ========== Register ==========
		proxy.registerAssets();
		proxy.registerTileEntities();
		proxy.registerRenders();
		
		// ========== Call Object Lists Setup ==========
		ObjectLists.createLists();
		
		// ========== Rename Vanilla Items ==========
		LanguageRegistry.addName(Item.horseArmorIron, "Iron Pet Armor");
		LanguageRegistry.addName(Item.horseArmorGold, "Gold Pet Armor");
		LanguageRegistry.addName(Item.horseArmorDiamond, "Diamond Pet Armor");
    }
	
	
	// ==================================================
	//                     Debugging
	// ==================================================
	public static void printDebug(String key, String message) {
		if("".equals(key) || config.getDebug(key)) {
			System.out.println("[LycanitesMobs] [Debug] " + message);
		}
	}
	
	public static void printWarning(String key, String message) {
		if("".equals(key) || config.getDebug(key)) {
			System.err.println("[LycanitesMobs] [WARNING] " + message);
		}
	}
}
