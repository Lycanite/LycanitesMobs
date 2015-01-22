package lycanite.lycanitesmobs.api.spawning;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SpawnTypeUnderground extends SpawnTypeLand {
    public String dimensionYLevelsSetup = "56; 0,56; -1,16; 1,64; 2,40; 7,40";
    public int defaultYLevel = 56;
    public Map<Integer, Integer> dimensionYLevels = new HashMap<Integer, Integer>();

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeUnderground(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }


    // ==================================================
    //                 Load from Config
    // ==================================================
    @Override
    public void loadFromConfig() {
        super.loadFromConfig();
        ConfigBase config = ConfigBase.getConfig(LycanitesMobs.group, "spawning");

        // ========== Y Level Config ==========
        this.dimensionYLevelsSetup = config.getString("Spawner Features", "Underground Spawn Dimension Y Levels", this.dimensionYLevelsSetup, "A list of Y levels for each dimension that should be used when determining the maximum spawn height for underground spawning. The first entry is the default to use, every other entry is a per dimension entry using this format: DefaultYLevel;DimensionID,YLevel;DimensionID,YLevel spaces will be ignored so can be used to make things look clearer.");
        boolean defaultSet = false;
        for(String dimensionYLevelEntry : this.dimensionYLevelsSetup.replace(" ", "").split(";")) {
            String[] dimensionYLevelEntryValues = dimensionYLevelEntry.split(",");

            // Get Default Y Level:
            if(!defaultSet) {
                if(NumberUtils.isNumber(dimensionYLevelEntryValues[0]))
                    this.defaultYLevel = Integer.parseInt(dimensionYLevelEntryValues[0]);
                defaultSet = true;
                continue;
            }

            // Get Dimension Y Level:
            if(!NumberUtils.isNumber(dimensionYLevelEntryValues[0]) || !NumberUtils.isNumber(dimensionYLevelEntryValues[1]))
                continue;
            this.dimensionYLevels.put(Integer.parseInt(dimensionYLevelEntryValues[0]), Integer.parseInt(dimensionYLevelEntryValues[1]));
        }
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    /**
     * Searches for coordinates to spawn mobs exactly at. By default this uses te block lists.
     * @param world The world to spawn in.
     * @param x X position.
     * @param y Y position.
     * @param z Z position
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
    	List<int[]> blockCoords = null;
        int range = this.getRange(world);
        ChunkPosition originPos = new ChunkPosition(x, this.getYLevelForWorld(world), z);

        for(int i = 0; i < this.blockLimit; i++) {
        	ChunkPosition chunkCoords = this.getRandomUndergroundLandCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<int[]>();
        		blockCoords.add(new int[] {chunkCoords.chunkPosX, chunkCoords.chunkPosY, chunkCoords.chunkPosZ});
        	}
        }
        
        return blockCoords;
    }


    // ==================================================
    //               Get Y Level For World
    // ==================================================
    public int getYLevelForWorld(World world) {
        if(this.dimensionYLevels.containsKey(world.provider.dimensionId))
            return this.dimensionYLevels.get(world.provider.dimensionId);
        return this.defaultYLevel;
    }


    // ==================================================
    //      Get Random Underground Land Spawn Coord
    // ==================================================
    /** Gets a random spawn position from a the provided origin chunk position.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition or null if no coord was found.
     */
    public ChunkPosition getRandomUndergroundLandCoord(World world, ChunkPosition originPos, int range) {
        int radius = Math.round(range * 0.5F);
        int[] xz = this.getRandomXZCoord(world, originPos.chunkPosX, originPos.chunkPosZ, rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, x, 0, z, rangeMin, originPos.chunkPosY, true, Blocks.air, true);
        return y > -1 ? new ChunkPosition(x, y, z) : null;
    }
}
