package com.lycanitesmobs.elementalmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.elementalmobs.dispenser.*;
import com.lycanitesmobs.elementalmobs.entity.*;
import com.lycanitesmobs.elementalmobs.info.AltarInfoCelestialGeonach;
import com.lycanitesmobs.elementalmobs.info.AltarInfoLunarGrue;
import com.lycanitesmobs.elementalmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;

@Mod(modid = ElementalMobs.modid, name = ElementalMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class ElementalMobs extends Submod {
	
	public static final String modid = "elementalmobs";
	public static final String name = "Lycanites Elemental Mobs";
	
	// Instance:
	@Instance(modid)
	public static ElementalMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.elementalmobs.ClientSubProxy", serverSide="com.lycanitesmobs.elementalmobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);

		AltarInfo celestialGeonachAltar = new AltarInfoCelestialGeonach("CelestialGeonachAltar");
		AltarInfo.addAltar(celestialGeonachAltar);

		AltarInfo lunarGrueAltar = new AltarInfoLunarGrue("LunarGrueAltar");
		AltarInfo.addAltar(lunarGrueAltar);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Elemental Mobs", 9)
				.setDimensionBlacklist("").setBiomes("ALL").setDungeonThemes("")
				.setEggName("elementalspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("elementalspawn", new ItemElementalEgg());

		ObjectManager.addItem("embercharge", new ItemEmberCharge());
		ObjectManager.addItem("emberscepter", new ItemScepterEmber(), 2, 1, 1);
		ObjectManager.addItem("cinderfallsword", new ItemSwordCinderfall("cinderfallsword", "swordcinderfall"), 2, 1, 1);
		ObjectManager.addItem("azurecinderfallsword", new ItemSwordCinderfallAzure("azurecinderfallsword", "swordcinderfallazure"));
		ObjectManager.addItem("verdantcinderfallsword", new ItemSwordCinderfallVerdant("verdantcinderfallsword", "swordcinderfallverdant"));

		ObjectManager.addItem("aquapulsecharge", new ItemAquaPulseCharge());
		ObjectManager.addItem("aquapulsescepter", new ItemScepterAquaPulse(), 2, 1, 1);

		ObjectManager.addItem("whirlwindcharge", new ItemWhirlwindCharge());

		ObjectManager.addItem("lifedraincharge", new ItemLifeDrainCharge());
		ObjectManager.addItem("lifedrainscepter", new ItemScepterLifeDrain(), 2, 1, 1);

		ObjectManager.addItem("frostboltcharge", new ItemFrostboltCharge());
		ObjectManager.addItem("frostboltscepter", new ItemScepterFrostbolt(), 2, 1, 1);

		ObjectManager.addItem("wraithsigil", new ItemWraithSigil());
	}

	@Override
	public void createBlocks() {
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("elementalspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "cinder", EntityCinder.class, 0xFF9900, 0xFFFF00)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1).setDungeonThemes("FIERY, NETHER, NECRO")
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("").setBlockCost(8)
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "jengu", EntityJengu.class, 0x000099, 0x4444FF)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("WATER, DUNGEON")
				.addSubspecies(new Subspecies("light", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("WATER")
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "geonach", EntityGeonach.class, 0x443333, 0xBBBBCC)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1).setDungeonThemes("MOUNTAIN, WASTELAND, NECRO")
				.addSubspecies(new Subspecies("keppel", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"))
				.addSubspecies(new Subspecies("celestial", "rare"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "djinn", EntityDjinn.class, 0xb6bbc2, 0xf0f1f1)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("MOUNTAIN, PARADISE, MAGICAL")
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setDimensions("-1, 1").setDimensionWhitelist(false)
				.setSpawnWeight(6).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "argus", EntityArgus.class, 0x0e5000, 0xe305dc)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("SHADOW, NECRO")
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("dark", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		newMob = new MobInfo(group, "volcan", EntityVolcan.class, 0x270d03, 0xfba905)
				.setPeaceful(false).setSummonable(true).setSummonCost(6).setDungeonLevel(2).setDungeonThemes("FIERY, NETHER, NECRO")
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "zephyr", EntityZephyr.class, 0xFFFFDD, 0xAABBFF)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1).setDungeonThemes("WATER, DUNGEON")
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "grue", EntityGrue.class, 0x191017, 0xBB44AA)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1).setDungeonThemes("SHADOW, NECRO")
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"))
				.addSubspecies(new Subspecies("lunar", "rare"));
		newMob.spawnInfo.setSpawnTypes("UNDERGROUND")
				.setBiomes("ALL").setDimensions("-1").setDimensionWhitelist(false)
				.setSpawnWeight(8).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "spriggan", EntitySpriggan.class, 0x997722, 0x008844)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("SHADOW, NECRO")
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(4).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "reiver", EntityReiver.class, 0xDDEEFF, 0x99DDEE)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0).setDungeonThemes("FROZEN, DUNGEON")
				.addSubspecies(new Subspecies("ashen", "uncommon")).addSubspecies(new Subspecies("golden", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY").setBlockCost(8).setBiomes("COLD, SNOWY, CONIFEROUS, -END")
				.setSpawnWeight(8).setAreaLimit(3).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "tremor", EntityTremor.class, 0xa1b5bf, 0x232d2e)
				.setPeaceful(false).setSummonable(true).setSummonCost(5).setDungeonLevel(3).setDungeonThemes("MOUNTAIN, WASTELAND, NECRO")
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("")
				.setSpawnWeight(1).setAreaLimit(3).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "wraith", EntityWraith.class, 0xFF9900, 0xFF0000)
				.setPeaceful(false).setSummonCost(1).setDungeonLevel(0).setDungeonThemes("NETHER, NECRO")
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("azure", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("PORTAL, NETHERSKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 4).setLightDark(true, true).setDungeonWeight(120);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "spectre", EntitySpectre.class, 0x4f0095, 0xff08d7)
				.setPeaceful(false).setSummonable(true).setSummonCost(4).setDungeonLevel(2).setDungeonThemes("SHADOW, NECRO")
				.addSubspecies(new Subspecies("azure", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setBiomes("ALL").setDimensions("1").setDimensionWhitelist(true)
				.setSpawnWeight(2).setAreaLimit(4).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());

		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());

		ObjectManager.addProjectile("whirlwind", EntityWhirlwind.class, ObjectManager.getItem("whirlwindcharge"), new DispenserBehaviorWhirlwind());

		ObjectManager.addProjectile("lifedrain", EntityLifeDrain.class, ObjectManager.getItem("lifedraincharge"), new DispenserBehaviorLifeDrain());
		ObjectManager.addProjectile("lifedrainend", EntityLifeDrainEnd.class);

		ObjectManager.addProjectile("frostbolt", EntityFrostbolt.class, ObjectManager.getItem("frostboltcharge"), new DispenserBehaviorFrostbolt());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {

	}

	@Override
	public void addRecipes() {

	}

	@Override
	public void editVanillaSpawns() {

	}
}
