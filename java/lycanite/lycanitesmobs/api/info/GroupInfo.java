package lycanite.lycanitesmobs.api.info;

import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.config.ConfigSpawning.SpawnDimensionSet;
import net.minecraft.world.biome.BiomeGenBase;



public class GroupInfo {

    // ========== Group General ==========
	/** The mod this group belongs to. **/
	public Object mod;
	
    /** The name of this group, normally displayed in the config. **/
    public String name;

    /** The filename of this group, used for assets, config, etc. This should usually match the sub-mod ID. **/
    public String filename;
    
    // ========== Mob IDs ==========
    /** The next available ID for registering a mob. **/
    public int nextMobID = 0;

    /** The next available ID for registering a projectile. **/
    public int nextProjectileID = 100;
	
	// ========== Spawn Dimensions ==========
    /** A comma separated list of dimensions that mobs in this group spawn in. As read from the config **/
    public String dimensionEntries = "";

	/** The list of dimension IDs that mobs in this group spawn. **/
	public int[] dimensionIDs = new int[0];
	
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes = new String[0];

    // ========== Spawn Biomes ==========
    /** The list of biomes that mobs in this group spawn. As read from the config. Store biome tags and special tags. **/
    public String biomeEntries = "";
	
	/** The list of biomes that mobs in this group spawn. Use this stores the actual biomes not biome tags. **/
	public BiomeGenBase[] biomes = new BiomeGenBase[0];
	
	// ========== Vanilla Controls ==========
	/** If true, this group will edit the vanilla mob spawns a little bit. **/
	public boolean controlVanillaSpawns = true;


    // ==================================================
    //                     Constructor
    // ==================================================
    public GroupInfo(Object mod, String name) {
    	this.mod = mod;
        this.name = name;
        this.filename = name.toLowerCase().replace(" ", "");
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
		this.controlVanillaSpawns = config.getBool("Vanilla Spawning", "Edit Vanilla Spawning", true, "If true, some vanilla spawns in this biome will be removed, note that vanilla mobs should still be easy to find, only they will be more biome specific.");
    }
    
    /** Loads all spawning settings, should be called in the init and not pre-init so that the biomes can all be registered in time. **/
    public void loadSpawningFromConfig() {
    	if("lycanitesmobs".equalsIgnoreCase(this.filename))
    		return;
    	
    	ConfigSpawning config = ConfigSpawning.getConfig(this, "spawning");
        config.setCategoryComment("Group Settings", "Here you can set the spawning settings for all mobs in this group that use the GROUP tag.");

        // Spawn Dimensions:
        SpawnDimensionSet spawnDimensions = config.getDimensions("Group Settings", this.getCfgName("Spawn Dimensions"), this.dimensionEntries, "Sets which dimensions mobs spawn in. You may enter dimension IDs or tags such as: ALL, VANILLA or GROUP. Multiple entries should be comma separated.");
        this.dimensionIDs = spawnDimensions.dimensionIDs;
        this.dimensionTypes = spawnDimensions.dimensionTypes;

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


    // ==================================================
    //                    Set Defaults
    // ==================================================
    // ========== Spawn Location ==========
    public GroupInfo setDimensions(String string) {
        this.dimensionEntries = string;
        return this;
    }

    public GroupInfo setBiomes(String string) {
        this.biomeEntries = string;
        return this;
    }
}
