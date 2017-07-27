package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.core.info.GroupInfo;
import lycanite.lycanitesmobs.core.info.MobInfo;
import lycanite.lycanitesmobs.core.info.ObjectLists;
import lycanite.lycanitesmobs.core.info.Subspecies;
import lycanite.lycanitesmobs.core.item.ItemCustomFood;
import lycanite.lycanitesmobs.core.item.ItemTreat;
import lycanite.lycanitesmobs.core.mobevent.MobEventBase;
import lycanite.lycanitesmobs.core.mobevent.MobEventManager;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.core.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.saltwatermobs.entity.*;
import lycanite.lycanitesmobs.saltwatermobs.item.ItemSaltwaterEgg;
import lycanite.lycanitesmobs.saltwatermobs.mobevent.MobEventSeaStorm;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
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

@Mod(modid = SaltwaterMobs.modid, name = SaltwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class SaltwaterMobs {
	
	public static final String modid = "saltwatermobs";
	public static final String name = "Lycanites Saltwater Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static SaltwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.saltwatermobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.saltwatermobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Saltwater Mobs", 8)
				.setDimensionBlacklist("-1,1").setBiomes("OCEAN, BEACH").setDungeonThemes("WATER, DUNGEON")
                .setEggName("saltwaterspawn");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("saltwaterspawn", new ItemSaltwaterEgg());

        Potion rawFoodEffectID = MobEffects.BLINDNESS;
        if(ObjectManager.getPotionEffect("weight") != null)
            rawFoodEffectID = ObjectManager.getPotionEffect("weight");
        ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
        ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));
        OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("ikameatraw"));

        ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.WATER_BREATHING, 20, 2, 1.0F).setAlwaysEdible());
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));
        OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("ikameatcooked"));

        ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.WATER_BREATHING, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));

		ObjectManager.addItem("raikotreat", new ItemTreat("raikotreat", group));
		
		// ========== Create Mobs ==========
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("saltwaterspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "lacedon", EntityLacedon.class, 0x000099, 0x2244FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "skylus", EntitySkylus.class, 0xFFCCDD, 0xBB2299)
		        .setPeaceful(false).setSummonable(true).setSummonCost(3).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ika", EntityIka.class, 0x99FFBB, 0x229944)
		        .setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "abtu", EntityAbtu.class, 0xFFBB00, 0x44AAFF)
		        .setPeaceful(false).setSummonCost(2).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(2).setAreaLimit(32).setGroupLimits(1, 5).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "raiko", EntityRaiko.class, 0xCCCCDD, 0xFF6633)
		        .setPeaceful(false).setTameable(true).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);
		
		
		// ========== Register Models ==========
		proxy.registerModels(this.group);
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
        // ========== Load All Mob Info from Configs ==========
        MobInfo.loadAllFromConfigs(this.group);
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("raiko") != null) {
			MobEventBase mobEvent = new MobEventSeaStorm("seastorm", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("seastorm")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.AIR};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("raiko"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Crafting ==========
        GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(ObjectManager.getItem("seashellmaki"), 1, 0),
                new Object[]{
                        Blocks.VINE,
                        Items.WHEAT,
                        ObjectManager.getItem("ikameatcooked"),
                }
        ));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("ikameatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("seashellmaki") }
			));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("raikotreat"), 4, 0),
				new Object[] { "TTT", "BBT", "TTT",
						Character.valueOf('T'), ObjectManager.getItem("ikameatcooked"),
						Character.valueOf('B'), Items.BONE
				}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("ikameatraw"), new ItemStack(ObjectManager.getItem("ikameatcooked"), 1), 0.5f);
	}
}
