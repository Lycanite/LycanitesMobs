package lycanite.lycanitesmobs.junglemobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.api.item.ItemTreat;
import lycanite.lycanitesmobs.junglemobs.block.BlockQuickWeb;
import lycanite.lycanitesmobs.junglemobs.entity.EntityConcapedeHead;
import lycanite.lycanitesmobs.junglemobs.entity.EntityConcapedeSegment;
import lycanite.lycanitesmobs.junglemobs.entity.EntityGeken;
import lycanite.lycanitesmobs.junglemobs.entity.EntityTarantula;
import lycanite.lycanitesmobs.junglemobs.entity.EntityUvaraptor;
import lycanite.lycanitesmobs.junglemobs.item.ItemJungleEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.init.Blocks;
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

@Mod(modid = JungleMobs.modid, name = JungleMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class JungleMobs {
	
	public static final String modid = "junglemobs";
	public static final String name = "Lycanites Jungle Mobs";
	public static GroupInfo group;
	
	// Instance:
	@Instance(modid)
	public static JungleMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="lycanite.lycanitesmobs.junglemobs.ClientSubProxy", serverSide="lycanite.lycanitesmobs.junglemobs.CommonSubProxy")
	public static CommonSubProxy proxy;
	
	// ==================================================
	//                Pre-Initialization
	// ==================================================
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		// ========== Config ==========
		group = new GroupInfo(this, "Jungle Mobs")
                .setDimensions("0").setBiomes("JUNGLE");
		group.loadFromConfig();
		
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("jungleegg", new ItemJungleEgg());
		
		ObjectManager.addItem("concapedemeatraw", new ItemCustomFood("concapedemeatraw", group, 2, 0.5F).setPotionEffect(Potion.moveSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("concapedemeatraw"));
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("concapedemeatraw"));
		
		ObjectManager.addItem("concapedemeatcooked", new ItemCustomFood("concapedemeatcooked", group, 6, 0.7F));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("concapedemeatcooked"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("concapedemeatcooked"));
		
		ObjectManager.addItem("tropicalcurry", new ItemCustomFood("tropicalcurry", group, 6, 0.7F).setPotionEffect(Potion.jump.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("tropicalcurry"));

		ObjectManager.addItem("uvaraptortreat", new ItemTreat("uvaraptortreat", group));

		// ========== Create Blocks ==========
		ObjectManager.addBlock("quickweb", new BlockQuickWeb());
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		group.loadSpawningFromConfig();
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("jungleegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "geken", EntityGeken.class, 0x00AA00, 0xFFFF00)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "uvaraptor", EntityUvaraptor.class, 0x00FF33, 0xFF00FF)
		        .setPeaceful(false).setSummonable(false).setSummonCost(4);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(5).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "concapede", EntityConcapedeHead.class, 0x111144, 0xDD0000)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(18).setAreaLimit(10).setGroupLimits(1, 1);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "concapedesegment", EntityConcapedeSegment.class, 0x000022, 0x990000)
		        .setPeaceful(true).setSummonable(false).setSummonCost(1);
		newMob.spawnInfo.setSpawnTypes("CREATURE").setDespawn(false)
				.setSpawnWeight(0).setAreaLimit(0).setGroupLimits(0, 0).setEnabled(false);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "tarantula", EntityTarantula.class, 0x008800, 0xDD0000)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("MONSTER")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 2);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("template", EntityTemplate.class, Item.templateCharge, new DispenserBehaviorPoisonRay());
		
		// ========== Register Models ==========
		proxy.registerModels();
	}
	
	
	// ==================================================
	//                Post-Initialization
	// ==================================================
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		ConfigBase config = ConfigBase.getConfig(group, "spawning");
		
		// ========== Remove Vanilla Spawns ==========
		BiomeGenBase[] biomes = group.biomes;
		if(group.controlVanillaSpawns) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCow.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntityPig.class, EnumCreatureType.creature, biomes);
			EntityRegistry.removeSpawn(EntitySheep.class, EnumCreatureType.creature, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("tropicalcurry"), 1, 0),
				new Object[] {
					Items.bowl,
					new ItemStack(Items.dye, 1, 3),
					Blocks.vine,
					ObjectManager.getItem("concapedemeatcooked")
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("uvaraptortreat"), 1, 0),
				new Object[] { "TTT", "BBT", "TTT",
				Character.valueOf('T'), ObjectManager.getItem("concapedemeatcooked"),
				Character.valueOf('B'), Items.bone
			}));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("concapedemeatraw"), new ItemStack(ObjectManager.getItem("concapedemeatcooked"), 1), 0.5f);
	}
}
