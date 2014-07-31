package lycanite.lycanitesmobs.api.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;


public class MobInfo {
	// ========== Global Settings ==========
	/** A map containing a Class to MobInfo connection where a shared MobInfo for each mob can be obtained using the mob's class as a key. **/
	public static Map<Class, MobInfo> mobClassToInfo = new HashMap<Class, MobInfo>();
	
	/** A map containing a (Code) Name to MobInfo connection where a shared MobInfo for each mob can be obtained using the mob's codename as a key. **/
	public static Map<String, MobInfo> mobNameToInfo = new HashMap<String, MobInfo>();
	
	/** If true, the name of a pet's owner will be shown in it's name tag. **/
	public static boolean ownerTags = true;
	
	/** Whether mob taming is allowed. **/
	public static boolean tamingEnabled = true;
	
	/** Whether mob mounting is allowed. **/
	public static boolean mountingEnabled = true;
	
	/** If true, all mobs that attack players will also attack villagers. **/
	public static boolean mobsAttackVillagers = false;
	
	/** If true, predators such as Ventoraptors will attack farm animals such as Sheep or Makas. **/
	public static boolean predatorsAttackAnimals = true;
	
	/** A static map containing all the global multipliers for each stat for each difficulty. They defaults are Easy: 0.5, Normal: 1.0 and Hard: 1.5. **/
	public static Map<String, Double> difficultyMutlipliers = new HashMap<String, Double>();
	
	/** A static ArrayList of all summonable creature names. **/
	public static List<String> summonableCreatures = new ArrayList<String>();
	
	/** A static Name to Instance map of all mob groups. **/
	public static Map<String, GroupInfo> mobGroups = new HashMap<String, GroupInfo>();
	
	// ========== Per Mob Settings ==========
	/** Mod Group **/
	public GroupInfo group;
	
	/** The name of this mob used by the ObjectManager and Config maps. Should be all lower case **/
	public String name = "mobname";
	
	/** Is this mob enabled? If disabled, it will still be registered, etc but wont randomly spawn or have a spawn egg. **/
	public boolean mobEnabled;
	
	/** The class that this mob instantiates with. **/
	public Class entityClass;

	/** The SpawnInfo used by this mob. **/
	public SpawnInfo spawnInfo;
	
	/** The background color of this mob's egg. **/
	public int eggBackColor;	
	
	/** The foreground color of this mob's egg. **/
	public int eggForeColor;
	
	/** If false, the default drops for this mob will be disabled, useful if the config needs to completely take over on what mobs drop. **/
	public boolean defaultDrops = true;
	
	/** A list of all the custom item drops this mob should drop, readily parsed from the config. To be safe, this list should be copied into the entity instance. **/
	public List<DropRate> customDrops = new ArrayList<DropRate>();

    // ========== Per Mob Settings With Varied Defaults ==========
    /** If true, this mob is allowed in Peaceful Difficulty. **/
    public boolean peacefulDifficulty;
	
	/** How many charges this creature normally costs to summon. **/
	public int summonCost = 1;

    /** Dungeon themes for this mob, used by Doomlike Dungeons. **/
    public String dungeonThemes = "";
	
	// ========== Per Mob Stats ==========
	public double multiplierDefense = 1.0D;
	public double multiplierSpeed = 1.0D;
	public double multiplierDamage = 1.0D;
	public double multiplierHaste = 1.0D;
	public double multiplierEffect = 1.0D;
	
	public int boostDefense = 0;
	public int boostSpeed = 0;
	public int boostDamage = 0;
	public int boostHaste = 0;
	public int boostEffect = 0;
	
	///** The model used by this mob. NOTE: This is currently unused, see AssetManager.getModel() **/
	//public ModelBase model;
	
    // ==================================================
    //        Load Global Settings From Config
    // ==================================================
	public static void loadGlobalSettings() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
		ownerTags = config.getBool("Pets", "Owner Tags", ownerTags, "If true, tamed mobs will display their owner's name in their name tag.");
		tamingEnabled = config.getBool("Pets", "Taming", tamingEnabled, "Set to false to disable pet/mount taming.");
		mountingEnabled = config.getBool("Pets", "Mounting", mountingEnabled, "Set to false to disable mounts.");

