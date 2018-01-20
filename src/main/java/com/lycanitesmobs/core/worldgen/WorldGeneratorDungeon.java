package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.dungeon.instance.DungeonInstance;
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
		ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
		int dungeonSizeMax = 35;
		List<DungeonInstance> nearbyDungeons = DungeonManager.getInstance().getNearbyDungeonInstances(world, chunkPos, dungeonSizeMax * 2);

		// Create New Instances:
		if(nearbyDungeons.isEmpty()) {
			for(int x = -1; x <= 1; x++) {
				for(int z = -1; z <= 1; z++) {
					if(x == 0 && z == 0) {
						continue;
					}
					if(x != 0 && z != 0) {
						continue;
					}
					DungeonInstance dungeonInstance = new DungeonInstance();
					dungeonInstance.setOrigin(new ChunkPos(chunkX + (dungeonSizeMax * x), chunkZ + (dungeonSizeMax * z)).getBlock(7, 0, 7));
					dungeonInstance.init(world);
					DungeonManager.getInstance().addDungeonInstance(dungeonInstance, world);
				}
			}
			return;
		}

		// Build Dungeons:
		nearbyDungeons = DungeonManager.getInstance().getNearbyDungeonInstances(world, chunkPos, 0);
		for(DungeonInstance dungeonInstance : nearbyDungeons) {
			dungeonInstance.buildChunk(world, chunkPos);
		}
    }
}
