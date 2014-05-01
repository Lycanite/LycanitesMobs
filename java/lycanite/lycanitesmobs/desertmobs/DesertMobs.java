package lycanite.lycanitesmobs.desertmobs;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.ILycaniteMod;
import lycanite.lycanitesmobs.api.dispenser.DispenserBehaviorMobEggCustom;
import lycanite.lycanitesmobs.api.info.MobInfo;
import lycanite.lycanitesmobs.api.info.ObjectLists;
import lycanite.lycanitesmobs.api.item.ItemCustomFood;
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
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.Items;
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
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = DesertMobs.modid, name = DesertMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid)
public class DesertMobs implements ILycaniteMod {
	
	public static final String modid = "desertmobs";
	public static final String name = "Lycanites Desert Mobs";
	public static final String domain = modid.toLowerCase();
	public static int mobID = -1;
	public static int projectileID = 99;
	public static Config config = new SubConfig();
	
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
		config.init(modid);
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Items ==========
		ObjectManager.addItem("DesertEgg", "Spawn", new ItemDesertEgg(config.itemIDs.get("DesertEgg")));
		ObjectManager.addItem("ThrowingScythe", "Throwing Scythe", new ItemThrowingScythe(config.itemIDs.get("ThrowingScythe")));
		
		ObjectManager.addItem("JoustMeatRaw", "Raw Joust Meat", new ItemCustomFood("JoustMeatRaw", domain, 2, 0.5F).setPotionEffect(Potion.moveSlowdown.id, 45, 2, 0.8F));
		ObjectLists.addItem("RawMeat", ObjectManager.getItem("JoustMeatRaw"));
		ObjectManager.addItem("JoustMeatCooked", "Cooked Joust Meat", new ItemCustomFood("JoustMeatCooked", domain, 6, 0.7F));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("JoustMeatCooked"));
		ObjectManager.addItem("AmberCake", "Amber Cake", new ItemCustomFood("AmberCake", domain, 6, 0.7F).setPotionEffect(Potion.moveSpeed.id, 60, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16));
		ObjectLists.addItem("CookedMeat", ObjectManager.getItem("AmberCake"));
		
		ObjectManager.addItem("MudshotCharge", "Mudshot Charge", new ItemMudshotCharge(config.itemIDs.get("MudshotCharge")));
		ObjectManager.addItem("ScytheScepter", "Scythe Scepter", new ItemScepterScythe(config.itemIDs.get("ScytheScepter")));
		ObjectManager.addItem("MudshotScepter", "Mudshot Scepter", new ItemScepterMudshot(config.itemIDs.get("MudshotScepter")));
		
		// ========== Create Blocks ==========
		
	}
	
	
	// ==================================================
	//                Initialization
	// ==================================================
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		// ========== Set Current Mod ==========
		ObjectManager.setCurrentMod(this);
		
		// ========== Create Mobs ==========
		BlockDispenser.dispenseBehaviorRegistry.putObject(ObjectManager.getItem("DesertEgg"), new DispenserBehaviorMobEggCustom());
		ObjectManager.addMob(new MobInfo(this, "CryptZombie", "Crypt Zombie", EntityCryptZombie.class, 0xCC9966, 0xAA8800, 2));
		ObjectManager.addMob(new MobInfo(this, "Crusk", EntityCrusk.class, 0xFFDDAA, 0x000000, 8));
		ObjectManager.addMob(new MobInfo(this, "Clink", EntityClink.class, 0xFFAAAA, 0x999999, 2).setSummonable(true));
		ObjectManager.addMob(new MobInfo(this, "Joust", EntityJoust.class, 0xFF9900, 0xFFFF00, 2));
		ObjectManager.addMob(new MobInfo(this, "JoustAlpha", "Joust Alpha", EntityJoustAlpha.class, 0xFF0000, 0xFFFF00, 4));
		ObjectManager.addMob(new MobInfo(this, "Erepede", EntityErepede.class, 0xDD9922, 0xFFDDFF, 6));
		ObjectManager.addMob(new MobInfo(this, "Gorgomite", EntityGorgomite.class, 0xCC9900, 0x884400, 1));
		ObjectManager.addMob(new MobInfo(this, "Manticore", EntityManticore.class, 0x442200, 0x990000, 2).setSummonable(true));
		
		// ========== Create Projectiles ==========
		ObjectManager.addProjectile("ThrowingScythe", EntityThrowingScythe.class, ObjectManager.getItem("ThrowingScythe"), new DispenserBehaviorThrowingScythe());
		ObjectManager.addProjectile("Mudshot", EntityMudshot.class, ObjectManager.getItem("MudshotCharge"), new DispenserBehaviorMudshot());
		
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
		BiomeGenBase[] biomes = this.config.getSpawnBiomesTypes();
		if(config.getFeatureBool("ControlVanilla")) {
			EntityRegistry.removeSpawn(EntityZombie.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.monster, biomes);
			EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.monster, biomes);
		}
		
		// ========== Crafting ==========
		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("ThrowingScythe"), 17, 0),
				new Object[] { Items.iron_ingot, ObjectManager.getItem("ThrowingScythe") }
			));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("ScytheScepter"), 1, 0),
				new Object[] { "CCC", "CRC", "CRC",
				Character.valueOf('C'), ObjectManager.getItem("ThrowingScythe"),
				Character.valueOf('R'), Items.blaze_rod
			}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(
				new ItemStack(ObjectManager.getItem("MudshotScepter"), 1, 0),
				new Object[] { " C ", " R ", " R ",
				Character.valueOf('C'), ObjectManager.getItem("MudshotCharge"),
				Character.valueOf('R'), Items.blaze_rod
			}));

		GameRegistry.addRecipe(new ShapelessOreRecipe(
				new ItemStack(ObjectManager.getItem("AmberCake"), 1, 0),
				new Object[] {
					Items.sugar,
					new ItemStack(Items.dye, 1, 2),
					ObjectManager.getItem("JoustMeatCooked")
				}
			));
		
		// ========== Smelting ==========
		GameRegistry.addSmelting(ObjectManager.getItem("JoustMeatRaw"), new ItemStack(ObjectManager.getItem("JoustMeatCooked"), 1), 0.5f);
	}
	
	
	// ==================================================
	//                    Mod Info
	// ==================================================
	@Override
	public DesertMobs getInstance() { return instance; }
	
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
