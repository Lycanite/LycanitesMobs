package lycanite.lycanitesmobs.arcticmobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;

import java.util.Random;

public class WorldGenOozeLakes extends WorldGenLakes {
    public String name = "Ooze Lakes";
    public GroupInfo group = ArcticMobs.group;
    public double generateChance = 0.04D;

    // Dimensions:
    /** A comma separated list of dimensions that this event can occur in. As read from the config **/
    public String dimensionEntries = "-1, 1";
    /** A blacklist of dimension IDs (changes to whitelist if dimensionWhitelist is true) that this event can occur in. **/
    public int[] dimensionBlacklist;
    /** Extra dimension type info, can contain values such as ALL or VANILLA. **/
    public String[] dimensionTypes;
    /** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
    public boolean dimensionWhitelist = false;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGenOozeLakes() {
        super(ObjectManager.getBlock("ooze"));

        ConfigSpawning config = ConfigSpawning.getConfig(this.group, "worldgen");

        config.setCategoryComment("WorldGen Chances", "The chance that each worldgen will generate. You can set this to 0 to disable the worldgen or use the dimension black/white list. 1 = all over, 0.04 fairly rare.");
        this.generateChance = config.getDouble("WorldGen Chances", this.name + " Chance", this.generateChance, "In COLD and SNOWY tagged biomes, this chance becomes 1 and lakes can appear above ground.");

        ConfigSpawning.SpawnDimensionSet dimensions = config.getDimensions("WorldGen Dimensions", this.name + " Dimensions", this.dimensionEntries);
        this.dimensionBlacklist = dimensions.dimensionIDs;
        this.dimensionTypes = dimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("WorldGen Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
    }


    // ==================================================
    //                    Generate
    // ==================================================
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        if(this.generateChance <= 0)
            return false;
        if(!this.isValidDimension(world))
            return false;
        return super.generate(world, random, x, y, z);
    }


    // ==================================================
    //                 Is Valid Dimension
    // ==================================================
    public boolean isValidDimension(World world) {
        boolean validDimension = false;
        // Check Types:
        for(String eventDimensionType : this.dimensionTypes) {
            if("ALL".equalsIgnoreCase(eventDimensionType)) {
                validDimension = true;
            }
            else if("VANILLA".equalsIgnoreCase(eventDimensionType)) {
                validDimension = world.provider.dimensionId > -2 && world.provider.dimensionId < 2;
            }
        }

        // Check IDs:
        if(!validDimension) {
            validDimension =  !this.dimensionWhitelist;
            for(int eventDimension : this.dimensionBlacklist) {
                if(world.provider.dimensionId == eventDimension) {
                    validDimension = this.dimensionWhitelist;
                    break;
                }
            }
        }

        return validDimension;
    }
}
