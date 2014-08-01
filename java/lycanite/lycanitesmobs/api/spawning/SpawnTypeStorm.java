package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import net.minecraft.world.World;

public class SpawnTypeStorm extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeStorm(String typeName) {
        super(typeName);
        CustomSpawner.instance.updateSpawnTypes.add(this);
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
        if(this.rate == 0 || tick % this.rate != 0)
            return false;
    	if(!world.isRaining())
    		return false;
    	double chance = this.chance;
    	if(world.isThundering())
    		chance = Math.min(chance * 2, 1.0D);
        if(world.rand.nextDouble() >= this.chance)
            return false;
        return true;
    }


    // ==================================================
    //               Get Spawn Coordinates
    // ==================================================
    @Override
    public List<int[]> getSpawnCoordinates(World world, int x, int y, int z) {
        return this.searchForBlockCoords(world, x, y, z); //TODO Implement searchForGroundCoords()
    }
}
