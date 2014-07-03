package lycanite.lycanitesmobs.mountainmobs;

import lycanite.lycanitesmobs.OldConfig;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorBoulderBlast;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityBoulderBlast;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityJabberwock;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityTroll;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityYale;
import lycanite.lycanitesmobs.mountainmobs.item.ItemBoulderBlastCharge;
import lycanite.lycanitesmobs.mountainmobs.item.ItemMountainEgg;
import lycanite.lycanitesmobs.mountainmobs.item.ItemScepterBoulderBlast;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MountainMobs.modid, name = MountainMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class MountainMobs implements ILycaniteMod {
	
	public static final String modid = "mountainmobs";
	public static final String name = "Lycanites Mountain Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static OldConfig config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static MountainMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.mountainmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.mountainmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		config.init(modid);
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Items ==========
		ObjectManager.addItem("mountainegg", new ItemMountainEgg());
		
		ObjectManager.addItem("boulderblastcharge", new ItemBoulderBlastCharge());
		ObjectManager.addItem("boulderblastscepter", new ItemScepterBoulderBlast());
		
		ObjectManager.addItem("yalemeatraw", new ItemCustomFood("yalemeatraw", domain, 2, 0.5F).setPotionEffect(Potion.digSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yalemeatraw"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("yalemeatraw"));
		
		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", domain, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("yalemeatcooked"));
		
		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", domain, 6, 0.7F).setPotionEffect(Potion.digSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("peakskebab"));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("mountainegg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "jabberwock", EntityJabberwock.class, 0x662222, 0xFFFFAA, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "troll", EntityTroll.class, 0x007711, 0xEEEEEE, 6).setSummonable(false));
		ObjectManager.addMob(new MobInfo(this, "yale", EntityYale.class, 0xFFEEAA, 0xFFDD77, 6).setSummonable(false));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("boulderblast", EntityBoulderBlast.class, ObjectManager.getItem("boulderblastcharge"), new DispenserBehaviorBoulderBlast());
		
		// ========== Register Models ==========
		proxy.registerModels();
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = this.config.getSpawnBiomesTypes();
		if(config.getFeatureBool("controlvanilla")) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("PeaksKebab"), 1, 0),
				new Object[] {
					Items.stick,
					Items.carrot,
					Items.melon,
					ObjectManager.getItem("YaleMeatCooked")
				}
			));
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("boulderblastscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("boulderblastcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("yalemeatraw"), new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public MountainMobs getInstance() { return instance; }
	
	@Override
	public String getModID() { return modid; }
	
	@Override
	public String getDomain() { return domain; }
	
	@Override
	public OldConfig getConfig() { return config; }
	
	@Override
	public int getNextMobID() { return ++this.mobID; }
	
	@Override
	public int getNextProjectileID() { return ++this.projectileID; }
}
