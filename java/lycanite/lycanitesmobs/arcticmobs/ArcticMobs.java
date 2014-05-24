package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.arcticmobs.block.BlockFrostweb;
import lycanite.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorFrostbolt;
import lycanite.lycanitesmobs.arcticmobs.dispenser.DispenserBehaviorFrostweb;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityFrostbolt;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityFrostweaver;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityFrostweb;
import lycanite.lycanitesmobs.arcticmobs.entity.EntityReiver;
import lycanite.lycanitesmobs.arcticmobs.item.ItemArcticEgg;
import lycanite.lycanitesmobs.arcticmobs.item.ItemFrostboltCharge;
import lycanite.lycanitesmobs.arcticmobs.item.ItemFrostwebCharge;
import lycanite.lycanitesmobs.arcticmobs.item.ItemScepterFrostbolt;
import lycanite.lycanitesmobs.arcticmobs.item.ItemScepterFrostweb;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ArcticMobs.modid, name = ArcticMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class ArcticMobs implements ILycaniteMod {
	
	public static final String modid = "arcticmobs";
	public static final String name = "Lycanites Arctic Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static ArcticMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.arcticmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.arcticmobs.CommonSubProxy")
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
		ObjectManager.addItem("arcticegg", new ItemArcticEgg());
		ObjectManager.addItem("frostboltcharge", new ItemFrostboltCharge());
		ObjectManager.addItem("frostboltscepter", new ItemScepterFrostbolt());
		ObjectManager.addItem("frostwebcharge", new ItemFrostwebCharge());
		ObjectManager.addItem("frostwebscepter", new ItemScepterFrostweb());

		// ========== Create Blocks ==========
		ObjectManager.addBlock("frostweb", new BlockFrostweb());
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("arcticegg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "reiver", EntityReiver.class, 0xDDEEFF, 0x99DDEE, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "frostweaver", EntityFrostweaver.class, 0xAADDFF, 0x226699, 2));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("frostbolt", EntityFrostbolt.class, ObjectManager.getItem("frostboltcharge"), new DispenserBehaviorFrostbolt());
		ObjectManager.addProjectile("frostweb", EntityFrostweb.class, ObjectManager.getItem("frostwebcharge"), new DispenserBehaviorFrostweb());
		
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
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("frostboltscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("frostboltcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("frostwebscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("frostwebcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("yetimeatraw").itemID, new ItemStack(ObjectManager.getItem("yetimeatcooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public ArcticMobs getInstance() { return instance; }
	
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
