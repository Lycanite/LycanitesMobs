package com.lycanitesmobs.core.dungeon.instance;

import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.Random;

public class SectorConnector {
	/** Sector Connectors link together one sector to another, each sector type has one or more connectors as well as a parent connector. */

	/** The block position of this connector. **/
	public BlockPos position;

	/** The Sector Instance that this connector belongs to. Null when this is used as the starting connector. **/
	public SectorInstance parentSector;

	/** The Sector Instance that is connected to this connector. Can be null if the connection is empty. **/
	public SectorInstance childSector;

	/** The facing direction of this connector. **/
	public EnumFacing facing = EnumFacing.SOUTH;

	/** Set to true when this connector is connected to a child sector or cannot be connected to anything (usually due to collisions). **/
	public boolean closed = false;

	/** The dungeon level that this connector is on. **/
	public int level = 0;


	/**
	 * Constructor
	 * @param position The block position of this connector.
	 * @param parentSector The sector that this connector belongs to. If null, collision checks are skipped.
	 * @param level The level that this connector is on.
	 * @param facing The facing of this sector (applied to child sectors).
	 */
	public SectorConnector(BlockPos position, SectorInstance parentSector, int level, EnumFacing facing) {
		this.position = position;
		this.parentSector = parentSector;
		this.level = level;
		this.facing = facing;
	}


	/**
	 * Returns true if this connector can be connected to.
	 * @param dungeonLayout The dungeon layout using this connector.
	 * @param sectorInstance If set, the specific sector instance to connect to, if null then collision checks are ignored.
	 * @return
	 */
	public boolean canConnect(DungeonLayout dungeonLayout, SectorInstance sectorInstance) {
		if(this.closed) {
			return false;
		}
		if(sectorInstance == null) {
			return true;
		}

		// Temp Set Connector:
		boolean tempConnector = false;
		if(sectorInstance.parentConnector == null) {
			sectorInstance.parentConnector = this;
			tempConnector = true;
		}

		// Connect To Sector Instance:
		for(SectorInstance nearbySector : sectorInstance.getNearbySectors()) {
			if(sectorInstance.collidesWith(nearbySector)) {
				if(tempConnector) {
					sectorInstance.parentConnector = null;
				}
				return false;
			}
		}

		if(tempConnector) {
			sectorInstance.parentConnector = null;
		}
		return true;
	}


	/**
	 * Builds an entrance at this connectors location within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random to use.
	 */
	public void buildEntrance(World world, ChunkPos chunkPos, Random random) {
		SectorInstance sectorInstance = this.parentSector;
		if(sectorInstance == null) {
			sectorInstance = this.childSector;
		}
		if(sectorInstance == null) {
			return;
		}

		// Get Position and Size:
		Vec3i size = sectorInstance.getRoomSize();
		if(this.childSector != null && sectorInstance != this.childSector) {
			Vec3i childSize = this.childSector.getRoomSize();
			size = new Vec3i(Math.min(size.getX(), childSize.getX()), Math.min(size.getY(), childSize.getY()), Math.min(size.getZ(), childSize.getZ()));
		}
		int entranceHeight = 2;
		int entranceRadius = 1;
		int entranceClearance = 1;
		int startX = this.position.getX();
		int stopX = this.position.getX();
		int startY = Math.max(1, this.position.getY() + 1);
		int stopY = Math.min(world.getHeight() - 1, this.position.getY() + 1 + entranceHeight);
		int startZ = this.position.getZ();
		int stopZ = this.position.getZ();

		// Calculate Rotation:
		if(this.facing == EnumFacing.SOUTH || this.facing == EnumFacing.NORTH) {
			startX -= entranceRadius;
			stopX += entranceRadius;
			if(size.getX() % 2 != 0) {
				startX -= 1;
			}
			startZ -= entranceClearance * 2;
			stopZ += entranceClearance * 2;
		}
		else {
			startZ -= entranceRadius;
			stopZ += entranceRadius;
			if(size.getZ() % 2 != 0) {
				startZ -= 1;
			}
			startX -= entranceClearance * 2;
			stopX += entranceClearance * 2;
		}

		// Build Entrance:
		for(int x = startX; x <= stopX; x++) {
			for(int y = startY; y <= stopY; y++) {
				for(int z = startZ; z <= stopZ; z++) {
					sectorInstance.placeBlock(world, chunkPos, new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), this.facing, random);
				}
			}
		}
	}


	/**
	 * Builds a test marker showing where this connector is and its facing.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random to use.
	 */
	public void buildTest(World world, ChunkPos chunkPos, Random random) {
		SectorInstance sectorInstance = this.parentSector;
		if(sectorInstance == null) {
			sectorInstance = this.childSector;
		}
		if(sectorInstance == null) {
			return;
		}

		// Build Center Block Marker:
		sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, 0), Blocks.GOLD_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);

		// Build Rotation Block Markers:
		if(this.facing == EnumFacing.SOUTH) {
			for(int z = 1; z <= 3; z++)
				sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, z), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
		}
		else if(this.facing == EnumFacing.EAST) {
			for(int x = 1; x <= 3; x++)
				sectorInstance.placeBlock(world, chunkPos, this.position.add(x, 1, 0), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
		}
		else if(this.facing == EnumFacing.NORTH) {
			for(int z = -1; z >= -3; z--)
				sectorInstance.placeBlock(world, chunkPos, this.position.add(0, 1, z), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
		}
		else {
			for(int x = -1; x >= -3; x--)
				sectorInstance.placeBlock(world, chunkPos, this.position.add(x, 1, 0), Blocks.REDSTONE_BLOCK.getDefaultState(), EnumFacing.SOUTH, random);
		}
	}
}
