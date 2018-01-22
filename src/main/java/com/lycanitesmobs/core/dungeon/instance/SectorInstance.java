package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.SectorLayer;
import com.lycanitesmobs.core.spawner.MobSpawn;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SectorInstance {
	/** Sector Instances makeup an entire Dungeon Layout. **/

	/** The Dungeon Layout that this instance belongs to. **/
	public DungeonLayout layout;

	/** The Dungeon Sector that this instance is using. **/
	public DungeonSector dungeonSector;

	/** The connector that this sector is connected to, cannot be null. **/
	public SectorConnector parentConnector;

	/** A list of connectors that this sector provides to connect to other sectors with. **/
	public List<SectorConnector> connectors = new ArrayList<>();

	/** The room size of this Sector Instance, this includes the inside and inner floor, walls and ceiling. **/
	public Vec3i roomSize;

	/** The collision size of this Sector Instance, includes the room size plus additional space taken up by negative Sector Segement Layers. **/
	public Vec3i collisionSize;

	/** The theme this Sector Instance is using. **/
	public DungeonTheme theme;

	/** If true, this sector has been built into the world. **/
	public boolean built = false;


	/**
	 * Constructor
	 * @param layout The Dungeon Layout to create this instance for.
	 * @param dungeonSector The Dungeon Sector to create this instance from.
	 * @param random The instance of Random to use.
	 */
	public SectorInstance(DungeonLayout layout, DungeonSector dungeonSector, Random random) {
		this.layout = layout;
		this.dungeonSector = dungeonSector;

		// Size:
		this.roomSize = this.dungeonSector.getRandomSize(random);
		this.collisionSize = new Vec3i(
				this.roomSize.getX() + this.dungeonSector.padding.getX(),
				this.roomSize.getY() + this.dungeonSector.padding.getY(),
				this.roomSize.getZ() + this.dungeonSector.padding.getZ()
		);

		// Structures:
		// TODO Structures
	}


	/**
	 * Initialises this Sector Instance.
	 * @param parentConnector The connector that this sector is connecting from.
	 * @param random The instance of Random to use.
	 */
	public void init(SectorConnector parentConnector, Random random) {
		this.parentConnector = parentConnector;
		if(this.parentConnector == null) {
			LycanitesMobs.printWarning("Dungeon", "Tried to initialise a Dungeon Sector without a Parent Connector!");
			return;
		}
		this.parentConnector.closed = true;
		if(this.layout.openConnectors.contains(this.parentConnector)) {
			this.layout.openConnectors.remove(this.parentConnector);
		}

		// Theme:
		if(this.dungeonSector.changeTheme || this.parentConnector.parentSector == null) {
			this.theme = this.layout.dungeonInstance.schematic.getRandomTheme(random);
		}
		else {
			this.theme = this.parentConnector.parentSector.theme;
		}

		// Connectors:
		BlockPos boundsMin = this.getCollisionBoundsMin();
		BlockPos boundsMax = this.getCollisionBoundsMax();
		Vec3i size = this.roomSize;
		if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
			size = new Vec3i(size.getZ(), size.getY(), size.getX());
		}
		int centerX = boundsMin.getX() + Math.round((float)size.getX() / 2);
		int centerZ = boundsMin.getZ() + Math.round((float)size.getZ() / 2);
		if("corridor".equalsIgnoreCase(this.dungeonSector.type) || "room".equalsIgnoreCase(this.dungeonSector.type) || "entrance".equalsIgnoreCase(this.dungeonSector.type)) {
			// Front Exit:
			BlockPos blockPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMax.getZ());
			if(this.parentConnector.rotation == 90) {
				blockPos = new BlockPos(boundsMax.getX(), this.parentConnector.position.getY(), centerZ);
			}
			else if(this.parentConnector.rotation == 180) {
				blockPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMin.getZ());
			}
			else if(this.parentConnector.rotation == 270) {
				blockPos = new BlockPos(boundsMin.getX(), this.parentConnector.position.getY(), centerZ);
			}
			this.addConnector(blockPos, this.parentConnector.level, this.parentConnector.rotation);

			// Side Exits:
			if("room".equalsIgnoreCase(this.dungeonSector.type)) {
				BlockPos leftPos = new BlockPos(boundsMin.getX(), this.parentConnector.position.getY(), centerZ);
				int leftRotation = 270;
				BlockPos rightPos = new BlockPos(boundsMax.getX(), this.parentConnector.position.getY(), centerZ);
				int rightRotation = 90;
				if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
					leftPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMax.getZ());
					leftRotation = 0;
					rightPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMin.getZ());
					rightRotation = 180;
				}
				this.addConnector(leftPos, this.parentConnector.level, leftRotation);
				this.addConnector(rightPos, this.parentConnector.level, rightRotation);
			}
		}
		else if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			// Lower Exit:
			int y = this.parentConnector.position.getY() - (size.getY() * 2);
			if(y > 0) {
				BlockPos blockPos = new BlockPos(centerX, y, boundsMax.getZ());
				if (this.parentConnector.rotation == 90) {
					blockPos = new BlockPos(boundsMax.getX(), y, centerZ);
				}
				else if (this.parentConnector.rotation == 180) {
					blockPos = new BlockPos(centerX, y, boundsMin.getZ());
				}
				else if (this.parentConnector.rotation == 270) {
					blockPos = new BlockPos(boundsMin.getX(), y, centerZ);
				}
				this.addConnector(blockPos, this.parentConnector.level + 1, this.parentConnector.rotation);
			}
		}

		//LycanitesMobs.printDebug("Dungeon", "Initialised Sector Instance - Bounds: " + this.getCollisionBoundsMin() + " to " + this.getCollisionBoundsMax());
	}


	/**
	 * Adds a new child Sector Connector to this Sector Instance.
	 * @param blockPos The position of the connector.
	 * @param level The level that the connector is on.
	 * @param rotation The rotation of the sector.
	 * @return The newly created Sector Connector.
	 */
	public SectorConnector addConnector(BlockPos blockPos, int level, int rotation) {
		SectorConnector connector = new SectorConnector(blockPos, this, level, rotation);
		this.connectors.add(connector);
		return connector;
	}


	/**
	 * Returns a random child connector for a Sector Instance to connect to.
	 * @param random The instance of Random to use.
	 * @return A random connector.
	 */
	public SectorConnector getRandomConnector(Random random, SectorInstance sectorInstance) {
		List<SectorConnector> openConnectors = this.getOpenConnectors(sectorInstance);
		if(openConnectors.isEmpty()) {
			return null;
		}
		if(openConnectors.size() == 1) {
			return openConnectors.get(0);
		}
		return openConnectors.get(random.nextInt(openConnectors.size()));
	}


	/**
	 * Returns a list of open connectors where they are not set to closed and have no child Sector Instance connected.
	 * @param sectorInstance The sector to get the open connectors for. If null, collision checks are skipped.
	 * @return A lit of open connectors.
	 */
	public List<SectorConnector> getOpenConnectors(SectorInstance sectorInstance) {
		List<SectorConnector> openConnectors = new ArrayList<>();
		for(SectorConnector connector : this.connectors) {
			if(connector.canConnect(this.layout, sectorInstance)) {
				openConnectors.add(connector);
			}
		}
		return openConnectors;
	}


	/**
	 * Returns a list of every ChunkPos that this Sector Instance occupies.
	 * @return A list of ChunkPos.
	 */
	public List<ChunkPos> getChunkPositions() {
		ChunkPos minChunkPos = new ChunkPos(this.getCollisionBoundsMin());
		ChunkPos maxChunkPos = new ChunkPos(this.getCollisionBoundsMax());
		List<ChunkPos> chunkPosList = new ArrayList<>();
		for(int x = minChunkPos.x; x <= maxChunkPos.x; x++) {
			for(int z = minChunkPos.z; z <= maxChunkPos.z; z++) {
				chunkPosList.add(new ChunkPos(x, z));
			}
		}
		return chunkPosList;
	}


	/**
	 * Returns a list of other sectors near this sector instance.
	 * @return A list of nearby sector instances.
	 */
	public List<SectorInstance> getNearbySectors() {
		List<SectorInstance> nearbySectors = new ArrayList<>();
		for(SectorInstance nearbySector : this.layout.sectors) {
			if(!nearbySectors.contains(nearbySector)) {
				nearbySectors.add(nearbySector);
			}
		}
		/*for(ChunkPos chunkPos : this.getChunkPositions()) {
			if(this.layout.sectorChunkMap.containsKey(chunkPos)) {
				for(SectorInstance nearbySector : this.layout.sectorChunkMap.get(chunkPos)) {
					if(!nearbySectors.contains(nearbySector)) {
						nearbySectors.add(nearbySector);
					}
				}
			}
		}*/
		return nearbySectors;
	}


	/**
	 * Returns true if this sector instance collides with the provided sector instance.
	 * @param sectorInstance The sector instance to check for collision with.
	 * @return True on collision.
	 */
	public boolean collidesWith(SectorInstance sectorInstance) {
		if(sectorInstance == this) {
			return false;
		}

		BlockPos boundsMin = this.getCollisionBoundsMin();
		BlockPos boundsMax = this.getCollisionBoundsMax();
		BlockPos targetMin = sectorInstance.getCollisionBoundsMin();
		BlockPos targetMax = sectorInstance.getCollisionBoundsMax();

		boolean withinX = boundsMin.getX() > targetMin.getX() && boundsMin.getX() < targetMax.getX();
		if(!withinX)
			withinX = boundsMax.getX() > targetMin.getX() && boundsMax.getX() < targetMax.getX();
		if(!withinX)
			return false;

		boolean withinY = boundsMin.getY() > targetMin.getY() && boundsMin.getY() < targetMax.getY();
		if(!withinY)
			withinY = boundsMax.getY() > targetMin.getY() && boundsMax.getY() < targetMax.getY();
		if(!withinY)
			return false;

		boolean withinZ = boundsMin.getZ() > targetMin.getZ() && boundsMin.getZ() < targetMax.getZ();
		if(!withinZ)
			withinZ = boundsMax.getZ() > targetMin.getZ() && boundsMax.getZ() < targetMax.getZ();
		if(!withinZ)
			return false;

		return true;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getBoundsMin(Vec3i boundsSize) {
		if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
			boundsSize = new Vec3i(boundsSize.getZ(), boundsSize.getY(), boundsSize.getX());
		}

		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					0
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds = bounds.add(
					0,
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					-boundsSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds = bounds.add(
					-boundsSize.getX(),
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}

		return bounds;
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getBoundsMax(Vec3i boundsSize) {
		if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
			boundsSize = new Vec3i(boundsSize.getZ(), boundsSize.getY(), boundsSize.getX());
		}

		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					boundsSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds = bounds.add(
					boundsSize.getX(),
					boundsSize.getY(),
					(int)Math.floor((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					0
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds = bounds.add(
					0,
					boundsSize.getY(),
					(int)Math.floor((double)boundsSize.getZ() / 2)
			);
		}
		return bounds;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance occupies.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getCollisionBoundsMin() {
		return this.getBoundsMin(this.collisionSize);
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance occupies.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getCollisionBoundsMax() {
		return this.getBoundsMax(this.collisionSize);
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance builds from.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMin() {
		return this.getBoundsMin(this.collisionSize);
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance builds to.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMax() {
		return this.getBoundsMax(this.collisionSize);
	}


	/**
	 * Places a block state in the world from this sector.
	 * @param world The world to place a block in.
	 * @param chunkPos The chunk position to build within.
	 * @param blockPos The position to place the block at.
	 * @param blockState The block state to place.
	 * @param random The instance of random, used for random mob spawns or loot on applicable blocks, etc.
	 */
	public void placeBlock(World world, ChunkPos chunkPos, BlockPos blockPos, IBlockState blockState, Random random) {
		// Restrict To Chunk Position:
		if(blockPos.getX() < chunkPos.getXStart() || blockPos.getX() > chunkPos.getXEnd()) {
			return;
		}
		if(blockPos.getY() <= 0 || blockPos.getY() >= world.getHeight()) {
			return;
		}
		if(blockPos.getZ() < chunkPos.getZStart() || blockPos.getZ() > chunkPos.getZEnd()) {
			return;
		}

		/*Block existingBlock = world.getBlockState(blockPos).getBlock();
		if(existingBlock == Blocks.GOLD_BLOCK || existingBlock == Blocks.REDSTONE_BLOCK) {
			return;
		}*/

		if(blockState.getBlock() == Blocks.TORCH) {
			blockState = Blocks.GLOWSTONE.getDefaultState();
		}

		// Set The Block:
		world.setBlockState(blockPos, blockState, 3);

		// Spawner:
		if(blockState.getBlock() == Blocks.MOB_SPAWNER) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileEntity;
				MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(this.parentConnector.level, random);
				if(mobSpawn != null) {
					ResourceLocation entityId = EntityList.getKey(mobSpawn.entityClass);
					if (entityId != null) {
						spawner.getSpawnerBaseLogic().setEntityId(entityId);
					}
				}
			}
			return;
		}

		// Chest:
		if(blockState.getBlock() == Blocks.CHEST) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if(tileEntity != null && tileEntity instanceof TileEntityChest) {
				TileEntityChest chest = (TileEntityChest)tileEntity;
				if(!"".equals(this.layout.dungeonInstance.schematic.lootTable)) {
					chest.setLootTable(new ResourceLocation(this.layout.dungeonInstance.schematic.lootTable), random.nextLong());
				}
				// TODO Add specific items to loot chests.
			}
		}
	}


	/**
	 * Builds this sector. Wont build at y level 0 or below or beyond world height.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void build(World world, ChunkPos chunkPos, Random random) {
		this.clearArea(world, chunkPos, random);
		this.buildFloor(world, chunkPos, random, 0);
		this.buildWalls(world, chunkPos, random);
		this.buildCeiling(world, chunkPos, random);
		this.buildEntrances(world, chunkPos, random);
		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			this.buildStairs(world, chunkPos, random);
			this.buildFloor(world, chunkPos, random, -(this.roomSize.getY() * 2));
		}
		/*for(SectorConnector connector : this.connectors) {
			connector.buildTest(world, chunkPos, random);
		}*/
	}


	/**
	 * Sets the area of this sector to air for building in from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void clearArea(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			startY = Math.max(1, startPos.getY() - (this.roomSize.getY() * 2));
		}

		for(int x = startX; x <= stopX; x++) {
			for(int y = startY; y <= stopY; y++) {
				for(int z = startZ; z <= stopZ; z++) {
					this.placeBlock(world, chunkPos, new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), random);
				}
			}
		}
	}


	/**
	 * Builds the floor of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 * @param offsetY The Y offset to build the floor at, useful for multiple floor sectors.
	 */
	public void buildFloor(World world, ChunkPos chunkPos, Random random, int offsetY) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin().add(0, offsetY, 0);
		BlockPos stopPos = this.getRoomBoundsMax().add(0, offsetY, 0);
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		for(int layerIndex : this.dungeonSector.floor.layers.keySet()) {
			int y = startY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			SectorLayer layer = this.dungeonSector.floor.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.rows.get((x - startX) % layer.rows.size());
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = row.get((z - startZ) % row.size());
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getFloor(buildChar, random);
					if(blockState.getBlock() != Blocks.AIR)
						this.placeBlock(world, chunkPos, buildPos, blockState, random);
				}
			}
		}
	}


	/**
	 * Builds the walls of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildWalls(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		Vec3i size = this.roomSize;
		if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
			size = new Vec3i(size.getZ(), size.getY(), size.getX());
		}
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY() + 1, stopPos.getY());
		int stopY = Math.max(startPos.getY() - 1, stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			startY = Math.max(1, startPos.getY() - (this.roomSize.getY() * 2));
		}

		for(int layerIndex : this.dungeonSector.wall.layers.keySet()) {
			SectorLayer layer = this.dungeonSector.wall.layers.get(layerIndex);
			for(int y = startY; y <= stopY; y++) {
				// Vertical Tiling:
				if(!layer.tileVertical && (y - startY) >= layer.rows.size()) {
					break;
				}

				// Y Limit:
				if(y <= 0 || y >= world.getHeight()) {
					continue;
				}

				// Get Row:
				List<Character> row = layer.rows.get((y - startY) % layer.rows.size());

				// Horizontal Tiling:
				int paddingX = layerIndex;
				int paddingZ = layerIndex;
				int horizontalOffset = 0;
				if(!layer.tileHorizontal) {
					horizontalOffset = Math.round((float)row.size() / 2);
					paddingX += Math.round((float)size.getX() / 2) - horizontalOffset;
					paddingZ += Math.round((float)size.getZ() / 2) - horizontalOffset;
				}

				// Build X:
				for(int x = startX + paddingX; x <= stopX - paddingX; x++) {
					char buildChar = row.get((x - (startX + paddingX) + horizontalOffset) % row.size());
					IBlockState blockState = this.theme.getWall(buildChar, random);
					if(blockState.getBlock() != Blocks.AIR) {
						this.placeBlock(world, chunkPos, new BlockPos(x, y, startZ + layerIndex), blockState, random);
						this.placeBlock(world, chunkPos, new BlockPos(x, y, stopZ - layerIndex), blockState, random);
					}
				}

				// Build Z:
				for(int z = startZ + paddingZ; z <= stopZ - paddingZ; z++) {
					char buildChar = row.get((z - (startZ + paddingZ) + horizontalOffset) % row.size());
					IBlockState blockState = this.theme.getWall(buildChar, random);
					if(blockState.getBlock() != Blocks.AIR) {
						this.placeBlock(world, chunkPos, new BlockPos(startX + layerIndex, y, z), blockState, random);
						this.placeBlock(world, chunkPos, new BlockPos(stopX - layerIndex, y, z), blockState, random);
					}
				}
			}
		}
	}


	/**
	 * Builds the ceiling of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildCeiling(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startPos.getY(), stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		for(int layerIndex : this.dungeonSector.ceiling.layers.keySet()) {
			int y = stopY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			SectorLayer layer = this.dungeonSector.ceiling.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.rows.get((x - startX) % layer.rows.size());
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = row.get((z - startZ) % row.size());
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getCeiling(buildChar, random);
					if(blockState.getBlock() != Blocks.AIR)
						this.placeBlock(world, chunkPos, buildPos, blockState, random);
				}
			}
		}
	}


	/**
	 * Builds the entrances of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildEntrances(World world, ChunkPos chunkPos, Random random) {
		this.parentConnector.buildEntrance(world, chunkPos, random);
	}


	/**
	 * Builds a set of stairs leading down to a lower room to start the next level.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildStairs(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		Vec3i size = this.roomSize;
		if(this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
			size = new Vec3i(size.getZ(), size.getY(), size.getX());
		}
		int centerX = startPos.getX() + Math.round((float)size.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2);

		int startX = centerX - 1;
		int stopX = centerX + 1;
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(1, startPos.getY() - (this.roomSize.getY() * 2));

		int startZ = centerZ - 1;
		int stopZ = centerZ + 1;

		for(int y = startY; y >= stopY; y--) {
			for(int x = startX; x <= stopX; x++) {
				for(int z = startZ; z <= stopZ; z++) {
					IBlockState blockState = Blocks.AIR.getDefaultState();

					// Center:
					if(x == centerX && z == centerZ) {
						blockState = this.theme.getFloor('1', random);
					}

					// Spiral Stairs:
					int step = startY - y % 8;
					int offsetX = startX - x;
					int offsetZ = startZ - z;
					if(step % 8 == 0 && offsetX == 0 && offsetZ == 0) {
						blockState = this.theme.getFloor('1', random);
					}
					else if(step % 8 == 1 && offsetX == 0 && offsetZ == 1) {
						blockState = Blocks.STONE_BRICK_STAIRS.getDefaultState();
					}
					else if(step % 8 == 2 && offsetX == 0 && offsetZ == 2) {
						blockState = this.theme.getFloor('1', random);
					}
					else if(step % 8 == 3 && offsetX == 1 && offsetZ == 2) {
						blockState = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.CLOCKWISE_90);
					}
					else if(step % 8 == 4 && offsetX == 2 && offsetZ == 2) {
						blockState = this.theme.getFloor('1', random);
					}
					else if(step % 8 == 3 && offsetX == 2 && offsetZ == 1) {
						blockState = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.CLOCKWISE_180);
					}
					else if(step % 8 == 4 && offsetX == 2 && offsetZ == 0) {
						blockState = this.theme.getFloor('1', random);
					}
					else if(step % 8 == 3 && offsetX == 1 && offsetZ == 0) {
						blockState = Blocks.STONE_BRICK_STAIRS.getDefaultState().withRotation(Rotation.COUNTERCLOCKWISE_90);
					}


					BlockPos buildPos = new BlockPos(x, y, z);
					this.placeBlock(world, chunkPos, buildPos, blockState, random);
				}
			}
		}
	}


	/**
	 * Formats this object into a String.
	 * @return A formatted string description of this object.
	 */
	@Override
	public String toString() {
		String bounds = "";
		if(this.parentConnector != null) {
			bounds = " Bounds: " + this.getCollisionBoundsMin() + " to " + this.getCollisionBoundsMax();
		}
		String size = " Size: " + this.collisionSize;
		return "Sector Instance Type: " + (this.dungeonSector == null ? "Unset" : this.dungeonSector.type) + " Parent Connector Pos: " + (this.parentConnector == null ? "Unset" : this.parentConnector.position) + size + bounds;
	}
}
