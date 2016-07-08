package lycanite.lycanitesmobs;

import lycanite.lycanitesmobs.core.block.BlockSlabCustom;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.entity.EntityCreatureRideable;
import lycanite.lycanitesmobs.core.info.EntityListCustom;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.info.MobInfo;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import lycanite.lycanitesmobs.core.item.ItemBase;
import lycanite.lycanitesmobs.core.item.ItemSlabCustom;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.client.Minecraft;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;
import java.util.Map;

public class ObjectManager {
	
	// Maps:
	public static Map<String, Block> blocks = new HashMap<String, Block>();
    public static Map<String, Item> items = new HashMap<String, Item>();
	public static Map<String, Fluid> fluids = new HashMap<String, Fluid>();
    public static Map<Block, Item> buckets = new HashMap<Block, Item>();
    public static Map<String, Class> tileEntities = new HashMap<String, Class>();
	public static Map<String, PotionBase> potionEffects = new HashMap<String, PotionBase>();
    public static int nextPotionID = 28;
	
	public static Map<String, EntityListCustom> entityLists = new HashMap<String, EntityListCustom>();
	public static Map<String, MobInfo> mobs = new HashMap<String, MobInfo>();
	
	public static Map<String, Class> projectiles = new HashMap<String, Class>();

    public static Map<String, DamageSource> damageSources = new HashMap<String, DamageSource>();

    public static Map<String, Achievement> achievements = new HashMap<String, Achievement>();
	
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
        GameRegistry.register(block);
        if(block instanceof BlockSlabCustom) {
            BlockSlabCustom blockSlab = (BlockSlabCustom)block;
            GameRegistry.register(new ItemSlabCustom(blockSlab, blockSlab, blockSlab.getDoubleBlock()), block.getRegistryName());
        }
        else
            GameRegistry.register(new ItemBlock(block), block.getRegistryName());
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
        FluidContainerRegistry.registerFluidContainer(fluid, new ItemStack(bucket), new ItemStack(Items.BUCKET));
        return bucket;
	}

	// ========== Item ==========
	public static Item addItem(String name, Item item) {
		name = name.toLowerCase();
		items.put(name, item);
		if(currentGroup != null)
			GameRegistry.register(item, new ResourceLocation(currentGroup.filename, name));
        LycanitesMobs.proxy.addItemRender(currentGroup, item);
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
        GameRegistry.register(potion, new ResourceLocation(LycanitesMobs.modid, name));
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
        EntityRegistry.registerModEntity(entityClass, name, projectileID, group.mod, 64, updateFrequency, true);

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


    // ========== Achievement ==========
    public static void addAchievement(String name, Achievement achievement) {
        name = name.toLowerCase();
        if(achievements.containsKey(name))
            return;
        achievement.registerStat();
        achievements.put(name, achievement);
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

    // ========== Achievement ==========
    public static Achievement getAchievement(String name) {
        name = name.toLowerCase();
        if(!achievements.containsKey(name)) return null;
        return achievements.get(name);
    }


    // ==================================================
    //           Register Block and Item Models
    // ==================================================
    @SideOnly(Side.CLIENT)
    public static void RegisterModels() {
        for(Item item : items.values()) {
            if(item instanceof ItemBase) {
                ItemBase itemBase = (ItemBase) item;
                if (itemBase.useItemColors())
                    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientProxy.itemColor, item);
            }
        }
        /* Moved to LycanitesMobs client proxy and called as block/item is registered.
        for(final Block block : blocks.values()) {

            // Fluids:
            if(block instanceof BlockFluidBase) {
                BlockFluidBase blockFluid = (BlockFluidBase)block;
                Item item = Item.getItemFromBlock(block);
                ModelBakery.registerItemVariants(item);
                final ModelResourceLocation fluidLocation = new ModelResourceLocation(blockFluid.group.filename + ":fluid", blockFluid.getFluid().getName());
                ModelLoader.setCustomMeshDefinition(item, new ItemMeshDefinition() {
                    @Override
                    public ModelResourceLocation getModelLocation(ItemStack itemStack) {
                        return fluidLocation;
                    }
                });
                ModelLoader.setCustomStateMapper(block, new StateMapperBase() {
                    @Override
                    protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
                        return fluidLocation;
                    }
                });
                continue;
            }

            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
        }

        for(Item item : items.values()) {
            if(item instanceof ItemBase) {
                ItemBase itemBase = (ItemBase)item;
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, itemBase.getModelResourceLocation());
                if(itemBase.useItemColors())
                    Minecraft.getMinecraft().getItemColors().registerItemColorHandler(ClientProxy.itemColor, item);
            }
            else
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
        }*/
    }
}
