package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.junglemobs.block.BlockQuickWeb;
import lycanite.lycanitesmobs.junglemobs.entity.EntityConcapedeHead;
import lycanite.lycanitesmobs.junglemobs.entity.EntityConcapedeSegment;
import lycanite.lycanitesmobs.junglemobs.entity.EntityGeken;
import lycanite.lycanitesmobs.junglemobs.entity.EntityTarantula;
import lycanite.lycanitesmobs.junglemobs.entity.EntityUvaraptor;
import lycanite.lycanitesmobs.junglemobs.item.ItemJungleEgg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
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

@Mod(modid = JungleMobs.modid, name = JungleMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class JungleMobs implements ILycaniteMod {
	
	public static final String modid = "junglemobs";
	public static final String name = "Lycanites Jungle Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static JungleMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.junglemobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.junglemobs.CommonSubProxy")
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
		ObjectManager.addItem("JungleEgg", "Spawn", new ItemJungleEgg(config.itemIDs.get("JungleEgg")));
		
		ObjectManager.addItem("ConcapedeMeatRaw", "Raw Concapede Meat", new ItemCustomFood(config.itemIDs.get("ConcapedeMeatRaw"), "ConcapedeMeatRaw", domain, 2, 0.5F).setPotionEffect(Potion.moveSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("RawMeat", ObjectManager.getItem("ConcapedeMeatRaw"));
		ObjectManager.addItem("ConcapedeMeatCooked", "Cooked Concapede Meat", new ItemCustomFood(config.itemIDs.get("ConcapedeMeatCooked"), "ConcapedeMeatCooked", domain, 6, 0.7F));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("ConcapedeMeatCooked"));
		ObjectManager.addItem("TropicalCurry", "Tropical Curry", new ItemCustomFood(config.itemIDs.get("TropicalCurry"), "TropicalCurry", domain, 6, 0.7F).setPotionEffect(Potion.jump.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("TropicalCurry"));

		// ========== Create Blocks ==========
		ObjectManager.addBlock("QuickWeb", "QuickWeb", new BlockQuickWeb(config.blockIDs.get("QuickWeb")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("JungleEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Geken", EntityGeken.class, 0x00AA00, 0xFFFF00, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "Uvaraptor", EntityUvaraptor.class, 0x00FF33, 0xFF00FF, 4));
		ObjectManager.addMob(new MobInfo(this, "Concapede", EntityConcapedeHead.class, 0x111144, 0xDD0000, 2));
		ObjectManager.addMob(new MobInfo(this, "ConcapedeSegment", "Concapede Segment", EntityConcapedeSegment.class, 0x000022, 0x990000, 1));
		ObjectManager.addMob(new MobInfo(this, "Tarantula", EntityTarantula.class, 0x008800, 0xDD0000, 2).setSummonable(true));
		
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
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("TropicalCurry"), 1, 0),
				new Object[] {
					Item.bowlEmpty,
					new ItemStack(Item.dyePowder, 1, 3),
					Block.vine,
					ObjectManager.getItem("ConcapedeMeatCooked")
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("ConcapedeMeatRaw").itemID, new ItemStack(ObjectManager.getItem("ConcapedeMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public JungleMobs getInstance() { return instance; }
	
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
