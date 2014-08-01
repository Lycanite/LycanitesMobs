package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.info.EntityListCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.SpawnInfo;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DungeonHooks;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<String, Block>();
	public static Map<String, Fluid> fluids = new HashMap<String, Fluid>();
    public static Map<Block, Item> buckets = new HashMap<Block, Item>();
	public static Map<String, Item> items = new HashMap<String, Item>();
	public static Map<String, PotionBase> potionEffects = new HashMap<String, PotionBase>();
	
	public static Map<String, EntityListCustom> entityLists = new HashMap<String, EntityListCustom>();
	public static Map<String, MobInfo> mobs = new HashMap<String, MobInfo>();
	
	public static Map<String, Class> projectiles = new HashMap<String, Class>();
	
	public static GroupInfo currentGroup;
	
    // ==================================================
    //                        Setup
    // ==================================================
	public static void setCurrentGroup(GroupInfo group) {
		currentGroup = group;
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

	// ========== Fluid ==========
	public static Fluid addFluid(Fluid fluid) {
		String name = fluid.getUnlocalizedName().toLowerCase();
		fluids.put(name, fluid);
		FluidRegistry.registerFluid(fluid);
        return fluid;
	}

	// ========== Bucket ==========
	public static Item addBucket(Item bucket, Block block) {
		buckets.put(block, bucket);
        return bucket;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		name = name.toLowerCase();
		items.put(name, item);
		if(currentGroup != null)
			GameRegistry.registerItem(item, name, currentGroup.name);
        return item;
	}

	// ========== Potion Effect ==========
	public static PotionBase addPotionEffect(String name, ConfigBase config, boolean isBad, int color, int iconX, int iconY) {
		int effectIDOverride = config.getInt("Potion Effects", name + " Effect Override ID", 0);
		name = name.toLowerCase();
		PotionBase potion = null;
		if(effectIDOverride > 0)
			potion = new PotionBase(effectIDOverride, "potion." + name, isBad, color);
		else
			potion = new PotionBase("potion." + name, isBad, color);
		potion.setIconIndex(iconX, iconY);
		potionEffects.put(name, potion);
		return potion;
	}
	
	// ========== Creature ==========
	public static MobInfo addMob(MobInfo mobInfo) {
        mobInfo.loadFromConfig();
		GroupInfo group = mobInfo.group;
		String filename = group.filename;
		
		String name = mobInfo.name.toLowerCase();
		SpawnInfo spawnInfo = mobInfo.spawnInfo;
		mobs.put(name, mobInfo);
		
		// Sounds:
		AssetManager.addSound(name + "_say", group, "entity." + name + ".say");
		AssetManager.addSound(name + "_hurt", group, "entity." + name + ".hurt");
		AssetManager.addSound(name + "_death", group, "entity." + name + ".death");
		AssetManager.addSound(name + "_step", group, "entity." + name + ".step");
		AssetManager.addSound(name + "_attack", group, "entity." + name + ".attack");
		AssetManager.addSound(name + "_jump", group, "entity." + name + ".jump");
		AssetManager.addSound(name + "_fly", group, "entity." + name + ".fly");
		AssetManager.addSound(name + "_tame", group, "entity." + name + ".tame");
		AssetManager.addSound(name + "_beg", group, "entity." + name + ".beg");
		AssetManager.addSound(name + "_eat", group, "entity." + name + ".eat");
		AssetManager.addSound(name + "_mount", group, "entity." + name + ".mount");
		
		// ID and Enabled Check:
		LycanitesMobs.printDebug("MobSetup", "~0==================== Mob Setup: "+ mobInfo.name +" ====================0~");
		int mobID = group.getNextMobID();
		if(!mobInfo.mobEnabled) {
			LycanitesMobs.printDebug("MobSetup", "Mob Disabled: " + name + " - " + mobInfo.entityClass + " (" + group.name + ")");
			return mobInfo;
		}
		
		// Mapping and Registration:
		if(!entityLists.containsKey(filename))
			entityLists.put(filename, new EntityListCustom());
		entityLists.get(filename).addMapping(mobInfo.entityClass, mobInfo.getRegistryName(), mobID, mobInfo.eggBackColor, mobInfo.eggForeColor);
		EntityRegistry.registerModEntity(mobInfo.entityClass, name, mobID, group.mod, 128, 3, true);
		
		// Debug Message - Added:
		LycanitesMobs.printDebug("MobSetup", "Mob Added: " + name + " - " + mobInfo.entityClass + " (" + group.name + ")");
		
		// Add Spawn:
		boolean spawnAdded = false;
		if(!SpawnInfo.disableAllSpawning) {
			if(spawnInfo.enabled && spawnInfo.spawnWeight > 0 && spawnInfo.spawnGroupMax > 0) {
				for(EnumCreatureType creatureType : spawnInfo.creatureTypes) {
					EntityRegistry.addSpawn(mobInfo.entityClass, spawnInfo.spawnWeight, spawnInfo.spawnGroupMin, spawnInfo.spawnGroupMax, creatureType, spawnInfo.biomes);
				}
				for(SpawnTypeBase spawnType : spawnInfo.spawnTypes) {
					spawnType.addSpawn(spawnInfo);
				}
				spawnAdded = true;
			}
		}
		
		// Debug Message - Spawn Added:
		if(spawnAdded) {
			LycanitesMobs.printDebug("MobSetup", "Mob Spawn Added - Weight: " + spawnInfo.spawnWeight + " Min: " + spawnInfo.spawnGroupMin + " Max: " + spawnInfo.spawnGroupMax);
			for(EnumCreatureType creatureType : spawnInfo.creatureTypes)
				LycanitesMobs.printDebug("MobSetup", "Vanilla Spawn Type: " + creatureType);
			for(SpawnTypeBase spawnType : spawnInfo.spawnTypes)
				LycanitesMobs.printDebug("MobSetup", "Custom Spawn Type: " + spawnType);
			String biomesList = "";
			if(LycanitesMobs.config.getBool("Debug", "MobSetup")) {
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
			for(String dimensionType : spawnInfo.dimensionTypes) {
				if(!"".equals(dimensionsList))
					dimensionsList += ", ";
				dimensionsList += dimensionType;
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
		GroupInfo group = currentGroup;
		AssetManager.addSound(name, group, "projectile." + name);
		
		int projectileID = group.getNextProjectileID();
		EntityRegistry.registerModEntity(entityClass, name, projectileID, group.mod, 64, 1, true);
		
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
