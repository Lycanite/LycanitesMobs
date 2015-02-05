package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.*;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.infernomobs.block.BlockFluidPureLava;
import lycanite.lycanitesmobs.infernomobs.block.BlockScorchfire;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorEmber;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorMagma;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorScorchfire;
import lycanite.lycanitesmobs.infernomobs.entity.EntityAfrit;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCephignis;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCinder;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import lycanite.lycanitesmobs.infernomobs.entity.EntityKhalk;
import lycanite.lycanitesmobs.infernomobs.entity.EntityLobber;
import lycanite.lycanitesmobs.infernomobs.entity.EntityMagma;
import lycanite.lycanitesmobs.infernomobs.entity.EntityScorchfireball;
import lycanite.lycanitesmobs.infernomobs.item.*;
import lycanite.lycanitesmobs.infernomobs.mobevent.MobEventCinderfall;
import lycanite.lycanitesmobs.infernomobs.mobevent.MobEventEruption;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = InfernoMobs.modid, name = InfernoMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class InfernoMobs {
	
	public static final String modid = "infernomobs";
	public static final String name = "Lycanites Inferno Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static InfernoMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.infernomobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.infernomobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Inferno Mobs")
				.setDimensionBlacklist("").setBiomes("ALL").setDungeonThemes("FIERY, NETHER, NECRO")
                .setEggName("infernoegg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Blocks/Fluids ==========
		Fluid fluid = ObjectManager.addFluid(new Fluid("purelava"));
		fluid.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluid));
		
		// ========== Create Items ==========
		ObjectManager.addItem("infernoegg", new ItemInfernoEgg());
		
		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potion.fireResistance.id, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("cephignismeatcooked"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cephignismeatcooked"));
		
		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potion.fireResistance.id, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6); // Fire Resistance
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));
		
		ObjectManager.addItem("embercharge", new ItemEmberCharge());
		ObjectManager.addItem("emberscepter", new ItemScepterEmber(), 2, 1, 1);
		ObjectManager.addItem("cinderfallsword", new ItemSwordCinderfall(), 2, 1, 1);
        ObjectManager.addItem("cinderfallswordazure", new ItemSwordCinderfallAzure());
        ObjectManager.addItem("cinderfallswordverdant", new ItemSwordCinderfallVerdant());
		ObjectManager.addItem("magmacharge", new ItemMagmaCharge());
		ObjectManager.addItem("magmascepter", new ItemScepterMagma(), 2, 1, 1);
        ObjectManager.addItem("scorchfirecharge", new ItemScorchfireCharge());
        ObjectManager.addItem("scorchfirescepter", new ItemScepterScorchfire(), 2, 1, 1);

		ObjectManager.addItem("afrittreat", new ItemTreat("afrittreat", group));

		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava(fluid).setContainerItem(Items.bucket));

        // ========== Create Blocks ==========
        AssetManager.addSound("scorchfire", group, "block.scorchfire");
        ObjectManager.addBlock("scorchfire", new BlockScorchfire());
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("infernoegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "cinder", EntityCinder.class, 0xFF9900, 0xFFFF00)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("FIRE").setBlockCost(8)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "lobber", EntityLobber.class, 0x330011, 0xFF5500)
		        .setPeaceful(false).setSummonable(false).setSummonCost(8).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cephignis", EntityCephignis.class, 0xFFBB00, 0xDD00FF)
		        .setPeaceful(true).setSummonable(false).setSummonCost(1).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(32).setDespawn(false)
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 3).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "afrit", EntityAfrit.class, 0x110000, 0x773300)
                .setPeaceful(false).setSummonable(false).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("violet", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "khalk", EntityKhalk.class, 0x442200, 0xFFAA22)
                .setPeaceful(false).setSummonable(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(32)
                .setSpawnWeight(1).setAreaLimit(2).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());
        ObjectManager.addProjectile("magma", EntityMagma.class, ObjectManager.getItem("magmacharge"), new DispenserBehaviorMagma());
        ObjectManager.addProjectile("scorchfireball", EntityScorchfireball.class, ObjectManager.getItem("scorchfirecharge"), new DispenserBehaviorScorchfire());

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
		ObjectManager.setCurrentGroup(this.group);
		
		// ========== Mob Events ==========
        MobEventBase mobEvent = new MobEventCinderfall("cinderfall", this.group).setDimensions("1");
        SpawnTypeBase eventSpawner = new SpawnTypeSky("cinderfall")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.air};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("cinder"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);

        mobEvent = new MobEventEruption("eruption", this.group).setDimensions("1");
        mobEvent.minDay = 10;
        eventSpawner = new SpawnTypeLand("eruption")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.air};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("lobber"));
        eventSpawner.addSpawn(MobInfo.getFromName("khalk"), 2);
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("searingtaco"), 1, 0),
				new Object[] {
					Items.blaze_powder,
					ObjectManager.getItem("cephignismeatcooked"),
					Items.wheat
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("cephignismeatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("searingtaco") }
			));

        if(ItemInfo.enableWeaponRecipes) {
            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("emberscepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("embercharge"),
                            Character.valueOf('R'), Items.blaze_rod
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("cinderfallsword"), 1, 0),
                    new Object[]{"CCC", "CSC", "CJC",
                            Character.valueOf('C'), ObjectManager.getItem("embercharge"),
                            Character.valueOf('S'), Items.diamond_sword,
                            Character.valueOf('J'), ObjectManager.getItem("soulgazer")
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("cinderfallswordazure"), 1, 0),
                    new Object[]{"DDD", "DSD", "DDD",
                            Character.valueOf('D'), Items.diamond,
                            Character.valueOf('S'), ObjectManager.getItem("cinderfallsword")
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("cinderfallswordverdant"), 1, 0),
                    new Object[]{"DDD", "DSD", "DDD",
                            Character.valueOf('D'), Items.emerald,
                            Character.valueOf('S'), ObjectManager.getItem("cinderfallsword")
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("magmascepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("magmacharge"),
                            Character.valueOf('R'), Items.blaze_rod
                    }));

            GameRegistry.addRecipe(new ShapedOreRecipe(
                    new ItemStack(ObjectManager.getItem("scorchfirescepter"), 1, 0),
                    new Object[]{"CCC", "CRC", "CRC",
                            Character.valueOf('C'), ObjectManager.getItem("scorchfirecharge"),
                            Character.valueOf('R'), Items.blaze_rod
                    }));
        }
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("afrittreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), Items.gunpowder,
				Character.valueOf('B'), Items.bone
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("bucketpurelava"), 1, 0),
				new Object[] {
					Items.lava_bucket,
					Items.ghast_tear
				}
			));
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("sauropodmeatraw").itemID, new ItemStack(ObjectManager.getItem("sauropodmeatcooked"), 1), 0.5f);
	}
}
