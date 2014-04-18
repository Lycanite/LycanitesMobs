package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntityLacedon;
import lycanite.lycanitesmobs.saltwatermobs.item.ItemSaltwaterEgg;
import net.minecraft.block.BlockDispenser;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid = SaltwaterMobs.modid, name = SaltwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {SaltwaterMobs.modid}, packetHandler = PacketHandler.class)
public class SaltwaterMobs implements ILycaniteMod {
	
	public static final String modid = "SaltwaterMobs";
	public static final String name = "Lycanites Saltwater Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static SaltwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.saltwatermobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.saltwatermobs.CommonSubProxy")
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
		ObjectManager.addItem("SaltwaterEgg", "Spawn", new ItemSaltwaterEgg(config.itemIDs.get("SaltwaterEgg")));
		//ObjectManager.addItem("EmberCharge", "Ember Charge", new ItemEmberCharge(config.itemIDs.get("EmberCharge")));
		//ObjectManager.addItem("EmberScepter", "Ember Scepter", new ItemScepterEmber(config.itemIDs.get("EmberScepter")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("SaltwaterEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Lacedon", EntityLacedon.class, 0x000099, 0x2244FF, 2));
		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("Ember", EntityEmber.class, ObjectManager.getItem("EmberCharge"), new DispenserBehaviorEmber());
		
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
		/*GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("EmberScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("EmberCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));*/
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("SauropodMeatRaw").itemID, new ItemStack(ObjectManager.getItem("SauropodMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public SaltwaterMobs getInstance() { return instance; }
	
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
