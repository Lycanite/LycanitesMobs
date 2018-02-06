package com.lycanitesmobs.core.info;

import com.lycanitesmobs.core.config.ConfigBase;

/** Loads global creature configs. **/
public class CreatureConfig {

	// Client:
	/** If true, all mobs that are a subspecies will always show their nametag. **/
	public boolean subspeciesTags = true;


	// Stats:
	/** The minimum base starting level of every mob. Cannot be less than 1. **/
	public int startingLevelMin = 1;

	/** The maximum base starting level of every mob. Ignored when not greater than the min level. **/
	public int startingLevelMax = 1;

	/** Increases the base start level by this amount of every world day that has gone by, use this to slowly level up mobs as the world gets older. Fractions can be used such as 0.05 levels per day. The levels are rounded down so +0.9 would be +0 levels. **/
	public double levelPerDay = 0;

	/** The maximum level to be able gain from levels per day. **/
	public int levelPerDayMax = 100;

	/** How many levels a mob gains multiplied by the local area difficulty level. Staying in an area for a while slowly increases the difficulty of that area ranging from 0.00 to 6.75. So 1.5 means level 10 at full local area difficulty. **/
	public double levelPerLocalDifficulty = 1.5;


	// Pets:
	/** If true, the name of a pet's owner will be shown in it's name tag. **/
	public boolean ownerTags = true;

	/** Whether mob taming is allowed. **/
	public boolean tamingEnabled = true;

	/** Whether mob mounting is allowed. **/
	public boolean mountingEnabled = true;

	/** Whether mob mounting for flying mobs is allowed. **/
	public boolean mountingFlightEnabled = true;

	/** If true, tamed mobs wont harm their owners. **/
	public boolean friendlyFire = true;


	// Beastiary:
	/** The chance that a creature gets added to the killing player's Beastiary on death, always 100% for bosses. **/
	public double beastiaryAddOnDeathChance = 0.15;


	// Bosses:
	/** How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures. **/
	public double bossAntiFlight = 3;


	// Interaction:
	/** If true, predators such as Ventoraptors will attack farm animals such as Sheep or Makas. **/
	public boolean predatorsAttackAnimals = true;

	/** If true, all mobs that attack players will also attack villagers. **/
	public boolean mobsAttackVillagers = true;

	/** If true, passive mobs will fight back when hit instead of running away. **/
	public boolean animalsFightBack = false;

	/** If true, some elemental mobs will fuse with each other on sight into a stronger different elemental. **/
	public boolean elementalFusion = true;

	/** If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at. **/
	public boolean disablePickupOffsets = false;


	// Variations:
	/** If true, mobs will have a chance of becoming a subspecies when spawned. **/
	public boolean subspeciesSpawn = true;

	/** If true, mobs will vary in sizes when spawned. **/
	public boolean randomSizes = true;


	// Drops:
	/** A string of global drops to add to every mob. **/
	public String globalDropsString = "";


