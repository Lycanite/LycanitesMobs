package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntityAbtu;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntityIka;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntityLacedon;
import lycanite.lycanitesmobs.saltwatermobs.entity.EntitySkylus;
import lycanite.lycanitesmobs.saltwatermobs.item.ItemSaltwaterEgg;
import net.minecraft.block.BlockDispenser;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

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
		group = new GroupInfo(this, "Saltwater Mobs")
                .setDimensions("0").setBiomes("OCEAN, BEACH").setDungeonThemes("WATER");
		group.loadFromConfig();

		// ========== Set Current Group ==========
		ObjectManager.setCurrentGroup(group);
		
		// ========== Create Items ==========
		ObjectManager.addItem("saltwateregg", new ItemSaltwaterEgg());

        int rawFoodEffectID = Potion.blindness.id;
        if(ObjectManager.getPotionEffect("weight") != null)
            rawFoodEffectID = ObjectManager.getPotionEffect("weight").getId();
        ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", group, 2, 0.5F).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
        ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));
        OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("ikameatraw"));

        ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", group, 6, 0.7F));
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));
        OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("ikameatcooked"));

        ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", group, 6, 0.7F).setPotionEffect(Potion.waterBreathing.id, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));
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
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("saltwateregg"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;
        
        newMob = new MobInfo(group, "lacedon", EntityLacedon.class, 0x000099, 0x2244FF)
		        .setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0);
		newMob.spawnInfo.setSpawnTypes("WATERCREATURE")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "skylus", EntitySkylus.class, 0xFFCCDD, 0xBB2299)
		        .setPeaceful(false).setSummonable(true).setSummonCost(3).setDungeonLevel(1);
		newMob.spawnInfo.setSpawnTypes("WATERCREATURE")
				.setSpawnWeight(6).setAreaLimit(5).setGroupLimits(1, 3);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "ika", EntityIka.class, 0x99FFBB, 0x229944)
		        .setPeaceful(true).setSummonable(false).setSummonCost(2).setDungeonLevel(-1);
		newMob.spawnInfo.setSpawnTypes("WATERCREATURE").setDespawn(false)
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "abtu", EntityAbtu.class, 0xFFBB00, 0x44AAFF)
		        .setPeaceful(false).setSummonable(false).setSummonCost(2).setDungeonLevel(2);
		newMob.spawnInfo.setSpawnTypes("WATERCREATURE")
				.setSpawnWeight(8).setAreaLimit(32).setGroupLimits(1, 5);
		ObjectManager.addMob(newMob);

		
		// ========== Create Projectiles ==========
		//ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());
		
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
                new ItemStack(ObjectManager.getItem("seashellmaki"), 1, 0),
                new Object[]{
                        Blocks.vine,
                        Items.wheat,
                        ObjectManager.getItem("ikameatcooked"),
                }
        ));

		/*GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("emberscepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("embercharge"),
				Character.valueOf('R'), Item.blazeRod
			}));*/
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("ikameatraw"), new ItemStack(ObjectManager.getItem("ikameatcooked"), 1), 0.5f);
	}
}
