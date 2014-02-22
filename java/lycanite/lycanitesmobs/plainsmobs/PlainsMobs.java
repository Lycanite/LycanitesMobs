package lycanite.lycanitesmobs.plainsmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityKobold;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMaka;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMakaAlpha;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityVentoraptor;
import lycanite.lycanitesmobs.plainsmobs.item.ItemPlainsEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.world.biome.BiomeGenBase;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;

@Mod(modid = PlainsMobs.modid, name = PlainsMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {PlainsMobs.modid}, packetHandler = PacketHandler.class)
public class PlainsMobs implements ILycaniteMod {
	
	public static final String modid = "PlainsMobs";
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
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("PlainsEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob("Kobold", EntityKobold.class, 0x996633, 0xFF7777);
		ObjectManager.addMob("Ventoraptor", EntityVentoraptor.class, 0x99BBFF, 0x0033FF);
		ObjectManager.addMob("Maka", EntityMaka.class, 0xAA8855, 0x221100);
		ObjectManager.addMob("MakaAlpha", EntityMakaAlpha.class, 0x663300, 0x000000);
		
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
		}
		
		// ========== Crafting ==========
		// No recipes yet.
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("SauropodMeatRaw").itemID, new ItemStack(ObjectManager.getItem("SauropodMeatCooked"), 1), 0.5f);
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
