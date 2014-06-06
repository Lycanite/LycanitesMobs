package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.swampmobs.block.BlockPoisonCloud;
import lycanite.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorPoisonRay;
import lycanite.lycanitesmobs.swampmobs.dispenser.DispenserBehaviorVenomShot;
import lycanite.lycanitesmobs.swampmobs.entity.EntityAspid;
import lycanite.lycanitesmobs.swampmobs.entity.EntityDweller;
import lycanite.lycanitesmobs.swampmobs.entity.EntityEttin;
import lycanite.lycanitesmobs.swampmobs.entity.EntityEyewig;
import lycanite.lycanitesmobs.swampmobs.entity.EntityGhoulZombie;
import lycanite.lycanitesmobs.swampmobs.entity.EntityLurker;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRay;
import lycanite.lycanitesmobs.swampmobs.entity.EntityPoisonRayEnd;
import lycanite.lycanitesmobs.swampmobs.entity.EntityRemobra;
import lycanite.lycanitesmobs.swampmobs.entity.EntityVenomShot;
import lycanite.lycanitesmobs.swampmobs.item.ItemPoisonGland;
import lycanite.lycanitesmobs.swampmobs.item.ItemScepterPoisonRay;
import lycanite.lycanitesmobs.swampmobs.item.ItemScepterVenomShot;
import lycanite.lycanitesmobs.swampmobs.item.ItemSwampEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
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

@Mod(modid = SwampMobs.modid, name = SwampMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class SwampMobs implements ILycaniteMod {
	
	public static final String modid = "swampmobs";
	public static final String name = "Lycanites Swamp Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static SwampMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.swampmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.swampmobs.CommonSubProxy")
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
		ObjectManager.addItem("swampegg", new ItemSwampEgg());
		
		ObjectManager.addItem("aspidmeatraw", new ItemCustomFood("aspidmeatraw", domain, 2, 0.5F).setPotionEffect(Potion.poison.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("aspidmeatraw"));
		
		ObjectManager.addItem("aspidmeatcooked", new ItemCustomFood("aspidmeatcooked", domain, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("aspidmeatcooked"));
		OreDictionary.registerOre("beef", ObjectManager.getItem("aspidmeatcooked"));
		
		ObjectManager.addItem("mosspie", new ItemCustomFood("mosspie", domain, 6, 0.7F).setPotionEffect(Potion.regeneration.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("mosspie"));

		ObjectManager.addItem("lurkertreat", new ItemTreat("lurkertreat", this.domain));
		ObjectManager.addItem("eyewigtreat", new ItemTreat("eyewigtreat", this.domain));
		
		ObjectManager.addItem("poisongland", new ItemPoisonGland());
		ObjectManager.addItem("poisonrayscepter", new ItemScepterPoisonRay());
		ObjectManager.addItem("venomshotscepter", new ItemScepterVenomShot());
		
		// ========== Create Blocks ==========
		AssetManager.addSound("poisoncloud", domain, "block.poisoncloud");
		ObjectManager.addBlock("poisoncloud", new BlockPoisonCloud());
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("swampegg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "ghoulzombie", EntityGhoulZombie.class, 0x009966, 0xAAFFDD, 2));
		ObjectManager.addMob(new MobInfo(this, "dweller", EntityDweller.class, 0x009922, 0x994499, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "ettin", EntityEttin.class, 0x669900, 0xFF6600, 6));
		ObjectManager.addMob(new MobInfo(this, "lurker", EntityLurker.class, 0x009900, 0x99FF00, 4));
		ObjectManager.addMob(new MobInfo(this, "eyewig", EntityEyewig.class, 0x000000, 0x009900, 4));
		ObjectManager.addMob(new MobInfo(this, "aspid", EntityAspid.class, 0x009944, 0x446600, 2));
		ObjectManager.addMob(new MobInfo(this, "remobra", EntityRemobra.class, 0x440066, 0xDD00FF, 2).setSummonable(true));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("poisonray", EntityPoisonRay.class, Items.fermented_spider_eye, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("poisonrayend", EntityPoisonRayEnd.class);
		ObjectManager.addProjectile("venomshot", EntityVenomShot.class, ObjectManager.getItem("poisongland"), new DispenserBehaviorVenomShot());
		
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
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("poisonrayscepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Items.fermented_spider_eye,
				Character.valueOf('P'), ObjectManager.getItem("poisongland"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("venomshotscepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Items.rotten_flesh,
				Character.valueOf('P'), ObjectManager.getItem("poisongland"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("mosspie"), 1, 0),
				new Object[] {
					Blocks.vine,
					Blocks.red_mushroom,
					ObjectManager.getItem("aspidmeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("mosspie"), 1, 0),
				new Object[] {
					Blocks.vine,
					Blocks.brown_mushroom,
					ObjectManager.getItem("aspidmeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("ventoraptortreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("cookedaspidmeat"),
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("eyewigtreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("poisongland"),
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("aspidmeatraw"), new ItemStack(ObjectManager.getItem("aspidmeatcooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public SwampMobs getInstance() { return instance; }
	
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
