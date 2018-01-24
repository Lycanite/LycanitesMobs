package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import com.lycanitesmobs.core.dungeon.definition.SectorLayer;
import com.lycanitesmobs.core.spawner.MobSpawn;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
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

	/** The room size of this Sector Instance, this includes the inside and inner floor, walls and ceiling. Used for building and sector to sector collision. **/
	protected Vec3i roomSize;

	/** The occupied size of this Sector Instance, includes the room size plus additional space taken up by sector layers, structures, padding, etc. **/
	protected Vec3i occupiedSize;

	/** The theme this Sector Instance is using. **/
	public DungeonTheme theme;

	/** The random light block for this sector instance to use. **/
	public IBlockState lightBlock;

	/** The random torch block for this sector instance to use. **/
	public IBlockState torchBlock;

	/** The random stairs block for this sector instance to use. **/
	public IBlockState stairBlock;

	/** The random pit block for this sector instance to use. **/
	public IBlockState pitBlock;

	/** How many chunks this sector has been built into. When this equals the total chunks this sector occupies it is considered fully built. **/
	public int chunksBuilt = 0;


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
		this.occupiedSize = new Vec3i(
				this.roomSize.getX() + Math.max(1, this.dungeonSector.padding.getX()),
				this.roomSize.getY() + this.dungeonSector.padding.getY(),
				this.roomSize.getZ() + Math.max(1, this.dungeonSector.padding.getZ())
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
		this.parentConnector.childSector = this;
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
		this.lightBlock = this.theme.getLight('B', random);
		this.torchBlock = this.theme.getTorch('B', random);
		this.stairBlock = this.theme.getStairs('B', random);
		this.pitBlock = this.theme.getPit('B', random);

		// Create Child Connectors:
		BlockPos boundsMin = this.getRoomBoundsMin();
		BlockPos boundsMax = this.getRoomBoundsMax();
		Vec3i size = this.getRoomSize();
		int centerX = boundsMin.getX() + Math.round((float)size.getX() / 2);
		int centerZ = boundsMin.getZ() + Math.round((float)size.getZ() / 2);
		if("corridor".equalsIgnoreCase(this.dungeonSector.type) || "room".equalsIgnoreCase(this.dungeonSector.type) || "entrance".equalsIgnoreCase(this.dungeonSector.type)) {
			// TODO Randomize Connectors Horizontally

			// Front Exit:
			BlockPos blockPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMax.getZ() + 1);
			if(this.parentConnector.facing == EnumFacing.EAST) {
				blockPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), centerZ);
			}
			else if(this.parentConnector.facing == EnumFacing.NORTH) {
				blockPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMin.getZ() - 1);
			}
			else if(this.parentConnector.facing == EnumFacing.WEST) {
				blockPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), centerZ);
			}
			this.addConnector(blockPos, this.parentConnector.level, this.parentConnector.facing);

			// Side Exits:
			if("room".equalsIgnoreCase(this.dungeonSector.type)) {
				BlockPos leftPos = new BlockPos(boundsMin.getX() - 1, this.parentConnector.position.getY(), centerZ);
				EnumFacing leftFacing = EnumFacing.WEST;
				BlockPos rightPos = new BlockPos(boundsMax.getX() + 1, this.parentConnector.position.getY(), centerZ);
				EnumFacing rightFacing = EnumFacing.EAST;
				if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
					leftPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMax.getZ() + 1);
					leftFacing = EnumFacing.SOUTH;
					rightPos = new BlockPos(centerX, this.parentConnector.position.getY(), boundsMin.getZ() - 1);
					rightFacing = EnumFacing.NORTH;
				}
				this.addConnector(leftPos, this.parentConnector.level, leftFacing);
				this.addConnector(rightPos, this.parentConnector.level, rightFacing);
			}
		}
		else if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {

			// Lower Exit:
			int y = this.parentConnector.position.getY() - (size.getY() * 2);
			if(y > 0) {
				BlockPos blockPos = new BlockPos(centerX, y, boundsMax.getZ() + 1);
				if (this.parentConnector.facing == EnumFacing.EAST) {
					blockPos = new BlockPos(boundsMax.getX() + 1, y, centerZ);
				}
				else if (this.parentConnector.facing == EnumFacing.NORTH) {
					blockPos = new BlockPos(centerX, y, boundsMin.getZ() - 1);
				}
				else if (this.parentConnector.facing == EnumFacing.WEST) {
					blockPos = new BlockPos(boundsMin.getX() - 1, y, centerZ);
				}
				this.addConnector(blockPos, this.parentConnector.level + 1, this.parentConnector.facing);
			}
		}

		//LycanitesMobs.printDebug("Dungeon", "Initialised Sector Instance - Bounds: " + this.getOccupiedBoundsMin() + " to " + this.getOccupiedBoundsMax());
	}


	/**
	 * Adds a new child Sector Connector to this Sector Instance.
	 * @param blockPos The position of the connector.
	 * @param level The level that the connector is on.
	 * @param facing The facing of the sector.
	 * @return The newly created Sector Connector.
	 */
	public SectorConnector addConnector(BlockPos blockPos, int level, EnumFacing facing) {
		SectorConnector connector = new SectorConnector(blockPos, this, level, facing);
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
		ChunkPos minChunkPos = new ChunkPos(this.getOccupiedBoundsMin().add(-1, 0, -1));
		ChunkPos maxChunkPos = new ChunkPos(this.getOccupiedBoundsMax().add(1, 0, 1));
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

		BlockPos boundsMin = this.getOccupiedBoundsMin();
		BlockPos boundsMax = this.getOccupiedBoundsMax();
		BlockPos targetMin = sectorInstance.getOccupiedBoundsMin();
		BlockPos targetMax = sectorInstance.getOccupiedBoundsMax();

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
	 * Returns the room size of this sector. X and Z are swapped when facing EAST or WEST. This is how large the room to be built is excluding extra blocks added for layers or structures, etc. Use for building and sector collision testing.
	 * @return A vector of the room size.
	 */
	public Vec3i getRoomSize() {
		if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
			return new Vec3i(this.roomSize.getZ(), this.roomSize.getY(), this.roomSize.getX());
		}
		return this.roomSize;
	}


	/**
	 * Returns the collision size of this sector. X and Z are swapped when facing EAST or WEST. This is how large this sector is including extra blocks added for layers or structures, etc. Use for detected what chunks this sector needs to generate in, etc.
	 * @return A vector of the collision size.
	 */
	public Vec3i getOccupiedSize() {
		if(this.parentConnector.facing == EnumFacing.EAST || this.parentConnector.facing == EnumFacing.WEST) {
			return new Vec3i(this.occupiedSize.getZ(), this.occupiedSize.getY(), this.occupiedSize.getX());
		}
		return this.occupiedSize;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance from the provided bounds size.
	 * @param boundsSize The xyz size to use when calculating bounds.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getBoundsMin(Vec3i boundsSize) {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.facing == EnumFacing.SOUTH) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					0
			);
		}
		else if(this.parentConnector.facing == EnumFacing.EAST) {
			bounds = bounds.add(
					0,
					0,
					-(int)Math.ceil((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.NORTH) {
			bounds = bounds.add(
					-(int)Math.ceil((double)boundsSize.getX() / 2),
					0,
					-boundsSize.getZ()
			);
		}
		else if(this.parentConnector.facing == EnumFacing.WEST) {
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
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.facing == EnumFacing.SOUTH) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					boundsSize.getZ()
			);
		}
		else if(this.parentConnector.facing == EnumFacing.EAST) {
			bounds = bounds.add(
					boundsSize.getX(),
					boundsSize.getY(),
					(int)Math.floor((double)boundsSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.facing == EnumFacing.NORTH) {
			bounds = bounds.add(
					(int)Math.floor((double)boundsSize.getX() / 2),
					boundsSize.getY(),
					0
			);
		}
		else if(this.parentConnector.facing == EnumFacing.WEST) {
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
	public BlockPos getOccupiedBoundsMin() {
		return this.getBoundsMin(this.getOccupiedSize()).add(-8, 0, -8);
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance occupies.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getOccupiedBoundsMax() {
		return this.getBoundsMax(this.getOccupiedSize()).add(-8, 0, -8);
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance builds from.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMin() {
		return this.getBoundsMin(this.getRoomSize());
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance builds to.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getRoomBoundsMax() {
		return this.getBoundsMax(this.getRoomSize());
	}


	/**
	 * Places a block state in the world from this sector.
	 * @param world The world to place a block in.
	 * @param chunkPos The chunk position to build within.
	 * @param blockPos The position to place the block at.
	 * @param blockState The block state to place.
	 * @param random The instance of random, used for random mob spawns or loot on applicable blocks, etc.
	 */
	public void placeBlock(World world, ChunkPos chunkPos, BlockPos blockPos, IBlockState blockState, EnumFacing facing, Random random) {
		// Restrict To Chunk Position:
		int chunkOffset = 8;
		if(blockPos.getX() < chunkPos.getXStart() + chunkOffset || blockPos.getX() > chunkPos.getXEnd() + chunkOffset) {
			return;
		}
		if(blockPos.getY() <= 0 || blockPos.getY() >= world.getHeight()) {
			return;
		}
		if(blockPos.getZ() < chunkPos.getZStart() + chunkOffset || blockPos.getZ() > chunkPos.getZEnd() + chunkOffset) {
			return;
		}


		// Block State and Flags:
		int flags = 3;

		// Torch:
		if(blockState.getBlock() == Blocks.TORCH) {
			blockState = blockState.withProperty(BlockTorch.FACING, facing);
			flags = 0;
		}

		// Chest:
		if(blockState.getBlock() == Blocks.CHEST) {
			blockState = blockState.withProperty(BlockChest.FACING, facing);
		}


		// Set The Block:
		world.setBlockState(blockPos, blockState, flags);


		// Tile Entities:

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
		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			this.buildStairs(world, chunkPos, random);
			this.buildFloor(world, chunkPos, random, -(this.getRoomSize().getY() * 2));
		}
		this.buildEntrances(world, chunkPos, random);
		this.chunksBuilt++;


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
			startY = Math.max(1, startPos.getY() - (this.getRoomSize().getY() * 2));
		}

		for(int x = startX; x <= stopX; x++) {
			for(int y = startY; y <= stopY; y++) {
				for(int z = startZ; z <= stopZ; z++) {
					this.placeBlock(world, chunkPos, new BlockPos(x, y, z), Blocks.AIR.getDefaultState(), EnumFacing.SOUTH, random);
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
				List<Character> row = layer.getRow(x - startX, stopX - startX);
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row);
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getFloor(this, buildChar, random);
					if(blockState.getBlock() != Blocks.AIR)
						this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
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

		Vec3i size = this.getRoomSize();
		int startX = Math.min(startPos.getX(), stopPos.getX());
		int stopX = Math.max(startPos.getX(), stopPos.getX());
		int startY = Math.min(startPos.getY() + 1, stopPos.getY());
		int stopY = Math.max(startPos.getY() - 1, stopPos.getY());
		int startZ = Math.min(startPos.getZ(), stopPos.getZ());
		int stopZ = Math.max(startPos.getZ(), stopPos.getZ());

		if("stairs".equalsIgnoreCase(this.dungeonSector.type)) {
			startY = Math.max(1, startPos.getY() - (size.getY() * 2));
		}

		for(int layerIndex : this.dungeonSector.wall.layers.keySet()) {
			SectorLayer layer = this.dungeonSector.wall.layers.get(layerIndex);
			for(int y = startY; y <= stopY; y++) {
				// Y Limit:
				if(y <= 0 || y >= world.getHeight()) {
					continue;
				}

				// Get Row:
				List<Character> row = layer.getRow(y - startY, stopY - startY);

				// Build Front/Back:
				for(int x = startX; x <= stopX; x++) {
					char buildChar = layer.getColumn(y - startY, stopY - startY, x - startX, stopX - startX, row);
					IBlockState blockState = this.theme.getWall(this, buildChar, random);
					if(blockState.getBlock() != Blocks.AIR) {
						this.placeBlock(world, chunkPos, new BlockPos(x, y, startZ + layerIndex), blockState, EnumFacing.SOUTH, random);
						this.placeBlock(world, chunkPos, new BlockPos(x, y, stopZ - layerIndex), blockState, EnumFacing.NORTH, random);
					}
				}

				// Build Left/Right:
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(y - startY, stopY - startY, z - startZ, stopZ - startZ, row);
					IBlockState blockState = this.theme.getWall(this, buildChar, random);
					if(blockState.getBlock() != Blocks.AIR) {
						this.placeBlock(world, chunkPos, new BlockPos(startX + layerIndex, y, z), blockState, EnumFacing.EAST, random);
						this.placeBlock(world, chunkPos, new BlockPos(stopX - layerIndex, y, z), blockState, EnumFacing.WEST, random);
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
				List<Character> row = layer.getRow(x - startX, stopX - startX);
				for(int z = startZ; z <= stopZ; z++) {
					char buildChar = layer.getColumn(x - startX, stopX - startX, z - startZ, stopZ - startZ, row);
					BlockPos buildPos = new BlockPos(x, y, z);
					IBlockState blockState = this.theme.getCeiling(this, buildChar, random);
					if(blockState.getBlock() != Blocks.AIR)
						this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.DOWN, random);
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

		Vec3i size = this.getRoomSize();
		int centerX = startPos.getX() + Math.round((float)size.getX() / 2);
		int centerZ = startPos.getZ() + Math.round((float)size.getZ() / 2);

		int startX = centerX - 1;
		int stopX = centerX + 1;
		int startY = Math.min(startPos.getY(), stopPos.getY());
		int stopY = Math.max(1, startPos.getY() - (size.getY() * 2));

		int startZ = centerZ - 1;
		int stopZ = centerZ + 1;

		IBlockState floorBlockState = this.theme.getFloor(this, 'B', random);
		IBlockState stairsBlockState = this.stairBlock;

		for(int y = startY; y >= stopY; y--) {
			for(int x = startX; x <= stopX; x++) {
				for(int z = startZ; z <= stopZ; z++) {
					IBlockState blockState = Blocks.AIR.getDefaultState();

					// Center:
					if(x == centerX && z == centerZ) {
						blockState = this.theme.getWall(this, 'B', random);
					}

					// Spiral Stairs:
					int step = startY - y % 8;
					int offsetX = x - startX;
					int offsetZ = z - startZ;
					if(step % 4 == 0) {
						if (offsetX == 0 && offsetZ == 0) {
							blockState = floorBlockState;
						}
						else if (offsetX == 0 && offsetZ == 1) {
							blockState = stairsBlockState;
						}
					}
					if(step % 4 == 1) {
						if (offsetX == 0 && offsetZ == 2) {
							blockState = floorBlockState;
						}
						else if (offsetX == 1 && offsetZ == 2) {
							blockState = stairsBlockState.withRotation(Rotation.COUNTERCLOCKWISE_90);
						}
					}
					if(step % 4 == 2) {
						if (offsetX == 2 && offsetZ == 2) {
							blockState = floorBlockState;
						}
						else if (offsetX == 2 && offsetZ == 1) {
							blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_180);
						}
					}
					if(step % 4 == 3) {
						if (offsetX == 2 && offsetZ == 0) {
							blockState = floorBlockState;
						}
						else if (offsetX == 1 && offsetZ == 0) {
							blockState = stairsBlockState.withRotation(Rotation.CLOCKWISE_90);
						}
					}

					BlockPos buildPos = new BlockPos(x, y, z);
					this.placeBlock(world, chunkPos, buildPos, blockState, EnumFacing.UP, random);
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
			bounds = " Bounds: " + this.getOccupiedBoundsMin() + " to " + this.getOccupiedBoundsMax();
		}
		String size = " Occupies: " + this.getOccupiedSize();
		return "Sector Instance Type: " + (this.dungeonSector == null ? "Unset" : this.dungeonSector.type) + " Parent Connector Pos: " + (this.parentConnector == null ? "Unset" : this.parentConnector.position) + size + bounds;
	}
}
