package com.lycanitesmobs.core.info;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.helpers.JSONHelper;
import com.lycanitesmobs.core.spawner.condition.SpawnCondition;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.*;

public class Subspecies {
    // ========== Subspecies Global ==========
    /** The weight used by the default subspecies. **/
    public static int baseSpeciesWeight = 400;

    /** Common weights used by most subspecies. **/
    public static Map<String, Integer> commonWeights = new HashMap<String, Integer>() {{
    	put("common", 100);
    	put("uncommon", 20);
    	put("rare", 2);
    	put("legendary", 1);
    }};

	/** A static map containing all the global multipliers for each stat for each subspecies. **/
	public static Map<String, Double> statMultipliers = new HashMap<>();

    /** The drop amount scale of uncommon subspecies. **/
    public static int uncommonDropScale = 2;

    /** The drop amount scale of rare subspecies. **/
    public static int rareDropScale = 5;

    /** The scale of experience for uncommon subspecies. **/
    public static double uncommonExperienceScale = 2.0D;

    /** The scale of experience for uncommon subspecies. **/
    public static double rareExperienceScale = 10.0D;

	/** The minimum amount of days before uncommon species start to spawn. **/
	public static int uncommonSpawnDayMin = 0;

	/** The minimum amount of days before rare species start to spawn. **/
	public static int rareSpawnDayMin = 0;

    /** Whether rare subspecies should show boss health bars or not. **/
    public static boolean rareHealthBars = false;

    // ========== Subspecies General ==========
    /** The index of this subspecies in MobInfo. Set by MobInfo when added. Should never be 0 as that is used by the default and will result in this subspecies being ignored. **/
    public int index;

	/** The skin of this subspecies. **/
	public String skin;

    /** The color of this subspecies. **/
    public String color;

    /** The rarity of this subspecies. **/
    public String rarity;

    /** The weight of this subspecies, used when randomly determining the subspecies of a mob. A base species uses the static baseSpeciesWeight value. **/
    public int weight;

    /** Higher priority Subspecies will always spawn in place of lower priority ones if they can spawn regardless of weight. Only increase this above 0 if a subspecies has conditions otherwise they will stop standard/base Subspecies from showing up. **/
    public int priority = 0;

    /** A list of Spawn Conditions required for this subspecies to spawn. **/
    public List<SpawnCondition> spawnConditions = new ArrayList<>();


