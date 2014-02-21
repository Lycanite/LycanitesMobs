package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorEmber;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCinder;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import lycanite.lycanitesmobs.infernomobs.item.ItemEmberCharge;
import lycanite.lycanitesmobs.infernomobs.item.ItemInfernoEgg;
import lycanite.lycanitesmobs.infernomobs.item.ItemScepterEmber;
import net.minecraft.block.BlockDispenser;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = InfernoMobs.modid, name = InfernoMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {InfernoMobs.modid}, packetHandler = PacketHandler.class)
public class InfernoMobs implements ILycaniteMod {
	
	public static final String modid = "InfernoMobs";
	public static final String name = "Lycanites Inferno Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static InfernoMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.infernomobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.infernomobs.CommonSubProxy")
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
		ObjectManager.addItem("InfernoEgg", "Spawn", new ItemInfernoEgg(config.itemIDs.get("InfernoEgg")));
		ObjectManager.addItem("EmberCharge", "Ember Charge", new ItemEmberCharge(config.itemIDs.get("EmberCharge")));

		ObjectManager.addItem("EmberScepter", "Ember Scepter", new ItemScepterEmber(config.itemIDs.get("EmberScepter")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("InfernoEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob("Cinder", EntityCinder.class, 0xFF9900, 0xFFFF00);
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("Ember", EntityEmber.class, ObjectManager.getItem("EmberCharge"), new DispenserBehaviorEmber());
		
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
		// N/A
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("EmberScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("EmberCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("SauropodMeatRaw").itemID, new ItemStack(ObjectManager.getItem("SauropodMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public InfernoMobs getInstance() { return instance; }
	
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
