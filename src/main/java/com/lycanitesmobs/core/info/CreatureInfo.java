package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.helpers.JSONHelper;

import java.awt.*;
import java.util.*;
import java.util.List;

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

	/** The Spawn Information for this creature. **/
	public CreatureSpawn creatureSpawn;


	// Stats:
	public int experience = 5;
	public double health = 20.0D;
	public double defense = 0.0D;
	public double speed = 24.0D; // Divided by 100 when applied.
	public double damage = 2.0D;
	public double haste = 1.0D;
	public double effect = 1.0D;
	public double pierce = 1.0D;
	public double sight = 16.0D;
	public double knockbackResistance = 0.0D;


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

	/** If true, this mob can be summoned as a minion. The entity must have pet AI for this. **/
	public boolean summonable = false;

	/** If true, this mob can be tamed as a pet. The entity must have pet AI and a treat item set for this. **/
	public boolean tameable = false;

	/** If true, this mob can be used as a mount. The entity must have mount AI for this. **/
	public boolean mountable = false;

	/** How many charges this creature normally costs to summon. **/
	public int summonCost = 1;

	/** The Dungeon Level of this mob, for Lycanites Dungeons this affects what floor the mob appears on, but this is also used by other mods such as Doomlike Dungeons to assess difficulty. Default: -1 (All levels). **/
	public int dungeonLevel = -1;


	// Items:
	/** A list of all the item drops available to this creature. **/
	public List<MobDrop> drops = new ArrayList<>();


	// Visuals:
	/** A custom scale to apply to the mob's size. **/
	public double sizeScale = 1;

	/** A custom scale to apply to the mob's hitbox. **/
	public double hitboxScale = 1;


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
		try {
			this.entityClass = Class.forName(json.get("class").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "[Creature] Unable to find the Java Entity Class: " + json.get("class").getAsString() + " for " + this.name);
		}
		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();
		if(json.has("dummy"))
			this.dummy = json.get("dummy").getAsBoolean();
		if(this.dummy)
			return;
		this.creatureSpawn = new CreatureSpawn();
		this.creatureSpawn.loadFromJSON(json.get("spawning").getAsJsonObject());

		if(json.has("experience"))
			this.experience = json.get("experience").getAsInt();
		if(json.has("health"))
			this.health = json.get("health").getAsDouble();
		if(json.has("defense"))
			this.defense = json.get("defense").getAsDouble();
		if(json.has("speed"))
			this.speed = json.get("speed").getAsDouble();
		if(json.has("damage"))
			this.damage = json.get("damage").getAsDouble();
		if(json.has("haste"))
			this.haste = json.get("haste").getAsDouble();
		if(json.has("effect"))
			this.effect = json.get("effect").getAsDouble();
		if(json.has("pierce"))
			this.pierce = json.get("pierce").getAsDouble();
		if(json.has("knockbackResistance"))
			this.knockbackResistance = json.get("knockbackResistance").getAsDouble();
		if(json.has("sight"))
			this.sight = json.get("sight").getAsDouble();

		this.eggBackColor = Color.decode(json.get("eggBackColor").getAsString()).getRGB();
		this.eggForeColor = Color.decode(json.get("eggForeColor").getAsString()).getRGB();

		if(json.has("boss"))
			this.boss = json.get("boss").getAsBoolean();
		if(json.has("subspecies")) {
			Iterator<JsonElement> jsonIterator = json.get("subspecies").getAsJsonArray().iterator();
			while(jsonIterator.hasNext()) {
				JsonObject jsonObject = jsonIterator.next().getAsJsonObject();
				Subspecies subspecies = new Subspecies(jsonObject.get("name").getAsString().toLowerCase(), jsonObject.get("type").getAsString().toLowerCase());
				subspecies.index = jsonObject.get("index").getAsInt();
				this.subspecies.put(subspecies.index, subspecies);
			}
		}
		this.elementName = json.get("element").getAsString();

		if(json.has("peaceful"))
			this.peaceful = json.get("peaceful").getAsBoolean();
		if(json.has("summonable"))
			this.summonable = json.get("summonable").getAsBoolean();
		if(json.has("tameable"))
			this.tameable = json.get("tameable").getAsBoolean();
		if(json.has("mountable"))
			this.mountable = json.get("mountable").getAsBoolean();
		if(json.has("summonCost"))
			this.summonCost = json.get("summonCost").getAsInt();
		if(json.has("dungeonLevel"))
			this.dungeonLevel = json.get("dungeonLevel").getAsInt();

		if(json.has("drops")) {
			JsonArray dropEntries = json.getAsJsonArray("drops");
			for(JsonElement mobDropJson : dropEntries) {
				MobDrop mobDrop = MobDrop.createFromJSON(mobDropJson.getAsJsonObject());
				if(mobDrop != null) {
					this.drops.add(mobDrop);
				}
			}
		}

		if(json.has("sizeScale"))
			this.sizeScale = json.get("sizeScale").getAsDouble();
		if(json.has("hitboxScale"))
			this.hitboxScale = json.get("hitboxScale").getAsDouble();
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
