package lycanite.lycanitesmobs.infernomobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.infernomobs.block.BlockFluidPureLava;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorEmber;
import lycanite.lycanitesmobs.infernomobs.dispenser.DispenserBehaviorMagma;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCephignis;
import lycanite.lycanitesmobs.infernomobs.entity.EntityCinder;
import lycanite.lycanitesmobs.infernomobs.entity.EntityEmber;
import lycanite.lycanitesmobs.infernomobs.entity.EntityLobber;
import lycanite.lycanitesmobs.infernomobs.entity.EntityMagma;
import lycanite.lycanitesmobs.infernomobs.item.ItemBucketPureLava;
import lycanite.lycanitesmobs.infernomobs.item.ItemEmberCharge;
import lycanite.lycanitesmobs.infernomobs.item.ItemInfernoEgg;
import lycanite.lycanitesmobs.infernomobs.item.ItemMagmaCharge;
import lycanite.lycanitesmobs.infernomobs.item.ItemScepterEmber;
import lycanite.lycanitesmobs.infernomobs.item.ItemScepterMagma;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.fluids.Fluid;
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
                .setDimensions("ALL").setBiomes("ALL");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Blocks/Fluids ==========
		Fluid fluid = ObjectManager.addFluid(new Fluid("purelava"));
		fluid.setLuminosity(15).setDensity(3000).setViscosity(5000).setTemperature(1100);
		ObjectManager.addBlock("purelava", new BlockFluidPureLava(fluid));
		
		// ========== Create Items ==========
		ObjectManager.addItem("infernoegg", new ItemInfernoEgg());
		
		ObjectManager.addItem("cephignismeatcooked", new ItemCustomFood("cephignismeatcooked", group, 6, 0.7F));
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("cephignismeatcooked"));
		OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("cephignismeatcooked"));
		
		ObjectManager.addItem("searingtaco", new ItemCustomFood("searingtaco", group, 6, 0.7F).setPotionEffect(Potion.fireResistance.id, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16)); // Fire Resistance
		ObjectLists.addItem("cookedfish", ObjectManager.getItem("searingtaco"));
		
		ObjectManager.addItem("embercharge", new ItemEmberCharge());
		ObjectManager.addItem("emberscepter", new ItemScepterEmber());
		ObjectManager.addItem("magmacharge", new ItemMagmaCharge());
		ObjectManager.addItem("magmascepter", new ItemScepterMagma());
		ObjectManager.addItem("bucketpurelava", new ItemBucketPureLava());
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
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("infernoegg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "cinder", EntityCinder.class, 0xFF9900, 0xFFFF00)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2);
		newMob.spawnInfo.setSpawnTypes("FIRE").setBlockCost(8)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "lobber", EntityLobber.class, 0x330011, 0xFF5500)
		        .setPeaceful(false).setSummonable(false).setSummonCost(8);
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(16)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "cephignis", EntityCephignis.class, 0xFFBB00, 0xDD00FF)
		        .setPeaceful(true).setSummonable(false).setSummonCost(1);
		newMob.spawnInfo.setSpawnTypes("LAVA").setBlockCost(32).setDespawn(false)
				.setSpawnWeight(4).setAreaLimit(6).setGroupLimits(1, 3).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());
		ObjectManager.addProjectile("magma", EntityMagma.class, ObjectManager.getItem("magmacharge"), new DispenserBehaviorMagma());
		
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
		
		// ========== Remove Vanilla Spawns ==========
		// N/A
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("searingtaco"), 1, 0),
				new Object[] {
					Items.blaze_powder,
					ObjectManager.getItem("cephignismeatcooked"),
					Items.wheat
				}
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("emberscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("embercharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("magmascepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("magmacharge"),
				Character.valueOf('R'), Items.blaze_rod
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
