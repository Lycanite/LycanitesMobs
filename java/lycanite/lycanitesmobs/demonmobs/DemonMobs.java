package lycanite.lycanitesmobs.demonmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectLists;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.PacketHandler;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.MobInfo;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
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
import lycanite.lycanitesmobs.demonmobs.item.ItemStaffBelph;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = DemonMobs.modid, name = DemonMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels = {DemonMobs.modid}, packetHandler = PacketHandler.class)
public class DemonMobs implements ILycaniteMod {
	
	public static final String modid = "DemonMobs";
	public static final String name = "Lycanites Demon Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
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
		config.init(modid);
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Items ==========
		ObjectManager.addItem("DemonEgg", "Spawn", new ItemDemonEgg(config.itemIDs.get("DemonEgg")));
		ObjectManager.addItem("DoomfireCharge", "Doomfire Charge", new ItemDoomfireball(config.itemIDs.get("DoomfireCharge")));
		ObjectManager.addItem("HellfireCharge", "Hellfire Charge", new ItemHellfireball(config.itemIDs.get("HellfireCharge")));
		ObjectManager.addItem("DevilstarCharge", "Devilstar Charge", new ItemDevilstar(config.itemIDs.get("DevilstarCharge")));
		ObjectManager.addItem("DemonicLightningCharge", "Demonic Lightning Charge", new ItemDemonicLightning(config.itemIDs.get("DemonicLightningCharge")));
		
		ObjectManager.addItem("PinkyMeatRaw", "Raw Pinky Meat", new ItemCustomFood(config.itemIDs.get("PinkyMeatRaw"), "PinkyMeatRaw", domain, 4, 0.5F).setPotionEffect(Potion.wither.id, 30, 0, 0.8F));
		ObjectLists.addItem("RawMeat", ObjectManager.getItem("PinkyMeatRaw"));
		ObjectManager.addItem("PinkyMeatCooked", "Cooked Pinky Meat", new ItemCustomFood(config.itemIDs.get("PinkyMeatCooked"), "PinkyMeatCooked", domain, 7, 0.7F));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("PinkyMeatCooked"));
		ObjectManager.addItem("DevilLasagna", "Devil Lasagna", new ItemCustomFood(config.itemIDs.get("DevilLasagna"), "DevilLasagna", domain, 7, 0.7F).setPotionEffect(Potion.damageBoost.id, 60, 0, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("DevilLasagna"));
		
		ObjectManager.addItem("DoomfireScepter", "Doomfire Scepter", new ItemScepterDoomfire(config.itemIDs.get("DoomfireScepter")));
		ObjectManager.addItem("HellfireScepter", "Hellfire Scepter", new ItemScepterHellfire(config.itemIDs.get("HellfireScepter")));
		ObjectManager.addItem("DevilstarScepter", "Devilstar Scepter", new ItemScepterDevilstar(config.itemIDs.get("DevilstarScepter")));
		ObjectManager.addItem("DemonicLightningScepter", "Demonic Lightning Scepter", new ItemScepterDemonicLightning(config.itemIDs.get("DemonicLightningScepter")));

		ObjectManager.addItem("BelphStaff", "Belph Summoning Staff", new ItemStaffBelph(config.itemIDs.get("BelphStaff")));
		
		// ========== Create Blocks ==========
		ObjectManager.addBlock("Hellfire", "Hellfire", new BlockHellfire(config.blockIDs.get("Hellfire")));
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("DemonEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "Belph", EntityBelph.class, 0x992222, 0x000000));
		ObjectManager.addMob(new MobInfo(this, "Behemoth", EntityBehemoth.class, 0xFF2222, 0xFF9900));
		ObjectManager.addMob(new MobInfo(this, "Pinky", EntityPinky.class, 0xFF0099, 0x990000));
		ObjectManager.addMob(new MobInfo(this, "Trite", EntityTrite.class, 0xFFFF88, 0x000000));
		ObjectManager.addMob(new MobInfo(this, "Asmodi", EntityAsmodi.class, 0x999944, 0x0000FF));
		ObjectManager.addMob(new MobInfo(this, "NetherSoul", "Nether Soul", EntityNetherSoul.class, 0xFF9900, 0xFF0000));
		ObjectManager.addMob(new MobInfo(this, "Cacodemon", EntityCacodemon.class, 0xFF0000, 0x000099));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("Hellfireball", EntityHellfireball.class, ObjectManager.getItem("HellfireCharge"), new DispenserBehaviorHellfireball());
		ObjectManager.addProjectile("Doomfireball", EntityDoomfireball.class, ObjectManager.getItem("DoomfireCharge"), new DispenserBehaviorDoomfireball());
		ObjectManager.addProjectile("Devilstar", EntityDevilstar.class, ObjectManager.getItem("DevilstarCharge"), new DispenserBehaviorDevilstar());
		ObjectManager.addProjectile("DemonicSpark", EntityDemonicSpark.class);
		ObjectManager.addProjectile("DemonicBlast", EntityDemonicBlast.class, ObjectManager.getItem("DemonicLightningCharge"), new DispenserBehaviorDemonicLightning());
		
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
		
		// ========== Alter Vanilla Spawns ==========
		BiomeGenBase[] biomes = this.config.getSpawnBiomesTypes();
		// For some insane reason Zombie Pigmen have a spawn rate of 100 by default! I'm now matching this with my mobs to see if it affects things.
		if(config.getFeatureBool("ControlVanilla")) {
			EntityRegistry.removeSpawn(EntityPigZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityGhast.class, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityPigZombie.class, 100, 1, 4, EnumCreatureType.monster, biomes);
			EntityRegistry.addSpawn(EntityGhast.class, 50, 1, 2, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("DoomfireScepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("DoomfireCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("HellfireScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("HellfireCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("DevilstarScepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("DevilstarCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("DemonicLightningScepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("DemonicLightningCharge"),
				Character.valueOf('R'), Item.blazeRod
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("DevilLasagna"), 1, 0),
				new Object[] {
					Item.netherStalkSeeds,
					Item.wheat,
					ObjectManager.getItem("PinkyMeatCooked")
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("PinkyMeatRaw").itemID, new ItemStack(ObjectManager.getItem("PinkyMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public DemonMobs getInstance() { return instance; }
	
	@Override
	public String getModID() { return modid; }
	
	@Override
	public String getDomain() { return domain; }
	
	@Override
	public Config getConfig() { return config; }
	
	@Override
	public int getNextMobID() { return ++this.mobID; }
	
	@Override
	public int getNextProjectileID() { return ++this.projectileID; }
}
