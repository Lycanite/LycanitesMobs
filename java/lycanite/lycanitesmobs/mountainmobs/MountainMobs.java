package lycanite.lycanitesmobs.mountainmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityJabberwock;
import lycanite.lycanitesmobs.mountainmobs.item.ItemMountainEgg;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
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
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = MountainMobs.modid, name = MountainMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {MountainMobs.modid}, packetHandler = PacketHandler.class)
public class MountainMobs implements ILycaniteMod {
	
	public static final String modid = "MountainMobs";
	public static final String name = "Lycanites Mountain Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
	// Instance:
	@Instance(modid)
	public static MountainMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.mountainmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.mountainmobs.CommonSubProxy")
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
		ObjectManager.addItem("MountainEgg", "Spawn", new ItemMountainEgg(config.itemIDs.get("MountainEgg")));
		
		//ObjectManager.addItem("YaleMeatRaw", "Raw Yale Meat", new ItemCustomFood(config.itemIDs.get("YaleMeatRaw"), "YaleMeatRaw", domain, 2, 0.5F).setPotionEffect(Potion.digSlowdown.id, 45, 2, 0.8F));
		//ObjectLists.addItem("RawMeat", ObjectManager.getItem("YaleMeatRaw"));
		//ObjectManager.addItem("YaleMeatCooked", "Cooked Yale Meat", new ItemCustomFood(config.itemIDs.get("YaleMeatCooked"), "YaleMeatCooked", domain, 6, 0.7F));
		//ObjectLists.addItem("CookedMeat", ObjectManager.getItem("YaleMeatCooked"));
		//ObjectManager.addItem("PeaksKebab", "Peaks Kebab", new ItemCustomFood(config.itemIDs.get("PeaksKebab"), "PeaksKebab", domain, 6, 0.7F).setPotionEffect(Potion.digSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		//ObjectLists.addItem("CookedMeat", ObjectManager.getItem("PeaksKebab"));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("MountainEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob("Jabberwock", EntityJabberwock.class, 0x662222, 0xFFFFAA);
		
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
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityChicken.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		/*GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("PeaksKebab"), 1, 0),
				new Object[] {
					Item.stick,
					Item.carrot,
					Item.melon,
					ObjectManager.getItem("YaleMeatCooked")
				}
			));*/
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("YaleMeatRaw").itemID, new ItemStack(ObjectManager.getItem("YaleMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public MountainMobs getInstance() { return instance; }
	
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
