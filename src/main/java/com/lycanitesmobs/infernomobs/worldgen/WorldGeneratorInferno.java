package com.lycanitesmobs.infernomobs.worldgen;

import com.lycanitesmobs.core.IWorldGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.fml.common.IWorldGenerator;

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
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
         this.pureLavaLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
