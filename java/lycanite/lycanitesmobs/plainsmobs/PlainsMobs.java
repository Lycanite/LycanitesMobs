package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityKobold;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMaka;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMakaAlpha;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityVentoraptor;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityZoataur;
import lycanite.lycanitesmobs.plainsmobs.item.ItemPlainsEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
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

@Mod(modid = PlainsMobs.modid, name = PlainsMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class PlainsMobs implements ILycaniteMod {
	
	public static final String modid = "plainsmobs";
	public static final String name = "Lycanites Plains Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static PlainsMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.plainsmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.plainsmobs.CommonSubProxy")
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
		ObjectManager.addItem("PlainsEgg", "Spawn", new ItemPlainsEgg(config.itemIDs.get("PlainsEgg")));
		
		ObjectManager.addItem("MakaMeatRaw", "Raw Maka Meat", new ItemCustomFood(config.itemIDs.get("MakaMeatRaw"), "MakaMeatRaw", domain, 2, 0.5F).setPotionEffect(Potion.weakness.id, 45, 2, 0.8F));
		ObjectLists.addItem("RawMeat", ObjectManager.getItem("MakaMeatRaw"));
		ObjectManager.addItem("MakaMeatCooked", "Cooked Maka Meat", new ItemCustomFood(config.itemIDs.get("MakaMeatCooked"), "MakaMeatCooked", domain, 6, 0.7F));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("MakaMeatCooked"));
		ObjectManager.addItem("BulwarkBurger", "Bulwark Burger", new ItemCustomFood(config.itemIDs.get("BulwarkBurger"), "BulwarkBurger", domain, 6, 0.7F).setPotionEffect(Potion.field_76444_x.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16)); // Absorbtion
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("BulwarkBurger"));
	}
	
	
	// ==================================================
	//                   Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("PlainsEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Kobold", EntityKobold.class, 0x996633, 0xFF7777, 1).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "Ventoraptor", EntityVentoraptor.class, 0x99BBFF, 0x0033FF, 4));
		ObjectManager.addMob(new MobInfo(this, "Maka", EntityMaka.class, 0xAA8855, 0x221100, 2));
		ObjectManager.addMob(new MobInfo(this, "MakaAlpha", "Maka Alpha", EntityMakaAlpha.class, 0x663300, 0x000000, 4));
		ObjectManager.addMob(new MobInfo(this, "Zoataur", "Zoataur", EntityZoataur.class, 0x442200, 0xFFDDBB, 4).setSummonable(true));
		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("Template", EntityTemplate.class, Item.templateCharge, new DispenserBehaviorPoisonRay());
		
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
		if(config.getFeatureBool("ControlVanilla")) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityChicken.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("BulwarkBurger"), 1, 0),
				new Object[] {
					Items.bread,
					ObjectManager.getItem("MakaMeatCooked"),
					Items.bread
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("MakaMeatRaw"), new ItemStack(ObjectManager.getItem("MakaMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public PlainsMobs getInstance() { return instance; }
	
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
