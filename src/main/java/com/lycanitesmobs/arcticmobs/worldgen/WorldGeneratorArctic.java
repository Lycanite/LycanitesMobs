package com.lycanitesmobs.arcticmobs.worldgen;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.IWorldGenBase;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.Random;

public class WorldGeneratorArctic implements IWorldGenerator {
    protected final IWorldGenBase oozeLakes;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorArctic() {
        this.oozeLakes = new WorldGenOozeLakes();
    }


    // ==================================================
    //                      Generate
    // ==================================================
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        this.oozeLakes.onWorldGen(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider);
    }
}
