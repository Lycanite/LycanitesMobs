package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.core.dungeon.DungeonManager;
import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DungeonInstance {
	/** A Dungeon Instance is a dungeon that is active in the world. **/

	/** The Schematic this instance builds from. Can be null when a Schematic has been removed or renamed in which case the dungeon is immediately set as complete. **/
	public DungeonSchematic schematic;

	/** If true, this dungeon has been fully built and does not need to generate its layout, etc. This is where all chunks this dungeon is in have been loaded. **/
	public boolean complete = false;

	/** The origin block position of this dungeon where it begins building from. **/
	public BlockPos originPos;

	/** The origin chunk position of this dungeon where it begins building from. **/
	public ChunkPos originChunk;

	/** The world that the dungeon should build in. **/
	public World world;

	/** The seed for generating this dungeon. All random decisions are based on this seed so that the Dungeon Layout can generate the same on world reload, etc. **/
	long seed = 0;

	/** The random instance to use when randomly generating, this can be seeded for consistent results. **/
	public Random random;

	/** The generated layout of this dungeon, this contains all randomly selected sectors and their structures, etc. This is null on complete dungeons. **/
	public DungeonLayout layout;


	/**
	 * Sets the origin position. This must be set before init. Reading from NBT sets this from the NBT data.
	 * @param blockPos The exact block position that this dungeon builds from.
	 * @param chunkPos The chunk position that this dungeon's origin block position is in.
	 */
	public void setOrigin(BlockPos blockPos, ChunkPos chunkPos) {
		this.originPos = blockPos;
		this.originChunk = chunkPos;
	}


	/**
	 * Initialises this Dungeon where if it's not complete it will generate its layout, etc. Should be called after readFromNBT when loading an existing dungeon and origin must be set.
	 * @param world The world that this Instance will build in.
	 */
	public void init(World world) {
		this.world = world;
		if(this.complete || this.world == null || this.originPos == null || this.originChunk == null) {
			return;
		}

		// Get Schematic:
		if(this.schematic == null) {
			List<DungeonSchematic> schematics = new ArrayList<>();
			for(DungeonSchematic schematic : DungeonManager.getInstance().schematics.values()) {
				if(schematic.canBuild(world, this.originPos)) {
					schematics.add(schematic);
				}
			}
			if(schematics.isEmpty()) {
				return;
			}
			if(schematics.size() == 1) {
				this.schematic = schematics.get(0);
			}
			else {
				this.schematic = schematics.get(this.world.rand.nextInt(schematics.size()));
			}
		}

		// Get Seed:
		if(this.seed == 0) {
			world.rand.nextLong();
		}
		this.random = new Random(this.seed);

		// Generate Layout:
		if(this.layout == null) {
			this.layout = new DungeonLayout(this);
			this.layout.generate(this.random);
		}
	}


	/**
	 * Loads this Dungeon Instance from the provided NBT data, this is mostly just if the dungeon is built and what its origin position is.
	 * @param nbtTagCompound The NBT Data to read from.
	 */
	public void readFromNBT(NBTTagCompound nbtTagCompound) {
		this.schematic = DungeonManager.getInstance().getSchematic(nbtTagCompound.getString("Schematic"));
		this.seed = nbtTagCompound.getLong("Seed");
		this.complete = nbtTagCompound.getBoolean("Complete");
		if(this.schematic == null) {
			this.complete = true;
		}
		int[] originPos = nbtTagCompound.getIntArray("OriginPos");
		this.originPos = new BlockPos(originPos[0], originPos[1], originPos[2]);
		int[] originChunk = nbtTagCompound.getIntArray("OriginChunk");
		this.originChunk = new ChunkPos(originChunk[0], originChunk[1]);
	}

	/**
	 * Writes this dungeon to NBT. Should only be called after the this instance has been initialised.
	 * @param nbtTagCompound The NBTData to write to.
	 * @return The written to NBTData. For chaining.
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbtTagCompound) {
		nbtTagCompound.setString("Schematic", this.schematic.name);
		nbtTagCompound.setLong("Seed", this.seed);
		nbtTagCompound.setBoolean("Complete", this.complete);
		nbtTagCompound.setIntArray("OriginPos", new int[] {this.originPos.getX(), this.originPos.getY(), this.originPos.getZ()});
		nbtTagCompound.setIntArray("OriginChunk", new int[] {this.originChunk.x, this.originChunk.z});
		return nbtTagCompound;
	}
}
