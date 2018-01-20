package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.info.MobDrop;
import com.lycanitesmobs.core.spawner.MobSpawn;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

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

		// Connectors:
		// TODO Connectors

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

		// Theme:
		if(this.dungeonSector.changeTheme || this.parentConnector.parentSector == null) {
			this.layout.dungeonInstance.schematic.getRandomTheme(random);
		}
		else {
			this.theme = this.parentConnector.parentSector.theme;
		}
	}


	/**
	 * Adds a new child Sector Connector to this Sector Instance.
	 * @param blockPos The position of the connector.
	 * @param rotation The rotation of the sector.
	 * @return The newly created Sector Connector.
	 */
	public SectorConnector addConnector(BlockPos blockPos, int rotation) {
		SectorConnector connector = new SectorConnector(blockPos, this, rotation);
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
		for (SectorConnector connector : this.connectors) {
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
		for(ChunkPos chunkPos : this.getChunkPositions()) {
			for(SectorInstance nearbySector : this.layout.sectorChunkMap.get(chunkPos)) {
				if(!nearbySectors.contains(nearbySector)) {
					nearbySectors.add(nearbySector);
				}
			}
		}
		return nearbySectors;
	}


	/**
	 * Returns true if this sector instance collides with the provided sector instance.
	 * @param sectorInstance The sector instance to check for collision with.
	 * @return True on collision.
	 */
	public boolean collidesWith(SectorInstance sectorInstance) {
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

		return false;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getBoundsMin(Vec3i boundsSize) {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					0
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds.add(
					0,
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					-boundsSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds.add(
					-boundsSize.getX(),
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		return this.parentConnector.position.add(bounds);
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getBoundsMax(Vec3i boundsSize) {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds.add(
					(int)Math.ceil((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					boundsSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds.add(
					boundsSize.getX(),
					boundsSize.getY(),
					(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds.add(
					(int)Math.ceil((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					0
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds.add(
					0,
					boundsSize.getY(),
					(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		return this.parentConnector.position.add(bounds);
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
	 * Builds this sector. Wont build at y level 0 or below or beyond world height.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void build(World world, ChunkPos chunkPos, Random random) {
		this.clearArea(world, chunkPos);
		this.buildFloor(world, chunkPos, random);
		this.buildWalls(world, chunkPos, random);
		this.buildCeiling(world, chunkPos, random);
		this.buildEntrances(world, chunkPos, random);
		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			this.buildStairs(world, chunkPos, random);
		}
	}


	/**
	 * Sets the area of this sector to air for building in from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 */
	public void clearArea(World world, ChunkPos chunkPos) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		int startBaseX = Math.min(startPos.getX(), stopPos.getX());
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int stopBaseX = Math.max(startPos.getX(), stopPos.getX());
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());

		int startY = Math.max(1, Math.min(startPos.getY(), stopPos.getY()));
		int stopY = Math.min(world.getHeight() - 1, Math.min(startPos.getY(), stopPos.getY()));

		int startBaseZ = Math.min(startPos.getZ(), stopPos.getZ());
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int stopBaseZ = Math.max(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());

		for(int x = startX; x <= stopX; x++) {
			for(int y = startY; y <= stopY; y++) {
				for(int z = startZ; z <= stopZ; z++) {
					world.setBlockToAir(new BlockPos(x, y, z));
				}
			}
		}
	}


	/**
	 * Builds the floor of this sector from within the chunk position.
	 * @param world The world to build in.
	 * @param chunkPos The chunk position to build within.
	 * @param random The instance of random, used for characters that are random.
	 */
	public void buildFloor(World world, ChunkPos chunkPos, Random random) {
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		int startBaseX = Math.min(startPos.getX(), stopPos.getX());
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int stopBaseX = Math.max(startPos.getX(), stopPos.getX());
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());

		int startY = Math.min(startPos.getY(), stopPos.getY());

		int startBaseZ = Math.min(startPos.getZ(), stopPos.getZ());
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int stopBaseZ = Math.max(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());

		for(int layerIndex : this.dungeonSector.floor.layers.keySet()) {
			int y = startY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			List<List<Character>> layer = this.dungeonSector.floor.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.get(x - startBaseX % layer.size());
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = row.get(z - startBaseZ % row.size());
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getFloor(buildChar, random);
					this.placeBlock(world, buildPos, blockState, random);
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

		int startBaseX = Math.min(startPos.getX(), stopPos.getX());
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int stopBaseX = Math.max(startPos.getX(), stopPos.getX());
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());

		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startPos.getY(), stopPos.getY());

		int startBaseZ = Math.min(startPos.getZ(), stopPos.getZ());
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int stopBaseZ = Math.max(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());

		for(int layerIndex : this.dungeonSector.wall.layers.keySet()) {
			List<List<Character>> layer = this.dungeonSector.wall.layers.get(layerIndex);
			for(int y = startY; y <= stopY; y++) {
				if(y <= 0 || y >= world.getHeight()) {
					continue;
				}
				List<Character> row = layer.get(y - startY % layer.size());

				// Build X:
				for(int x = startX; x <= stopX; x++) {
					char buildChar = row.get(x - startBaseX % row.size());
					IBlockState blockState = this.theme.getWall(buildChar, random);
					this.placeBlock(world, new BlockPos(x, y, startZ + layerIndex), blockState, random);
					this.placeBlock(world, new BlockPos(x, y, stopZ - layerIndex), blockState, random);
				}

				// Build Z:
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = row.get(z - startBaseZ % row.size());
					IBlockState blockState = this.theme.getWall(buildChar, random);
					this.placeBlock(world, new BlockPos(startX + layerIndex, y, z), blockState, random);
					this.placeBlock(world, new BlockPos(stopX - layerIndex, y, z), blockState, random);
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

		int startBaseX = Math.min(startPos.getX(), stopPos.getX());
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int stopBaseX = Math.max(startPos.getX(), stopPos.getX());
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());

		int startY = Math.min(startPos.getY(), stopPos.getY());

		int startBaseZ = Math.min(startPos.getZ(), stopPos.getZ());
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int stopBaseZ = Math.max(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());

		for(int layerIndex : this.dungeonSector.ceiling.layers.keySet()) {
			int y = startY + layerIndex;
			if(y <= 0 || y >= world.getHeight()) {
				continue;
			}
			List<List<Character>> layer = this.dungeonSector.ceiling.layers.get(layerIndex);
			for(int x = startX; x <= stopX; x++) {
				List<Character> row = layer.get(x - startBaseX % layer.size());
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = row.get(z - startBaseZ % row.size());
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getCeiling(buildChar, random);
					this.placeBlock(world, buildPos, blockState, random);
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
		// Get Start and Stop Positions:
		BlockPos startPos = this.getRoomBoundsMin();
		BlockPos stopPos = this.getRoomBoundsMax();

		int entranceRadius = 1;
		int centerX = startPos.getX() + Math.round((float)this.roomSize.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)this.roomSize.getZ() / 2);

		int startBaseX = Math.min(startPos.getX(), stopPos.getX());
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int startEntranceX = Math.max(centerX - entranceRadius, chunkPos.getXStart());
		int stopBaseX = Math.max(startPos.getX(), stopPos.getX());
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());
		int stopEntranceX = Math.min(centerX + entranceRadius, chunkPos.getXEnd());

		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(startY + (entranceRadius * 2), Math.max(startPos.getY(), stopPos.getY()));

		int startBaseZ = Math.min(startPos.getZ(), stopPos.getZ());
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int startEntranceZ = Math.max(centerZ - entranceRadius, chunkPos.getZStart());
		int stopBaseZ = Math.max(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());
		int stopEntranceZ = Math.min(centerZ + entranceRadius, chunkPos.getZEnd());

		for(int layerIndex : this.dungeonSector.wall.layers.keySet()) {
			List<List<Character>> layer = this.dungeonSector.wall.layers.get(layerIndex);
			for(int y = startY; y <= stopY; y++) {
				if(y <= 0 || y >= world.getHeight()) {
					continue;
				}
				List<Character> row = layer.get(y - startY % layer.size());

				// Build X:
				if("room".equalsIgnoreCase(this.dungeonSector.type) || this.parentConnector.rotation == 0 || this.parentConnector.rotation == 180) {
					for (int x = startEntranceX; x <= stopEntranceX; x++) {
						world.setBlockToAir(new BlockPos(x, y, startZ));
						world.setBlockToAir(new BlockPos(x, y, stopZ));
					}
				}

				// Build Z:
				if("room".equalsIgnoreCase(this.dungeonSector.type) || this.parentConnector.rotation == 90 || this.parentConnector.rotation == 270) {
					for (int z = startEntranceZ; z <= stopEntranceZ; z++) {
						world.setBlockToAir(new BlockPos(startX, y, z));
						world.setBlockToAir(new BlockPos(stopX, y, z));
					}
				}
			}
		}
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

		int centerX = startPos.getX() + Math.round((float)this.roomSize.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)this.roomSize.getZ() / 2);

		int startBaseX = centerX - 1;
		int startX = Math.max(startBaseX, chunkPos.getXStart());
		int stopBaseX = centerX + 1;
		int stopX = Math.min(stopBaseX, chunkPos.getXEnd());

		int startY = Math.min(world.getHeight() -1, Math.min(startPos.getY(), stopPos.getY()));
		int stopY = Math.max(1, startPos.getY() - (this.roomSize.getY() * 2));

		int startBaseZ = centerZ - 1;
		int startZ = Math.max(startBaseZ, chunkPos.getZStart());
		int stopBaseZ = centerZ + 1;
		int stopZ = Math.min(stopBaseZ, chunkPos.getZEnd());

		for(int y = startY; y >= stopY; y--) {
			for(int x = startX; x <= stopX; x++) {
				for(int z = startZ; z <= stopZ; z++) {
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = Blocks.AIR.getDefaultState();
					if(x == centerX && z == centerZ) {
						blockState = this.theme.getFloor('1', random);
					}
					// TODO Build Spiral Staircase
					this.placeBlock(world, buildPos, blockState, random);
				}
			}
		}
	}


	/**
	 * Places a block state in the world from this sector.
	 * @param world The world to place a block in.
	 * @param blockPos The position to place the block at.
	 * @param blockState The block state to place.
	 * @param random The instance of random, used for random mob spawns or loot on applicable blocks, etc.
	 */
	public void placeBlock(World world, BlockPos blockPos, IBlockState blockState, Random random) {
		if(blockState.getBlock() == Blocks.AIR) {
			return;
		}

		world.setBlockState(blockPos, blockState);

		// Spawner:
		if(blockState.getBlock() == Blocks.MOB_SPAWNER) {
			TileEntity tileEntity = world.getTileEntity(blockPos);
			if(tileEntity != null && tileEntity instanceof TileEntityMobSpawner) {
				TileEntityMobSpawner spawner = (TileEntityMobSpawner)tileEntity;
				MobSpawn mobSpawn = this.layout.dungeonInstance.schematic.getRandomMobSpawn(random);
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
}
