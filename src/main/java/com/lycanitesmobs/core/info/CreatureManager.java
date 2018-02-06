package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.spawner.SpawnerMobRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureManager extends JSONLoader {
	public static CreatureManager INSTANCE;

	/** Handles all global creature general config settings. **/
	public CreatureConfig config;

	/** Handles all global creature spawning config settings. **/
	public CreatureSpawnConfig spawnConfig;

	/** A map of all creatures by name. **/
	public Map<String, CreatureInfo> creatures = new HashMap<>();

	/** A map of all creatures by class. **/
	public Map<Class, CreatureInfo> creatureClassMap = new HashMap<>();

	/** The map of all creatures by name to be used when reloading json. **/
	public Map<String, CreatureInfo> oldCreatures = new HashMap<>();

	/** A list of mod groups that have loaded with this Creature Manager. **/
	public List<GroupInfo> loadedGroups = new ArrayList<>();

	/** A map containing all the global multipliers for each stat for each difficulty. **/
	public Map<String, Double> difficultyMultipliers = new HashMap<>();

	/** A map containing all the global multipliers for each stat for mob level scaling. **/
	public Map<String, Double> levelMultipliers = new HashMap<>();

	/** The global multiplier to use for the health of tamed creatures. **/
	public double tamedHealthMultiplier = 3;

	/** Set to true if Doomlike Dungeons is loaded allowing mobs to register their Dungeon themes. **/
	public boolean dlDungeonsLoaded = false;


	/** Returns the main Creature Manager INSTANCE or creates it and returns it. **/
	public static CreatureManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CreatureManager();
		}
		return INSTANCE;
	}


	/**
	 * Constructor
	 */
	public CreatureManager() {
		this.config = new CreatureConfig();
		this.spawnConfig = new CreatureSpawnConfig();
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		this.config.loadConfig(ConfigBase.getConfig(LycanitesMobs.group, "general"));
		this.spawnConfig.loadConfig(ConfigBase.getConfig(LycanitesMobs.group, "spawning"));
	}


	/** Loads all JSON Elements. Should only be done on pre-init and before Creature Info is loaded. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
		try {
			if(!this.loadedGroups.contains(groupInfo)) {
				this.loadedGroups.add(groupInfo);
			}
			this.oldCreatures = new HashMap<>(this.creatures);
			this.creatures.clear();
			this.creatureClassMap.clear();
			this.loadAllJson(groupInfo, "Creature", "creatures", "name", false);
			this.oldCreatures.clear();
			LycanitesMobs.printDebug("Creature", "Complete! " + this.creatures.size() + " JSON Creature Info Loaded In Total.");
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "No Creatures loaded for: " + groupInfo.name);
		}
	}


	@Override
	public void parseJson(GroupInfo groupInfo, String name, JsonObject json) {
		CreatureInfo creatureInfo = new CreatureInfo(groupInfo);
		creatureInfo.loadFromJSON(json);
		if(creatureInfo.name == null) {
			LycanitesMobs.printWarning("", "Unable to load " + name + " json due to missing name.");
			return;
		}


		// Already Exists:
		if(this.oldCreatures.containsKey(creatureInfo.name)) {
			creatureInfo = this.oldCreatures.get(creatureInfo.name);
			creatureInfo.loadFromJSON(json);
		}

		this.creatures.put(creatureInfo.name, creatureInfo);
		this.creatureClassMap.put(creatureInfo.entityClass, creatureInfo);
	}


	/**
	 * Initialises all creatures. Called after all creatures are loaded.
	 */
	public void initAll() {
		SpawnerMobRegistry.SPAWNER_MOB_REGISTRIES.clear();
		for(CreatureInfo creature : this.creatures.values()) {
			creature.init();
		}
	}


	/**
	 * Registers all creatures. Can only be called once and during post init.
	 */
	public void registerAll() {
		for(CreatureInfo creature : this.creatures.values()) {
			creature.register();
		}
	}


	/**
	 * Reloads all Creature JSON.
	 */
	public void reload() {
		this.loadConfig();
		for(GroupInfo group : this.loadedGroups) {
			this.loadAllFromJSON(group);
		}
		this.initAll();
	}


	/**
	 * Gets a creature by name.
	 * @param creatureName The name of the creature to get.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreature(String creatureName) {
		if(!this.creatures.containsKey(creatureName))
			return null;
		return this.creatures.get(creatureName);
	}


	/**
	 * Gets a creature by class.
	 * @param creatureClass The class of the creature to get.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreature(Class creatureClass) {
		if(!this.creatureClassMap.containsKey(creatureClass))
			return null;
		return this.creatureClassMap.get(creatureClass);
	}


	/**
	 * Gets a creature by entity id.
	 * @param entityId The the entity id of the creature to get. Periods will be replaced with semicolons.
	 * @return The Creature Info.
	 */
	public CreatureInfo getCreatureFromId(String entityId) {
		entityId = entityId.replace(".", ":");
		String[] mobIdParts = entityId.toLowerCase().split(":");
		return this.getCreature(mobIdParts[mobIdParts.length - 1]);
	}


	/**
	 * Returns a global difficulty multiplier for a stat.
	 * @param difficultyName The difficulty name.
	 * @param statName The stat name.
	 * @return The multiplier.
	 */
	public double getDifficultyMultiplier(String difficultyName, String statName) {
		String key = difficultyName.toUpperCase() + "-" + statName.toUpperCase();
		if(!this.difficultyMultipliers.containsKey(key)) {
			return 1;
		}
		return this.difficultyMultipliers.get(key);
	}


	/**
	 * Returns a global level multiplier for a stat.
	 * @param statName The stat name.
	 * @return The multiplier.
	 */
	public double getLevelMultiplier(String statName) {
		if(!this.levelMultipliers.containsKey(statName.toUpperCase())) {
			return 1;
		}
		return this.levelMultipliers.get(statName.toUpperCase());
	}
}
