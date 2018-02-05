package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatureManager extends JSONLoader {
	public static CreatureManager INSTANCE;

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


	/** Returns the main Creature Manager INSTANCE or creates it and returns it. **/
	public static CreatureManager getInstance() {
		if(INSTANCE == null) {
			INSTANCE = new CreatureManager();
		}
		return INSTANCE;
	}


	/** Called during early start up, loads all global configs into this manager. **/
	public void loadConfig() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
	}


	/** Loads all JSON Elements. Should only be done on pre-init and before Creature Info is loaded. **/
	public void loadAllFromJSON(GroupInfo groupInfo) {
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
	 * Reloads all Creature JSON.
	 */
	public void reload() {
		for(GroupInfo group : this.loadedGroups) {
			this.loadAllFromJSON(group);
		}
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
