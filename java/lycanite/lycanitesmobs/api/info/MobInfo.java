package lycanite.lycanitesmobs.api.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ILycaniteMod;


public class MobInfo {
	// ========== Global Settings ==========
	/** A map containing a Class to MobInfo connection where a shared MobInfo for each mob can be obtained using the mob's class as a key. **/
	public static Map<Class, MobInfo> mobClassToInfo = new HashMap<Class, MobInfo>();
	
	/** If true, the name of a pet's owner will be shown in it's name tag. **/
	public static boolean ownerTags = true;
	
	/** Whether mob taming is allowed. **/
	public static boolean tamingEnabled = true;
	
	/** Whether mob mounting is allowed. **/
	public static boolean mountingEnabled = true;
	
	/** If true, predators such as Ventoraptors will attack farm animals such as Sheep or Makas. **/
	public static boolean predatorsAttackAnimals = true;
	
	/** A static map containing all the global multipliers for each stat for each difficulty. They defaults are Easy: 0.5, Normal: 1.0 and Hard: 1.5. **/
	public static Map<String, Double> difficultyMutlipliers = new HashMap<String, Double>();
	
	// ========== Per Mob Settings ==========
	/** Mod Class **/
	public ILycaniteMod mod;
	
	/** The name of this mob used by the ObjectManager and Config maps. **/
	public String name = "MobName";
	
	/** The title used by this mob for displaying in game.. **/
	public String title = "Mob Name";
	
	/** Is this mob enabled? If disabled, it will still be registered, etc but wont randomly spawn or have a spawn egg. **/
	public boolean mobEnabled;
	
	/** If true, this mob is allowed in Peaceful Difficulty. **/
	public boolean peacefulDifficulty;
	
	/** The class that this mob instantiates with. **/
	public Class entityClass;

	/** The SpawnInfo used by this mob. **/
	public SpawnInfo spawnInfo;
	
	/** The background color of this mob's egg. **/
	public int eggBackColor;	
	
	/** The foreground color of this mob's egg. **/
	public int eggForeColor;
	
	/** If false, the default drops for this mob will be disabled, useful if the config needs to completely take over on what mobs drop. **/
	public boolean defaultDrops = false;
	
	/** A list of all the custom item drops this mob should drop, readily parsed from the config. To be safe, this list should be copied into the entity instance. **/
	public List<DropRate> customDrops = new ArrayList<DropRate>();
	
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
		Config config = LycanitesMobs.config;
		ownerTags = config.getFeatureBool("OwnerTags");
		tamingEnabled = config.getFeatureBool("MobTaming");
		mountingEnabled = config.getFeatureBool("MobMounting");
		predatorsAttackAnimals = config.getFeatureBool("PredatorsAttackAnimals");
		
		difficultyMutlipliers = new HashMap<String, Double>(config.difficultyMultipliers);
	}
	
	
    // ==================================================
    //                     Constructor
    // ==================================================
	public MobInfo(ILycaniteMod mod, String name, String title, Class entityClass, int eggBack, int eggFore) {
		this.mod = mod;
		
		this.name = name;
		this.title = title;
		
		Config config = mod.getConfig();
		this.mobEnabled = config.mobsEnabled.containsKey(name) ? mod.getConfig().mobsEnabled.get(name) : false;
		this.peacefulDifficulty = config.mobsPeaceful.get(name);
		this.spawnInfo = new SpawnInfo(this);
		this.entityClass = entityClass;
		
		this.eggBackColor = eggBack;
		this.eggForeColor = eggFore;
		
		// Load Item Drops:
		this.defaultDrops = config.defaultDrops.get(name);
		String customDropsString = config.customDrops.get(name).replace(" ", "");
		if(customDropsString != null && customDropsString.length() > 0)
    		for(String customDropEntryString : customDropsString.split(",")) {
    			String[] customDropValues = customDropEntryString.split(":");
    			if(customDropValues.length == 5) {
					int dropID = Integer.parseInt(customDropValues[0]);
					int dropMeta = Integer.parseInt(customDropValues[1]);
					float dropChance = Float.parseFloat(customDropValues[2]);
					int dropMin = Integer.parseInt(customDropValues[3]);
					int dropMax = Integer.parseInt(customDropValues[4]);
					this.customDrops.add(new DropRate(dropID, dropMeta, dropChance).setMinAmount(dropMin).setMaxAmount(dropMax));
    			}
    		}
		
		// Load Stats:
		this.multiplierDefense = config.defenseMultipliers.get(name);
		this.multiplierSpeed = config.speedMultipliers.get(name);
		this.multiplierDamage = config.damageMultipliers.get(name);
		this.multiplierHaste = config.hasteMultipliers.get(name);
		this.multiplierEffect = config.effectMultipliers.get(name);

		this.boostDefense = config.defenseBoosts.get(name);
		this.boostSpeed = config.speedBoosts.get(name);
		this.boostDamage = config.damageBoosts.get(name);
		this.boostHaste = config.hasteBoosts.get(name);
		this.boostEffect = config.effectBoosts.get(name);
		
		mobClassToInfo.put(entityClass, this);
	}
	
	// For most mobs where the code name and title are the same (no spaces, etc).
	public MobInfo(ILycaniteMod mod, String setName, Class setClass, int setEggBack, int setEggFore) {
		this(mod, setName, setName, setClass, setEggBack, setEggFore);
	}
	
	
	// ==================================================
    //                 Names and Titles
    // ==================================================
	public String getRegistryName() {
		return this.mod.getModID() + "." + this.name;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
	/*/ ========== Model ==========
	public void setModel(ModelBase setModel) {
		model = setModel;
	}*/
}
