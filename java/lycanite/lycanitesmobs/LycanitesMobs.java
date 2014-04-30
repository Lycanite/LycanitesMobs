package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.entity.EntityPortal;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.item.ItemSoulgazer;
import lycanite.lycanitesmobs.api.item.ItemStaffBlood;
import lycanite.lycanitesmobs.api.item.ItemStaffSavage;
import lycanite.lycanitesmobs.api.item.ItemStaffStable;
import lycanite.lycanitesmobs.api.item.ItemStaffSturdy;
import lycanite.lycanitesmobs.api.item.ItemStaffSummoning;
import lycanite.lycanitesmobs.api.packet.PacketPipeline;
import lycanite.lycanitesmobs.api.spawning.CustomSpawner;
import lycanite.lycanitesmobs.api.spawning.SpawnType;
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
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = LycanitesMobs.modid, name = LycanitesMobs.name, version = LycanitesMobs.version)
public class LycanitesMobs implements ILycaniteMod {
	
	public static final String modid = "lycanitesmobs";
	public static final String name = "Lycanites Mobs";
	public static final String version = "1.5.0 - MC 1.6.4";
	public static final String domain = modid.toLowerCase();
	
	public static final PacketPipeline packetPipeline = new PacketPipeline();
	
	public static int mobID = -1;
	public static int projectileID = 99;
	
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static LycanitesMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.ClientProxy", serverSide="lycanite.lycanitesmobs.CommonProxy")
	public static CommonProxy proxy;
	
	// Creative Tab:
	public static final CreativeTabs creativeTab = new CreativeTab(CreativeTabs.getNextID(), modid);
	
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
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Items ==========
		ObjectManager.addItem("Soulgazer", "Soulgazer", new ItemSoulgazer());
		ObjectManager.addItem("SummoningStaff", "Summoning Staff", new ItemStaffSummoning());
		ObjectManager.addItem("StableSummoningStaff", "Stable Summoning Staff", new ItemStaffStable());
		ObjectManager.addItem("BloodSummoningStaff", "Blood Summoning Staff", new ItemStaffBlood());
		ObjectManager.addItem("SturdySummoningStaff", "Sturdy Summoning Staff", new ItemStaffSturdy());
		ObjectManager.addItem("SavageSummoningStaff", "Savage Summoning Staff", new ItemStaffSavage());
	}
	
	
	// ==================================================
	//                  Initialization
	// ==================================================
	@EventHandler
    public void load(FMLInitializationEvent event) {
		// ========== Register and Initialize Handlers ==========
		proxy.registerEvents();
		packetPipeline.initialize();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new GuiHandler());
		
		// ========== Register Entities ==========
		int specialEntityID = 0;
		EntityRegistry.registerModEntity(EntityPortal.class, "SummoningPortal", specialEntityID++, instance, 64, 1, true);
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
    public void postInit(FMLPostInitializationEvent event) {
		// ========== Register and Initialize Handlers/Objects ==========
		packetPipeline.postInitialize();
		proxy.registerAssets();
		proxy.registerTileEntities();
		proxy.registerRenders();
		
		// ========== Call Object Lists Setup ==========
		ObjectLists.createLists();
		
		// ========== Rename Vanilla Items ==========
		LanguageRegistry.addName(Items.iron_horse_armor, "Iron Pet Armor");
		LanguageRegistry.addName(Items.golden_horse_armor, "Golden Pet Armor");
		LanguageRegistry.addName(Items.diamond_horse_armor, "Diamond Pet Armor");
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("Soulgazer"), 1, 0),
				new Object[] { "GBG", "BDB", "GBG",
				Character.valueOf('G'), Items.gold_ingot,
				Character.valueOf('D'), Items.diamond,
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("SummoningStaff"), 1, 0),
				new Object[] { " E ", " B ", " G ",
				Character.valueOf('E'), Items.ender_pearl,
				Character.valueOf('B'), Items.bone,
				Character.valueOf('G'), Items.gold_ingot
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("StableSummoningStaff"), 1, 0),
				new Object[] { " D ", " S ", " G ",
				Character.valueOf('S'), ObjectManager.getItem("SummoningStaff"),
				Character.valueOf('G'), Items.gold_ingot,
				Character.valueOf('D'), Items.diamond
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("BloodSummoningStaff"), 1, 0),
				new Object[] { "RRR", "BSB", "NDN",
				Character.valueOf('S'), ObjectManager.getItem("SummoningStaff"),
				Character.valueOf('R'), Items.redstone,
				Character.valueOf('B'), Items.bone,
				Character.valueOf('N'), Items.nether_wart,
				Character.valueOf('D'), Items.diamond
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("SturdySummoningStaff"), 1, 0),
				new Object[] { "III", "ISI", " O ",
				Character.valueOf('S'), ObjectManager.getItem("SummoningStaff"),
				Character.valueOf('I'), Items.iron_ingot,
				Character.valueOf('O'), Blocks.obsidian
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("SavageSummoningStaff"), 1, 0),
				new Object[] { "LLL", "BSB", "GGG",
				Character.valueOf('S'), ObjectManager.getItem("SummoningStaff"),
				Character.valueOf('B'), Items.bone,
				Character.valueOf('G'), Items.ghast_tear,
				Character.valueOf('L'), new ItemStack(Items.dye, 1, 4)
			}));
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
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public LycanitesMobs getInstance() { return instance; }
	
	@Override
	public String getModID() { return modid; }
	
	@Override
	public String getDomain() { return domain; }
	
	@Override
	public Config getConfig() { return config; }
	
	@Override
	public int getNextMobID() { return ++this.mobID; }
	
	@Override
	public int getNextProjectileID() { return ++this.projectileID; }
}
