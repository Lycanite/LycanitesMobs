package lycanite.lycanitesmobs.api.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lycanite.lycanitesmobs.AssetManager;
import lycanite.lycanitesmobs.Config;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.ILycaniteMod;
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
	
	// ========== Per Mob Settings ==========
	/** Mod Class **/
	public ILycaniteMod mod;
	
	/** The name of this mob used by the ObjectManager and Config maps. Should be all lower case **/
	public String name = "mobname";
	
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
	
	/** How many charges this creature normally costs to summon. **/
	public int summonCost = 1;
	
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
		mobsAttackVillagers = config.getFeatureBool("MobsAttackVillagers");
		
		difficultyMutlipliers = new HashMap<String, Double>(config.difficultyMultipliers);
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
	public MobInfo(ILycaniteMod mod, String name, Class entityClass, int eggBack, int eggFore, int summonCost) {
		this.mod = mod;
		
		this.name = name;
		
		Config config = mod.getConfig();
		this.mobEnabled = config.mobsEnabled.containsKey(name) ? mod.getConfig().mobsEnabled.get(name) : false;
		this.peacefulDifficulty = config.mobsPeaceful.get(name);
		this.spawnInfo = new SpawnInfo(this);
		this.entityClass = entityClass;
		
		this.eggBackColor = eggBack;
		this.eggForeColor = eggFore;
		
		this.summonCost = summonCost;
		
		// Load Item Drops:
		this.defaultDrops = config.defaultDrops.get(name);
		String customDropsString = config.customDrops.get(name).replace(" ", "");
		if(customDropsString != null && customDropsString.length() > 0) {
    		for(String customDropEntryString : customDropsString.split(",")) {
				String[] customDropValues = customDropEntryString.split(":");
				if(customDropValues.length >= 2) {
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
		mobNameToInfo.put(this.name, this);
	}
	
	
	// ==================================================
    //                 Names and Titles
    // ==================================================
	public String getRegistryName() {
		return this.mod.getModID() + "." + this.name;
	}
	
	public String getTitle() {
		return StatCollector.translateToLocal("entity." + getRegistryName() + ".name");
	}
	
	
	// ==================================================
    //                        Icon
    // ==================================================
	public ResourceLocation getIcon() {
		ResourceLocation texture = AssetManager.getTexture(this.name + "_icon");
		if(texture == null) {
			AssetManager.addTexture(this.name + "_icon", this.mod.getDomain(), "textures/guis/" + this.name.toLowerCase() + "_icon.png");
			texture = AssetManager.getTexture(this.name + "_icon");
		}
		return texture;
	}
	
	
    // ==================================================
    //                        Set
    // ==================================================
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
	}*/
}
