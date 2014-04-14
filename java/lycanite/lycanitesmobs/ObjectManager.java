package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
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
		PotionBase potion = new PotionBase(PotionBase.customPotionStartID + config.effectIDs.get(name), isBad, color);
		potion.setPotionName(name);
		potion.setIconIndex(iconX, iconY);
		potionEffects.put(name, potion);
		return potion;
	}
	
	// ========== Creature ==========
	public static void addMob(MobInfo mobInfo) {
		ILycaniteMod mod = mobInfo.mod;
		String modid = mod.getModID();
		String domain = mod.getDomain();
		
		String name = mobInfo.name;
		SpawnInfo spawnInfo = mobInfo.spawnInfo;
		mobs.put(name, mobInfo);
		
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
		
		// ID and Enabled Check:
		LycanitesMobs.printDebug("MobSetup", "~0==================== Mob Setup: "+ mobInfo.name +" ====================0~");
		int mobID = mod.getNextMobID();
		if(!mobInfo.mobEnabled) {
			LycanitesMobs.printDebug("MobSetup", "Mob Disabled: " + name + " - " + mobInfo.entityClass + " (" + modid + ")");
			return;
		}
		
		// Mapping and Registration:
		if(!entityLists.containsKey(domain))
			entityLists.put(domain, new EntityList());
		entityLists.get(domain).addMapping(mobInfo.entityClass, mobInfo.getRegistryName(), mobID, mobInfo.eggBackColor, mobInfo.eggForeColor);
		EntityRegistry.registerModEntity(mobInfo.entityClass, name, mobID, mod.getInstance(), 128, 3, true);
		LanguageRegistry.instance().addStringLocalization("entity." + mobInfo.getRegistryName() + ".name", "en_US", mobInfo.title);
		
		// Debug Message - Added:
		LycanitesMobs.printDebug("MobSetup", "Mob Added: " + name + " - " + mobInfo.entityClass + " (" + modid + ")");
		
		// Add Spawn:
		boolean spawnAdded = false;
		if(!SpawnInfo.disableAllSpawning) {
			if(spawnInfo.enabled && spawnInfo.spawnWeight > 0 && spawnInfo.spawnGroupMax > 0) {
				if(spawnInfo.creatureType != null) {
					EntityRegistry.addSpawn(mobInfo.entityClass, spawnInfo.spawnWeight, spawnInfo.spawnGroupMin, spawnInfo.spawnGroupMax, spawnInfo.creatureType, spawnInfo.biomes);
				}
				if(spawnInfo.spawnType != null) {
					spawnInfo.spawnType.addSpawn(spawnInfo);
				}
				spawnAdded = true;
			}
		}
		
		// Debug Message - Spawn Added:
		if(spawnAdded) {
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Added - Type: " + spawnInfo.spawnTypeName + " Weight: " + spawnInfo.spawnWeight + " Min: " + spawnInfo.spawnGroupMin + " Max: " + spawnInfo.spawnGroupMax);
			LycanitesMobs.printDebug("MobSetup", "Vanilla Spawn Type: " + spawnInfo.creatureType);
			LycanitesMobs.printDebug("MobSetup", "Custom Spawn Type: " + (spawnInfo.spawnType != null ? spawnInfo.spawnType.typeName : "null"));
			String biomesList = "";
			if(LycanitesMobs.config.getDebug("MobSetup")) {
				for(BiomeGenBase biome : spawnInfo.biomes) {
					if(!"".equals(biomesList))
						biomesList += ", ";
					biomesList += biome.biomeName;
				}
			}
			LycanitesMobs.printDebug("MobSetup", "Biomes: " + biomesList);
			String dimensionsList = "";
			for(int dimensionID : spawnInfo.dimensionIDs) {
				if(!"".equals(dimensionsList))
					dimensionsList += ", ";
				dimensionsList += Integer.toString(dimensionID);
			}
			LycanitesMobs.printDebug("MobSetup", "Dimensions: " + dimensionsList);
		}
		else
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Not Added: The spawning of this mob (or all mobs) must be disabled or this mobs spawn weight or max group size is 0.");
		
		// Dungeon Spawn:
		if(!SpawnInfo.disableDungeonSpawners) {
			if(spawnInfo.dungeonWeight > 0) {
				DungeonHooks.addDungeonMob(mobInfo.getRegistryName(), spawnInfo.dungeonWeight);
				LycanitesMobs.printDebug("MobSetup", "Dungeon Spawn Added - Weight: " + spawnInfo.dungeonWeight);
			}
		}
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
		if(!mobs.containsKey(mobName)) return new int[0];
		return mobs.get(mobName).spawnInfo.dimensionIDs;
	}
}
