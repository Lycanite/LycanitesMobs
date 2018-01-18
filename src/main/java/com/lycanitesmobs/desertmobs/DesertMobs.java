package com.lycanitesmobs.desertmobs;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.Submod;
import com.lycanitesmobs.core.dispenser.DispenserBehaviorMobEggCustom;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.core.info.MobInfo;
import com.lycanitesmobs.core.info.ObjectLists;
import com.lycanitesmobs.core.info.Subspecies;
import com.lycanitesmobs.core.item.ItemCustomFood;
import com.lycanitesmobs.core.item.ItemTreat;
import com.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorMudshot;
import com.lycanitesmobs.desertmobs.dispenser.DispenserBehaviorThrowingScythe;
import com.lycanitesmobs.desertmobs.entity.*;
import com.lycanitesmobs.desertmobs.item.*;
import net.minecraft.block.BlockDispenser;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

@Mod(modid = DesertMobs.modid, name = DesertMobs.name, version = LycanitesMobs.version, dependencies = "required-after:" + LycanitesMobs.modid, acceptedMinecraftVersions = LycanitesMobs.acceptedMinecraftVersions)
public class DesertMobs extends Submod {
	
	public static final String modid = "desertmobs";
	public static final String name = "Lycanites Desert Mobs";
	
	// Instance:
	@Instance(modid)
	public static DesertMobs instance;
	
