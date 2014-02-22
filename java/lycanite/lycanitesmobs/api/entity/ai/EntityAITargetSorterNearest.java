package lycanite.lycanitesmobs.api.entity.ai;

import java.util.Comparator;
import net.minecraft.entity.Entity;

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
    public int compare(Object par1Obj, Object par2Obj) {
        return this.compareDistanceSq((Entity)par1Obj, (Entity)par2Obj);
    }
    
    
    // ==================================================
  	//                   Compare Distance
  	// ==================================================
    public int compareDistanceSq(Entity targetA, Entity targetB) {
        double distanceA = this.host.getDistanceSqToEntity(targetA);
        double distanceB = this.host.getDistanceSqToEntity(targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
    }
}
