package lycanite.lycanitesmobs;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.SpawnInfo;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
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
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {LycanitesMobs.modid}, packetHandler = PacketHandler.class)
public class LycanitesMobs {
	
	public static final String modid = "LycanitesMobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.4.9e - MC 1.6.4";
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
		
		// ========== Spawn Info ==========
		SpawnInfo.loadGlobalSettings();
		
		// ========== Add Custom Potion Effects ==========
		Potion[] potionTypes;
		for(Field f : Potion.class.getDeclaredFields()) {
			f.setAccessible(true);
			try {
				if(f.getName().equals("potionTypes") || f.getName().equals("field_76425_a")) {
					Field modfield = Field.class.getDeclaredField("modifiers");
					modfield.setAccessible(true);
					modfield.setInt(f, f.getModifiers() & ~Modifier.FINAL);
					
					potionTypes = (Potion[])f.get(null);
					final Potion[] newPotionTypes = new Potion[256];
					System.arraycopy(potionTypes, 0, newPotionTypes, 0, potionTypes.length);
					f.set(null, newPotionTypes);
				}
			}
			catch (Exception e) {
				System.err.println("[Lycanites Mobs] An error occured when adding custom potion effects:");
				System.err.println(e);
			}
		}
		
		ObjectManager.addPotionEffect("Paralysis", config, true, 0xFFFF00, 1, 0);
		ObjectManager.addPotionEffect("Leech", config, true, 0x00FF99, 7, 0);
		ObjectManager.addPotionEffect("Penetration", config, true, 0x222222, 6, 1);
		ObjectManager.addPotionEffect("Recklessness", config, true, 0xFF0044, 4, 0);
		ObjectManager.addPotionEffect("Rage", config, true, 0xFF4400, 4, 0);
		ObjectManager.addPotionEffect("Weight", config, true, 0x000022, 1, 0);
		ObjectManager.addPotionEffect("Swiftswimming", config, true, 0x0000FF, 0, 2);
		
		MinecraftForge.EVENT_BUS.register(new EventListener());
		MinecraftForge.EVENT_BUS.register(new CustomSpawner());
		MinecraftForge.EVENT_BUS.register(new PotionEffects());
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
}
