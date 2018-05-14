package com.lycanitesmobs.elementalmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.AltarInfo;
import com.lycanitesmobs.core.info.GroupInfo;
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

		ObjectManager.addItem("acidsplashcharge", new ItemAcidSplashCharge());

		ObjectManager.addItem("lightball", new ItemLightBall());

		ObjectManager.addItem("lifedraincharge", new ItemLifeDrainCharge());
		ObjectManager.addItem("lifedrainscepter", new ItemScepterLifeDrain(), 2, 1, 1);

		ObjectManager.addItem("crystalshard", new ItemCrystalShard());

		ObjectManager.addItem("frostboltcharge", new ItemFrostboltCharge());
		ObjectManager.addItem("frostboltscepter", new ItemScepterFrostbolt(), 2, 1, 1);

		ObjectManager.addItem("faeboltcharge", new ItemFaeboltCharge());

		ObjectManager.addItem("aetherwavecharge", new ItemAetherwaveCharge());

		ObjectManager.addItem("wraithsigil", new ItemWraithSigil());
	}

	@Override
	public void createBlocks() {
	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("elementalspawn"), new DispenserBehaviorMobEggCustom());

		// Projectiles:
		ObjectManager.addProjectile("ember", EntityEmber.class, ObjectManager.getItem("embercharge"), new DispenserBehaviorEmber());

		ObjectManager.addProjectile("aquapulse", EntityAquaPulse.class, ObjectManager.getItem("aquapulsecharge"), new DispenserBehaviorAquaPulse());

		ObjectManager.addProjectile("whirlwind", EntityWhirlwind.class, ObjectManager.getItem("whirlwindcharge"), new DispenserBehaviorWhirlwind());

		ObjectManager.addProjectile("acidsplash", EntityAcidSplash.class, ObjectManager.getItem("acidsplashcharge"), new DispenserBehaviorAcidSplash(), true);

		ObjectManager.addProjectile("lightball", EntityLightBall.class, ObjectManager.getItem("lightball"), new DispenserBehaviorLightBall());

		ObjectManager.addProjectile("lifedrain", EntityLifeDrain.class, ObjectManager.getItem("lifedraincharge"), new DispenserBehaviorLifeDrain());
		ObjectManager.addProjectile("lifedrainend", EntityLifeDrainEnd.class, false);

		ObjectManager.addProjectile("crystalshard", EntityCrystalShard.class, ObjectManager.getItem("crystalshard"), new DispenserBehaviorCrystalShard());

		ObjectManager.addProjectile("frostbolt", EntityFrostbolt.class, ObjectManager.getItem("frostboltcharge"), new DispenserBehaviorFrostbolt());

		ObjectManager.addProjectile("faebolt", EntityFaeBolt.class, ObjectManager.getItem("faeboltcharge"), new DispenserBehaviorFaebolt());

		ObjectManager.addProjectile("aetherwave", EntityAetherwave.class, ObjectManager.getItem("aetherwavecharge"), new DispenserBehaviorAetherwave());
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
