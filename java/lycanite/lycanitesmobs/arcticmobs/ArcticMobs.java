package lycanite.lycanitesmobs.arcticmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
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
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.item.Item;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = ArcticMobs.modid, name = ArcticMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {ArcticMobs.modid}, packetHandler = PacketHandler.class)
public class ArcticMobs implements ILycaniteMod {
	
	public static final String modid = "ArcticMobs";
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
		ObjectManager.addItem("ArcticEgg", "Spawn", new ItemArcticEgg(config.itemIDs.get("ArcticEgg")));
		ObjectManager.addItem("FrostboltCharge", "Frostbolt Charge", new ItemFrostboltCharge(config.itemIDs.get("FrostboltCharge")));
		ObjectManager.addItem("FrostboltScepter", "Frostbolt Scepter", new ItemScepterFrostbolt(config.itemIDs.get("FrostboltScepter")));
		ObjectManager.addItem("FrostwebCharge", "Frostweb Charge", new ItemFrostwebCharge(config.itemIDs.get("FrostwebCharge")));
		ObjectManager.addItem("FrostwebScepter", "Frostweb Scepter", new ItemScepterFrostweb(config.itemIDs.get("FrostwebScepter")));

		// ========== Create Blocks ==========
		ObjectManager.addBlock("Frostweb", "Frostweb", new BlockFrostweb(config.blockIDs.get("Frostweb")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("ArcticEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Reiver", EntityReiver.class, 0xDDEEFF, 0x99DDEE));
		ObjectManager.addMob(new MobInfo(this, "Frostweaver", EntityFrostweaver.class, 0xAADDFF, 0x226699));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("Frostbolt", EntityFrostbolt.class, ObjectManager.getItem("FrostboltCharge"), new DispenserBehaviorFrostbolt());
		ObjectManager.addProjectile("Frostweb", EntityFrostweb.class, ObjectManager.getItem("FrostwebCharge"), new DispenserBehaviorFrostweb());
		
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
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("FrostboltScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("FrostboltCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("FrostwebScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("FrostwebCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("YetiMeatRaw").itemID, new ItemStack(ObjectManager.getItem("YetiMeatCooked"), 1), 0.5f);
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
