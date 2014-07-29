package lycanite.lycanitesmobs.saltwatermobs;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.OldConfig;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
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
public class SaltwaterMobs implements ILycaniteMod {
	
	public static final String modid = "saltwatermobs";
	public static final String name = "Lycanites Saltwater Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static OldConfig config = new SubConfig();
	
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
		config.init(modid);
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Items ==========
		ObjectManager.addItem("saltwateregg", new ItemSaltwaterEgg());

        int rawFoodEffectID = Potion.blindness.id;
        if(ObjectManager.getPotionEffect("weight") != null)
            rawFoodEffectID = ObjectManager.getPotionEffect("weight").getId();
        ObjectManager.addItem("ikameatraw", new ItemCustomFood("ikameatraw", domain, 2, 0.5F).setPotionEffect(rawFoodEffectID, 45, 2, 0.8F));
        ObjectLists.addItem("rawfish", ObjectManager.getItem("ikameatraw"));
        OreDictionary.registerOre("listAllfishraw", ObjectManager.getItem("ikameatraw"));

        ObjectManager.addItem("ikameatcooked", new ItemCustomFood("ikameatcooked", domain, 6, 0.7F));
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("ikameatcooked"));
        OreDictionary.registerOre("listAllfishcooked", ObjectManager.getItem("ikameatcooked"));

        ObjectManager.addItem("seashellmaki", new ItemCustomFood("seashellmaki", domain, 6, 0.7F).setPotionEffect(Potion.waterBreathing.id, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
        ObjectLists.addItem("cookedfish", ObjectManager.getItem("seashellmaki"));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("saltwateregg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "lacedon", EntityLacedon.class, 0x000099, 0x2244FF, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "skylus", EntitySkylus.class, 0xFFCCDD, 0xBB2299, 3).setSummonable(true));
        ObjectManager.addMob(new MobInfo(this, "ika", EntityIka.class, 0x99FFBB, 0x229944, 2).setSummonable(false));
        ObjectManager.addMob(new MobInfo(this, "abtu", EntityAbtu.class, 0xFFBB00, 0x44AAFF, 2).setSummonable(false));
		
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
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
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
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public SaltwaterMobs getInstance() { return instance; }
	
	@Override
	public String getModID() { return modid; }
	
	@Override
	public String getDomain() { return domain; }
	
	@Override
	public OldConfig getConfig() { return config; }
	
	@Override
	public int getNextMobID() { return ++this.mobID; }
	
	@Override
	public int getNextProjectileID() { return ++this.projectileID; }
}
