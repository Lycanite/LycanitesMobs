package com.lycanitesmobs.core.info;

import com.lycanitesmobs.AssetManager;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.config.ConfigBase;
import com.lycanitesmobs.core.mods.DLDungeons;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.EntityRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MobInfo {
	// ========== Global Settings ==========
	/** A map containing a Class to MobInfo connection where a shared MobInfo for each mob can be obtained using the mob's class as a key. **/
	public static Map<Class, MobInfo> mobClassToInfo = new HashMap<Class, MobInfo>();
	
	/** A map containing a (Code) Name to MobInfo connection where a shared MobInfo for each mob can be obtained using the mob's codename as a key. **/
	public static Map<String, MobInfo> mobNameToInfo = new HashMap<String, MobInfo>();
	
	/** If true, all mobs that are a subspecies will always show their nametag. **/
	public static boolean subspeciesTags = true;
	
	/** If true, the name of a pet's owner will be shown in it's name tag. **/
	public static boolean ownerTags = true;
	
	/** Whether mob taming is allowed. **/
	public static boolean tamingEnabled = true;
	
	/** Whether mob mounting is allowed. **/
	public static boolean mountingEnabled = true;

    /** Whether mob mounting for flying mobs is allowed. **/
    public static boolean mountingFlightEnabled = true;
	
	/** If true, all mobs that attack players will also attack villagers. **/
	public static boolean mobsAttackVillagers = true;

    /** If true, predators such as Ventoraptors will attack farm animals such as Sheep or Makas. **/
    public static boolean predatorsAttackAnimals = true;

    /** If true, mobs will have a chance of becoming a subspecies when spawned. **/
    public static boolean subspeciesSpawn = true;

    /** If true, mobs will vary in sizes when spawned. **/
    public static boolean randomSizes = true;
	
	/** If true, tamed mobs wont harm their owners. **/
	public static boolean friendlyFire = true;

    /** The chance that a creature gets added to the killing player's Beastiary on death, always 100% for bosses. **/
    public static double beastiaryAddOnDeathChance = 0.15;

    /** How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures. **/
    public static double bossAntiFlight = 3;

    /** If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at. **/
    public static boolean disablePickupOffsets = false;
	
	/** A static map containing all the global multipliers for each stat for each difficulty. The defaults are Easy: 0.5, Normal: 1.0 and Hard: 1.5. **/
	public static Map<String, Double> difficultyMultipliers = new HashMap<>();

	/** A static map containing all the global multipliers for each stat for mob level scaling. **/
	public static Map<String, Double> levelMultipliers = new HashMap<>();
	
	/** A static ArrayList of all summonable creature names. **/
	public static List<String> summonableCreatures = new ArrayList<>();

    /** A static ArrayList of all tameable creature names. **/
    public static List<String> tameableCreatures = new ArrayList<>();
	
	/** A static Name to Instance map of all mob groups. **/
	public static Map<String, GroupInfo> mobGroups = new HashMap<>();
	
	/** Set to true if Doomlike Dungeons is loaded allowing mobs to register their Dungeon themes. **/
	public static boolean dlDungeonsLoaded = false;


	// ========== Per Mob Settings ==========
	/** Mod Group **/
	public GroupInfo group;
	
	/** A unique per group ID number for this mob, projectiles and other entities also require IDs. **/
	public int mobID;
	
	/** The name of this mob used by the ObjectManager and Config maps. Should be all lower case **/
	public String name = "mobname";

    /** The Resource Location used to reference this mob. Use the method getResourceLocation() as it will generate one if not yet generated. **/
    protected ResourceLocation resourceLocation;
	
	/** Is this mob enabled? If disabled, it will still be registered, etc but wont randomly spawn or have a spawn egg. **/
	public boolean mobEnabled = true;
	
	/** The class that this mob instantiates with. **/
	public Class entityClass;

	/** The SpawnInfo used by this mob. **/
	public SpawnInfo spawnInfo;

    /** Subspecies **/
    public Map<Integer, Subspecies> subspecies = new HashMap<Integer, Subspecies>();

    /** Subspecies Amount, used when automatically adding subspecies as well as when cycling through subspecies. **/
    public int subspeciesAmount = 0;
	
	/** The background color of this mob's egg. **/
	public int eggBackColor;	
	
	/** The foreground color of this mob's egg. **/
	public int eggForeColor;
	
	/** If false, the default drops for this mob will be disabled, useful if the config needs to completely take over on what mobs drop. **/
	public boolean defaultDrops = true;
	
	/** A list of all the custom item drops this mob should drop, readily parsed from the config. To be safe, this list should be copied into the entity INSTANCE. **/
	public List<DropRate> customDrops = new ArrayList<DropRate>();

    /** If true, this is a boss mob. Bosses are updated more frequently and have a larger tracking range by default. **/
    public boolean boss = false;
	
	/** If true, this is not a true mob, for example the fear entity. It will also not be automatically registered. **/
	public boolean dummy = false;

    // ========== Per Mob Settings With Varied Defaults ==========
    /** If true, this mob is allowed in Peaceful Difficulty. **/
    public boolean peacefulDifficulty;
	
	/** How many charges this creature normally costs to summon. **/
	public int summonCost = 1;

    /** Dungeon themes for this mob, used by Doomlike Dungeons. **/
    public String dungeonThemes = "GROUP";
	
	/** A rank used to level this mob for dungeons. This is currently only used by Doomlike Dungeons. -1 = Not for Dungeons, 0 = Common, 1 = Tough, 2 = Brute, 3 = Elite **/
	public int dungeonLevel = -1;

    /** A custom scale to apply to the mob's size. **/
    public double sizeScale = 1;

    /** A custom scale to apply to the mob's hitbox. **/
    public double hitboxScale = 1;
	
	// ========== Per Mob Stats ==========
    public double multiplierHealth = 1.0D;
	public double multiplierDefense = 1.0D;
	public double multiplierSpeed = 1.0D;
	public double multiplierDamage = 1.0D;
	public double multiplierHaste = 1.0D;
	public double multiplierEffect = 1.0D;
	public double multiplierPierce = 1.0D;

    public int boostHealth = 0;
	public int boostDefense = 0;
	public int boostSpeed = 0;
	public int boostDamage = 0;
	public int boostHaste = 0;
	public int boostEffect = 0;
	public int boostPierce = 0;
	
	///** The model used by this mob. NOTE: This is currently unused, see AssetManager.getModel() **/
	//public ModelBase model;
	
    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
	public static void loadGlobalSettings() {
        // GUI:
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
		config.setCategoryComment("GUI", "Mostly client side settings that affect visuals such as mob names or inventory tabs, etc.");
		subspeciesTags = config.getBool("GUI", "Subspecies Tags", subspeciesTags, "If true, all mobs that are a subspecies will always show their nametag.");

        // Pets:
		config.setCategoryComment("Pets", "Here you can control all settings related to taming and mounting.");
		ownerTags = config.getBool("Pets", "Owner Tags", ownerTags, "If true, tamed mobs will display their owner's name in their name tag.");
		tamingEnabled = config.getBool("Pets", "Taming", tamingEnabled, "Set to false to disable pet/mount taming.");
		mountingEnabled = config.getBool("Pets", "Mounting", mountingEnabled, "Set to false to disable mounts.");
        mountingFlightEnabled = config.getBool("Pets", "Flying Mounting", mountingFlightEnabled, "Set to false to disable flying mounts, if all mounts are disable this option doesn't matter.");
		friendlyFire = config.getBool("Pets", "Friendly Fire", friendlyFire, "If true, pets, minions, etc can't harm their owners (with ranged attacks, etc).");

		// Beastiary:
        config.setCategoryComment("Beastiary", "Here you can control all settings related to the player's Beastiary.");
        beastiaryAddOnDeathChance = config.getDouble("Beastiary", "Add Creature On Kill Chance", beastiaryAddOnDeathChance, "The chance that creatures are added to the player's Beastiary when killed, the Soulgazer can also be used to add creatures. Bosses are always a 100% chance.");

        // Bosses:
        config.setCategoryComment("Bosses", "Here you can control all settings related to boss creatures, this does not include rare subspecies (mini bosses).");
        bossAntiFlight = config.getDouble("Bosses", "How much higher players must be relative to a boss' y position (feet) to trigger anti flight measures.");

        // Interaction:
		config.setCategoryComment("Mob Interaction", "Here you can control how mobs interact with other mobs.");
        predatorsAttackAnimals = config.getBool("Mob Interaction", "Predators Attack Animals", predatorsAttackAnimals, "Set to false to prevent predator mobs from attacking animals/farmable mobs.");
		mobsAttackVillagers = config.getBool("Mob Interaction", "Mobs Attack Villagers", mobsAttackVillagers, "Set to false to prevent mobs that attack players from also attacking villagers.");
        disablePickupOffsets = config.getBool("Mob Interaction", "Disable Pickup Offset", disablePickupOffsets, "If true, when a mob picks up a player, the player will be positioned where the mob is rather than offset to where the mob is holding the player at.");

        // Variations:
        config.setCategoryComment("Mob Variations", "Settings for how mobs randomly vary such as subspecies. Subspecies are uncommon and rare variants of regular mobs, uncommon subspecies tend to be a bit tougher and rare subspecies are quite powerful and can be considered as mini bosses..");
        subspeciesSpawn = config.getBool("Mob Variations", "Subspecies Can Spawn", subspeciesSpawn, "Set to false to prevent subspecies from spawning, this will not affect mobs that have already spawned as subspecies.");
        randomSizes = config.getBool("Mob Variations", "Random Sizes", randomSizes, "Set to false to prevent mobs from having a random size variation when spawning, this will not affect mobs that have already spawned.");
        Subspecies.loadGlobalSettings(config);

        // Stats:
		String[] statNames = new String[] {"Health", "Defense", "Speed", "Damage", "Haste", "Effect", "Pierce"};

        // Difficulty:
        String[] difficultyNames = new String[] {"Easy", "Normal", "Hard"};
        double[] difficultyDefaults = new double[] {0.5D, 1.0D, 1.1D};
		difficultyMultipliers = new HashMap<String, Double>();
        config.setCategoryComment("Difficulty Multipliers", "Here you can scale the stats of every mob on a per difficulty basis. Note that on easy, speed is kept at 1.0 by default as 0.5 makes them stupidly slow.");
        int difficultyIndex = 0;
        for(String difficultyName : difficultyNames) {
            for(String statName : statNames) {
                double defaultValue = difficultyDefaults[difficultyIndex];
                if("Easy".equalsIgnoreCase(difficultyName) && ("Health".equalsIgnoreCase(statName) || "Speed".equalsIgnoreCase(statName)))
                    defaultValue = 1.0D;
                if("Hard".equalsIgnoreCase(difficultyName) && ("Speed".equalsIgnoreCase(statName)))
                    defaultValue = 1.0D;
                difficultyMultipliers.put((difficultyName + "-" + statName).toUpperCase(), config.getDouble("Difficulty Multipliers", difficultyName + " " + statName, defaultValue));
            }
            difficultyIndex++;
        }

        // Level:
		config.setCategoryComment("Level Multipliers", "Normally mobs are level 1, but Spawners can increase their level. Here you can adjust the percentage of each stat that is added per extra level. So by default at level 2 a mobs health is increased by 10%, at level 3 20% and so on.");
		for(String statName : statNames) {
        	double levelValue = 0.1;
			if("Health".equalsIgnoreCase(statName))
				levelValue = 0.1D;
			if("Haste".equalsIgnoreCase(statName))
				levelValue = 0.05D;
			if("Speed".equalsIgnoreCase(statName))
				levelValue = 0.01D;
			levelMultipliers.put(statName.toUpperCase(), config.getDouble("Level Multipliers", statName, levelValue));
		}
    }
	
	/**
	 * Get MobInfo from mob name.
	 * @param mobName The name of the mob, such as "cryptzombie", should be lower case, no spaces.
	 * @return
	 */
	public static MobInfo getFromName(String mobName) {
		mobName = mobName.toLowerCase();
		if(!mobNameToInfo.containsKey(mobName))
			return null;
		return mobNameToInfo.get(mobName);
	}

	/**
	 * Get MobInfo from mob id.
	 * @param mobId The id of the mob. Ex: mountainmobs.jabberwock
	 * @return
	 */
	public static MobInfo getFromId(String mobId) {
		String[] mobIdParts = mobId.toLowerCase().split("\\.");
		return getFromName(mobIdParts[mobIdParts.length - 1]);
	}
	
	/**
	 * Get MobInfo from class.
	 * @param mobClass The class of the mob, such as EntityAspid.class.
	 * @return
	 */
	public static MobInfo getFromClass(Class mobClass) {
		if(!mobNameToInfo.containsKey(mobClass))
			return null;
		return mobNameToInfo.get(mobClass);
	}
	
	/**
	 * Tells every registered MobInfo for this group to load from the configs. This should be called in init so that all mobs can read entity IDs.
	 * @return
	 */
	public static void loadAllFromConfigs(GroupInfo group) {
		for(MobInfo mobInfo : mobNameToInfo.values()) {
			if(mobInfo != null && mobInfo.group == group)
				mobInfo.loadFromConfigs();
		}
	}

    /**
     * Tells every registered MobInfo to load their from the configs. This should be called in post init so that Biome Tags and other things are definitely set.
     * @return
     */
    public static void loadAllSpawningFromConfigs() {
        for(MobInfo mobInfo : mobNameToInfo.values()) {
            if(mobInfo != null)
                mobInfo.spawnInfo.loadFromConfig();
        }
    }
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(GroupInfo group, String name, Class entityClass, int eggBack, int eggFore) {
		this.group = group;
        this.group.mobInfos.add(this);
		if(!mobGroups.containsKey(group.filename))
			mobGroups.put(group.filename, group);
		this.mobID = this.group.getNextMobID();
		this.name = name;
        this.entityClass = entityClass;
        this.eggBackColor = eggBack;
        this.eggForeColor = eggFore;
        this.spawnInfo = new SpawnInfo(this);

		mobClassToInfo.put(entityClass, this);
		mobNameToInfo.put(this.name, this);
	}


    // ==================================================
    //                 Load from Config
    // ==================================================
    public void loadFromConfigs() {
    	if(this.dummy) return;
    	
        // General Info:
        ConfigBase config = ConfigBase.getConfig(this.group, "general");
        config.setCategoryComment("Enabled Mobs", "Here you can completely disable any mob.");
        this.mobEnabled = config.getBool("Enabled Mobs", this.getCfgName("Enabled"), this.mobEnabled);
        
        config.setCategoryComment("Peaceful Mob", "Here you may control whether or not each mob is allowed in Peaceful Difficulty.");
        this.peacefulDifficulty = config.getBool("Peaceful Mobs", this.getCfgName("On Peaceful"), this.peacefulDifficulty);
        
        config.setCategoryComment("Summoning Costs", "How much summoning focus each mob costs. This includes mobs that can't be summoned by the summoning staff as there might be other methods of summoning them in the future.");
        this.summonCost = config.getInt("Summoning Costs", this.getCfgName("Summoning Cost"), this.summonCost);
        
        config.setCategoryComment("Dungeon Themes", "Here you can set the Dungeon Theme of each mob. These are used by Doomlike Dungeons and might be used by other things later.");
        this.dungeonThemes = config.getString("Dungeon Themes", this.getCfgName("Themes"), this.dungeonThemes);

        config.setCategoryComment("Dungeon Levels", "The dungeon level of this mob, used by Doomlike Dungeons. -1 = Not for Dungeons, 0 = Common, 1 = Tough, 2 = Brute, 3 = Elite");
        this.dungeonLevel = config.getInt("Dungeon Level", this.getCfgName("Dungeon Level"), this.dungeonLevel);
        this.dungeonLevel = Math.min(3, Math.max(-1, this.dungeonLevel));
        if(dlDungeonsLoaded) DLDungeons.addMob(this);
        
        // Load Item Drops:
        config.setCategoryComment("Default Item Drops", "If false, only custom item drops are dropped.");
        this.defaultDrops = config.getBool("Default Item Drops", this.getCfgName("Default Drops"), this.defaultDrops);

        config.setCategoryComment("Custom Item Drops", "Allows for custom items drops per mob. Format is: mod:item,metadata,chance,min,max Multiple drops should be semicolon separated and chances are in decimal format. You can also add an additional comma and then a subspecies ID to restrict that drop to a certain subspecies like so: mod:item,metadata,chance,min,max,subspecies. minecraft:wool,2,0.25,0,3 is Green Wool with a 25% drop rate and will drop 0 to 3 blocks. Be sure to use a colon for mod:item and commas for everything else in an entry. Semicolons can be used to separate multiple entries.");
        String customDropsString = config.getString("Custom Item Drops", this.getCfgName("Custom Drops"), "");
        if(customDropsString != null && customDropsString.length() > 0) {
            for(String customDropEntryString : customDropsString.split(";")) {
                String[] customDropValues = customDropEntryString.split(",");
                if(customDropValues.length >= 5) {

                    String dropName = customDropValues[0];
                    int dropMeta = Integer.parseInt(customDropValues[1]);
                    float dropChance = Math.max(0F, Math.min(1F, Float.parseFloat(customDropValues[2])));
                    int dropMin = Integer.parseInt(customDropValues[3]);
                    int dropMax = Integer.parseInt(customDropValues[4]);
                    int subspeciesID = -1;
                    if(customDropValues.length >= 6)
                        subspeciesID = Integer.parseInt(customDropValues[5]);

                    ItemStack drop = null;
                    if(Item.getByNameOrId(dropName) != null)
                        drop = new ItemStack((Item)Item.getByNameOrId(dropName), 1, dropMeta);
                    else if(Block.getBlockFromName(dropName) != null)
                        drop = new ItemStack((Block)Block.getBlockFromName(dropName), 1, dropMeta);

                    DropRate dropRate = new DropRate(drop, dropChance).setMinAmount(dropMin).setMaxAmount(dropMax);
                    dropRate.setSubspecies(subspeciesID);

                    this.customDrops.add(dropRate);
                }
            }
        }

        // Load Stats:
        config = ConfigBase.getConfig(this.group, "stats");
        config.setCategoryComment("Multipliers", "Here you can scale each mob stat,for INSTANCE setting 2 will double the stat, setting 0.5 will half it.");
        this.multiplierHealth = config.getDouble("Multipliers", this.getCfgName("Health"), this.multiplierHealth, "The maximum amount of health each mob has. Already spawned mobs will not be affected by any changes. 1 health = half a heart.");
        this.multiplierDefense = config.getDouble("Multipliers", this.getCfgName("Defense"), this.multiplierDefense, "How much damage is blocked, minimum damage dealt is 1.");
        this.multiplierSpeed = config.getDouble("Multipliers", this.getCfgName("Speed"), this.multiplierSpeed, "Movement speed.");
        this.multiplierDamage = config.getDouble("Multipliers", this.getCfgName("Damage"), this.multiplierDamage, "Damage dealt, both melee and ranged.");
        this.multiplierHaste = config.getDouble("Multipliers", this.getCfgName("Haste"), this.multiplierHaste, "Attack and ability speeds.");
        this.multiplierEffect = config.getDouble("Multipliers", this.getCfgName("Effect"), this.multiplierEffect, "Effect strengths and durations.");
        this.multiplierPierce = config.getDouble("Multipliers", this.getCfgName("Pierce"), this.multiplierPierce, "Affects how much damage the mob deals that ignores armor. At 1.0 for every 5 damage dealt, 1 damage ignores armor. At 2.0 for every 3 (2.5 rouded) damage dealt 1 damage ignores armor. At 0.5 for every 10 damage dealt, 1 damage ignores armor.");

        config.setCategoryComment("Boosts", "Here you can increase or decrease each stat by a specific amount. (Use a negative number to decrease.)");
        this.boostHealth = config.getInt("Boosts", this.getCfgName("Health"), this.boostHealth, "The maximum amount of health each mob has. Already spawned mobs will not be affected by any changes. 1 health = half a heart.");
        this.boostDefense = config.getInt("Boosts", this.getCfgName("Defense"), this.boostDefense, "How much damage is blocked, minimum damage dealt is 1.");
        this.boostSpeed = config.getInt("Boosts", this.getCfgName("Speed"), this.boostSpeed, "Movement speed. Average speed is 28");
        this.boostDamage = config.getInt("Boosts", this.getCfgName("Damage"), this.boostDamage, "Damage dealt, both melee and ranged. 1 = half a heart.");
        this.boostHaste = config.getInt("Boosts", this.getCfgName("Haste"), this.boostHaste, "Attack and ability speeds in ticks. Average attack rate is 20 (1 second).");
        this.boostEffect = config.getInt("Boosts", this.getCfgName("Effect"), this.boostEffect, "Effect strengths and durations in ticks (20 ticks = 1 second).");
        this.boostPierce = config.getInt("Boosts", this.getCfgName("Pierce"), this.boostPierce, "Use to directly decrease or increase the piercing value. By default it is 5 so for every 5 damage dealt, 1 damage ignores armor. A positive boost lowers the attack required per armor piercing damage, therefore a boost of 2 will lower the piercing stat from 5 to 3.");

        config.setCategoryComment("Additional", "Here you can control the miscellaneous features of each mob.");
        this.sizeScale = config.getDouble("Additional", this.getCfgName("Size Scale"), this.sizeScale, "A custom scale to apply to the mob's actual size, the hitbox will also scale to match and will then scale again if a custom hitbox scale is set.");
        this.hitboxScale = config.getDouble("Additional", this.getCfgName("Hitbox Scale"), this.hitboxScale, "A custom scale to apply to the mob's hitbox, note that large hitboxes can affect pathing and hitboxes can't be smaller than half a block.");

        // Register Mob:
        this.registerMob();

        // Add Achievements:
        ItemStack achievementStack = new ItemStack(ObjectManager.getItem("mobtoken"));
        achievementStack.setTagInfo("Mob", new NBTTagString(this.name));
        ObjectManager.addStat(this.name + ".kill", new StatBase(this.name + ".kill", new TextComponentString(this.name + ".kill")));
        ObjectManager.addStat(this.name + ".learn", new StatBase(this.name + ".learn", new TextComponentString(this.name + ".learn")));
        if(this.isSummonable())
            ObjectManager.addStat(this.name + ".summon", new StatBase(this.name + ".summon", new TextComponentString(this.name + ".summon")));
        if(this.isTameable())
            ObjectManager.addStat(this.name + ".tame", new StatBase(this.name + ".tame", new TextComponentString(this.name + ".tame")));
    }


    // ==================================================
    //                    Register Mob
    // ==================================================
    /** Registers this mob to vanilla and custom entity lists. **/
    public void registerMob() {
    	// ID and Enabled Check:
		LycanitesMobs.printDebug("MobSetup", "~0==================== Mob Setup: "+ this.name + " [" + this.mobID +"] ====================0~");
		if(!this.mobEnabled) {
			LycanitesMobs.printDebug("MobSetup", "Mob Disabled: " + name + " - " + this.entityClass + " (" + group.name + ")");
		}
		
		// Mapping and Registration:
		if(!ObjectManager.entityLists.containsKey(this.group.filename))
			ObjectManager.entityLists.put(this.group.filename, new EntityListCustom());
		ObjectManager.entityLists.get(this.group.filename).addMapping(this.entityClass, this.getResourceLocation(), this.eggBackColor, this.eggForeColor);
		EntityRegistry.registerModEntity(this.getResourceLocation(), this.entityClass, name, this.mobID, this.group.mod, this.isBoss() ? 256 : 128, this.isBoss() ? 6 : 3, true);

		// Debug Message - Added:
		LycanitesMobs.printDebug("MobSetup", "Mob Added: " + name + " - " + this.entityClass + " (" + group.name + ")");
    }
	
	
	// ==================================================
    //                 Names and Titles
    // ==================================================
    public String getEntityID() {
        return this.name;
    }

	public String getRegistryName() {
		return this.group.filename + "." + this.name;
	}

    public ResourceLocation getResourceLocation() {
        if(this.resourceLocation == null)
            this.resourceLocation = new ResourceLocation(this.group.filename, this.name);
        return this.resourceLocation;
    }

    public String getCfgName(String configKey) {
        return this.getTitle() + " " + configKey;
    }
	
	public String getTitle() {
		return I18n.translateToLocal("entity." + this.getRegistryName() + ".name");
	}
	
	public String getDescription() {
		return I18n.translateToLocal("entity." + this.getRegistryName() + ".description");
	}
	
	
	// ==================================================
    //                        Icon
    // ==================================================
	public ResourceLocation getIcon() {
		ResourceLocation texture = AssetManager.getTexture(this.name + "_icon");
		if(texture == null) {
			AssetManager.addTexture(this.name + "_icon", this.group, "textures/guis/" + this.name.toLowerCase() + "_icon.png");
			texture = AssetManager.getTexture(this.name + "_icon");
		}
		return texture;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
    // ========== Peaceful ==========
    public MobInfo setPeaceful(boolean bool) {
        this.peacefulDifficulty = bool;
        return this;
    }

    // ========== Dungeon Themes ==========
    /**
     * Sets the default dungeon themes.
     * @param themes An array of Strings for each theme. Themes are: FOREST, PLAINS, MOUNTAIN, SWAMP, WATER , DESERT, WASTELAND, JUNGLE, FROZEN, NETHER, END, MUSHROOM, MAGICAL, DUNGEON, NECRO, URBAN, FIERY, SHADOW, PARADISE
	 * @return MobInfo INSTANCE for chaining.
     */
    public MobInfo setDungeonThemes(String themes) {
        this.dungeonThemes = themes;
        return this;
    }

    // ========== Mob Level ==========
    /**
     * Sets the mob's level, used by DLDungeons.
     * @param level The dungeon level: -1 = Not for Dungeons, 0 = Common, 1 = Tough, 2 = Brute, 3 = Elite
     * @return
     */
    public MobInfo setDungeonLevel(int level) {
        this.dungeonLevel = level;
        return this;
    }

    // ========== Summon Cost ==========
    public MobInfo setSummonCost(int integer) {
        this.summonCost = integer;
        return this;
    }

	// ========== Summonable ==========
	public MobInfo setSummonable(boolean summonable) {
		if(summonable && !summonableCreatures.contains(this.name))
			summonableCreatures.add(this.name);
		if(!summonable)
			summonableCreatures.remove(this.name);
		return this;
	}
	
	public boolean isSummonable() {
		return summonableCreatures.contains(this.name);
	}

    // ========== Tameable ==========
    public MobInfo setTameable(boolean tameable) {
        if(tameable && !tameableCreatures.contains(this.name))
            tameableCreatures.add(this.name);
        if(!tameable)
            tameableCreatures.remove(this.name);
        return this;
    }

    public boolean isTameable() {
        if(!tamingEnabled)
            return false;
        return tameableCreatures.contains(this.name);
    }

    // ========== Boss ==========
    public MobInfo setBoss(boolean bool) {
        this.boss = bool;
        return this;
    }
    public boolean isBoss() {
        return this.boss;
    }

	// ========== Dummy ==========
	public MobInfo setDummy(boolean bool) {
		this.dummy = bool;
		return this;
	}
	
	/*/ ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
		return this;
	}*/


    // ==================================================
    //                     Subspecies
    // ==================================================
    // ========== Add Subspecies ==========
    public MobInfo addSubspecies(Subspecies newSubspecies) {
        this.subspeciesAmount++;
        if(newSubspecies != null) {
            newSubspecies.mobInfo = this;
            newSubspecies.index = this.subspeciesAmount;
        }
        this.subspecies.put(this.subspeciesAmount, newSubspecies);
        return this;
    }

    // ========== Get Subspecies ==========
    public Subspecies getSubspecies(int index) {
        return this.subspecies.get(index);
    }

    // ========== Get Random Subspecies ==========
    /**
     * Gets a random subspecies, normally used by a new mob when spawned.
     * @param entity The entity that has this subspecies.
     * @param rare If true, there will be much higher odds of a subspecies being picked.
     * @return A Subspecies or null if using the base species.
     */
    public Subspecies getRandomSubspecies(EntityLivingBase entity, boolean rare) {
    	LycanitesMobs.printDebug("Subspecies", "~0===== Subspecies =====0~");
    	LycanitesMobs.printDebug("Subspecies", "Selecting random subspecies for: " + entity);
        if(rare)
            LycanitesMobs.printDebug("Subspecies", "The conditions have been set to rare increasing the chances of a subspecies being picked.");
    	if(this.subspeciesAmount < 1) {
        	LycanitesMobs.printDebug("Subspecies", "No species available, will be base species.");
    		return null;
    	}
    	LycanitesMobs.printDebug("Subspecies", "Subspecies Available: " + this.subspeciesAmount);
    	
        // Get Weights:
        int baseSpeciesWeightScaled = Subspecies.baseSpeciesWeight;
        if(rare)
            baseSpeciesWeightScaled /= 4;
        int totalWeight = baseSpeciesWeightScaled;
        for(Subspecies subspeciesEntry : this.subspecies.values()) {
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
        for(Subspecies subspeciesEntry : this.subspecies.values()) {
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


    // ========== Get Child Subspecies ==========
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
