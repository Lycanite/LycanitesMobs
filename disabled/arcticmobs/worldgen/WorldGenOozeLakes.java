package lycanite.lycanitesmobs.arcticmobs.worldgen;

import lycanite.lycanitesmobs.ObjectManager;
import lycanite.lycanitesmobs.api.IWorldGenBase;
import lycanite.lycanitesmobs.api.config.ConfigSpawning;
import lycanite.lycanitesmobs.api.info.GroupInfo;
import lycanite.lycanitesmobs.arcticmobs.ArcticMobs;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class WorldGenOozeLakes extends WorldGenLakes implements IWorldGenBase {
    public String name = "Ooze Lakes";
    public GroupInfo group = ArcticMobs.group;
    public double generateUndergroundChance = 0.04D;
    public double generateSurfaceChance = 0.125D;

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
        this.generateUndergroundChance = config.getDouble("WorldGen Chances", this.name + " Underground Chance", this.generateUndergroundChance);
        this.generateSurfaceChance = config.getDouble("WorldGen Chances", this.name + " Surface Chance", this.generateSurfaceChance, "Ooze only generates on the surface when in COLD and SNOWY tagged biomes.");

        ConfigSpawning.SpawnDimensionSet dimensions = config.getDimensions("WorldGen Dimensions", this.name + " Dimensions", this.dimensionEntries);
        this.dimensionBlacklist = dimensions.dimensionIDs;
        this.dimensionTypes = dimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("WorldGen Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
    }


    // ==================================================
    //                    Generate
    // ==================================================
    @Override
    public void onWorldGen(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
        if(!this.isValidDimension(world))
            return;

        if(this.generateSurfaceChance > 0) {
            BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
            BiomeDictionary.Type[] biomeTypes = BiomeDictionary.getTypesForBiome(biome);
            boolean typeValid = false;
            for(BiomeDictionary.Type type : biomeTypes) {
                if((type == BiomeDictionary.Type.SNOWY) || (type == BiomeDictionary.Type.COLD)) {
                    typeValid = true;
                    break;
                }
            }

            if(typeValid && (this.generateSurfaceChance >= 1 || random.nextDouble() <= this.generateSurfaceChance)) {
                int x = chunkX * 16 + random.nextInt(16);
                int z = chunkZ * 16 + random.nextInt(16);
                int y = random.nextInt(128);
                this.generate(world, random, x, y, z);
            }
        }

        if(this.generateUndergroundChance > 0 && (this.generateUndergroundChance >= 1 || random.nextDouble() <= this.generateUndergroundChance)) {
            int x = chunkX * 16 + random.nextInt(16);
            int z = chunkZ * 16 + random.nextInt(16);
            int top = Math.max(1, world.getTopSolidOrLiquidBlock(x, z) - 10);
            if(top > 0) {
                int y = random.nextInt(top);
                this.generate(world, random, x, y, z);
            }
        }
    }

    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
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
