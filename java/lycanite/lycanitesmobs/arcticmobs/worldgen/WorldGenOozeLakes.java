package lycanite.lycanitesmobs.arcticmobs.worldgen;

import cpw.mods.fml.common.IWorldGenerator;
import lycanite.lycanitesmobs.ObjectManager;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenLakes;

import java.util.Random;

public class WorldGenOozeLakes extends WorldGenLakes {

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGenOozeLakes() {
        super(ObjectManager.getBlock("ooze"));
    }


    // ==================================================
    //                    Generate
    // ==================================================
    @Override
    public boolean generate(World world, Random random, int x, int y, int z) {
        return super.generate(world, random, x, y, z);
    }
}
