package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Comparator;

import net.minecraft.entity.Entity;
import net.minecraft.util.ChunkCoordinates;

public class EntityAITargetSorterNearest implements Comparator {
    private final Entity host;
    
    // ==================================================
  	//                    Constructor
  	// ==================================================

    public EntityAITargetSorterNearest(Entity setHost) {
        this.host = setHost;
    }
    
    
    // ==================================================
   	//                      Compare
   	// ==================================================
    public int compare(Object objectA, Object objectB) {
    	if(objectA instanceof Entity && objectB instanceof Entity)
    		return this.compareDistanceSq((Entity)objectA, (Entity)objectB);
    	if(objectA instanceof ChunkCoordinates && objectB instanceof ChunkCoordinates)
    		return this.compareDistanceSq((ChunkCoordinates)objectA, (ChunkCoordinates)objectB);
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

    public int compareDistanceSq(ChunkCoordinates targetA, ChunkCoordinates targetB) {
    	ChunkCoordinates hostCoords = new ChunkCoordinates((int)this.host.posX, (int)this.host.posY, (int)this.host.posZ);
        double distanceA = hostCoords.getDistanceSquaredToChunkCoordinates(targetA);
        double distanceB = hostCoords.getDistanceSquaredToChunkCoordinates(targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }
}
