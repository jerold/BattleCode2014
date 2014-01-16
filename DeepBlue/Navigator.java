package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/13/14.
 *
 */
public class Navigator {

    static final int DIRECTION_WEIGHT_TRAIL = -30;

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
        stayPut = 999;
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
        else {
            flowDirectionAssessment();
            tryMove();
        }
    }

    /*
     *
     */
    public void tryMove() throws GameActionException
    {
//        rc.setIndicatorString(0, "Strategy: " + (map.pathingStrat == RoadMap.PathingStrategy.FlowBug ? "Flow" : "Not Flow"));
//        rc.setIndicatorString(1, "N: " + (int)directions[0] +
//                ", NE: " + (int)directions[1] +
//                ", E: " + (int)directions[2] +
//                ", SE: " + (int)directions[3] +
//                ", S: " + (int)directions[4] +
//                ", SW: " + (int)directions[5] +
//                ", W: " + (int)directions[6] +
//                ", NW: " + (int)directions[7]);

        if (rc.isActive()) {
            Direction dir = bestDirection();
//            rc.setIndicatorString(2, "Direction: " + dir);
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

        // Weight for snail trail direction
        directions[MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc).ordinal()] += DIRECTION_WEIGHT_TRAIL;
    }

    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

        // Weight for snail trail direction
        directions[MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc).ordinal()] += DIRECTION_WEIGHT_TRAIL;

        for (int i=0; i<8; i++) {
            // Distance weights
            directions[i] += Utilities.distanceBetweenTwoPoints(destination, rc.getLocation().add(Utilities.directionByOrdinal[i]));
        }
    }

    //================================================================================
    // Flow Methods
    //================================================================================

    public void flowDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };

        // Weight for snail trail direction
        directions[MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc).ordinal()] += DIRECTION_WEIGHT_TRAIL;

        for (int i=0; i<8; i++) {
            // Flow weights
            directions[i] += map.flowValueForLocation(rc.getLocation().add(Utilities.directionByOrdinal[i]));

            // Distance weights
            directions[i] += Utilities.distanceBetweenTwoPoints(destination, rc.getLocation().add(Utilities.directionByOrdinal[i]));
        }
   }
}
