package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/13/14.
 *
 */
public class Navigator {

    static final int DIRECTION_WEIGHT_TRAIL = 3;

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
        if (passive) {
            maneuver();
            return;
        } else
            directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

        // Movement while in range of enemies
    }

    /*
     * Goes forward with Macro Pathing to destination, and getting closer to friendly units
     */
    public void maneuver() throws GameActionException
    {
        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
            defaultDirectionAssessment();
        else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
            smartDirectionAssessment();
        else
            flowDirectionAssessment();
    }

    /*
     *
     */
    public void tryMove() throws GameActionException
    {
        rc.setIndicatorString(0, "Strategy: " + (map.pathingStrat == RoadMap.PathingStrategy.FlowBug ? "Flow" : "Not Flow"));
        rc.setIndicatorString(1, "North: " + directions[0] + ", NE: " + directions[1] + ", East: " + directions[2] + ", SE: " + directions[3]);
        rc.setIndicatorString(2, "South: " + directions[4] + ", SW: " + directions[5] + ", West: " + directions[6] + ", NW: " + directions[7]);

        if (rc.isActive()) {
            Direction dir = bestDirection();
            if (rc.canMove(dir))
                rc.move(dir);
        }
    }

    public Direction bestDirection()
    {
        double min = stayPut;
        Direction bestDirection = null;

        for (int i = 0; i < 8; ++i) {
            if (min <= directions[i]) continue;
            min = directions[i];
            bestDirection = Utilities.directionByOrdinal[i];
        }
        return bestDirection;
    }

    public void setDestination(MapLocation location) throws GameActionException
    {
        destination = location;
    }

    //================================================================================
    // Default Methods
    //================================================================================

    public void defaultDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    }

    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
    }

    //================================================================================
    // Flow Methods
    //================================================================================

    public void flowDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        directions[MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc).ordinal()] -= DIRECTION_WEIGHT_TRAIL;

        for (int i=0; i<8; i++) {
            directions[i] += map.flowValueForLocation(rc.getLocation().add(Utilities.directionByOrdinal[i]));
        }
    }
}
