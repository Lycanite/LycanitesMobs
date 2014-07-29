package lycanite.lycanitesmobs.api.spawning;

import java.util.Comparator;

public class CoordSorterNearest implements Comparator {
    public int[] coord;
	
    // ==================================================
  	//                    Constructor
  	// ==================================================
    public CoordSorterNearest(int[] coord) {
        this.coord = coord;
    }
    
    
    // ==================================================
  	//                     Compare
  	// ==================================================
	@Override
	public int compare(Object targetA, Object targetB) {
		double distanceA = this.getDistanceSqCoord((int[])targetA);
        double distanceB = this.getDistanceSqCoord((int[])targetB);
        return distanceA < distanceB ? -1 : (distanceA > distanceB ? 1 : 0);
	}
	
	
    // ==================================================
  	//                  Get Distance
  	// ==================================================
    public double getDistanceSqCoord(int[] targetCoord) {
        double d0 = this.coord[0] - targetCoord[0];
        double d1 = this.coord[1] - targetCoord[1];
        double d2 = this.coord[2] - targetCoord[2];
        return d0 * d0 + d1 * d1 + d2 * d2;
    }
}