        predatorsAttackAnimals = config.getBool("Mob Interaction", "Predators Attack Animals", predatorsAttackAnimals, "Set to false to prevent predator mobs from attacking animals/farmable mobs.");
		mobsAttackVillagers = config.getBool("Mob Interaction", "Mobs Attack Villagers", mobsAttackVillagers, "Set to false to prevent mobs that attack players from also attacking villagers.");

        String[] difficultyNames = new String[] {"Easy", "Normal", "Hard"};
        double[] difficultyDefaults = new double[] {0.5D, 1.0D, 1.5D};
        String[] statNames = new String[] {"Defense", "Speed", "Damage", "Haste", "Effect"};
		difficultyMutlipliers = new HashMap<String, Double>();
        config.setCategoryComment("Difficulty Multipliers", "Here you can scale te stats of every mob on a per difficulty basis. Note that on easy, speed is kept at 100% as 0.5 makes them stupidly slow.");
        int difficultyIndex = 0;
        for(String difficultyName : difficultyNames) {
            for(String statName : statNames) {
                double defaultValue = difficultyDefaults[difficultyIndex];
                if("Easy".equalsIgnoreCase(difficultyName) && "Speed".equalsIgnoreCase(statName))
                    defaultValue = 1.0D;
                difficultyMutlipliers.put((difficultyName + "-" + statName).toUpperCase(), config.getDouble("Difficulty Multipliers", difficultyName + " " + statName, defaultValue));
            }
            difficultyIndex++;
        }
    }
	
	/**
	 * Get MobInfo from mob name.
	 * @param mobName The name of the mob, such as "trite", should be lower case, no spaces.
	 * @return
	 */
	public static MobInfo getFromName(String mobName) {
		mobName = mobName.toLowerCase();
		if(!mobNameToInfo.containsKey(mobName))
			return null;
		return mobNameToInfo.get(mobName);
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
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(GroupInfo group, String name, Class entityClass, int eggBack, int eggFore) {
		this.group = group;
		if(!mobGroups.containsKey(group.filename))
			mobGroups.put(group.filename, group);

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
    public void loadFromConfig() {
        // General Info:
        ConfigBase config = ConfigBase.getConfig(this.group, "general");
        this.mobEnabled = config.getBool("Enabled Mobs", this.getCfgName("Enabled"), this.mobEnabled, "Set to false to disable.");
        this.peacefulDifficulty = config.getBool("Peaceful Mobs", this.getCfgName("On Peaceful"), this.peacefulDifficulty, "Set to true to allow in peaceful.");
        this.summonCost = config.getInt("Summoning Costs", this.getCfgName("Summoning Cost"), this.summonCost, "How much focus to summon.");
        this.dungeonThemes = config.getString("Dungeon Themes", this.getCfgName("Themes"), this.dungeonThemes, "Sets the themes, currently only used by Doomlike Dungeons.");

        // Load Item Drops:
        config.setCategoryComment("Default Item Drops", "If false, only custom item drops are dropped.");
        this.defaultDrops = config.getBool("Default Item Drops", this.getCfgName("Default Drops"), this.defaultDrops);
        config.setCategoryComment("Custom Item Drops", "Allows for custom items drops per mob. Format is: mod:item;metadata;chance;min;max Multiple drops should be comma separated. minecraft:wool;2;0.25;0;3 is Green Wool with a 25% drop rate and will drop 0 to 3 blocks. Be sure to use a colon for mod:item and semicolons for everything else.");
        String customDropsString = config.getString("Custom Item Drops", this.getCfgName("Custom Drops"), "");
        if(customDropsString != null && customDropsString.length() > 0) {
            for(String customDropEntryString : customDropsString.split(";")) {
                String[] customDropValues = customDropEntryString.split(",");
                if(customDropValues.length >= 5) {
                    String dropName = customDropValues[0];
                    int dropMeta = Integer.parseInt(customDropValues[1]);
                    float dropChance = Float.parseFloat(customDropValues[2]);
                    int dropMin = Integer.parseInt(customDropValues[3]);
                    int dropMax = Integer.parseInt(customDropValues[4]);
                    ItemStack drop = null;
                    if(Item.itemRegistry.getObject(dropName) != null)
                        drop = new ItemStack((Item)Item.itemRegistry.getObject(dropName), 1, dropMeta);
                    else if(Block.blockRegistry.getObject(dropName) != null)
                        drop = new ItemStack((Block)Block.blockRegistry.getObject(dropName), 1, dropMeta);
                    this.customDrops.add(new DropRate(drop, dropChance).setMinAmount(dropMin).setMaxAmount(dropMax));
                }
            }
        }

        // Load Stats:
        config = ConfigBase.getConfig(this.group, "stats");
        config.setCategoryComment("Multipliers", "Here you can scale each mob stat,for instance setting 2 will double the stat, setting 0.5 will half it.");
        this.multiplierDefense = config.getDouble("Multipliers", this.getCfgName("Defense"), this.multiplierDefense, "How much damage is blocked, minimum damage dealt is 1.");
        this.multiplierSpeed = config.getDouble("Multipliers", this.getCfgName("Speed"), this.multiplierSpeed, "Movement speed.");
        this.multiplierDamage = config.getDouble("Multipliers", this.getCfgName("Damage"), this.multiplierDamage, "Damage dealt, both melee and ranged.");
        this.multiplierHaste = config.getDouble("Multipliers", this.getCfgName("Haste"), this.multiplierHaste, "Attack and ability speeds.");
        this.multiplierEffect = config.getDouble("Multipliers", this.getCfgName("Effect"), this.multiplierEffect, "Effect strengths and durations.");

        config.setCategoryComment("Boosts", "Here you can increase or decrease each stat by a specific amount. (Use a negative number to decrease.)");
        this.boostDefense = config.getInt("Boosts", this.getCfgName("Defense"), this.boostDefense, "How much damage is blocked, minimum damage dealt is 1.");
        this.boostSpeed = config.getInt("Boosts", this.getCfgName("Speed"), this.boostSpeed, "Movement speed. Average speed is 28");
        this.boostDamage = config.getInt("Boosts", this.getCfgName("Damage"), this.boostDamage, "Damage dealt, both melee and ranged. 1 = half a heart.");
        this.boostHaste = config.getInt("Boosts", this.getCfgName("Haste"), this.boostHaste, "Attack and ability speeds in ticks. Average attack rate is 20 (1 second).");
        this.boostEffect = config.getInt("Boosts", this.getCfgName("Effect"), this.boostEffect, "Effect strengths and durations in ticks (20 ticks = 1 second).");

        // Spawn Info:
        this.spawnInfo.loadFromConfig();
    }
	
	
	// ==================================================
    //                 Names and Titles
    // ==================================================
	public String getRegistryName() {
		return this.group.filename + "." + this.name;
	}

    public String getCfgName(String configKey) {
        return this.getTitle() + " " + configKey;
    }
	
	public String getTitle() {
		return StatCollector.translateToLocal("entity." + getRegistryName() + ".name");
	}
	
	public String getDescription() {
		return StatCollector.translateToLocal("entity." + getRegistryName() + ".description");
	}
	
	
	// ==================================================
    //                        Icon
    // ==================================================
	public ResourceLocation getIcon() {
		ResourceLocation texture = AssetManager.getTexture(this.name + "_icon");
		if(texture == null) {
			AssetManager.addTexture(this.name + "_icon", this.group.filename, "textures/guis/" + this.name.toLowerCase() + "_icon.png");
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
    public MobInfo setDungeonThemes(String string) {
        this.dungeonThemes = string;
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
	
	/*/ ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
		return this;
	}*/
}
