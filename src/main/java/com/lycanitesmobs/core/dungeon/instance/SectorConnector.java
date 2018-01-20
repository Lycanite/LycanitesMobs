package com.lycanitesmobs.core.dungeon.instance;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class SectorConnector {
	/** Sector Connectors link together one sector to another, each sector type has one or more connectors as well as a parent connector. */

	/** The block position of this connector. **/
	public BlockPos position;

	/** The Sector Instance that this connector belongs to. Null when this is used as the starting connector. **/
	public SectorInstance parentSector;

	/** The Sector Instance that is connected to this connector. Can be null if the connection is empty. **/
	public SectorInstance childSector;

	/** The rotation of this connector. Where 0 is +Z as forwards. Only works with exact intervals of 90. **/
	public int rotation = 0;

	/** Set to true when this connector is connected to a child sector or cannot be connected to anything (usually due to collisions). **/
	public boolean closed = false;


	/**
	 * Constructor
	 * @param position The block position of this connector.
	 * @param parentSector The sector that this connector belongs to. If null, collision checks are skipped.
	 */
	public SectorConnector(BlockPos position, SectorInstance parentSector, int rotation) {
		this.position = position;
		this.parentSector = parentSector;
	}


	/**
	 * Returns true if this connector can be connected to.
	 * @param dungeonLayout The dungeon layout using this connector.
	 * @param sectorInstance If set, the specific sector instance to connect to, if null then collision checks are ignored.
	 * @return
	 */
	public boolean canConnect(DungeonLayout dungeonLayout, SectorInstance sectorInstance) {
		if(sectorInstance == null) {
			return !this.closed;
		}

		// Connect To Sector Instance:
		for(SectorInstance nearbySector : sectorInstance.getNearbySectors()) {
			if(sectorInstance.collidesWith(nearbySector)) {
				return false;
			}
		}

		return true;
	}
}