package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WorldGeneratorDungeon implements IWorldGenerator {

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorDungeon() {
        // TODO Load generation config settings if needed.
    }


    // ==================================================
    //                      Generate
    // ==================================================
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return;
		}

		try {
			ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
			int dungeonSizeMax = 35;
			List<DungeonInstance> nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, dungeonSizeMax * 2);

			// Create New Instances:
			if (nearbyDungeons.isEmpty()) {
				for (int x = -1; x <= 1; x++) {
					for (int z = -1; z <= 1; z++) {
						if (x == 0 && z == 0) {
							continue;
						}
						if (x != 0 && z != 0) {
							continue;
						}
						LycanitesMobs.printDebug("Dungeon", "Creating A New Dungeon At Chunk: X" + (chunkX + (dungeonSizeMax * x)) + " Z" + (chunkZ + (dungeonSizeMax * z)));
						DungeonInstance dungeonInstance = new DungeonInstance();
						BlockPos dungeonPos = new ChunkPos(chunkX + (dungeonSizeMax * x), chunkZ + (dungeonSizeMax * z)).getBlock(7, world.getSeaLevel(), 7);
						dungeonInstance.setOrigin(dungeonPos);
						extendedWorld.addDungeonInstance(dungeonInstance);
						dungeonInstance.init(world);
					}
				}
				return;
			}

			// Build Dungeons:
			nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, 0);
			for(DungeonInstance dungeonInstance : nearbyDungeons) {
				if(dungeonInstance.world == null) {
					dungeonInstance.init(world);
				}
				dungeonInstance.buildChunk(world, chunkPos);
			}
		}
		catch(Exception e) {
			LycanitesMobs.printWarning("Dungeon", "An exception occurred when trying to generate a dungeon.");
			if(LycanitesMobs.config.getBool("Debug", "Dungeon", false)) {
				e.printStackTrace();
			}
		}
    }
}
