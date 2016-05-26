package lycanite.lycanitesmobs.api.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Comparator;

public class TargetSorterNearest implements Comparator {
    private final Entity host;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================

    public TargetSorterNearest(Entity setHost) {
        this.host = setHost;
    }
    
    
    // ==================================================
   	//                      Compare
   	// ==================================================
    public int compare(Object objectA, Object objectB) {
    	if(objectA instanceof Entity && objectB instanceof Entity)
    		return this.compareDistanceSq((Entity)objectA, (Entity)objectB);
    	if(objectA instanceof BlockPos && objectB instanceof BlockPos)
    		return this.compareDistanceSq((BlockPos)objectA, (BlockPos)objectB);
    	return 0;
    }
    
    
    // ==================================================
  	//                   Compare Distance
  	// ==================================================
    public int compareDistanceSq(Entity targetA, Entity targetB) {
        double distanceA = this.host.getDistanceSqToEntity(targetA);
        double distanceB = this.host.getDistanceSqToEntity(targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }

    public int compareDistanceSq(BlockPos targetA, BlockPos targetB) {
        BlockPos hostCoords = new BlockPos((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ);
        double distanceA = hostCoords.getDistance(targetA.getX(), targetA.getY(), targetA.getZ());
        double distanceB = hostCoords.getDistance(targetB.getX(), targetB.getY(), targetB.getZ());
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }
}
