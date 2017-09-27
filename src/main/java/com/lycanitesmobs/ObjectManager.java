package com.lycanitesmobs;

import com.lycanitesmobs.core.block.BlockSlabCustom;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.info.EntityListCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.item.ItemBase;
import com.lycanitesmobs.core.item.ItemSlabCustom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<>();
    public static Map<String, Item> items = new HashMap<>();
    public static Map<Item, GroupInfo> itemGroups = new HashMap<>();
	public static Map<String, Fluid> fluids = new HashMap<>();
    public static Map<Block, Item> buckets = new HashMap<>();
    public static Map<String, Class> tileEntities = new HashMap<>();
	public static Map<String, PotionBase> potionEffects = new HashMap<>();
	
	public static Map<String, EntityListCustom> entityLists = new HashMap<>();
	public static Map<String, MobInfo> mobs = new HashMap<>();
	
	public static Map<String, Class> projectiles = new HashMap<>();

    public static Map<String, DamageSource> damageSources = new HashMap<>();

    public static Map<String, StatBase> stats = new HashMap<>();
	
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
        if(block instanceof BlockSlabCustom) {
            BlockSlabCustom blockSlab = (BlockSlabCustom)block;
            ItemSlabCustom itemSlabCustom = new ItemSlabCustom(blockSlab, blockSlab, blockSlab.getDoubleBlock());
            items.put(name, itemSlabCustom);
            itemGroups.put(itemSlabCustom, currentGroup);
        }
        else {
            ItemBlock itemBlock = new ItemBlock(block);
            itemBlock.setRegistryName(block.getRegistryName());
            items.put(name, itemBlock);
            itemGroups.put(itemBlock, currentGroup);
        }
        LycanitesMobs.proxy.addBlockRender(currentGroup, block);
        return block;
	}

	// ========== Fluid ==========
	public static Fluid addFluid(String fluidName) {
        GroupInfo group = currentGroup;
        Fluid fluid = new Fluid(fluidName, new ResourceLocation(group.filename + ":blocks/" + fluidName + "_still"), new ResourceLocation(group.filename + ":blocks/" + fluidName + "_flow"));
		String name = fluid.getUnlocalizedName().toLowerCase();
		fluids.put(name, fluid);
		FluidRegistry.registerFluid(fluid);
        return fluid;
	}

	// ========== Bucket ==========
	public static Item addBucket(Item bucket, Block block, Fluid fluid) {
		buckets.put(block, bucket);
        //FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.BUCKET)); TODO Try to find what this has changed to, if possible.
        return bucket;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		name = name.toLowerCase();
		items.put(name, item);
        itemGroups.put(item, currentGroup);

        // Fluid Dispenser:
        if(item instanceof ItemBucket) {
            IBehaviorDispenseItem ibehaviordispenseitem = new BehaviorDefaultDispenseItem() {
                private final BehaviorDefaultDispenseItem dispenseBehavior = new BehaviorDefaultDispenseItem();

                public ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
                    ItemBucket itembucket = (ItemBucket)stack.getItem();
                    BlockPos blockpos = source.getBlockPos().offset(source.getBlockState().getValue(BlockDispenser.FACING));
                    return itembucket.tryPlaceContainedLiquid(null, source.getWorld(), blockpos) ? new ItemStack(Items.BUCKET) : this.dispenseBehavior.dispense(source, stack);
                }
            };
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, ibehaviordispenseitem);
        }

        return item;
	}

	public static Item addItem(String name, Item item, int weight, int minAmount, int maxAmount) {
		Utilities.addDungeonLoot(new ItemStack(item), minAmount, maxAmount, weight);
		Utilities.addStrongholdLoot(new ItemStack(item), minAmount, maxAmount, (weight * 2));
		Utilities.addVillageLoot(new ItemStack(item), minAmount, maxAmount, (weight));
		return addItem(name, item);
	}

    // ========== Tile Entity ==========
    public static Class addTileEntity(String name, Class tileEntityClass) {
        name = name.toLowerCase();
        tileEntities.put(name, tileEntityClass);
        GameRegistry.registerTileEntity(tileEntityClass, LycanitesMobs.modid + "." + name);
        return tileEntityClass;
    }

    // ========== Potion Effect ==========
	public static PotionBase addPotionEffect(String name, ConfigBase config, boolean isBad, int color, int iconX, int iconY, boolean goodEffect) {
        PotionBase potion = new PotionBase("potion." + name, isBad, color);
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
        if(mobInfo.isSummonable() || mobInfo.isTameable())
		    AssetManager.addSound(name + "_tame", group, "entity." + name + ".tame");
        if(mobInfo.isSummonable() || mobInfo.isTameable())
            AssetManager.addSound(name + "_beg", group, "entity." + name + ".beg");
        if(mobInfo.isTameable())
            AssetManager.addSound(name + "_eat", group, "entity." + name + ".eat");
        if(EntityCreatureRideable.class.isAssignableFrom(mobInfo.entityClass) && (mobInfo.isSummonable() || mobInfo.isTameable()))
            AssetManager.addSound(name + "_mount", group, "entity." + name + ".mount");
        if(mobInfo.isBoss())
            AssetManager.addSound(name + "_phase", group, "entity." + name + ".phase");

        return mobInfo;
	}
	

	// ========== Projectile ==========
    public static void addProjectile(String name, Class entityClass, int updateFrequency) {
        name = name.toLowerCase();
        GroupInfo group = currentGroup;
        AssetManager.addSound(name, group, "projectile." + name);

        int projectileID = group.getNextProjectileID();
        EntityRegistry.registerModEntity(new ResourceLocation(group.filename, name), entityClass, name, projectileID, group.mod, 64, updateFrequency, true);

        projectiles.put(name, entityClass);
        group.projectileClasses.add(entityClass);
    }

	public static void addProjectile(String name, Class entityClass) {
		addProjectile(name, entityClass, 1);
	}
	
	public static void addProjectile(String name, Class entityClass, Item item, BehaviorProjectileDispense dispenseBehaviour) {
		name = name.toLowerCase();
		addProjectile(name, entityClass);
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(item, dispenseBehaviour);
	}


    // ========== Damage Source ==========
    public static void addDamageSource(String name, DamageSource damageSource) {
        name = name.toLowerCase();
        damageSources.put(name, damageSource);
    }


    // ========== Stat ==========
    public static void addStat(String name, StatBase stat) {
        name = name.toLowerCase();
        if(stats.containsKey(name))
            return;
        stat.registerStat();
        stats.put(name, stat);
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

    // ========== Tile Entity ==========
    public static Class getTileEntity(String name) {
        name = name.toLowerCase();
        if(!tileEntities.containsKey(name)) return null;
        return tileEntities.get(name);
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

    // ========== Stat ==========
    public static StatBase getStat(String name) {
        name = name.toLowerCase();
        if(!stats.containsKey(name)) return null;
        return stats.get(name);
    }


    // ==================================================
    //                  Registry Events
    // ==================================================
    // ========== Blocks ==========
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        event.getRegistry().registerAll(blocks.values().toArray(new Block[blocks.size()]));
    }

    // ========== Items ==========
    public static void registerItems(RegistryEvent.Register<Item> event) {
	    for(Item item : items.values()) {
	        if(item.getRegistryName() == null) {
	            LycanitesMobs.printWarning("", "Item: " + item + " has no Registry Name!");
            }
            event.getRegistry().register(item);
            LycanitesMobs.proxy.addItemRender(itemGroups.get(item), item);
        }
    }

    // ========== Items ==========
    public static void registerPotions(RegistryEvent.Register<Potion> event) {
        event.getRegistry().registerAll(potionEffects.values().toArray(new Potion[potionEffects.size()]));
    }


    // ==================================================
    //           Register Block and Item Models
    // ==================================================
    @SideOnly(Side.CLIENT)
    public static void RegisterModels() {
        for(Item item : items.values()) {
            if(item instanceof ItemBase) {
                ItemBase itemBase = (ItemBase) item;
                if (itemBase.useItemColors()) {
                    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientProxy.itemColor, item);
                }
            }
        }
    }
}
