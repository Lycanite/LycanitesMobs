package com.lycanitesmobs.core.info;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.EntityCreatureRideable;
import com.lycanitesmobs.core.entity.EntityCreatureTameable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.awt.*;
import java.util.*;
import java.util.List;

/** Contains various information about a creature from default spawn information to stats, etc. **/
public class CreatureInfo {
	public static ElementInfo DEFAULT_ELEMENT = new ElementInfo();

	// Core Info:
	/** The name of this mob. Lowercase, no space, used for language entries and for generating the entity id, etc. Required. **/
	protected String name;

	/** The entity class used by this creature. **/
	public Class<? extends EntityLiving> entityClass;

	/** The group that this mob belongs to. **/
	public GroupInfo group;

	/** If false, this mob will be removed from the world if present and wont be allowed by any spawners. **/
	public boolean enabled = true;

	/** If true, this is not a true mob, for example the fear entity. It will also not be automatically registered, etc. **/
	public boolean dummy = false;

	/** The Spawn Information for this creature. **/
	public CreatureSpawn creatureSpawn;


	// Stats:
	public double width = 0.8D;
	public double height = 1.8D;

	public int experience = 5;
	public double health = 20.0D;
	public double defense = 0.0D;
	public double armor = 0.0D;
	public double speed = 24.0D; // Divided by 100 when applied.
	public double damage = 2.0D;
	public double attackSpeed = 1.0D; // Seconds per melee.
	public double rangedSpeed = 0.5D; // Seconds per ranged.
	public double effect = 1.0D; // Seconds of effect.
	public double amplifier = -1.0D; // No effect when less than 0.
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
	public List<ItemDrop> drops = new ArrayList<>();

	/** A json array containing a list of drops to be loaded during init. **/
	protected JsonArray dropsJson;


