package lycanite.lycanitesmobs.infernomobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import lycanite.lycanitesmobs.api.IWorldGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class WorldGeneratorInferno implements IWorldGenerator {
    protected final IWorldGenBase pureLavaLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorInferno() {
        this.pureLavaLakes = new WorldGenPureLavaLakes();
    }


    // ==================================================
    //                      Generate
    // ==================================================
     @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
         this.pureLavaLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
