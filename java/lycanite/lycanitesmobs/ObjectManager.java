package lycanite.lycanitesmobs;
import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.info.EntityListCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
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

    public static Map<String, DamageSource> damageSources = new HashMap<String, DamageSource>();
	
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
	public static Item addBucket(Item bucket, Block block, Fluid fluid) {
		buckets.put(block, bucket);
        FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.bucket));
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

	public static Item addItem(String name, Item item, int weight, int minAmount, int maxAmount) {
		Utilities.addDungeonLoot(new ItemStack(item), minAmount, maxAmount, weight);
		Utilities.addStrongholdLoot(new ItemStack(item), minAmount, maxAmount, (weight * 2));
		Utilities.addVillageLoot(new ItemStack(item), minAmount, maxAmount, (weight));
		return addItem(name, item);
	}

	// ========== Potion Effect ==========
	public static PotionBase addPotionEffect(String name, ConfigBase config, boolean isBad, int color, int iconX, int iconY, boolean goodEffect) {
		int effectIDOverride = config.getInt("Potion Effects", name + " Effect Override ID", 0);
		name = name.toLowerCase();
		PotionBase potion;
		if(effectIDOverride > 0)
			potion = new PotionBase(effectIDOverride, "potion." + name, isBad, color);
		else
			potion = new PotionBase("potion." + name, isBad, color);
		potion.setIconIndex(iconX, iconY);
		potionEffects.put(name, potion);
		ObjectLists.addEffect(goodEffect ? "buffs" : "debuffs", potion);
		return potion;
	}
	
	// ========== Creature ==========
	public static MobInfo addMob(MobInfo mobInfo) {
		GroupInfo group = mobInfo.group;
		String name = mobInfo.name.toLowerCase();
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


    // ========== Damage Source ==========
    public static void addDamageSource(String name, DamageSource damageSource) {
        name = name.toLowerCase();
        damageSources.put(name, damageSource);
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

    // ========== Damage Source ==========
    public static DamageSource getDamageSource(String name) {
        name = name.toLowerCase();
        if(!damageSources.containsKey(name)) return null;
        return damageSources.get(name);
    }
}