	// Scale:
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
		this.creatureSpawn = new CreatureSpawn();
		this.element = DEFAULT_ELEMENT;
	}


	/** Loads this creature from a JSON object. **/
	public void loadFromJSON(JsonObject json) {
		this.name = json.get("name").getAsString();
		try {
			this.entityClass = (Class<? extends EntityLiving>) Class.forName(json.get("class").getAsString());
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("", "[Creature] Unable to find the Java Entity Class: " + json.get("class").getAsString() + " for " + this.getName());
		}
		if(json.has("enabled"))
			this.enabled = json.get("enabled").getAsBoolean();
		if(json.has("dummy"))
			this.dummy = json.get("dummy").getAsBoolean();
		if(this.dummy)
			return;
		this.creatureSpawn.loadFromJSON(json.get("spawning").getAsJsonObject());

		if(json.has("width"))
			this.width = json.get("width").getAsDouble();
		if(json.has("height"))
			this.height = json.get("height").getAsDouble();

		if(json.has("experience"))
			this.experience = json.get("experience").getAsInt();
		if(json.has("health"))
			this.health = json.get("health").getAsDouble();
		if(json.has("defense"))
			this.defense = json.get("defense").getAsDouble();
		if(json.has("armor"))
			this.armor = json.get("armor").getAsDouble();
		if(json.has("speed"))
			this.speed = json.get("speed").getAsDouble();
		if(json.has("damage"))
			this.damage = json.get("damage").getAsDouble();
		if(json.has("attackSpeed"))
			this.attackSpeed = json.get("attackSpeed").getAsDouble();
		if(json.has("rangedSpeed"))
			this.rangedSpeed = json.get("rangedSpeed").getAsDouble();
		if(json.has("effect"))
			this.effect = json.get("effect").getAsDouble();
		if(json.has("amplifier"))
			this.amplifier = json.get("amplifier").getAsDouble();
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
			this.dropsJson = json.getAsJsonArray("drops");
		}

		if(json.has("sizeScale"))
			this.sizeScale = json.get("sizeScale").getAsDouble();
		if(json.has("hitboxScale"))
			this.hitboxScale = json.get("hitboxScale").getAsDouble();
	}


	/** Initialises this Creature Info, should be called after pre-init and when reloading. **/
	public void init() {
		if(this.dummy)
			return;

		// Element:
		this.element = ElementManager.getInstance().getElement(this.elementName);
		if(this.element == null) {
			throw new RuntimeException("[Creature] Unable to initialise Creature Info for " + this.getName() + " as the element " + this.elementName + " cannot be found.");
		}

		// Item Drops:
		this.drops.clear();
		if(this.dropsJson != null) {
			for(JsonElement mobDropJson : this.dropsJson) {
				ItemDrop itemDrop = ItemDrop.createFromJSON(mobDropJson.getAsJsonObject());
				if(itemDrop != null) {
					this.drops.add(itemDrop);
				}
				else {
					LycanitesMobs.printWarning("", "[Creature] Unable to add item drop to creature: " + this.name + ".");
				}
			}
		}

		// Spawning:
		this.creatureSpawn.init(this);
	}


	/**
	 * Registers this creature to vanilla and custom entity lists. Must be called after init and only during game startup and only by its own submod.
	 */
	public void register() {
		if(this.dummy)
			return;

		// ID and Enabled Check:
		if(!this.enabled) {
			LycanitesMobs.printDebug("Creature", "Creature Disabled: " + this.getName() + " - " + this.entityClass + " (" + group.name + ")");
		}

		// Mapping and Registration:
		if(!ObjectManager.entityLists.containsKey(this.group.filename)) {
			ObjectManager.entityLists.put(this.group.filename, new EntityListCustom());
		}
		ObjectManager.entityLists.get(this.group.filename).addMapping(this.entityClass, this.getResourceLocation(), this.eggBackColor, this.eggForeColor);
		EntityRegistry.registerModEntity(this.getResourceLocation(), this.entityClass, this.getName(), this.group.getNextMobID(), this.group.mod, 128, 3, true);

		// Add Stats:
		ItemStack achievementStack = new ItemStack(ObjectManager.getItem("mobtoken"));
		achievementStack.setTagInfo("Mob", new NBTTagString(this.getName()));
		ObjectManager.addStat(this.getName() + ".kill", new StatBase(this.getName() + ".kill", new TextComponentString(this.getName() + ".kill")));
		ObjectManager.addStat(this.getName() + ".learn", new StatBase(this.getName() + ".learn", new TextComponentString(this.getName() + ".learn")));
		if(this.isSummonable()) {
			ObjectManager.addStat(this.getName() + ".summon", new StatBase(this.getName() + ".summon", new TextComponentString(this.getName() + ".summon")));
		}
		if(this.isTameable()) {
			ObjectManager.addStat(this.getName() + ".tame", new StatBase(this.getName() + ".tame", new TextComponentString(this.getName() + ".tame")));
		}

		// Add Sounds:
		AssetManager.addSound(name + "_say", group, "entity." + name + ".say");
		AssetManager.addSound(name + "_hurt", group, "entity." + name + ".hurt");
		AssetManager.addSound(name + "_death", group, "entity." + name + ".death");
		AssetManager.addSound(name + "_step", group, "entity." + name + ".step");
		AssetManager.addSound(name + "_attack", group, "entity." + name + ".attack");
		AssetManager.addSound(name + "_jump", group, "entity." + name + ".jump");
		AssetManager.addSound(name + "_fly", group, "entity." + name + ".fly");
		if(this.isSummonable() || this.isTameable() || EntityCreatureTameable.class.isAssignableFrom(this.entityClass)) {
			AssetManager.addSound(name + "_tame", group, "entity." + name + ".tame");
			AssetManager.addSound(name + "_beg", group, "entity." + name + ".beg");
		}
		if(this.isTameable())
			AssetManager.addSound(name + "_eat", group, "entity." + name + ".eat");
		if(this.isMountable())
			AssetManager.addSound(name + "_mount", group, "entity." + name + ".mount");
		if(this.isBoss())
			AssetManager.addSound(name + "_phase", group, "entity." + name + ".phase");

		// Register Spawning:
		this.creatureSpawn.register(this);

		// Debug Message - Added:
		LycanitesMobs.printDebug("Creature", "Creature Added: " + this.getName() + " - " + this.entityClass + " (" + this.group.name + ")");
	}


	/**
	 * Returns the name of this creature, this is the unformatted lowercase name. Ex: lurker
	 * @return Creature name.
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * Returns the registry id of this creature. Ex: swampmobs:lurker
	 * @return Creature registry entity id.
	 */
	public String getEntityId() {
		return this.group.filename + ":" + this.getName();
	}


	/**
	 * Returns the resource location for this creature.
	 * @return Creature resource location.
	 */
	public ResourceLocation getResourceLocation() {
		return new ResourceLocation(this.group.filename, this.getName());
	}


	/**
	 * Returns the language key for this creature. Ex: swampmobs.lurker
	 * @return Creature language key.
	 */
	public String getLocalisationKey() {
		return this.group.filename + "." + this.getName();
	}

	/**
	 * Returns a translated title for this creature. Ex: Lurker
	 * @return The display name of this creature.
	 */
	public String getTitle() {
		return I18n.translateToLocal("entity." + this.getLocalisationKey() + ".name");
	}


	/**
	 * Returns a translated description of this creature.
	 * @return The creature description.
	 */
	public String getDescription() {
		return I18n.translateToLocal("entity." + this.getLocalisationKey() + ".description");
	}


	/**
	 * Returns the resource location for the GUI icon of this creature.
	 * @return Creature icon resource location.
	 */
	public ResourceLocation getIcon() {
		ResourceLocation texture = AssetManager.getTexture(this.getName() + "_icon");
		if(texture == null) {
			AssetManager.addTexture(this.getName() + "_icon", this.group, "textures/guis/" + this.getName() + "_icon.png");
			texture = AssetManager.getTexture(this.getName() + "_icon");
		}
		return texture;
	}


	/**
	 * Returns if this creature is summonable.
	 * @return True if creature is summonable.
	 */
	public boolean isSummonable() {
		return this.summonable && EntityCreatureTameable.class.isAssignableFrom(this.entityClass);
	}


	/**
	 * Returns if this creature is tameable.
	 * @return True if creature is tameable.
	 */
	public boolean isTameable() {
		return this.tameable && EntityCreatureTameable.class.isAssignableFrom(this.entityClass);
	}


	/**
	 * Returns if this creature is mountable.
	 * @return True if creature is mountable.
	 */
	public boolean isMountable() {
		return this.mountable && EntityCreatureRideable.class.isAssignableFrom(this.entityClass);
	}


	/**
	 * Returns if this creature is a boss.
	 * @return True if creature is a boss.
	 */
	public boolean isBoss() {
		return this.boss;
	}


	/**
	 * Returns a subspecies for the provided index or null if invalid.
	 * @param index The index of the subspecies for this creature.
	 * @return Creature subspecies.
	 */
	public Subspecies getSubspecies(int index) {
		if(!this.subspecies.containsKey(index)) {
			return null;
		}
		return this.subspecies.get(index);
	}


	/**
	 * Gets a random subspecies, normally used by a new mob when spawned.
	 * @param entity The entity that has this subspecies.
	 * @param rare If true, there will be much higher odds of a subspecies being picked.
	 * @return A Subspecies or null if using the base species.
	 */
	public Subspecies getRandomSubspecies(EntityLivingBase entity, boolean rare) {
		LycanitesMobs.printDebug("Subspecies", "~0===== Subspecies =====0~");
		LycanitesMobs.printDebug("Subspecies", "Selecting random subspecies for: " + entity);
		if(rare) {
			LycanitesMobs.printDebug("Subspecies", "The conditions have been set to rare increasing the chances of a subspecies being picked.");
		}
		if(this.subspecies.isEmpty()) {
			LycanitesMobs.printDebug("Subspecies", "No species available, will be base species.");
			return null;
		}
		LycanitesMobs.printDebug("Subspecies", "Subspecies Available: " + this.subspecies.size());

		// Get Viable Subspecies:
		List<Subspecies> possibleSubspecies = new ArrayList<>();
		for(Subspecies subspeciesEntry : this.subspecies.values()) {
			if(subspeciesEntry.canSpawn(entity)) {
				possibleSubspecies.add(subspeciesEntry);
			}
		}
		if(possibleSubspecies.isEmpty()) {
			LycanitesMobs.printDebug("Subspecies", "No species allowed, will be base species.");
			return null;
		}
		LycanitesMobs.printDebug("Subspecies", "Subspecies Allowed: " + possibleSubspecies.size());

		// Get Weights:
		int baseSpeciesWeightScaled = Subspecies.baseSpeciesWeight;
		if(rare)
			baseSpeciesWeightScaled /= 4;
		int totalWeight = baseSpeciesWeightScaled;
		for(Subspecies subspeciesEntry : possibleSubspecies) {
			totalWeight += subspeciesEntry.weight;
		}
		LycanitesMobs.printDebug("Subspecies", "Total Weight: " + totalWeight);

		// Roll and Check Default:
		int roll = entity.getRNG().nextInt(totalWeight);
		LycanitesMobs.printDebug("Subspecies", "Rolled: " + roll);
		if(roll <= baseSpeciesWeightScaled) {
			LycanitesMobs.printDebug("Subspecies", "Base species selected: " + baseSpeciesWeightScaled);
			return null;
		}

		// Get Random Subspecies:
		int checkWeight = baseSpeciesWeightScaled;
		for(Subspecies subspeciesEntry : possibleSubspecies) {
			checkWeight += subspeciesEntry.weight;
			if(roll <= checkWeight) {
				LycanitesMobs.printDebug("Subspecies", "Subspecies selected: " + subspeciesEntry.name + " - " + subspeciesEntry.weight);
				return subspeciesEntry;
			}
		}

		LycanitesMobs.printWarning("Subspecies", "The roll was higher than the Total Weight, this shouldn't happen.");
		return null;
	}

	public Subspecies getRandomSubspecies(EntityLivingBase entity) {
		return this.getRandomSubspecies(entity, false);
	}

	/**
	 * Used for when two mobs breed to randomly determine the subspecies of the child.
	 * @param entity The entity that has this subspecies, currently only used to get RNG.
	 * @param hostSubspeciesIndex The index of the subspecies of the host entity.
	 * @param partnerSubspecies The subspecies of the partner. Null if the partner is default.
	 * @return
	 */
	public Subspecies getChildSubspecies(EntityLivingBase entity, int hostSubspeciesIndex, Subspecies partnerSubspecies) {
		Subspecies hostSubspecies = this.getSubspecies(hostSubspeciesIndex);
		int partnerSubspeciesIndex = (partnerSubspecies != null ? partnerSubspecies.index : 0);
		if(hostSubspeciesIndex == partnerSubspeciesIndex)
			return hostSubspecies;

		int hostWeight = (hostSubspecies != null ? hostSubspecies.weight : Subspecies.baseSpeciesWeight);
		int partnerWeight = (partnerSubspecies != null ? partnerSubspecies.weight : Subspecies.baseSpeciesWeight);
		int roll = entity.getRNG().nextInt(hostWeight + partnerWeight);
		if(roll > hostWeight)
			return partnerSubspecies;
		return hostSubspecies;
	}
}
