package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3i;
import org.apache.commons.lang3.math.NumberUtils;

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
		ChunkPos minChunkPos = new ChunkPos(this.getBoundsMin());
		ChunkPos maxChunkPos = new ChunkPos(this.getBoundsMax());
		List<ChunkPos> chunkPosList = new ArrayList<>();
		for(int x = minChunkPos.x; x <= maxChunkPos.x; x++) {
			for(int z = minChunkPos.z; z <= maxChunkPos.z; z++) {
				chunkPosList.add(new ChunkPos(x, z));
			}
		}
		return chunkPosList;
	}


	/**
	 * Returns the minimum xyz position that this Sector Instance occupies.
	 * @return The minimum bounds position (corner).
	 */
	public BlockPos getBoundsMin() {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds.add(
					-(int)Math.ceil((double)this.collisionSize.getX() / 2),
					0,
					0
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds.add(
					0,
					0,
					-(int)Math.ceil((double)this.collisionSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds.add(
					-(int)Math.ceil((double)this.collisionSize.getX() / 2),
					0,
					-this.collisionSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds.add(
					-this.collisionSize.getX(),
					0,
					-(int)Math.ceil((double)this.collisionSize.getZ() / 2)
			);
		}
		return bounds;
	}


	/**
	 * Returns the maximum xyz position that this Sector Instance occupies.
	 * @return The maximum bounds position (corner).
	 */
	public BlockPos getBoundsMax() {
		BlockPos bounds = new BlockPos(this.parentConnector.position);
		if(this.parentConnector.rotation == 0) {
			bounds.add(
					(int)Math.ceil((double)this.collisionSize.getX() / 2),
					this.collisionSize.getY(),
					this.collisionSize.getZ()
			);
		}
		else if(this.parentConnector.rotation == 90) {
			bounds.add(
					this.collisionSize.getX(),
					this.collisionSize.getY(),
					(int)Math.ceil((double)this.collisionSize.getZ() / 2)
			);
		}
		else if(this.parentConnector.rotation == 180) {
			bounds.add(
					(int)Math.ceil((double)this.collisionSize.getX() / 2),
					this.collisionSize.getY(),
					0
			);
		}
		else if(this.parentConnector.rotation == 270) {
			bounds.add(
					0,
					this.collisionSize.getY(),
					(int)Math.ceil((double)this.collisionSize.getZ() / 2)
			);
		}
		return bounds;
	}
}
