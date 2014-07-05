package lycanite.lycanitesmobs;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.info.EntityListCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DungeonHooks;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<String, Block>();
	public static Map<String, Item> items = new HashMap<String, Item>();
	public static Map<String, PotionBase> potionEffects = new HashMap<String, PotionBase>();
	
	public static Map<String, EntityListCustom> entityLists = new HashMap<String, EntityListCustom>();
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
	public static Block addBlock(String name, Block block) {
		name = name.toLowerCase();
		blocks.put(name, block);
		GameRegistry.registerBlock(block, name);
        return block;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		name = name.toLowerCase();
		items.put(name, item);
		if(currentMod != null)
			GameRegistry.registerItem(item, name, currentMod.getModID());
        return item;
	}

	// ========== Potion Effect ==========
	public static PotionBase addPotionEffect(String name, OldConfig config, boolean isBad, int color, int iconX, int iconY) {
		name = name.toLowerCase();
		PotionBase potion = null;
		if(config.effectIDs.get(name.toLowerCase()) != null && config.effectIDs.get(name.toLowerCase()) > 0)
			potion = new PotionBase(config.effectIDs.get(name), "potion." + name, isBad, color);
		else
			potion = new PotionBase("potion." + name, isBad, color);
		potion.setIconIndex(iconX, iconY);
		potionEffects.put(name, potion);
		return potion;
	}
	
	// ========== Creature ==========
	public static MobInfo addMob(MobInfo mobInfo) {
		ILycaniteMod mod = mobInfo.mod;
		String modid = mod.getModID();
		String domain = mod.getDomain();
		
		String name = mobInfo.name.toLowerCase();
		SpawnInfo spawnInfo = mobInfo.spawnInfo;
		mobs.put(name, mobInfo);
		
		// Sounds:
		AssetManager.addSound(name + "_say", domain, "entity." + name + ".say");
		AssetManager.addSound(name + "_hurt", domain, "entity." + name + ".hurt");
		AssetManager.addSound(name + "_death", domain, "entity." + name + ".death");
		AssetManager.addSound(name + "_step", domain, "entity." + name + ".step");
		AssetManager.addSound(name + "_attack", domain, "entity." + name + ".attack");
		AssetManager.addSound(name + "_jump", domain, "entity." + name + ".jump");
		AssetManager.addSound(name + "_fly", domain, "entity." + name + ".fly");
		AssetManager.addSound(name + "_tame", domain, "entity." + name + ".tame");
		AssetManager.addSound(name + "_beg", domain, "entity." + name + ".beg");
		AssetManager.addSound(name + "_eat", domain, "entity." + name + ".eat");
		AssetManager.addSound(name + "_mount", domain, "entity." + name + ".mount");
		
		// ID and Enabled Check:
		LycanitesMobs.printDebug("MobSetup", "~0==================== Mob Setup: "+ mobInfo.name +" ====================0~");
		int mobID = mod.getNextMobID();
		if(!mobInfo.mobEnabled) {
			LycanitesMobs.printDebug("MobSetup", "Mob Disabled: " + name + " - " + mobInfo.entityClass + " (" + modid + ")");
			return mobInfo;
		}
		
		// Mapping and Registration:
		if(!entityLists.containsKey(domain))
			entityLists.put(domain, new EntityListCustom());
		entityLists.get(domain).addMapping(mobInfo.entityClass, mobInfo.getRegistryName(), mobID, mobInfo.eggBackColor, mobInfo.eggForeColor);
		EntityRegistry.registerModEntity(mobInfo.entityClass, name, mobID, mod.getInstance(), 128, 3, true);
		
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

        return mobInfo;
	}
	

	// ========== Projectile ==========
	public static void addProjectile(String name, Class entityClass) {
		name = name.toLowerCase();
		ILycaniteMod mod = currentMod;
		AssetManager.addSound(name, mod.getDomain(), "projectile." + name);
		
		int projectileID = mod.getNextProjectileID();
		EntityRegistry.registerModEntity(entityClass, name, projectileID, mod.getInstance(), 64, 1, true);
		
		projectiles.put(name, entityClass);
	}
	
	public static void addProjectile(String name, Class entityClass, Item item, BehaviorProjectileDispense dispenseBehaviour) {
		name = name.toLowerCase();
		addProjectile(name, entityClass);
		BlockDispenser.dispenseBehaviorRegistry.putObject(item, dispenseBehaviour);
	}
	
	
    // ==================================================
    //                        Get
    // ==================================================
	// ========== Block ==========
	public static Block getBlock(String name) {
		name = name.toLowerCase();
		if(!blocks.containsKey(name)) return null;
		return blocks.get(name);
	}
	
	// ========== Item ==========
	public static Item getItem(String name) {
		name = name.toLowerCase();
		if(!items.containsKey(name)) return null;
		return items.get(name);
	}
	
	// ========== Potion Effect ==========
	public static PotionBase getPotionEffect(String name) {
		name = name.toLowerCase();
		if(!potionEffects.containsKey(name)) return null;
		return potionEffects.get(name);
	}
	
	// ========== Mob ==========
	public static Class getMob(String mobName) {
		mobName = mobName.toLowerCase();
		if(!mobs.containsKey(mobName)) return null;
		return mobs.get(mobName).entityClass;
	}
	
	public static MobInfo getMobInfo(String mobName) {
		mobName = mobName.toLowerCase();
		if(!mobs.containsKey(mobName)) return null;
		return mobs.get(mobName);
	}

	public static int[] getMobDimensions(String mobName) {
		mobName = mobName.toLowerCase();
		if(!mobs.containsKey(mobName)) return new int[0];
		return mobs.get(mobName).spawnInfo.dimensionIDs;
	}
}
