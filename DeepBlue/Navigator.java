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

    Navigator(RobotController inRc,UnitCache inCache,RoadMap inMap)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        destination = null;
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        stayPut = 0.0;
    }

    public boolean engaging() throws GameActionException
    {
        return cache.nearbyEnemies().length > 0;
    }

    /*
     * Micro Movements based on enemy contact
     */
    public void adjustFire() throws GameActionException
    {

    }

    /*
     * Goes forward with Macro Pathing to destination, and getting closer to friendly units
     */
    public void maneuver(boolean passive) throws GameActionException
    {
        Direction direction = null;
        MapLocation rcLocation = rc.getLocation();

        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug) {
            MicroPathing.getNextDirection(rc.getLocation().directionTo(rc.senseEnemyHQLocation()), true, rc);
        }

    }

    /*
     *
     */
    public void tryMove() throws GameActionException
    {

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
