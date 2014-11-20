package lycanite.lycanitesmobs.plainsmobs;

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
import lycanite.lycanitesmobs.plainsmobs.entity.EntityKobold;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMaka;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityMakaAlpha;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityRoc;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityVentoraptor;
import lycanite.lycanitesmobs.plainsmobs.entity.EntityZoataur;
import lycanite.lycanitesmobs.plainsmobs.item.ItemPlainsEgg;
import lycanite.lycanitesmobs.plainsmobs.mobevent.MobEventWindStorm;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityPig;
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

@Mod(modid = PlainsMobs.modid, name = PlainsMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class PlainsMobs {
	
	public static final String modid = "plainsmobs";
	public static final String name = "Lycanites Plains Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static PlainsMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.plainsmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.plainsmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Plains Mobs")
				.setDimensionBlacklist("-1,1").setBiomes("PLAINS, SAVANNA, -SNOWY").setDungeonThemes("PLAINS, WASTELAND, URBAN")
                .setEggName("plainsegg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("plainsegg", new ItemPlainsEgg());
		
		ObjectManager.addItem("makameatraw", new ItemCustomFood("makameatraw", group, 2, 0.5F).setPotionEffect(Potion.weakness.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("makameatraw"));
		OreDictionary.registerOre("listAllporkraw", ObjectManager.getItem("makameatraw"));
		
		ObjectManager.addItem("makameatcooked", new ItemCustomFood("makameatcooked", group, 6, 0.7F).setPotionEffect(Potion.field_76444_x.id, 10, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("makameatcooked"));
		OreDictionary.registerOre("listAllporkcooked", ObjectManager.getItem("makameatcooked"));
		
		ObjectManager.addItem("bulwarkburger", new ItemCustomFood("bulwarkburger", group, 6, 0.7F).setPotionEffect(Potion.field_76444_x.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6); // Absorbtion
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("bulwarkburger"));

		ObjectManager.addItem("ventoraptortreat", new ItemTreat("ventoraptortreat", group));
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("plainsegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "kobold", EntityKobold.class, 0x996633, 0xFF7777)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ventoraptor", EntityVentoraptor.class, 0x99BBFF, 0x0033FF)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "maka", EntityMaka.class, 0xAA8855, 0x221100)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(12).setAreaLimit(10).setGroupLimits(2, 5).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "makaalpha", EntityMakaAlpha.class, 0x663300, 0x000000)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(4).setGroupLimits(1, 2).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "zoataur", EntityZoataur.class, 0x442200, 0xFFDDBB)
		        .setPeaceful(false).setSummonable(true).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(4).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "roc", EntityRoc.class, 0xAA0000, 0x00DD44)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("Template", EntityTemplate.class, Item.templateCharge, new DispenserBehaviorPoisonRay());
		
		// ========== Register Models ==========
		proxy.registerModels();
	}
	
	
	// ==================================================
	//                   Initialization
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
        if(MobInfo.getFromName("roc") != null) {
			MobEventBase mobEvent = new MobEventWindStorm("windstorm", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeSky("windstorm")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(3);
	        eventSpawner.materials = new Material[] {Material.air};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("roc"));
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityChicken.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("bulwarkburger"), 1, 0),
				new Object[] {
					Items.bread,
					ObjectManager.getItem("makameatcooked"),
					Items.bread
				}
			));
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("makameatcooked"), 1, 0),
				new Object[] { ObjectManager.getItem("bulwarkburger") }
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("ventoraptortreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("makameatcooked"),
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("makameatraw"), new ItemStack(ObjectManager.getItem("makameatcooked"), 1), 0.5f);
	}
}
