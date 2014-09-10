package lycanite.lycanitesmobs.desertmobs;

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
import lycanite.lycanitesmobs.api.spawning.SpawnTypeLand;
import lycanite.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorMudshot;
import lycanite.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorThrowingScythe;
import lycanite.lycanitesmobs.desertmobs.entity.EntityClink;
import lycanite.lycanitesmobs.desertmobs.entity.EntityCrusk;
import lycanite.lycanitesmobs.desertmobs.entity.EntityCryptZombie;
import lycanite.lycanitesmobs.desertmobs.entity.EntityErepede;
import lycanite.lycanitesmobs.desertmobs.entity.EntityGorgomite;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoust;
import lycanite.lycanitesmobs.desertmobs.entity.EntityJoustAlpha;
import lycanite.lycanitesmobs.desertmobs.entity.EntityManticore;
import lycanite.lycanitesmobs.desertmobs.entity.EntityMudshot;
import lycanite.lycanitesmobs.desertmobs.entity.EntityThrowingScythe;
import lycanite.lycanitesmobs.desertmobs.item.ItemDesertEgg;
import lycanite.lycanitesmobs.desertmobs.item.ItemMudshotCharge;
import lycanite.lycanitesmobs.desertmobs.item.ItemScepterMudshot;
import lycanite.lycanitesmobs.desertmobs.item.ItemScepterScythe;
import lycanite.lycanitesmobs.desertmobs.item.ItemThrowingScythe;
import lycanite.lycanitesmobs.desertmobs.mobevent.MobEventMarchOfTheGorgomites;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
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

@Mod(modid = DesertMobs.modid, name = DesertMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class DesertMobs {
	
	public static final String modid = "desertmobs";
	public static final String name = "Lycanites Desert Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static DesertMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.desertmobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.desertmobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Desert Mobs")
                .setDimensions("0").setBiomes("SANDY, WASTELAND, MESA, -COLD").setDungeonThemes("DESERT, WASTELAND, URBAN")
                .setEggName("desertegg");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("desertegg", new ItemDesertEgg());
		ObjectManager.addItem("throwingscythe", new ItemThrowingScythe());
		
		ObjectManager.addItem("joustmeatraw", new ItemCustomFood("joustmeatraw", group, 2, 0.5F).setPotionEffect(Potion.moveSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("joustmeatraw"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("joustmeatraw"));
		
		ObjectManager.addItem("joustmeatcooked", new ItemCustomFood("joustmeatcooked", group, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("joustmeatcooked"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("joustmeatcooked"));
		
		ObjectManager.addItem("ambercake", new ItemCustomFood("ambercake", group, 6, 0.7F).setPotionEffect(Potion.moveSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("ambercake"));

		ObjectManager.addItem("crusktreat", new ItemTreat("crusktreat", group));
		ObjectManager.addItem("erepedetreat", new ItemTreat("erepedetreat", group));
		
		ObjectManager.addItem("mudshotcharge", new ItemMudshotCharge());
		ObjectManager.addItem("scythescepter", new ItemScepterScythe());
		ObjectManager.addItem("mudshotscepter", new ItemScepterMudshot());
		
		// ========== Create Blocks ==========

		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("desertegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "cryptzombie", EntityCryptZombie.class, 0xCC9966, 0xAA8800)
		        .setPeaceful(false).setSummonable(false).setSummonCost(2).setDungeonLevel(0)
		        .addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "crusk", EntityCrusk.class, 0xFFDDAA, 0x000000)
		        .setPeaceful(false).setSummonable(false).setSummonCost(2).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(2).setAreaLimit(3).setGroupLimits(1, 1);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "clink", EntityClink.class, 0xFFAAAA, 0x999999)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "joust", EntityJoust.class, 0xFF9900, 0xFFFF00)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2).setDungeonLevel(-1)
		        .addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 5).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "joustalpha", EntityJoustAlpha.class, 0xFF0000, 0xFFFF00)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4).setDungeonLevel(2)
		        .addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 2).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "erepede", EntityErepede.class, 0xDD9922, 0xFFDDFF)
		        .setPeaceful(false).setSummonable(false).setSummonCost(6).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "gorgomite", EntityGorgomite.class, 0xCC9900, 0x884400)
		        .setPeaceful(false).setSummonable(false).setSummonCost(1).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(40).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "manticore", EntityManticore.class, 0x442200, 0x990000)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
		        .addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(4).setAreaLimit(10).setGroupLimits(1, 5);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("throwingscythe", EntityThrowingScythe.class, ObjectManager.getItem("throwingscythe"), new DispenserBehaviorThrowingScythe());
		ObjectManager.addProjectile("mudshot", EntityMudshot.class, ObjectManager.getItem("mudshotcharge"), new DispenserBehaviorMudshot());
		
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
        if(MobInfo.getFromName("gorgomite") != null) {
			MobEventBase mobEvent = new MobEventMarchOfTheGorgomites("marchofthegorgomites", this.group);
			SpawnTypeBase eventSpawner = new SpawnTypeLand("marchofthegorgomites")
	            .setChance(1.0D).setBlockLimit(32).setMobLimit(8);
	        eventSpawner.materials = new Material[] {Material.air};
	        eventSpawner.ignoreBiome = true;
	        eventSpawner.ignoreLight = true;
	        eventSpawner.forceSpawning = true;
	        eventSpawner.ignoreMobConditions = true;
	        eventSpawner.addSpawn(MobInfo.getFromName("gorgomite").spawnInfo);
	        mobEvent.addSpawner(eventSpawner);
			MobEventManager.instance.addWorldEvent(mobEvent);
        }
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("throwingscythe"), 17, 0),
				new Object[] { Items.iron_ingot, ObjectManager.getItem("throwingscythe") }
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("scythescepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("throwingscythe"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("mudshotscepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("mudshotcharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("ambercake"), 1, 0),
				new Object[] {
					Items.sugar,
					new ItemStack(Items.dye, 1, 2),
					ObjectManager.getItem("joustmeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("crusktreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("joustmeatcooked"),
				Character.valueOf('B'), Items.bone
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("erepedetreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), Items.gold_ingot,
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("joustmeatraw"), new ItemStack(ObjectManager.getItem("joustmeatcooked"), 1), 0.5f);
	}
}
