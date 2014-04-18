package lycanite.lycanitesmobs.forestmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.forestmobs.entity.EntityEnt;
import lycanite.lycanitesmobs.forestmobs.entity.EntityTrent;
import lycanite.lycanitesmobs.forestmobs.item.ItemForestEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
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

@Mod(modid = ForestMobs.modid, name = ForestMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {ForestMobs.modid}, packetHandler = PacketHandler.class)
public class ForestMobs implements ILycaniteMod {
	
	public static final String modid = "ForestMobs";
	public static final String name = "Lycanites Forest Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static ForestMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.forestmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.forestmobs.CommonSubProxy")
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
		ObjectManager.addItem("ForestEgg", "Spawn", new ItemForestEgg(config.itemIDs.get("ForestEgg")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("ForestEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Ent", EntityEnt.class, 0x997700, 0x00FF22, 2));
		ObjectManager.addMob(new MobInfo(this, "Trent", EntityTrent.class, 0x663300, 0x00AA11, 6));
		
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
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
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
	public ForestMobs getInstance() { return instance; }
	
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
