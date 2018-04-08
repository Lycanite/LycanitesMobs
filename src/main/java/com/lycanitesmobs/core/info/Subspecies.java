package com.lycanitesmobs.core.info;


import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.entity.CreatureStats;
import com.lycanitesmobs.core.entity.EntityCreatureBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

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

    /** The name of this subspecies. **/
    public String name;

    /** The type of this subspecies. **/
    public String type;

    /** The weight of this subspecies, used when randomly determining the subspecies of a mob. A base species uses the static baseSpeciesWeight value. **/
    public int weight;


    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
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


	/**
	 * Constructor for creating a specialized subspecies not related to other subspecies without a type).
	 * @param setName The name of the Subspecies Set.
	 * @param setWeight The Weight of the Subspecies.
	 */
	public Subspecies(String setName, int setWeight) {
        this.name = setName.toLowerCase();
        this.weight = setWeight;
    }

	/**
	 * Constructor for creating a Subspecies based on a type ('uncommon', 'rare', etc).
	 * @param name The name of the Subspecies.
	 * @param type The type of the Subspecies.
	 */
	public Subspecies(String name, String type) {
        this.name = name.toLowerCase();
        this.type = type;
        this.weight = commonWeights.get(type);
    }


	/**
	 * Gets the display name of this Subspecies.
	 * @return The Subpsecies title.
	 */
	public String getTitle() {
        return I18n.translateToLocal("subspecies." + this.name + ".name");
    }


	/**
	 * Returns if this Subspecies is allowed to be used on the spawned entity.
	 * @return True if this Subspecies is allowed.
	 */
	public boolean canSpawn(EntityLivingBase entityLiving) {
		if(entityLiving != null) {
			World world = entityLiving.getEntityWorld();
			int day = (int)Math.floor(world.getTotalWorldTime() / 23999D);
			int spawnDayMin = 0;
			if("uncommon".equalsIgnoreCase(this.type)) {
				spawnDayMin = uncommonSpawnDayMin;
			}
			else if("rare".equalsIgnoreCase(this.type)) {
				spawnDayMin = rareSpawnDayMin;
			}
			if(day < spawnDayMin) {
				return false;
			}
		}
		return true;
	}
}
