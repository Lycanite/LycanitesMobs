package lycanite.lycanitesmobs.api.spawning;

import java.util.List;

import net.minecraft.world.World;

public class SpawnTypeRock extends SpawnTypeBase {

    // ==================================================
    //                     Constructor
    // ==================================================
    public SpawnTypeRock(String typeName) {
        super(typeName);
        CustomSpawner.instance.oreBreakSpawnTypes.add(this);
    }


    // ==================================================
    //                 Check Spawn Chance
    // ==================================================
    @Override
    public boolean canSpawn(long tick, World world, int x, int y, int z) {
        if(world.rand.nextDouble() >= this.chance)
            return false;
        return true;
    }


    // ==================================================
    //                 Order Coordinates
    // ==================================================
    @Override
    public List<int[]> orderCoords(List<int[]> coords, int x, int y, int z) {
        return this.orderCoordsCloseToFar(coords, x, y, z);
    }
}
