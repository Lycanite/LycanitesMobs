package lycanite.lycanitesmobs.shadowmobs;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.Subspecies;
import lycanite.lycanitesmobs.api.mobevent.MobEventBase;
import lycanite.lycanitesmobs.api.mobevent.MobEventManager;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeBase;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.api.spawning.SpawnTypeSky;
import lycanite.lycanitesmobs.shadowmobs.block.BlockShadowfire;
import lycanite.lycanitesmobs.shadowmobs.dispenser.DispenserBehaviorBloodleech;
import lycanite.lycanitesmobs.shadowmobs.dispenser.DispenserBehaviorSpectralbolt;
import lycanite.lycanitesmobs.shadowmobs.entity.*;
import lycanite.lycanitesmobs.shadowmobs.item.*;
import lycanite.lycanitesmobs.shadowmobs.mobevent.MobEventBlackPlague;
import lycanite.lycanitesmobs.shadowmobs.mobevent.MobEventShadowGames;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

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
		group = new GroupInfo(this, "Shadow Mobs")
				.setDimensionBlacklist("1,2,-100").setDimensionWhitelist(true).setBiomes("END").setDungeonThemes("NECRO, SHADOW, DUNGEON")
                .setEggName("shadowegg");
		group.loadFromConfig();

		
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		
		// ========== Create Items ==========
		ObjectManager.addItem("shadowegg", new ItemShadowEgg());
		
		ObjectManager.addItem("spectralboltcharge", new ItemSpectralboltCharge());
		ObjectManager.addItem("spectralboltscepter", new ItemScepterSpectralbolt(), 2, 1, 1);
		
		ObjectManager.addItem("bloodleechcharge", new ItemBloodleechCharge());
		ObjectManager.addItem("bloodleechscepter", new ItemScepterBloodleech(), 2, 1, 1);
		
		/*ObjectManager.addItem("chupacabrameatraw", new ItemCustomFood("chupacabrameatraw", group, 2, 0.5F).setPotionEffect(Potion.digSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("chupacabrameatraw"));
		OreDictionary.registerOre("listAllmuttonraw", ObjectManager.getItem("chupacabrameatraw"));
		
		ObjectManager.addItem("chupacabrameatcooked", new ItemCustomFood("chupacabrameatcooked", group, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("chupacabrameatcooked"));
		OreDictionary.registerOre("listAllmuttoncooked", ObjectManager.getItem("chupacabrameatcooked"));
		
		ObjectManager.addItem("bloodchilli", new ItemCustomFood("bloodchilli", group, 6, 0.7F).setPotionEffect(Potion.digSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bloodchilli"));*/

        ObjectManager.addItem("geistliver", new ItemGeistLiver());
		
		
		// ========== Create Blocks ==========
		AssetManager.addSound("shadowfire", group, "block.shadowfire");
		ObjectManager.addBlock("shadowfire", new BlockShadowfire());
		
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("shadowegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "grue", EntityGrue.class, 0x191017, 0xBB44AA)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("crimson", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("DARKNESS, MONSTER")
				.setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);
        
        newMob = new MobInfo(group, "phantom", EntityPhantom.class, 0x101519, 0xDD2233)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("DEATH, SLEEP, MONSTER")
				.setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);
		AssetManager.addSound("phantom_say_jon", group, "entity.phantom.say.jon");
        
        newMob = new MobInfo(group, "epion", EntityEpion.class, 0x553300, 0xFF22DD)
		        .setPeaceful(false).setSummonable(false).setSummonCost(3).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("russet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, LUNAR")
				.setBiomes("GROUP, SPOOKY")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

        newMob = new MobInfo(group, "geist", EntityGeist.class, 0x705449, 0x310e08)
                .setPeaceful(false).setSummonable(false).setSummonCost(3).setDungeonLevel(2)
                .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
        newMob.spawnInfo.setSpawnTypes("UNDEATH, MONSTER")
                .setBiomes("GROUP, SPOOKY")
                .setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
        ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("spectralbolt", EntitySpectralbolt.class, ObjectManager.getItem("spectralboltcharge"), new DispenserBehaviorSpectralbolt());
		ObjectManager.addProjectile("bloodleech", EntityBloodleech.class, ObjectManager.getItem("bloodleechcharge"), new DispenserBehaviorBloodleech());
		
		
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
		MobEventBase mobEvent = new MobEventShadowGames("shadowgames", this.group).setDimensions("-1");
		mobEvent.minDay = 10;
		SpawnTypeBase eventSpawner = new SpawnTypeSky("shadowgames")
            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.air};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
    	eventSpawner.addSpawn(MobInfo.getFromName("grue"));
    	eventSpawner.addSpawn(MobInfo.getFromName("phantom"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);

        mobEvent = new MobEventBlackPlague("blackplague", this.group).setDimensions("-1");
        mobEvent.minDay = 10;
        eventSpawner = new SpawnTypeLand("blackplague")
                .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
        eventSpawner.materials = new Material[] {Material.air};
        eventSpawner.ignoreBiome = true;
        eventSpawner.ignoreLight = true;
        eventSpawner.forceSpawning = true;
        eventSpawner.ignoreMobConditions = true;
        eventSpawner.addSpawn(MobInfo.getFromName("geist"));
        eventSpawner.addSpawn(MobInfo.getFromName("ghoulzombie"));
        eventSpawner.addSpawn(MobInfo.getFromName("cryptzombie"));
        mobEvent.addSpawner(eventSpawner);
        MobEventManager.instance.addWorldEvent(mobEvent);
		
		
		// ========== Crafting ==========
		/*GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("peakskebab"), 1, 0),
				new Object[] {
					Items.stick,
					Items.carrot,
					Items.melon,
					ObjectManager.getItem("yalemeatcooked")
				}
			));*/
        
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("spectralboltscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("spectralboltcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
        
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("bloodleechscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("bloodleechcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		
		// ========== Smelting ==========
		//GameRegistry.addSmelting(ObjectManager.getItem("yalemeatraw"), new ItemStack(ObjectManager.getItem("yalemeatcooked"), 1), 0.5f);
	}
}