	/**
	 * Loads settings from the config.
	 * @param config The config to load from.
	 */
	public void loadConfig(ConfigBase config) {
		// Client GUI:
		config.setCategoryComment("GUI", "Mostly client side settings that affect visuals such as mob names or inventory tabs, etc.");
		this.subspeciesTags = config.getBool("GUI", "Subspecies Tags", this.subspeciesTags, "If true, all mobs that are a subspecies will always show their nametag.");

		// Stats:
		config.setCategoryComment("Base Starting Level", "The base starting level is the level every mob will start at. Mob Events, Special Spawners and other things will then add onto this base level.");
		startingLevelMin = config.getInt("Base Starting Level", "Starting Level Min", startingLevelMin, "The minimum base starting level of every mob. Cannot be less than 1.");
		startingLevelMax = config.getInt("Base Starting Level", "Starting Level Max", startingLevelMax, "The maximum base starting level of every mob. Ignored when not greater than the min level.");
		levelPerDay = config.getDouble("Base Starting Level", "Level Gain Per Day", levelPerDay, "Increases the base start level by this amount of every world day that has gone by, use this to slowly level up mobs as the world gets older. Fractions can be used such as 0.05 levels per day. The levels are rounded down so +0.9 would be +0 levels.");
		levelPerDayMax = config.getInt("Base Starting Level", "Level Gain Per Day Max", levelPerDayMax, "The maximum level to be able gain from levels per day.");
		levelPerLocalDifficulty = config.getDouble("Base Starting Level", "Level Gain Per Local Difficulty", levelPerLocalDifficulty, "How many levels a mob gains multiplied by the local area difficulty level. Staying in an area for a while slowly increases the difficulty of that area ranging from 0.00 to 6.75. So 1.5 means level 10 at full local area difficulty.");

		// Pets:
		config.setCategoryComment("Pets", "Here you can control all settings related to taming and mounting.");
		this.ownerTags = config.getBool("Pets", "Owner Tags", this.ownerTags, "If true, tamed mobs will display their owner's name in their name tag.");
		this.tamingEnabled = config.getBool("Pets", "Taming", this.tamingEnabled, "Set to false to disable pet/mount taming.");
		this.mountingEnabled = config.getBool("Pets", "Mounting", this.mountingEnabled, "Set to false to disable mounts.");
		this.mountingFlightEnabled = config.getBool("Pets", "Flying Mounting", this.mountingFlightEnabled, "Set to false to disable flying mounts, if all mounts are disable this option doesn't matter.");
		this.friendlyFire = config.getBool("Pets", "Friendly Fire", this.friendlyFire, "If true, pets, minions, etc can't harm their owners (with ranged attacks, etc).");

		// Beastiary:
		config.setCategoryComment("Beastiary", "Here you can control all settings related to the player's Beastiary.");
		this.beastiaryAddOnDeathChance = config.getDouble("Beastiary", "Add Creature On Kill Chance", this.beastiaryAddOnDeathChance, "The chance that creatures are added to the player's Beastiary when killed, the Soulgazer can also be used to add creatures. Bosses are always a 100% chance.");

		// Bosses:
		config.setCategoryComment("Bosses", "Here you can control all settings related to boss creatures, this does not include rare subspecies (mini bosses).");
		this.bossAntiFlight = config.getDouble("Bosses", "How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures.", this.bossAntiFlight);

		// Interaction:
		config.setCategoryComment("Mob Interaction", "Here you can control how mobs interact with other mobs.");
		this.predatorsAttackAnimals = config.getBool("Mob Interaction", "Predators Attack Animals", this.predatorsAttackAnimals, "Set to false to prevent predator mobs from attacking animals/farmable mobs.");
		this.mobsAttackVillagers = config.getBool("Mob Interaction", "Mobs Attack Villagers", this.mobsAttackVillagers, "Set to false to prevent mobs that attack players from also attacking villagers.");
		this.animalsFightBack = config.getBool("Mob Interaction", "Animals Fight Back", this.animalsFightBack, "If true, passive mobs will fight back when hit instead of running away.");
		this.elementalFusion = config.getBool("Mob Interaction", "Elemental Fusion", this.elementalFusion, "If true, some elemental mobs will fuse with each other on sight into a stronger different elemental.");
		this.disablePickupOffsets = config.getBool("Mob Interaction", "Disable Pickup Offset", this.disablePickupOffsets, "If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at.");

		// Variations:
		config.setCategoryComment("Mob Variations", "Settings for how mobs randomly vary such as subspecies. Subspecies are uncommon and rare variants of regular mobs, uncommon subspecies tend to be a bit tougher and rare subspecies are quite powerful and can be considered as mini bosses..");
		subspeciesSpawn = config.getBool("Mob Variations", "Subspecies Can Spawn", subspeciesSpawn, "Set to false to prevent subspecies from spawning, this will not affect mobs that have already spawned as subspecies.");
		randomSizes = config.getBool("Mob Variations", "Random Sizes", randomSizes, "Set to false to prevent mobs from having a random size variation when spawning, this will not affect mobs that have already spawned.");
		Subspecies.loadGlobalSettings(config);

		// Drops:
		config.setCategoryComment("Custom Item Drops", "Here you can add a global list of item drops to add to every mob from Lycanites Mobs. Format is: mod:item,metadata,chance,min,max Multiple drops should be semicolon separated and chances are in decimal format. You can also add an additional comma and then a subspecies ID to restrict that drop to a certain subspecies like so: mod:item,metadata,chance,min,max,subspecies. minecraft:wool,2,0.25,0,3 is Green Wool with a 25% drop rate and will drop 0 to 3 blocks. Be sure to use a colon for mod:item and commas for everything else in an entry. Semicolons can be used to separate multiple entries.");
		globalDropsString = config.getString("Default Item Drops", "Global Drops", globalDropsString, "");
	}
}
