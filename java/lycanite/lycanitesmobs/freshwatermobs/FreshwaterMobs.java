package lycanite.lycanitesmobs.freshwatermobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.Subspecies;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.freshwatermobs.dispenser.DispenserBehaviorAquaPulse;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityAquaPulse;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityJengu;
import lycanite.lycanitesmobs.freshwatermobs.entity.EntityZephyr;
import lycanite.lycanitesmobs.freshwatermobs.item.ItemAquaPulseCharge;
import lycanite.lycanitesmobs.freshwatermobs.item.ItemFreshwaterEgg;
import lycanite.lycanitesmobs.freshwatermobs.item.ItemScepterAquaPulse;
import lycanite.lycanitesmobs.freshwatermobs.mobevent.MobEventTsunami;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = FreshwaterMobs.modid, name = FreshwaterMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class FreshwaterMobs {
	
	public static final String modid = "freshwatermobs";
	public static final String name = "Lycanites Freshwater Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static FreshwaterMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.freshwatermobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.freshwatermobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Freshwater Mobs")
				.setDimensionBlacklist("-1,1").setBiomes("ALL, -OCEAN, -BEACH").setDungeonThemes("WATER")
                .setEggName("freshwateregg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("freshwateregg", new ItemFreshwaterEgg());

        int rawFoodEffectID = Potion.weakness.id;
        if(ObjectManager.getPotionEffect("penetration") != null)
            rawFoodEffectID = ObjectManager.getPotionEffect("penetration").getId();

        ObjectManager.addItem("aquapulsecharge", new ItemAquaPulseCharge());
        ObjectManager.addItem("aquapulsescepter", new ItemScepterAquaPulse(), 200, 1, 1);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("freshwateregg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "jengu", EntityJengu.class, 0x000099, 0x4444FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "zephyr", EntityZephyr.class, 0xFFFFDD, 0xAABBFF)
                .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("STORM")
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3);
        ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());
		
		// ========== Register Models ==========
		proxy.registerModels();
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
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("jengu") != null || MobInfo.getFromName("zephyr") != null) {
			MobEventBase mobEvent = new MobEventTsunami("tsunami", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("tsunami")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.air};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        if(MobInfo.getFromName("jengu") != null)
	        	eventSpawner.addSpawn(MobInfo.getFromName("jengu").spawnInfo);
	        if(MobInfo.getFromName("zephyr") != null)
	        	eventSpawner.addSpawn(MobInfo.getFromName("zephyr").spawnInfo);
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Crafting ==========
        /*GameRegistry.addRecipe(new ShapelessOreRecipe(
                new ItemStack(ObjectManager.getItem("seashellmaki"), 1, 0),
                new Object[]{
                        Blocks.vine,
                        Items.wheat,
                        ObjectManager.getItem("ikameatcooked"),
                }
        ));*/

		GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("aquapulsescepter"), 1, 0),
                new Object[] { "CCC", "CRC", "CRC",
                Character.valueOf('C'), ObjectManager.getItem("aquapulsecharge"),
                Character.valueOf('R'), Items.blaze_rod
        }));
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("ikameatraw"), new ItemStack(ObjectManager.getItem("ikameatcooked"), 1), 0.5f);
	}
}
