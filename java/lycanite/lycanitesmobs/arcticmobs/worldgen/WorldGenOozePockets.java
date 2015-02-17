package lycanite.lycanitesmobs.arcticmobs.worldgen;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenLiquids;

import java.util.Random;

public class WorldGenOozePockets extends WorldGenLiquids {
    public String name = "Ooze Pockets";
    public GroupInfo group = ArcticMobs.group;

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
    public WorldGenOozePockets() {
        super(ObjectManager.getBlock("ooze"));

        ConfigSpawning config = ConfigSpawning.getConfig(this.group, "worldgen");
        ConfigSpawning.SpawnDimensionSet dimensions = config.getDimensions("Event Dimensions", this.name + " Dimensions", this.dimensionEntries);
        this.dimensionBlacklist = dimensions.dimensionIDs;
        this.dimensionTypes = dimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("Event Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
    }


    // ==================================================
    //                    Generate
    // ==================================================
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
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
