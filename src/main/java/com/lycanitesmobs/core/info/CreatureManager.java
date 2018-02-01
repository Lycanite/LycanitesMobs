package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.JSONLoader;
import com.lycanitesmobs.core.config.ConfigBase;

import java.util.HashMap;
import java.util.Map;

public class CreatureManager extends JSONLoader {
	public static CreatureManager INSTANCE;



	/** A map of all creatures by name. **/
	public Map<String, CreatureInfo> creatures = new HashMap<>();

	/** A map of all creatures by class. **/
	public Map<Class, CreatureInfo> creatureClassMap = new HashMap<>();


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
		this.creatures.clear();
		this.creatureClassMap.clear();
		this.loadAllJson(groupInfo, "Creature", "creatures", "name", false);
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
		this.creatures.put(creatureInfo.name, creatureInfo);
		this.creatureClassMap.put(creatureInfo.entityClass, creatureInfo);
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
}
