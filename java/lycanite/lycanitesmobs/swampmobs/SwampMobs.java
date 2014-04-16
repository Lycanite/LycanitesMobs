package lycanite.lycanitesmobs.swampmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
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
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.item.Item;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = SwampMobs.modid, name = SwampMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {SwampMobs.modid}, packetHandler = PacketHandler.class)
public class SwampMobs implements ILycaniteMod {
	
	public static final String modid = "SwampMobs";
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
		ObjectManager.addItem("SwampEgg", "Spawn", new ItemSwampEgg(config.itemIDs.get("SwampEgg")));
		
		ObjectManager.addItem("AspidMeatRaw", "Raw Aspid Meat", new ItemCustomFood(config.itemIDs.get("AspidMeatRaw"), "AspidMeatRaw", domain, 2, 0.5F).setPotionEffect(Potion.poison.id, 45, 2, 0.8F));
		ObjectLists.addItem("RawMeat", ObjectManager.getItem("AspidMeatRaw"));
		ObjectManager.addItem("AspidMeatCooked", "Cooked Aspid Meat", new ItemCustomFood(config.itemIDs.get("AspidMeatCooked"), "AspidMeatCooked", domain, 6, 0.7F));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("AspidMeatCooked"));
		ObjectManager.addItem("MossPie", "Moss Pie", new ItemCustomFood(config.itemIDs.get("MossPie"), "MossPie", domain, 6, 0.7F).setPotionEffect(Potion.regeneration.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("MossPie"));
		
		ObjectManager.addItem("PoisonGland", "Poison Gland", new ItemPoisonGland(config.itemIDs.get("PoisonGland")));
		ObjectManager.addItem("PoisonRayScepter", "Poison Ray Scepter", new ItemScepterPoisonRay(config.itemIDs.get("PoisonRayScepter")));
		ObjectManager.addItem("VenomShotScepter", "Venom Shot Scepter", new ItemScepterVenomShot(config.itemIDs.get("VenomShotScepter")));
		
		// ========== Create Blocks ==========
		AssetManager.addSound("PoisonCloud", domain, "block/poisoncloud.wav");
		ObjectManager.addBlock("PoisonCloud", "Poison Cloud", new BlockPoisonCloud(config.blockIDs.get("PoisonCloud")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("SwampEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "GhoulZombie", "Ghoul Zombie", EntityGhoulZombie.class, 0x009966, 0xAAFFDD));
		ObjectManager.addMob(new MobInfo(this, "Dweller", EntityDweller.class, 0x009922, 0x994499));
		ObjectManager.addMob(new MobInfo(this, "Ettin", EntityEttin.class, 0x669900, 0xFF6600));
		ObjectManager.addMob(new MobInfo(this, "Lurker", EntityLurker.class, 0x009900, 0x99FF00));
		ObjectManager.addMob(new MobInfo(this, "Eyewig", EntityEyewig.class, 0x000000, 0x009900));
		ObjectManager.addMob(new MobInfo(this, "Aspid", EntityAspid.class, 0x009944, 0x446600));
		ObjectManager.addMob(new MobInfo(this, "Remobra", EntityRemobra.class, 0x440066, 0xDD00FF));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("PoisonRay", EntityPoisonRay.class, Item.fermentedSpiderEye, new DispenserBehaviorPoisonRay());
		ObjectManager.addProjectile("PoisonRayEnd", EntityPoisonRayEnd.class);
		ObjectManager.addProjectile("VenomShot", EntityVenomShot.class, ObjectManager.getItem("PoisonGland"), new DispenserBehaviorVenomShot());
		
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
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("PoisonRayScepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Item.fermentedSpiderEye,
				Character.valueOf('P'), ObjectManager.getItem("PoisonGland"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("VenomShotScepter"), 1, 0),
				new Object[] { "CPC", "CRC", "CRC",
				Character.valueOf('C'), Item.rottenFlesh,
				Character.valueOf('P'), ObjectManager.getItem("PoisonGland"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("MossPie"), 1, 0),
				new Object[] {
					Block.vine,
					Item.fermentedSpiderEye,
					ObjectManager.getItem("AspidMeatCooked")
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("AspidMeatRaw").itemID, new ItemStack(ObjectManager.getItem("AspidMeatCooked"), 1), 0.5f);
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
