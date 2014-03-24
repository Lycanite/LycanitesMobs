package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.MobInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DungeonHooks;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<String, Block>();
	public static Map<String, Item> items = new HashMap<String, Item>();
	public static Map<String, PotionBase> potionEffects = new HashMap<String, PotionBase>();
	
	public static Map<String, EntityList> entityLists = new HashMap<String, EntityList>();
	public static Map<String, MobInfo> mobs = new HashMap<String, MobInfo>();
	public static Map<String, int[]> mobDimensions = new HashMap<String, int[]>();
	public static Map<String, Integer> fireSpawns = new HashMap<String, Integer>();
	
	public static Map<String, Class> projectiles = new HashMap<String, Class>();
	
	public static ILycaniteMod currentMod;
	
    // ==================================================
    //                        Setup
    // ==================================================
	public static void setCurrentMod(ILycaniteMod mod) {
		currentMod = mod;
	}
	
	
    // ==================================================
    //                        Add
    // ==================================================
	// ========== Block ==========
	public static void addBlock(String name, String title, Block block) {
		blocks.put(name, block);
		LanguageRegistry.addName(block, title);
		GameRegistry.registerBlock(block, name);
	}

	// ========== Item ==========
	public static void addItem(String name, String title, Item item) {
		items.put(name, item);
		LanguageRegistry.addName(item, title);
		if(currentMod != null)
			GameRegistry.registerItem(item, name, currentMod.getModID());
	}

	// ========== Potion Effect ==========
	public static PotionBase addPotionEffect(String name, Config config, boolean isBad, int color, int iconX, int iconY) {
		PotionBase potion = new PotionBase(config.effectIDs.get(name), isBad, color);
		potion.setPotionName(name);
		potion.setIconIndex(iconX, iconY);
		potionEffects.put(name, potion);
		return potion;
	}
	
	// ========== Creature ==========
	//TODO Test!
	public static void addMob(MobInfo mobInfo) {
		ILycaniteMod mod = mobInfo.mod;
		String modid = mod.getModID();
		String domain = mod.getDomain();
		Config config = mod.getConfig();
		
		String name = mobInfo.name;
		Class entityClass = mobInfo.entityClass;
		
		// Sounds:
		String filename = name.toLowerCase();
		AssetManager.addSound(name + "Say", domain, "entity/" + filename + "/say.wav");
		AssetManager.addSound(name + "Hurt", domain, "entity/" + filename + "/hurt.wav");
		AssetManager.addSound(name + "Death", domain, "entity/" + filename + "/death.wav");
		AssetManager.addSound(name + "Step", domain, "entity/" + filename + "/step.wav");
		AssetManager.addSound(name + "Attack", domain, "entity/" + filename + "/attack.wav");
		AssetManager.addSound(name + "Jump", domain, "entity/" + filename + "/jump.wav");
		AssetManager.addSound(name + "Tame", domain, "entity/" + filename + "/tame.wav");
		AssetManager.addSound(name + "Beg", domain, "entity/" + filename + "/beg.wav");
		AssetManager.addSound(name + "Eat", domain, "entity/" + filename + "/eat.wav");
		AssetManager.addSound(name + "Mount", domain, "entity/" + filename + "/mount.wav");
		
		// ID and Mapping:
		int mobID = mod.getNextMobID();
		if(!config.mobsEnabled.get(name))
			return;
		if(!entityLists.containsKey(domain))
			entityLists.put(domain, new EntityList());
		entityLists.get(domain).addMapping(entityClass, mobInfo.getRegistryName(), mobID, mobInfo.eggBackColor, mobInfo.eggForeColor);
		EntityRegistry.registerModEntity(entityClass, name, mobID, mod.getInstance(), 128, 3, true);
		LanguageRegistry.instance().addStringLocalization("entity." + mobInfo.getRegistryName() + ".name", "en_US", mobInfo.title);
		
		// Debug Message - Added:
		LycanitesMobs.printDebug("MobSetup", "Mob Added: " + name + " - " + entityClass + " (" + modid + ")");
		
		// Add Spawn:
		BiomeGenBase[] spawnBiomes = new BiomeGenBase[0];
		if(!LycanitesMobs.config.getFeatureBool("DisableAllSpawning")) {
			mobDimensions.put(name, config.getSpawnDimensions(name));
			if(config.spawnEnabled.get(name) && config.spawnWeights.get(name) > 0 && config.spawnMaxs.get(name) > 0) {
				spawnBiomes = config.getSpawnBiomesTypes(name);
				if(config.spawnTypes.get(name) != null)
					EntityRegistry.addSpawn(entityClass, config.spawnWeights.get(name), config.spawnMins.get(name), config.spawnMaxs.get(name), config.spawnTypes.get(name), spawnBiomes);
				else if(config.customSpawns.containsKey(name))
					fireSpawns.put(name, config.spawnWeights.get(name));
			}
		}
		
		// Dungeon Spawn:
		if(!LycanitesMobs.config.getFeatureBool("DisableDungeonSpawners")) {
			int dungeonWeight = config.spawnWeights.get(name) * 25;
			if(dungeonWeight > 0 && (config.spawnTypes.get(name) == EnumCreatureType.monster || config.spawnTypes.get(name) == null))
				DungeonHooks.addDungeonMob(mobInfo.getRegistryName(), dungeonWeight);
		}
		
		// Debug Message - Spawn Added:
		if(!LycanitesMobs.config.getFeatureBool("DisableAllSpawning")) {
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Added - Weight: " + config.spawnWeights.get(name) + " Min: " + config.spawnMins.get(name) + " Max: " + config.spawnMaxs.get(name));
			String biomesList = "";
			if(LycanitesMobs.config.getDebug("MobSetup")) {
				for(BiomeGenBase biome : spawnBiomes) {
					if(!"".equals(biomesList))
						biomesList += ", ";
					biomesList += biome.biomeName;
				}
			}
			LycanitesMobs.printDebug("MobSetup", "Biomes: " + biomesList);
			String dimensionsList = "";
			for(int dimensionID : config.getSpawnDimensions(name)) {
				if(!"".equals(dimensionsList))
					dimensionsList += ", ";
				dimensionsList += Integer.toString(dimensionID);
			}
			LycanitesMobs.printDebug("MobSetup", "Dimensions: " + dimensionsList);
		}
		
		mobs.put(name, mobInfo);
	}
	

	// ========== Projectile ==========
	public static void addProjectile(String name, Class entityClass) {
		ILycaniteMod mod = currentMod;
		String filename = name.toLowerCase();
		AssetManager.addSound(name, mod.getDomain(), "projectile/" + filename + ".wav");

		int projectileID = mod.getNextProjectileID();
		EntityRegistry.registerModEntity(entityClass, name, projectileID, mod.getInstance(), 64, 1, true);
		LanguageRegistry.instance().addStringLocalization("entity." + name + ".name", "en_US", name);
		
		projectiles.put(name, entityClass);
	}
	
	public static void addProjectile(String name, Class entityClass, Item item, BehaviorProjectileDispense dispenseBehaviour) {
		addProjectile(name, entityClass);
		BlockDispenser.dispenseBehaviorRegistry.putObject(item, dispenseBehaviour);
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Block ==========
	public static Block getBlock(String name) {
		if(!blocks.containsKey(name)) return null;
		return blocks.get(name);
	}
	
	// ========== Item ==========
	public static Item getItem(String name) {
		if(!items.containsKey(name)) return null;
		return items.get(name);
	}
	
	// ========== Potion Effect ==========
	public static PotionBase getPotionEffect(String name) {
		if(!potionEffects.containsKey(name)) return null;
		return potionEffects.get(name);
	}
	
	// ========== Mob ==========
	public static Class getMob(String mobName) {
		if(!mobs.containsKey(mobName)) return null;
		return mobs.get(mobName).entityClass;
	}
	
	public static MobInfo getMobInfo(String mobName) {
		if(!mobs.containsKey(mobName)) return null;
		return mobs.get(mobName);
	}

	public static int[] getMobDimensions(String mobName) {
		if(!mobDimensions.containsKey(mobName)) return new int[0];
		return mobDimensions.get(mobName);
	}
}
