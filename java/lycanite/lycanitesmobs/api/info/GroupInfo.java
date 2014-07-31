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
    /** A comma separated list of dimensions that this mob spawns in. As read from the config **/
    public String dimensionEntries = "";

	/** The list of dimension IDs that this mob spawns in. **/
	public int[] dimensionIDs = new int[0];
	
	/** Extra dimension type info, can contain values such as ALL or VANILLA. **/
	public String[] dimensionTypes = new String[0];

    // ========== Spawn Biomes ==========
    /** The list of biomes that this mob spawns in. As read from the config. **/
    public String biomeEntries = "";
	
	/** The list of biomes that this mob spawns in. Use this to get the biomes not biomeTypes. **/
	public BiomeGenBase[] biomes = new BiomeGenBase[0];


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
    public void loadFromConfig() {
    	if("lycanitesmobs".equalsIgnoreCase(this.filename))
    		return;
    	
    	ConfigSpawning config = ConfigSpawning.getConfig(this, "spawning");
    	
    	// Spawn Dimensions:
        config.setCategoryComment("Spawn Dimensions", "Sets which dimensions mobs spawn in. You may enter dimension IDs or tags such as: ALL, VANILLA or GROUP. Multiple entries should be comma separated.");
        SpawnDimensionSet spawnDimensions = config.getDimensions("Spawn Dimensions", this.getCfgName("Spawn Dimensions"), this.dimensionEntries);

        // Spawn Biomes:
		config.setCategoryComment("Spawn Biomes", "Sets which biomes this mob spawns in using Biome Tags. Multiple entries should be comma separated and can be subtractive if provided with a - in front.");
		this.biomes = config.getBiomes("Spawn Biomes", this.getCfgName("Spawn Biomes"), this.biomeEntries);
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
}