	/**
	 * Loads global Subspecies config values, etc.
	 * @param config The config instance to load values from.
	 */
	public static void loadGlobalSettings(ConfigBase config) {
        baseSpeciesWeight = config.getInt("Mob Variations", "Subspecies Base Weight", baseSpeciesWeight, "The weight of base subspecies (regular mobs).");
        commonWeights.put("uncommon", config.getInt("Mob Variations", "Subspecies Uncommon Weight", commonWeights.get("uncommon"), "The weight of uncommon subspecies (such as Azure, Verdant, Scarlet, etc)."));
        commonWeights.put("rare", config.getInt("Mob Variations", "Subspecies Rare Weight", commonWeights.get("rare"), "The weight of rare subspecies (such as Lunar or Celestial)."));

        // Difficulty:
        String[] subspeciesNames = new String[] {"uncommon", "rare"};
		statMultipliers = new HashMap<>();
        config.setCategoryComment("Subspecies Multipliers", "Here you can scale the stats of every mob on a per subspecies basis.");
        for(String subspeciesName : subspeciesNames) {
            for(String statName : CreatureStats.STAT_NAMES) {
                double defaultValue = 1.0;
				if("uncommon".equals(subspeciesName)) {
					if("health".equals(statName)) {
						defaultValue = 2;
					}
				}
                if("rare".equals(subspeciesName)) {
					if("health".equals(statName)) {
						defaultValue = 20;
					}
					else if("attackSpeed".equals(statName)) {
						defaultValue = 2;
					}
					else if("rangedSpeed".equals(statName)) {
						defaultValue = 2;
					}
					else if("effect".equals(statName)) {
						defaultValue = 2;
					}
				}
                statMultipliers.put((subspeciesName + "-" + statName).toUpperCase(), config.getDouble("Subspecies Multipliers", subspeciesName + " " + statName, defaultValue));
            }
        }


        uncommonDropScale = config.getInt("Mob Variations", "Subspecies Uncommon Item Drops Scale", uncommonDropScale, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its item drops amount is multiplied by this value.");
        rareDropScale = config.getInt("Mob Variations", "Subspecies Rare Item Drops Scale", rareDropScale, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its item drops amount is multiplied by this value.");

        uncommonExperienceScale = config.getDouble("Mob Variations", "Subspecies Uncommon Experience Scale", uncommonExperienceScale, "When a creature with the uncommon subspecies (Azure, Verdant, etc) dies, its experience amount is multiplied by this value.");
        rareExperienceScale = config.getDouble("Mob Variations", "Subspecies Rare Experience Scale", rareExperienceScale, "When a creature with the rare subspecies (Celestial, Lunar, etc) dies, its experience amount is multiplied by this value.");

		uncommonSpawnDayMin = config.getInt("Mob Variations", "Subspecies Uncommon Spawn Day Min", uncommonSpawnDayMin, "The minimum amount of days before uncommon species start to spawn.");
		rareSpawnDayMin = config.getInt("Mob Variations", "Subspecies Rare Spawn Day Min", rareSpawnDayMin, "The minimum amount of days before rare species start to spawn.");

		rareHealthBars = config.getBool("Mob Variations", "Subspecies Rare Health Bars", rareHealthBars, "If set to true, rare subspecies such as the Lunar Grue or Celestial Geonach will display boss health bars.");
    }


    public static Subspecies createFromJSON(JsonObject json) {
		// Rarity:
		String rarity = "uncommon";
		if(json.has("rarity")) {
			rarity = json.get("rarity").getAsString().toLowerCase();
		}
		else if(json.has("type")) {
			rarity = json.get("type").getAsString().toLowerCase();
		}

		// Skin:
		String skin = null;
		if(json.has("skin")) {
			skin = json.get("skin").getAsString().toLowerCase();
		}

		// Color:
		String color = null;
		if(json.has("color")) {
			color = json.get("color").getAsString().toLowerCase();
		}
		else if(json.has("name")) {
			color = json.get("name").getAsString().toLowerCase();
		}

		if(skin == null && color == null) {
			throw new RuntimeException("Invalid subspecies added with no Skin and/or Color defined! At least one value must be set.");
		}
		Subspecies subspecies = new Subspecies(skin, color, rarity);
		subspecies.index = json.get("index").getAsInt();

		// Priority:
		if(json.has("priority")) {
			subspecies.priority = json.get("priority").getAsInt();
		}

		// Conditions:
		if(json.has("conditions")) {
			JsonArray jsonArray = json.get("conditions").getAsJsonArray();
			Iterator<JsonElement> jsonIterator = jsonArray.iterator();
			while (jsonIterator.hasNext()) {
				JsonObject conditionJson = jsonIterator.next().getAsJsonObject();
				SpawnCondition spawnCondition = SpawnCondition.createFromJSON(conditionJson);
				subspecies.spawnConditions.add(spawnCondition);
			}
		}

		return subspecies;
	}


	/**
	 * Constructor for creating a color/skin Subspecies based on a rarity.
	 * @param skin The skin of the Subspecies. Can be null or default skin.
	 * @param color The color of the Subspecies. Can be null for default color.
	 * @param rarity The rarity of the Subspecies ('common', 'uncommon' or 'rare'.
	 */
	public Subspecies(@Nullable String skin, @Nullable String color, String rarity) {
        this.color = color.toLowerCase();
        this.skin = skin.toLowerCase();
        this.rarity = rarity;
        this.weight = commonWeights.get(rarity);
    }


	/**
	 * Gets the display name of this Subspecies.
	 * @return The Subspecies title.
	 */
	public String getTitle() {
		String subspeciesKey = "";
		if(this.skin != null) {
			subspeciesKey += "." + this.skin;
		}
		if(this.color != null) {
			subspeciesKey += "." + this.color;
		}
        return I18n.translateToLocal("subspecies" + subspeciesKey + ".name");
    }


	/**
	 * Returns if this Subspecies is allowed to be used on the spawned entity.
	 * @return True if this Subspecies is allowed.
	 */
	public boolean canSpawn(EntityLivingBase entityLiving) {
		if(entityLiving != null) {
			World world = entityLiving.getEntityWorld();

			// Spawn Day Limit:
			int day = (int)Math.floor(world.getTotalWorldTime() / 23999D);
			int spawnDayMin = 0;
			if("uncommon".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = uncommonSpawnDayMin;
			}
			else if("rare".equalsIgnoreCase(this.rarity)) {
				spawnDayMin = rareSpawnDayMin;
			}
			if(day < spawnDayMin) {
				return false;
			}

			// Check Conditions:
			for(SpawnCondition condition : this.spawnConditions) {
				if(!condition.isMet(world, null, entityLiving.getPosition())) {
					return false;
				}
			}
		}
		return true;
	}


	@Override
	public String toString() {
		String subspeciesName = this.color != null ? this.color : "normal";
		if(this.skin != null) {
			subspeciesName += " - " + this.skin;
		}
		subspeciesName += " - " + this.weight;
		return subspeciesName;
	}
}
