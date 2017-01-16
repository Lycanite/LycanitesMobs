package lycanite.lycanitesmobs.core;

import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public interface IWorldGenBase {

    public void onWorldGen(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider);

}
