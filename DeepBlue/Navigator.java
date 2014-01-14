package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/13/14.
 *
 */
public class Navigator {
    RobotController rc;
    UnitCache cache;
    RoadMap map;

    MapLocation destination;
    double directions[];
    double stayPut;
    RoadMap.PathingStrategy pathStrat;

    Navigator(RobotController inRc,UnitCache inCache,RoadMap inMap)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        destination = rc.getLocation();
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        stayPut = 0.0;
        pathStrat = RoadMap.PathingStrategy.DefaultBug;
    }

    public boolean engaging() throws GameActionException
    {
        return cache.nearbyEnemies().length > 0;
    }

    /*
     * Micro Movements based on enemy contact
     */
    public void adjustFire(boolean passive) throws GameActionException
    {

    }

    /*
     * Goes forward with Macro Pathing to destination, and getting closer to friendly units
     */
    public void maneuver() throws GameActionException
    {
        for (int i=0; i<8; i++) {

        }

        Direction direction = null;
        MapLocation rcLocation = rc.getLocation();

        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug) {
            int ordinalDirection = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc).ordinal();
            directions[ordinalDirection] += 1;
        }

    }

    /*
     *
     */
    public void tryMove() throws GameActionException
    {
//        rc.move(bestDirection());
//        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    }

    public Direction bestDirection()
    {
        double max = stayPut;
        Direction bestDirection = null;

        for (int i = 0; i < 8; ++i) {
            if (max >= directions[i]) continue;
            max = directions[i];
            bestDirection = Utilities.directionByOrdinal[i];
        }
        return bestDirection;
    }

    public void setDestination(MapLocation location) throws GameActionException
    {
        destination = location;
    }
}
