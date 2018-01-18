package com.lycanitesmobs.core.dungeon.instance;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.dungeon.definition.DungeonSector;
import net.minecraft.util.math.ChunkPos;

import java.util.*;

public class DungeonLayout {
	/** A Dungeon Layout is a procedurally generated collection of connecting Sectors with Structures and a Theme as well as other properties. **/

	/** The Dungeon Instance that this Layout belongs to. **/
	public DungeonInstance dungeonInstance;

	/** A list of generated Sector Instances. **/
	public List<SectorInstance> sectors = new ArrayList<>();

	/** A map that stores all sectors in each ChunkPos. Used for finding nearby sectors when collision detecting and when world generating. **/
	public Map<ChunkPos, List<SectorInstance>> sectorChunkMap = new HashMap<>();

	/** The starting connector to use, this should have no parent Sector Instance assigned. **/
	public SectorConnector originConnector;

	/** A list of open Sector Connectors for stemming from. **/
	List<SectorConnector> openConnectors = new ArrayList<>();


	/**
	 * Constructor
	 * @param dungeonInstance The Dungeon Instance that this Layout will belong to.
	 */
	public DungeonLayout(DungeonInstance dungeonInstance) {
		this.dungeonInstance = dungeonInstance;
	}


	/**
	 * Generates (or regenerates) this entire Dungeon Layout.
	 */
	public void generate(Random random) {
		LycanitesMobs.printDebug("Dungeon", "Starting Dungeon Instance Generation...");
		this.sectors.clear();
		this.sectorChunkMap.clear();
		this.openConnectors.clear();

		// Start:
		SectorInstance entranceSector = this.start(random);
		LycanitesMobs.printDebug("Dungeon", "Created Entrance Sector: " + entranceSector);

		// Levels:
		int yLevel = this.dungeonInstance.originPos.getY();
		SectorInstance exitSector = entranceSector;
		int level = 1;
		while(yLevel > 0) {
			int sectorCount = this.dungeonInstance.schematic.getRandomSectorCount(random);
			LycanitesMobs.printDebug("Dungeon", "Starting Level " + level + " - Sector Count: " + sectorCount);

			// Snake:
			int snakeCount = Math.round((float)sectorCount * 0.2f);
			exitSector = this.snake(random, exitSector, snakeCount);
			LycanitesMobs.printDebug("Dungeon", "Snake Sectors: " + snakeCount + " - From Sector: " + exitSector);
			if(exitSector.parentConnector == null) {
				yLevel = 0;
			}
			else {
				yLevel = exitSector.parentConnector.position.getY();
			}

			// Stem:
			sectorCount -= snakeCount;
			LycanitesMobs.printDebug("Dungeon", "Stem Sectors: " + sectorCount);
			while(sectorCount > 0) {
				sectorCount = this.stem(random, this.openConnectors.toArray(new SectorConnector[this.openConnectors.size()]), sectorCount).size();
			}

			LycanitesMobs.printDebug("Dungeon", "Completed Level " + level + ".");
			level++;
		}

		LycanitesMobs.printDebug("Dungeon", "Dungeon Instance Generation Complete!");
	}


	/**
	 * First Phase - Starts generation by creating an entrance.
	 * @param random The instance of Random to use.
	 * @return The generated entrance sector.
	 */
	public SectorInstance start(Random random) {
		this.originConnector = new SectorConnector(this.dungeonInstance.originPos, null, 90 * random.nextInt(4));
		DungeonSector entranceDungeonSector = this.dungeonInstance.schematic.getRandomSector("entrance", random);
		SectorInstance entranceSector = new SectorInstance(this, entranceDungeonSector, random);
		entranceSector.init(this.originConnector, random);
		this.sectors.add(entranceSector);

		return entranceSector;
	}


	/**
	 * Second Phase - Generates a linear set of sectors from the starting sector to a finishing sector.
	 * @param random The instance of Random to use.
	 * @param startSector The Sector Instance to snake from.
	 * @param length How many sectors to generate.
	 * @return The end sector.
	 */
	public SectorInstance snake(Random random, SectorInstance startSector, int length) {
		List<SectorInstance> generatedSectors = new ArrayList<>();
		SectorInstance lastSector = startSector;
		for(int i = 0; i < length; i++) {
			DungeonSector dungeonSector = this.dungeonInstance.schematic.getRandomSector("entrance", random);
			SectorInstance sectorInstance = new SectorInstance(this, dungeonSector, random);
			sectorInstance.init(lastSector.getRandomConnector(random, sectorInstance), random);
		}

		return generatedSectors.get(generatedSectors.size() - 1);
	}


	/**
	 * Third Phase - Generates sectors from the provided array of connectors.
	 * @param random The instance of Random to use.
	 * @param connectors A list of connectors to generate new sectors from.
	 * @param length How many sectors to generate.
	 * @return A list of generated sectors.
	 */
	public List<SectorInstance> stem(Random random, SectorConnector[] connectors, int length) {
		List<SectorInstance> generatedSectors = new ArrayList<>();
		// TODO Create Stem Generation.

		return generatedSectors;
	}


	/**
	 * Adds a Sector Instance to this Dungeon Layout for reference. Also adds all open Sector Connectors that it has.
	 * @param sectorInstance The Sector Instance to add.
	 */
	public void addSectorInstance(SectorInstance sectorInstance) {
		this.sectors.add(sectorInstance);
		for(ChunkPos chunkPos : sectorInstance.getChunkPositions()) {
			if(!this.sectorChunkMap.containsKey(chunkPos)) {
				this.sectorChunkMap.put(chunkPos, new ArrayList<>());
			}
			this.sectorChunkMap.get(chunkPos).add(sectorInstance);
		}
		this.openConnectors.addAll(sectorInstance.getOpenConnectors(null));
	}
}
