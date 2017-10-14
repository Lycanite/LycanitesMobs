package com.lycanitesmobs.infernomobs.worldgen;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.IWorldGenBase;
import com.lycanitesmobs.core.config.ConfigSpawning;
import com.lycanitesmobs.core.info.GroupInfo;
import com.lycanitesmobs.infernomobs.InfernoMobs;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraft.world.gen.feature.WorldGenLakes;

import java.util.Random;

public class WorldGenPureLavaLakes extends WorldGenLakes implements IWorldGenBase {
    public String name = "Pure Lava Lakes";
    public GroupInfo group = InfernoMobs.instance.group;
    public double generateUndergroundChance = 0.04D;
    public double generateSurfaceChance = 0;

    // Dimensions:
    /** A comma separated list of dimensions that this event can occur in. As read from the config **/
    public String dimensionEntries = "-1";
    /** A blacklist of dimension IDs (changes to whitelist if dimensionWhitelist is true) that this event can occur in. **/
    public int[] dimensionBlacklist;
    /** Extra dimension type info, can contain values such as ALL or VANILLA. **/
    public String[] dimensionTypes;
    /** Controls the behaviour of how Dimension IDs are read. If true only listed Dimension IDs are allowed instead of denied. **/
    public boolean dimensionWhitelist = true;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGenPureLavaLakes() {
        super(ObjectManager.getBlock("purelava"));

        ConfigSpawning config = ConfigSpawning.getConfig(this.group, "worldgen");

        config.setCategoryComment("WorldGen Chances", "The chance that each worldgen will generate. You can set this to 0 to disable the worldgen or use the dimension black/white list. 1 = all over, 0.04 fairly rare.");
        this.generateUndergroundChance = config.getDouble("WorldGen Chances", this.name + " Underground Chance", this.generateUndergroundChance);
        this.generateSurfaceChance = config.getDouble("WorldGen Chances", this.name + " Surface Chance", this.generateSurfaceChance);

        ConfigSpawning.SpawnDimensionSet dimensions = config.getDimensions("WorldGen Dimensions", this.name + " Dimensions", this.dimensionEntries);
        this.dimensionBlacklist = dimensions.dimensionIDs;
        this.dimensionTypes = dimensions.dimensionTypes;
        this.dimensionWhitelist = config.getBool("WorldGen Dimensions", this.name + " Dimensions Whitelist Mode", this.dimensionWhitelist);
    }


    // ==================================================
    //                    Generate
    // ==================================================
    @Override
    public void onWorldGen(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if(!this.isValidDimension(world))
            return;

        if(this.generateSurfaceChance > 0 && (this.generateSurfaceChance >= 1 || random.nextDouble() <= this.generateSurfaceChance)) {
            int x = (chunkX * 16) + 8;
            int z = (chunkZ * 16) + 8;
            int y = random.nextInt(128);
            this.generate(world, random, new BlockPos(x, y, z));
        }

        if(this.generateUndergroundChance > 0 && (this.generateUndergroundChance >= 1 || random.nextDouble() <= this.generateUndergroundChance)) {
            int x = (chunkX * 16) + 8;
            int z = (chunkZ * 16) + 8;
            int top = Math.max(1, world.getTopSolidOrLiquidBlock(new BlockPos(x, 0, z)).getY() - 10);
            if(top > 0) {
                int y = random.nextInt(top);
                this.generate(world, random, new BlockPos(x, y, z));
            }
        }
    }

    @Override
    public boolean generate(World world, Random random, BlockPos pos) {
        return super.generate(world, random, pos);
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
                validDimension = world.provider.getDimension() > -2 && world.provider.getDimension() < 2;
            }
        }

        // Check IDs:
        if(!validDimension) {
            validDimension =  !this.dimensionWhitelist;
            for(int eventDimension : this.dimensionBlacklist) {
                if(world.provider.getDimension() == eventDimension) {
                    validDimension = this.dimensionWhitelist;
                    break;
                }
            }
        }

        return validDimension;
    }
}
