package com.lycanitesmobs.core.dungeon.instance;

import net.minecraft.util.math.BlockPos;

public class SectorConnector {
	/** Sector Connectors link together one sector to another, each sector type has one or more connectors as well as a parent connector. */

	/** The block position of this connector. **/
	public BlockPos position;

	/** The Sector Instance that this connector belongs to. Null when this is used as the starting connector. **/
	public SectorInstance parentSector;

	/** The Sector Instance that is connected to this connector. Can be null if the connection is empty. **/
	public SectorInstance childSector;

	/** Set to true when this connector is connected to a child sector or cannot be connected to anything (usually due to collisions). **/
	public boolean closed = false;


	public SectorConnector(BlockPos position, SectorInstance parentSector) {
		this.position = position;
		this.parentSector = parentSector;
	}
}