	// Proxy:
	@SidedProxy(clientSide="com.lycanitesmobs.desertmobs.ClientSubProxy", serverSide="com.lycanitesmobs.desertmobs.CommonSubProxy")
	public static CommonSubProxy proxy;


	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		super.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		super.postInit(event);
	}

	@Mod.EventHandler
	public void registerEntities(RegistryEvent.Register<EntityEntry> event) {
		super.registerEntities(event);
	}


	@Override
	public void initialSetup() {
		group = new GroupInfo(this, "Desert Mobs", 4)
				.setDimensionBlacklist("-1,1").setBiomes("SANDY, WASTELAND, MESA, -COLD").setDungeonThemes("DESERT, WASTELAND, NECRO")
				.setEggName("desertspawn");
		group.loadFromConfig();
	}

	@Override
	public void createItems() {
		ObjectManager.addItem("desertspawn", new ItemDesertEgg());
		ObjectManager.addItem("throwingscythe", new ItemThrowingScythe());

		ObjectManager.addItem("joustmeatraw", new ItemCustomFood("joustmeatraw", group, 2, 0.5F, ItemCustomFood.FOOD_CLASS.RAW).setPotionEffect(MobEffects.SLOWNESS, 45, 2, 0.8F));
		ObjectLists.addItem("rawmeat", ObjectManager.getItem("joustmeatraw"));

		ObjectManager.addItem("joustmeatcooked", new ItemCustomFood("joustmeatcooked", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.COOKED).setPotionEffect(MobEffects.SPEED, 20, 2, 1.0F).setAlwaysEdible());
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("joustmeatcooked"));

		ObjectManager.addItem("ambercake", new ItemCustomFood("ambercake", group, 6, 0.7F, ItemCustomFood.FOOD_CLASS.MEAL).setPotionEffect(MobEffects.SPEED, 120, 2, 1.0F).setAlwaysEdible().setMaxStackSize(16), 3, 1, 6);
		ObjectLists.addItem("cookedmeat", ObjectManager.getItem("ambercake"));

		ObjectManager.addItem("crusktreat", new ItemTreat("crusktreat", group));
		ObjectManager.addItem("erepedetreat", new ItemTreat("erepedetreat", group));

		ObjectManager.addItem("mudshotcharge", new ItemMudshotCharge());
		ObjectManager.addItem("scythescepter", new ItemScepterScythe(), 2, 1, 1);
		ObjectManager.addItem("mudshotscepter", new ItemScepterMudshot(), 2, 1, 1);
	}

	@Override
	public void createBlocks() {

	}

	@Override
	public void createEntities() {
		// Mobs:
		BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(ObjectManager.getItem("desertspawn"), new DispenserBehaviorMobEggCustom());
		MobInfo newMob;

		newMob = new MobInfo(group, "cryptzombie", EntityCryptZombie.class, 0xCC9966, 0xAA8800)
				.setPeaceful(false).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("scarlet", "uncommon")).addSubspecies(new Subspecies("verdant", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "sutiramu", EntitySutiramu.class, 0x83553b, 0x2c1c0f)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(0)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(8).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "crusk", EntityCrusk.class, 0xFFDDAA, 0x000000)
				.setPeaceful(false).setTameable(true).setSummonCost(8).setDungeonLevel(2)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(2).setAreaLimit(3).setGroupLimits(1, 1).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "clink", EntityClink.class, 0xFFAAAA, 0x999999)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "joust", EntityJoust.class, 0xFF9900, 0xFFFF00)
				.setPeaceful(true).setSummonCost(2).setDungeonLevel(-1)
				.addSubspecies(new Subspecies("russet", "uncommon")).addSubspecies(new Subspecies("light", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("CREATURE, ANIMAL").setDespawn(false)
				.setSpawnWeight(10).setAreaLimit(10).setGroupLimits(1, 5).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "joustalpha", EntityJoustAlpha.class, 0xFF0000, 0xFFFF00)
				.setPeaceful(false).setSummonCost(4).setDungeonLevel(2)
				.addSubspecies(new Subspecies("verdant", "uncommon")).addSubspecies(new Subspecies("violet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("").setDespawn(false)
				.setSpawnWeight(2).setAreaLimit(2).setGroupLimits(1, 2).setLightDark(true, false).setDungeonWeight(0);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "erepede", EntityErepede.class, 0xDD9922, 0xFFDDFF)
				.setPeaceful(false).setTameable(true).setSummonCost(6).setDungeonLevel(1)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("ashen", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(4).setAreaLimit(5).setGroupLimits(1, 2).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "gorgomite", EntityGorgomite.class, 0xCC9900, 0x884400)
				.setPeaceful(false).setSummonCost(1).setDungeonLevel(1)
				.addSubspecies(new Subspecies("dark", "uncommon")).addSubspecies(new Subspecies("keppel", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("MONSTER, BEAST")
				.setSpawnWeight(6).setAreaLimit(40).setGroupLimits(1, 3).setLightDark(false, true);
		ObjectManager.addMob(newMob);

		newMob = new MobInfo(group, "manticore", EntityManticore.class, 0x442200, 0x990000)
				.setPeaceful(false).setSummonable(true).setSummonCost(2).setDungeonLevel(1)
				.addSubspecies(new Subspecies("golden", "uncommon")).addSubspecies(new Subspecies("scarlet", "uncommon"));
		newMob.spawnInfo.setSpawnTypes("SKY")
				.setSpawnWeight(6).setAreaLimit(10).setGroupLimits(1, 5).setLightDark(false, true);
		ObjectManager.addMob(newMob);


		// Projectiles:
		ObjectManager.addProjectile("throwingscythe", EntityThrowingScythe.class, ObjectManager.getItem("throwingscythe"), new DispenserBehaviorThrowingScythe());
		ObjectManager.addProjectile("mudshot", EntityMudshot.class, ObjectManager.getItem("mudshotcharge"), new DispenserBehaviorMudshot());
	}

	@Override
	public void registerModels() {
		proxy.registerModels(this.group);
	}

	@Override
	public void registerOres() {
		OreDictionary.registerOre("listAllchickenraw", ObjectManager.getItem("joustmeatraw"));
		OreDictionary.registerOre("listAllchickencooked", ObjectManager.getItem("joustmeatcooked"));
	}

	@Override
	public void addRecipes() {
		GameRegistry.addSmelting(ObjectManager.getItem("joustmeatraw"), new ItemStack(ObjectManager.getItem("joustmeatcooked"), 1), 0.5f);
	}

	@Override
	public void editVanillaSpawns() {
		EntityRegistry.removeSpawn(EntitySkeleton.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityCreeper.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntitySpider.class, EnumCreatureType.MONSTER, this.group.biomes);
		EntityRegistry.removeSpawn(EntityWitch.class, EnumCreatureType.MONSTER, this.group.biomes);
	}
}
