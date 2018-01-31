package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.core.helpers.JSONHelper;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Contains various information about a creature from default spawn information to stats, etc. **/
public class CreatureInfo {

	// Core Info:
	/** The name of this mob. Lowercase, no space, used for language entries and for generating the entity id, etc. Required. **/
	public String name;

	/** The entity class used by this creature. **/
	public Class entityClass;

	/** The group that this mob belongs to. **/
	public GroupInfo group;

	/** If false, this mob will be removed from the world if present and wont be allowed by any spawners. **/
	public boolean enabled = true;

	/** If true, this is not a true mob, for example the fear entity. It will also not be automatically registered, etc. **/
	public boolean dummy = false;


	// Spawn Egg:
	/** The background color of this mob's egg. Required. **/
	public int eggBackColor;

	/** The foreground color of this mob's egg. Required. **/
	public int eggForeColor;


	// Creature Type:
	/** If true, this creature is a boss creature and should use special boss features such as boss health bars and dps taken limits, etc. **/
	public boolean boss = false;

	/** The Subspecies that this creature can use. **/
	public Map<Integer, Subspecies> subspecies = new HashMap<>();

	/** The name of the Element of this creature, affects buffs and debuffs amongst other things. **/
	protected String elementName;

	/** The Element of this creature, affects buffs and debuffs amongst other things. **/
	public ElementInfo element;


	// Creature Difficulty:
	/** If true, this mob is allowed on Peaceful Difficulty. **/
	public boolean peaceful = false;

	/** How many charges this creature normally costs to summon. **/
	public int summonCost = 1;

	/** The Dungeon Level of this mob, for Lycanites Dungeons this affects what floor the mob appears on, but this is also used by other mods such as Doomlike Dungeons to assess difficulty. Default: -1 (All levels). **/
	public int dungeonLevel = -1;

	// Items:
	/** A list of all the item drops available to this creature. **/
	public List<MobDrop> drops = new ArrayList<>();


	/**
	 * Constructor
	 * @param group The group that this creature definition will belong to.
	 */
	public CreatureInfo(GroupInfo group) {
		this.group = group;
	}


	/** Loads this element from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();

		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();

		if(json.has("dummy"))
			this.dummy = json.get("dummy").getAsBoolean();
		if(this.dummy)
			return;

		this.eggBackColor = Color.decode(json.get("eggBackColor").getAsString()).getRGB();
		this.eggForeColor = Color.decode(json.get("eggForeColor").getAsString()).getRGB();

		if(json.has("boss"))
			this.boss = json.get("boss").getAsBoolean();

		this.elementName = json.get("element").getAsString();
	}


	/** Initialises this Creature Info, should be called after pre-init. **/
	public void init() {
		if(this.dummy)
			return;

		// Element:
		this.element = ElementManager.getInstance().getElement(this.elementName);
		if(this.element == null) {
			throw new RuntimeException("[Creature] Unable to initialise Creature Info for " + this.name + " as the element " + this.elementName + " cannot be found.");
		}
	}
}
