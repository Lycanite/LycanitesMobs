package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.Subspecies;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.demonmobs.block.BlockHellfire;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDevilstar;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorDoomfireball;
import lycanite.lycanitesmobs.demonmobs.dispenser.DispenserBehaviorHellfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityAsmodi;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBehemoth;
import lycanite.lycanitesmobs.demonmobs.entity.EntityBelph;
import lycanite.lycanitesmobs.demonmobs.entity.EntityCacodemon;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDemonicBlast;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDemonicSpark;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDevilstar;
import lycanite.lycanitesmobs.demonmobs.entity.EntityDoomfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityHellfireball;
import lycanite.lycanitesmobs.demonmobs.entity.EntityNetherSoul;
import lycanite.lycanitesmobs.demonmobs.entity.EntityPinky;
import lycanite.lycanitesmobs.demonmobs.entity.EntityTrite;
import lycanite.lycanitesmobs.demonmobs.item.ItemDemonEgg;
import lycanite.lycanitesmobs.demonmobs.item.ItemDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.item.ItemDevilstar;
import lycanite.lycanitesmobs.demonmobs.item.ItemDoomfireball;
import lycanite.lycanitesmobs.demonmobs.item.ItemHellfireball;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDemonicLightning;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDevilstar;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterDoomfire;
import lycanite.lycanitesmobs.demonmobs.item.ItemScepterHellfire;
import lycanite.lycanitesmobs.demonmobs.mobevent.MobEventHellsFury;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class DemonMobs {
	
	public static final String modid = "demonmobs";
	public static final String name = "Lycanites Demon Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static DemonMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.demonmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.demonmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Demon Mobs")
                .setDimensionBlacklist("-1").setDimensionWhitelist(true).setBiomes("NETHER").setDungeonThemes("NETHER, FIERY, DUNGEON, SHADOW")
                .setEggName("demonegg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("demonegg", new ItemDemonEgg());
		ObjectManager.addItem("doomfirecharge", new ItemDoomfireball());
		ObjectManager.addItem("hellfirecharge", new ItemHellfireball());
		ObjectManager.addItem("devilstarcharge", new ItemDevilstar());
		ObjectManager.addItem("demoniclightningcharge", new ItemDemonicLightning());
		
		ObjectManager.addItem("pinkymeatraw", new ItemCustomFood("pinkymeatraw", group, 4, 0.5F).setPotionEffect(Potion.wither.id, 30, 0, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("pinkymeatraw"));
		OreDictionary.registerOre("listAllbeefraw", ObjectManager.getItem("pinkymeatraw"));
		
		ObjectManager.addItem("pinkymeatcooked", new ItemCustomFood("pinkymeatcooked", group, 7, 0.7F).setPotionEffect(Potion.damageBoost.id, 10, 0, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("pinkymeatcooked"));
		OreDictionary.registerOre("listAllbeefcooked", ObjectManager.getItem("pinkymeatcooked"));
		
		ObjectManager.addItem("devillasagna", new ItemCustomFood("devillasagna", group, 7, 0.7F).setPotionEffect(Potion.damageBoost.id, 60, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("devillasagna"));

		ObjectManager.addItem("pinkytreat", new ItemTreat("pinkytreat", group));
		ObjectManager.addItem("cacodemontreat", new ItemTreat("cacodemontreat", group));
		
		ObjectManager.addItem("doomfirescepter", new ItemScepterDoomfire(), 2, 1, 1);
		ObjectManager.addItem("hellfirescepter", new ItemScepterHellfire(), 2, 1, 1);
		ObjectManager.addItem("devilstarscepter", new ItemScepterDevilstar(), 2, 1, 1);
		ObjectManager.addItem("demoniclightningscepter", new ItemScepterDemonicLightning(), 2, 1, 1);
		
		// ========== Create Blocks ==========
		AssetManager.addSound("hellfire", group, "block.hellfire");
		ObjectManager.addBlock("hellfire", new BlockHellfire());
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("demonegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "belph", EntityBelph.class, 0x992222, 0x000000)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 4).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "behemoth", EntityBehemoth.class, 0xFF2222, 0xFF9900)
		        .setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(2).setAreaLimit(5).setGroupLimits(1, 1).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "pinky", EntityPinky.class, 0xFF0099, 0x990000)
		        .setPeaceful(true).setSummonable(false).setSummonCost(4).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "trite", EntityTrite.class, 0xFFFF88, 0x000000)
		        .setPeaceful(false).setSummonable(false).setSummonCost(1).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(10).setAreaLimit(40).setGroupLimits(1, 10).setDungeonWeight(200);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "asmodi", EntityAsmodi.class, 0x999944, 0x0000FF)
		        .setPeaceful(false).setSummonable(false).setSummonCost(8).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setDungeonWeight(40);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "nethersoul", EntityNetherSoul.class, 0xFF9900, 0xFF0000)
		        .setPeaceful(false).setSummonable(false).setSummonCost(1).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, PORTAL, SKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 4).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cacodemon", EntityCacodemon.class, 0xFF0000, 0x000099)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("PORTAL, SKY")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 1).setDungeonWeight(80);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("hellfireball", EntityHellfireball.class, ObjectManager.getItem("hellfirecharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("doomfireball", EntityDoomfireball.class, ObjectManager.getItem("doomfirecharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("devilstar", EntityDevilstar.class, ObjectManager.getItem("devilstarcharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("demonicspark", EntityDemonicSpark.class);
		ObjectManager.addProjectile("demonicblast", EntityDemonicBlast.class, ObjectManager.getItem("demoniclightningcharge"), new DispenserBehaviorDemonicLightning());
		
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
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		// ========== Mob Events ==========
        if(MobInfo.getFromName("nethersoul") != null) {
			MobEventBase mobEvent = new MobEventHellsFury("hellsfury", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("hellsfury")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(5);
	        eventSpawner.materials = new Material[] {Material.air};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("nethersoul"));
	        eventSpawner.addSpawn(MobInfo.getFromName("cacodemon"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("doomfirescepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("doomfirecharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("hellfirescepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("hellfirecharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("devilstarscepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("devilstarcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("demoniclightningscepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("demoniclightningcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("devillasagna"), 1, 0),
				new Object[] {
					Items.nether_wart,
					Items.wheat,
					ObjectManager.getItem("pinkymeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("pinkytreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("hellfirecharge"),
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("cacodemontreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("pinkymeatcooked"),
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("pinkymeatraw"), new ItemStack(ObjectManager.getItem("pinkymeatcooked"), 1), 0.5f);
	}
}
