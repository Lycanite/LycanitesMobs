package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.demonmobs.block.BlockHellfire;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDevilstar;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDoomfireball;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorHellfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityAsmodi;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBehemoth;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBelph;
import lycanite.lycanitesmobs.demonmobs.entity.EntityCacodemon;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDemonicBlast;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDemonicSpark;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDevilstar;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDoomfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityNetherSoul;
import lycanite.lycanitesmobs.demonmobs.entity.EntityPinky;
import lycanite.lycanitesmobs.demonmobs.entity.EntityTrite;
import lycanite.lycanitesmobs.demonmobs.item.ItemDemonEgg;
import lycanite.lycanitesmobs.demonmobs.item.ItemDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.item.ItemDevilstar;
import lycanite.lycanitesmobs.demonmobs.item.ItemDoomfireball;
import lycanite.lycanitesmobs.demonmobs.item.ItemHellfireball;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDevilstar;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDoomfire;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterHellfire;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
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

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class DemonMobs implements ILycaniteMod {
	
	public static final String modid = "demonmobs";
	public static final String name = "Lycanites Demon Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static DemonMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.demonmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.demonmobs.CommonSubProxy")
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
		ObjectManager.addItem("demonegg", new ItemDemonEgg());
		ObjectManager.addItem("doomfirecharge", new ItemDoomfireball());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireball());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstar());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightning());
		
		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", domain, 4, 0.5F).setPotionEffect(Potion.wither.id, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));
		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", domain, 7, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));
		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", domain, 7, 0.7F).setPotionEffect(Potion.damageBoost.id, 60, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));
		
		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire());
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire());
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar());
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning());
		
		// ========== Create Blocks ==========
		AssetManager.addSound("hellfire", domain, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("demonegg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "belph", EntityBelph.class, 0x992222, 0x000000, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "behemoth", EntityBehemoth.class, 0xFF2222, 0xFF9900, 6).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "pinky", EntityPinky.class, 0xFF0099, 0x990000, 4));
		ObjectManager.addMob(new MobInfo(this, "trite", EntityTrite.class, 0xFFFF88, 0x000000, 1));
		ObjectManager.addMob(new MobInfo(this, "asmodi", EntityAsmodi.class, 0x999944, 0x0000FF, 8));
		ObjectManager.addMob(new MobInfo(this, "nethersoul", EntityNetherSoul.class, 0xFF9900, 0xFF0000, 1));
		ObjectManager.addMob(new MobInfo(this, "cacodemon", EntityCacodemon.class, 0xFF0000, 0x000099, 6));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class);
		ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
		
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
		
		// ========== Alter Vanilla Spawns ==========
		BiomeGenBase[] biomes = this.config.getSpawnBiomesTypes();
		// For some insane reason Zombie Pigmen have a spawn rate of 100 by default! I'm now matching this with my mobs to see if it affects things.
		if(config.getFeatureBool("ControlVanilla")) {
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("doomfirescepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("doomfirecharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("hellfirescepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("hellfirecharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("devilstarscepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("devilstarcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("demoniclightningscepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("demoniclightningcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("devillasagna"), 1, 0),
				new Object[] {
					Items.nether_wart,
					Items.wheat,
					ObjectManager.getItem("pinkymeatcooked")
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public DemonMobs getInstance() { return instance; }
	
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
