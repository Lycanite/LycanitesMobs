package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.core.dungeon.definition.DungeonSchematic;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import com.lycanitesmobs.core.dungeon.definition.DungeonTheme;
import net.minecraft.util.math.Vec3i;

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
	 * @param dungeonSector The Dungeon Sector to create this instance from.
	 * @param parentConnector The Sector Connector this is connected to.
	 * @param random The instance of Random to use.
	 */
	public SectorInstance(DungeonLayout layout, DungeonSector dungeonSector, SectorConnector parentConnector, Random random) {
		this.layout = layout;
		this.dungeonSector = dungeonSector;
		this.parentConnector = parentConnector;

		// Size:
		this.roomSize = this.dungeonSector.getRandomSize(random);
		this.collisionSize = new Vec3i(
				this.roomSize.getX() + this.dungeonSector.padding.getX(),
				this.roomSize.getY() + this.dungeonSector.padding.getY(),
				this.roomSize.getZ() + this.dungeonSector.padding.getZ()
		);

		// Connectors:
		// TODO Connectors

		// Theme:
		if(this.dungeonSector.changeTheme || this.parentConnector.parentSector == null) {
			this.layout.dungeonInstance.schematic.getRandomTheme(random);
		}
		else {
			this.theme = this.parentConnector.parentSector.theme;
		}

		// Structures:
		// TODO Structures
	}


	/**
	 * Returns a random child connector.
	 * @param random The instance of Random to use.
	 * @return A random connector.
	 */
	public SectorConnector getRandomConnector(Random random) {
		if(this.connectors.isEmpty()) {
			return null;
		}
		if(this.connectors.size() == 1) {
			return this.connectors.get(0);
		}
		else {
			return this.connectors.get(random.nextInt(this.connectors.size()));
		}
	}
}
