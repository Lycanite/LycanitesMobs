package lycanite.lycanitesmobs.api.spawning;

import lycanite.lycanitesmobs.LycanitesMobs;
import lycanite.lycanitesmobs.api.config.ConfigBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
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
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, BlockPos originPos, boolean rare) {
        if(!super.canSpawn(tick, world, originPos, rare))
            return false;
        if(world.provider.getDimension() == 1) // Seconds fail chance for The End.
            return world.rand.nextDouble() >= this.chance;
        return true;
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
     * @param pos Spawn origin position.
     * @return A list of int arrays, each array should contain 3 integers of x, y and z. Should return an empty list instead of null else a waning will show.
     */
    @Override
    public List<BlockPos> getSpawnCoordinates(World world, BlockPos pos) {
        if(world.provider.getDimension() == 1) // Act as Land Spawn Type in The End.
            return super.getSpawnCoordinates(world, pos);

    	List<BlockPos> blockCoords = null;
        int range = this.getRange(world);
        BlockPos originPos = new BlockPos(pos.getX(), this.getYLevelForWorld(world), pos.getZ());

        for(int i = 0; i < this.blockLimit; i++) {
            BlockPos chunkCoords = this.getRandomUndergroundLandCoord(world, originPos, range);
        	if(chunkCoords != null) {
        		if(blockCoords == null)
        			blockCoords = new ArrayList<BlockPos>();
        		blockCoords.add(chunkCoords);
        	}
        }
        
        return blockCoords;
    }


    // ==================================================
    //               Get Y Level For World
    // ==================================================
    public int getYLevelForWorld(World world) {
        if(this.dimensionYLevels.containsKey(world.provider.getDimension()))
            return this.dimensionYLevels.get(world.provider.getDimension());
        return this.defaultYLevel;
    }


    // ==================================================
    //      Get Random Underground Land Spawn Coord
    // ==================================================
    /** Gets a random spawn position from a the provided origin chunk position.
     * @param world The world to search for coordinates in.
     * @return Returns a ChunkPosition or null if no coord was found.
     */
    public BlockPos getRandomUndergroundLandCoord(World world, BlockPos originPos, int range) {
        int radius = Math.round(range * 0.5F);
        int[] xz = this.getRandomXZCoord(world, originPos.getX(), originPos.getZ(), rangeMin, range);
        int x = xz[0];
        int z = xz[1];
        int y = this.getRandomYCoord(world, new BlockPos(x, 0, z), rangeMin, originPos.getY(), true, Blocks.AIR, true);
        return y > -1 ? new BlockPos(x, y, z) : null;
    }
}
