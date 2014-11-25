package lycanite.lycanitesmobs.api.info;

import java.util.HashMap;
import java.util.Map;

import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.config.ConfigSpawning.SpawnDimensionSet;
import net.minecraft.world.biome.BiomeGenBase;


public class GroupInfo {
	/** A map containing all groups by their name. **/
	public static Map<String, GroupInfo> groups = new HashMap<String, GroupInfo>();

    // ========== Group General ==========
	/** The mod this group belongs to. **/
	public Object mod;
	
    /** The name of this group, normally displayed in the config. **/
    public String name;

    /** The filename of this group, used for assets, config, etc. This should usually match the sub-mod ID. **/
    public String filename;

    /** The name of the egg item this group uses. **/
    public String eggName = "spawnegg";
    
    // ========== Mob IDs ==========
    /** The next available ID for registering a mob. **/
    public int nextMobID = 0;

    /** The next available ID for registering a projectile. **/
    public int nextProjectileID = 100;
	
	// ========== Spawn Dimensions ==========
    /** A comma separated list of dimensions that mobs in this group spawn in. As read from the config **/
    public String dimensionEntries = "";

	/** A blacklist of dimension IDs (changes to whitelist if dimensionWhitelist is true) that this mob spawns in. **/
	public int[] dimensionBlacklist;
	
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes;
	
	/** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
	public boolean dimensionWhitelist = false;

    // ========== Spawn Biomes ==========
    /** The list of biomes that mobs in this group spawn. As read from the config. Store biome tags and special tags. **/
    public String biomeEntries = "";
	
	/** The list of biomes that mobs in this group spawn. Use this stores the actual biomes not biome tags. **/
	public BiomeGenBase[] biomes = new BiomeGenBase[0];
	
	// ========== Vanilla Controls ==========
	/** If true, this group will edit the vanilla mob spawns a little bit. **/
	public boolean controlVanillaSpawns = true;

	// ========== Dungeon Themes ==========
    /** Dungeon themes for this mob, used by Doomlike Dungeons. **/
    public String dungeonThemes = "";


    // ==================================================
    //                     Static Init
    // ==================================================
    /** Should be called in the init before MobInfos have their spawning loaded. GroupInfos registered to the groups map should be automatically called.**/
    public static void loadAllSpawningFromConfigs() {
    	for(GroupInfo group : groups.values())
    		group.loadSpawningFromConfig();
    }


    // ==================================================
    //                     Constructor
    // ==================================================
    public GroupInfo(Object mod, String name) {
    	this.mod = mod;
        this.name = name;
        this.filename = name.toLowerCase().replace(" ", "");
        
        groups.put(this.name, this);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    /** Can be used to load all pre-init config settings. **/
    public void loadFromConfig() {
    	if("lycanitesmobs".equalsIgnoreCase(this.filename))
    		return;
    	
    	ConfigSpawning config = ConfigSpawning.getConfig(this, "spawning");
        config.setCategoryComment("Group Settings", "Here you can set the spawning settings for all mobs in this group that use the GROUP tag.");
        
        // Vanilla Controls:
		config = ConfigSpawning.getConfig(this, "general");
		config.setCategoryComment("Vanilla Spawning", "Here you may control settings that affect vanilla Minecraft.");
		this.controlVanillaSpawns = config.getBool("Vanilla Spawning", "Edit Vanilla Spawning", true, "If true, some vanilla spawns in this group's biomes will be removed, note that vanilla mobs should still be easy to find, only they will be more biome specific.");
		
		// Dungeon Themes:
		this.dungeonThemes = config.getString("Group Settings", this.getCfgName("Themes"), this.dungeonThemes, "Here you can set the Dungeon Theme of this mob group. These are used by Doomlike Dungeons and might be used by other things later. Multiple entries should be comma seperated.");
    }
    
    /** Loads all spawning settings, should be called in the init and not pre-init so that the biomes can all be registered in time. **/
    public void loadSpawningFromConfig() {
    	if("lycanitesmobs".equalsIgnoreCase(this.filename))
    		return;
    	
    	ConfigSpawning config = ConfigSpawning.getConfig(this, "spawning");
        config.setCategoryComment("Group Settings", "Here you can set the spawning settings for all mobs in this group that use the GROUP tag.");

        // Spawn Dimensions:
        SpawnDimensionSet spawnDimensions = config.getDimensions("Group Settings", this.getCfgName("Spawn Dimensions"), this.dimensionEntries, "Sets which dimensions (by ID) that mobs WILL NOT spawn in. However if 'Spawn Dimensions Whitelist Mode' is set to true, it will instead set which dimensions they WILL ONLY spawn in. Multiple entries should be comma separated. Note that some Spawn Types ignore this such as the PORTAL type.");
        this.dimensionBlacklist = spawnDimensions.dimensionIDs;
        this.dimensionTypes = spawnDimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("Group Settings", this.getCfgName("Spawn Dimensions Whitelist Mode"), this.dimensionWhitelist, "If true, the 'Spawn Dimensions' list will act as a whitelist instead of a blacklist.");

        // Spawn Biomes:
		this.biomes = config.getBiomes("Group Settings", this.getCfgName("Spawn Biomes"), this.biomeEntries, "Sets which biomes this mob spawns in using Biome Tags. Multiple entries should be comma separated and can be subtractive if provided with a - in front.");
	}


    // ==================================================
    //                      Names
    // ==================================================
    public String getCfgName(String configKey) {
        return this.name + " " + configKey;
    }


    // ==================================================
    //                    Entity IDs
    // ==================================================
    public int getNextMobID() {
        int id = this.nextMobID;
        this.nextMobID++;
        return id;
    }

    public int getNextProjectileID() {
        int id = this.nextProjectileID;
        this.nextProjectileID++;
        return id;
    }

    public String getEggName() {
        return this.eggName;
    }


    // ==================================================
    //                    Set Defaults
    // ==================================================
    // ========== Spawn Location ==========
    public GroupInfo setDimensionBlacklist(String string) {
        this.dimensionEntries = string;
        return this;
    }
    
    public GroupInfo setDimensionWhitelist(boolean bool) {
        this.dimensionWhitelist = bool;
        return this;
    }

    public GroupInfo setBiomes(String string) {
        this.biomeEntries = string;
        return this;
    }
    
    /**
     * Sets the default dungeon themes.
     * @param string An array of Strings for each theme. Themes are: FOREST, PLAINS, MOUNTAIN, SWAMP, WATER, DESERT, WASTELAND, JUNGLE, FROZEN, NETHER, END, MUSHROOM, MAGICAL, DUNGEON, NECRO, URBAN, FIERY, SHADOW, PARADISE
	 * @return MobInfo instance for chaining.
     */
    public GroupInfo setDungeonThemes(String string) {
        this.dungeonThemes = string;
        return this;
    }

    public GroupInfo setEggName(String eggName) {
        this.eggName = eggName;
        return this;
    }
}
