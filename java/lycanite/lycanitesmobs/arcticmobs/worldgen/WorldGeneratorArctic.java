package lycanite.lycanitesmobs.arcticmobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.common.BiomeDictionary;

import java.util.Random;

public class WorldGeneratorArctic implements IWorldGenerator {
    protected final WorldGenOozeLakes oozeLakes;
    //protected final WorldGenerator oozePockets;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorArctic() {
        this.oozeLakes = new WorldGenOozeLakes();
        //this.oozePockets = new WorldGenOozePockets();
    }


    // ==================================================
    //                      Generate
    // ==================================================
     @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
         BiomeGenBase biome = world.getBiomeGenForCoords(chunkX, chunkZ);
         BiomeDictionary.Type[] biomeTypes = BiomeDictionary.getTypesForBiome(biome);
         boolean typeValid = false;
         for(BiomeDictionary.Type type : biomeTypes) {
             if((type == BiomeDictionary.Type.SNOWY) || (type == BiomeDictionary.Type.COLD)) {
                 typeValid = true;
                 break;
             }
         }

         if(typeValid) {
             int x = chunkX * 16 + random.nextInt(16);
             int z = chunkZ * 16 + random.nextInt(16);
             int y = random.nextInt(128);
             this.oozeLakes.generate(world, random, x, y, z);
         }

         if(random.nextDouble() <= this.oozeLakes.generateChance) {
             int x = chunkX * 16 + random.nextInt(16);
             int z = chunkZ * 16 + random.nextInt(16);
             int top = Math.max(1, world.getTopSolidOrLiquidBlock(x, z) - 10);
             if(top > 0) {
                 int y = random.nextInt(top);
                 this.oozeLakes.generate(world, random, x, y, z);
             }
         }
    }
}
