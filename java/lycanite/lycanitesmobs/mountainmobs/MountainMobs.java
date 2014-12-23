package lycanite.lycanitesmobs.mountainmobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.info.Subspecies;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorArcaneLaserStorm;
import lycanite.lycanitesmobs.mountainmobs.dispenser.DispenserBehaviorBoulderBlast;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaser;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserEnd;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityArcaneLaserStorm;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityBeholder;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityBoulderBlast;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityGeonach;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityJabberwock;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityTroll;
import lycanite.lycanitesmobs.mountainmobs.entity.EntityYale;
import lycanite.lycanitesmobs.mountainmobs.item.ItemArcaneLaserStormCharge;
import lycanite.lycanitesmobs.mountainmobs.item.ItemBoulderBlastCharge;
import lycanite.lycanitesmobs.mountainmobs.item.ItemMountainEgg;
import lycanite.lycanitesmobs.mountainmobs.item.ItemScepterArcaneLaserStorm;
import lycanite.lycanitesmobs.mountainmobs.item.ItemScepterBoulderBlast;
import lycanite.lycanitesmobs.mountainmobs.mobevent.MobEventBoulderDash;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
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

@Mod(modid = MountainMobs.modid, name = MountainMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class MountainMobs {
	
	public static final String modid = "mountainmobs";
	public static final String name = "Lycanites Mountain Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static MountainMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.mountainmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.mountainmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Mountain Mobs")
				.setDimensionBlacklist("-1,1").setBiomes("MOUNTAIN").setDungeonThemes("MOUNTAIN, WASTELAND, DUNGEON")
                .setEggName("mountainegg");
		group.loadFromConfig();

		
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("mountainegg", new ItemMountainEgg());
		
		ObjectManager.addItem("boulderblastcharge", new ItemBoulderBlastCharge());
		ObjectManager.addItem("boulderblastscepter", new ItemScepterBoulderBlast(), 2, 1, 1);
        ObjectManager.addItem("arcanelaserstormcharge", new ItemArcaneLaserStormCharge());
        ObjectManager.addItem("arcanelaserstormscepter", new ItemScepterArcaneLaserStorm(), 2, 1, 1);
		
		ObjectManager.addItem("yalemeatraw", new ItemCustomFood("yalemeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(Potion.digSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("yalemeatraw"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("yalemeatraw"));
		
		ObjectManager.addItem("yalemeatcooked", new ItemCustomFood("yalemeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(Potion.digSpeed.id, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("yalemeatcooked"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("yalemeatcooked"));
		
		ObjectManager.addItem("peakskebab", new ItemCustomFood("peakskebab", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(Potion.digSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("peakskebab"));
		
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("mountainegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "jabberwock", EntityJabberwock.class, 0x662222, 0xFFFFAA)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "troll", EntityTroll.class, 0x007711, 0xEEEEEE)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "yale", EntityYale.class, 0xFFEEAA, 0xFFDD77)
		        .setPeaceful(true).setSummonable(false).setSummonCost(1).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(14).setAreaLimit(5).setGroupLimits(1, 4).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "geonach", EntityGeonach.class, 0x443333, 0xBBBBCC)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("keppel", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"))
                .addSubspecies(new Subspecies("celestial", "rare"));
		newMob.spawnInfo.setSpawnTypes("ROCK")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "beholder", EntityBeholder.class, 0x442211, 0x44AA33)
		        .setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(1).setAreaLimit(1).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
        ObjectManager.addProjectile("boulderblast", EntityBoulderBlast.class, ObjectManager.getItem("boulderblastcharge"), new DispenserBehaviorBoulderBlast());
        ObjectManager.addProjectile("arcanelaserstorm", EntityArcaneLaserStorm.class, ObjectManager.getItem("arcanelaserstormcharge"), new DispenserBehaviorArcaneLaserStorm());
        ObjectManager.addProjectile("arcanelaser", EntityArcaneLaser.class);
        ObjectManager.addProjectile("arcanelaserend", EntityArcaneLaserEnd.class);


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
        if(MobInfo.getFromName("geonach") != null) {
			MobEventBase mobEvent = new MobEventBoulderDash("boulderdash", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("boulderdash")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.air};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("geonach"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
        
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
		}
		
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("peakskebab"), 1, 0),
				new Object[] {
					Items.stick,
					Items.carrot,
					Items.melon,
					ObjectManager.getItem("yalemeatcooked")
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("peakskebab") }
			));

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("boulderblastscepter"), 1, 0),
                new Object[] { "CCC", "CRC", "CRC",
                        Character.valueOf('C'), ObjectManager.getItem("boulderblastcharge"),
                        Character.valueOf('R'), Items.blaze_rod
                }));

        GameRegistry.addRecipe(new ShapedOreRecipe(
                new ItemStack(ObjectManager.getItem("arcanelaserstormscepter"), 1, 0),
                new Object[] { " C ", " R ", " R ",
                        Character.valueOf('C'), ObjectManager.getItem("arcanelaserstormcharge"),
                        Character.valueOf('R'), Items.blaze_rod
                }));
		
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("yalemeatraw"), new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1), 0.5f);
	}
}
