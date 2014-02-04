package DeepBlue;

import battlecode.common.MapLocation;
/**
 * Directly from the BattleCode 2014 Lectures
 */
public class VectorFunctions {
    static final int LOC_MOD = 1000;


    public static MapLocation findClosest(MapLocation[] manyLocs, MapLocation point){
		int closestDist = 10000000;
		int challengerDist = closestDist;
		MapLocation closestLoc = null;
		for(MapLocation m:manyLocs){
			challengerDist = point.distanceSquaredTo(m);
			if(challengerDist<closestDist){
				closestDist = challengerDist;
				closestLoc = m;
			}
		}
		return closestLoc;
	}
	public static MapLocation mladd(MapLocation m1, MapLocation m2){
		return new MapLocation(m1.x+m2.x,m1.y+m2.y);
	}
	
	public static MapLocation mldivide(MapLocation bigM, int divisor){
		return new MapLocation(bigM.x/divisor, bigM.y/divisor);
	}
	
	public static MapLocation mlmultiply(MapLocation bigM, int factor){
		return new MapLocation(bigM.x*factor, bigM.y*factor);
	}
	
	public static int locToInt(MapLocation m){
		return (m.x*LOC_MOD + m.y);
	}
	
	public static MapLocation intToLoc(int i){
		return new MapLocation(i/LOC_MOD,i%LOC_MOD);
	}

	public static MapLocation bigBoxCenter(MapLocation bigBoxLoc, int bigBoxSize){
		return mladd(mlmultiply(bigBoxLoc,bigBoxSize),new MapLocation(bigBoxSize/2,bigBoxSize/2));
	}
}

