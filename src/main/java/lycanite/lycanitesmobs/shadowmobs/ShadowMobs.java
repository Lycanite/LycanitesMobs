package lycanite.lycanitesmobs.shadowmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.RecipeMaker;
import lycanite.lycanitesmobs.core.config.ConfigBase;
import lycanite.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.core.info.*;
import lycanite.lycanitesmobs.core.item.ItemCustomFood;
import lycanite.lycanitesmobs.core.item.ItemTreat;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import lycanite.lycanitesmobs.core.mobevent.MobEventManager;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.shadowmobs.block.BlockShadowfire;
import lycanite.lycanitesmobs.shadowmobs.dispenser.DispenserBehaviorBloodleech;
import lycanite.lycanitesmobs.shadowmobs.dispenser.DispenserBehaviorSpectralbolt;
import lycanite.lycanitesmobs.shadowmobs.entity.*;
import lycanite.lycanitesmobs.shadowmobs.info.AltarInfoLunarGrue;
import lycanite.lycanitesmobs.shadowmobs.item.*;
import lycanite.lycanitesmobs.shadowmobs.mobevent.MobEventBlackPlague;
import lycanite.lycanitesmobs.shadowmobs.mobevent.MobEventShadowGames;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

@Mod(modid = ShadowMobs.modid, name = ShadowMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class ShadowMobs {
	
	public static final String modid = "shadowmobs";
	public static final String name = "Lycanites Shadow Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static ShadowMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.shadowmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.shadowmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Shadow Mobs", 10)
				.setDimensionBlacklist("1,-100").setDimensionWhitelist(true).setBiomes("END").setDungeonThemes("SHADOW, NECRO")
                .setEggName("shadowspawn");
		group.loadFromConfig();

		
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("shadowspawn", new ItemShadowEgg());
        ObjectManager.addItem("soulstoneshadow", new ItemSoulstoneShadow(group));
		
		ObjectManager.addItem("spectralboltcharge", new ItemSpectralboltCharge());
		ObjectManager.addItem("spectralboltscepter", new ItemScepterSpectralbolt(), 2, 1, 1);
		
		ObjectManager.addItem("bloodleechcharge", new ItemBloodleechCharge());
		ObjectManager.addItem("bloodleechscepter", new ItemScepterBloodleech(), 2, 1, 1);

        ItemCustomFood rawMeat = new ItemCustomFood("chupacabrameatraw", group, 4, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.HUNGER, 45, 2, 0.8F);
        if(ObjectManager.getPotionEffect("fear") != null)
            rawMeat.setPotionEffect(ObjectManager.getPotionEffect("fear"), 10, 2, 0.8F);
        ObjectManager.addItem("chupacabrameatraw", rawMeat);
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("chupacabrameatraw"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("chupacabrameatraw"));

        ItemCustomFood cookedMeat = new ItemCustomFood("chupacabrameatcooked", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setAlwaysEdible();
        if(ObjectManager.getPotionEffect("leech") != null)
            cookedMeat.setPotionEffect(ObjectManager.getPotionEffect("leech"), 10, 1, 1.0F);
        ObjectManager.addItem("chupacabrameatcooked", cookedMeat);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("chupacabrameatcooked"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("chupacabrameatcooked"));

        ItemCustomFood meal = new ItemCustomFood("bloodchili", group, 7, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setAlwaysEdible();
        meal.setMaxStackSize(16);
        if(ObjectManager.getPotionEffect("leech") != null)
            meal.setPotionEffect(ObjectManager.getPotionEffect("leech"), 60, 1, 1.0F);
        ObjectManager.addItem("bloodchili", meal, 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bloodchili"));

        ObjectManager.addItem("geistliver", new ItemGeistLiver());

        ObjectManager.addItem("chupacabratreat", new ItemTreat("chupacabratreat", group));
        ObjectManager.addItem("shadetreat", new ItemTreat("shadetreat", group));
		
		
		// ========== Create Blocks ==========
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());

        RecipeMaker.addStoneBlocks(group, "shadow", Blocks.OBSIDIAN);
		
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("shadowspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "grue", EntityGrue.class, 0x191017, 0xBB44AA)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("crimson", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"))
                .addSubspecies(new Subspecies("lunar", "rare"));
		newMob.spawnInfo.setSpawnTypes("UNDERGROUND, DARKNESS")
                .setBiomes("ALL").setDimensions("-1").setDimensionWhitelist(false)
				.setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);
        
        newMob = new MobInfo(group, "phantom", EntityPhantom.class, 0x101519, 0xDD2233)
		        .setPeaceful(false).setSummonable(false).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("DEATH, SLEEP, MONSTER")
                .setBiomes("SPOOKY").setDimensions("-1000").setDimensionWhitelist(false)
				.setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);
		AssetManager.addSound("phantom_say_jon", group, "entity.phantom.say.jon");
        
        newMob = new MobInfo(group, "epion", EntityEpion.class, 0x553300, 0xFF22DD)
		        .setPeaceful(false).setSummonCost(3).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, LUNAR")
                .setBiomes("GROUP, SPOOKY").setDimensions("-1, 1").setDimensionWhitelist(false)
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "geist", EntityGeist.class, 0x705449, 0x310e08)
                .setPeaceful(false).setSummonCost(3).setDungeonLevel(2)
                .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("UNDEATH, MONSTER")
                .setBiomes("GROUP, SPOOKY")
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "chupacabra", EntityChupacabra.class, 0x36251b, 0xaa8c63)
                .setPeaceful(true).setTameable(true).setSummonCost(3).setDungeonLevel(2)
                .addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("UNDERGROUND").setDespawn(false)
                .setBiomes("ALL").setDimensions("-1").setDimensionWhitelist(false)
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "shade", EntityShade.class, 0x000402, 0x102336)
                .setPeaceful(true).setTameable(true).setSummonCost(4).setDungeonLevel(3)
                .addSubspecies(new Subspecies("keppel", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("UNDERGROUND, DARKNESS").setDespawn(false)
                .setBiomes("ALL").setDimensions("-1").setDimensionWhitelist(false)
                .setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "darkling", EntityDarkling.class, 0x10191a, 0x9dfbcd)
                .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
                .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("UNDERGROUND, DARKNESS")
                .setBiomes("ALL").setDimensions("-1").setDimensionWhitelist(false)
                .setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
        ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("spectralbolt", EntitySpectralbolt.class, ObjectManager.getItem("spectralboltcharge"), new DispenserBehaviorSpectralbolt());
		ObjectManager.addProjectile("bloodleech", EntityBloodleech.class, ObjectManager.getItem("bloodleechcharge"), new DispenserBehaviorBloodleech());
		
		
		// ========== Register Models ==========
		proxy.registerModels(this.group);
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		
		// ========== Mob Events ==========
		MobEventBase mobEvent = new MobEventShadowGames("shadowgames", this.group).setDimensions("-1");
		mobEvent.minDay = 10;
		SpawnTypeBase eventSpawner = new SpawnTypeSky("shadowgames_sky")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.AIR};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
    	eventSpawner.addSpawn(MobInfo.getFromName("grue"));
        eventSpawner.addSpawn(MobInfo.getFromName("darkling"));
        mobEvent.addSpawner(eventSpawner);
        eventSpawner = new SpawnTypeLand("shadowgames_land")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.AIR};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("grue"));
        eventSpawner.addSpawn(MobInfo.getFromName("shade"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);

        mobEvent = new MobEventBlackPlague("blackplague", this.group).setDimensions("-1");
        mobEvent.minDay = 10;
        eventSpawner = new SpawnTypeLand("blackplague")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(8);
        eventSpawner.materials = new Material[] {Material.AIR};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("geist"));
        eventSpawner.addSpawn(MobInfo.getFromName("phantom"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);


        // ========== Altars ==========
        AltarInfo lunarGrueAltar = new AltarInfoLunarGrue("LunarGrueAltar");
        AltarInfo.addAltar(lunarGrueAltar);


        // ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("bloodchili"), 1, 0),
				new Object[] {
					Items.BOWL,
                    new ItemStack(Items.DYE, 1, 3),
					ObjectManager.getItem("chupacabrameatcooked")
				}
			));

        if(ItemInfo.enableWeaponRecipes) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("spectralboltscepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("spectralboltcharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("bloodleechscepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("bloodleechcharge"),
                            Character.valueOf('R'), Items.BLAZE_ROD
                    }));
        }

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("chupacabratreat"), 4, 0),
                new Object[] { "TTT", "BBT", "TTT",
                        Character.valueOf('T'), ObjectManager.getItem("geistliver"),
                        Character.valueOf('B'), Items.BONE
                }));

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("shadetreat"), 4, 0),
                new Object[] { "TTT", "BBT", "TTT",
                        Character.valueOf('T'), Items.ENDER_PEARL,
                        Character.valueOf('B'), Items.BONE
                }));
		
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("chupacabrameatraw"), new ItemStack(ObjectManager.getItem("chupacabrameatcooked"), 1), 0.5f);
	}
}
