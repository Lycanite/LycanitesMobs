package com.lycanitesmobs.core.worldgen;

import com.lycanitesmobs.ExtendedWorld;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.config.ConfigBase;
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
import java.util.UUID;

public class WorldGeneratorDungeon implements IWorldGenerator {
	public boolean enabled;
	public int dungeonDistance = 35;

    // ==================================================
    //                    Constructors
    // ==================================================
    public WorldGeneratorDungeon() {
		ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "general");
		this.enabled = config.getBool("Dungeons", "Dungeons Enabled", this.enabled, "If false, all Lycanites Mobs Dungeons are disabled, set to true to enable the Dungeon System. (The JSON files are still loaded but don't do anything.)");
		this.dungeonDistance = config.getInt("Dungeons", "Dungeon Distance", this.dungeonDistance, "The average distance in chunks that dungeons are spaced apart from each other.");
	}


    // ==================================================
    //                      Generate
    // ==================================================
    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
    	if(!this.enabled) {
    		return;
		}

		ExtendedWorld extendedWorld = ExtendedWorld.getForWorld(world);
		if(extendedWorld == null) {
			return;
		}

		try {
			ChunkPos chunkPos = new ChunkPos(chunkX, chunkZ);
			int dungeonSizeMax = this.dungeonDistance;
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
						int yPos = world.getSeaLevel();
						BlockPos dungeonPos = new ChunkPos(chunkX + (dungeonSizeMax * x), chunkZ + (dungeonSizeMax * z)).getBlock(7, yPos, 7);
						dungeonInstance.setOrigin(dungeonPos);
						extendedWorld.addDungeonInstance(dungeonInstance, new UUID(world.rand.nextLong(), world.rand.nextLong()));
						dungeonInstance.init(world);
					}
				}
				return;
			}

			// Build Dungeons:
			nearbyDungeons = extendedWorld.getNearbyDungeonInstances(chunkPos, 0);
			for(DungeonInstance dungeonInstance : nearbyDungeons) {
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
