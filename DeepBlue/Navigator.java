package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/13/14.
 *
 */
public class Navigator {

    static final int DIRECTION_WEIGHT_TRAIL = -1;

    RobotController rc;
    UnitCache cache;
    RoadMap map;

    MapLocation destination;
    double directions[];
    Direction heading;
    double stayPut;
    RoadMap.PathingStrategy pathStrat;

    boolean bugging;
    boolean bugLeft;
    int bugStartDistanceFromDestination;

    Navigator(RobotController inRc,UnitCache inCache,RoadMap inMap)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        map.observingNavigator = this;

        destination = rc.getLocation();
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        stayPut = 999;
        pathStrat = RoadMap.PathingStrategy.DefaultBug;
        bugging = false;
        bugLeft = false;
        bugStartDistanceFromDestination = 0;
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
        rc.setIndicatorString(0, "Near Node "+map.idForNearestNode(rc.getLocation()));

        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
            defaultMovement();
        else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
            smartMovement();
        else
            smartMovement();
    }

    public void pathingStrategyChanged() throws GameActionException
    {
        System.out.println("New Path to Destination ["+map.pathingStrat+"]");
    }

    public void setDestination(MapLocation location) throws GameActionException
    {
        destination = location;
    }

    private void bugMove() throws GameActionException
    {
        if (bugLeft) {
            // If we run into a wall
            while (!rc.canMove(heading))
                heading = heading.rotateRight();

            // keep in contact with left wall
            if (rc.canMove(heading.rotateLeft()))
                heading = heading.rotateLeft();

            if (!rc.isActive()) rc.yield();
            rc.move(heading);
        } else {
            // If we run into a wall
            while (!rc.canMove(heading))
                heading = heading.rotateLeft();

            // keep in contact with left wall
            if (rc.canMove(heading.rotateRight()))
                heading = heading.rotateRight();

            if (!rc.isActive()) rc.yield();
            rc.move(heading);
        }
    }



    //================================================================================
    // Default Methods
    //================================================================================

    public void defaultMovement() throws GameActionException
    {
        if (!bugging) {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc);
            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                    rc.move(heading);
            } else {
                bugging = true;
                bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                bugLeft = !bugLeft;
            }
        } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
            bugging = false;

        if (bugging)
            bugMove();
    }




    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartMovement() throws GameActionException
    {
//        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
//        MapLocation myLoc = rc.getLocation();
//        int[] costs = map.ordinalDirectionCosts(myLoc);
//
//        // Weight for snail trail direction
//        directions[map.directionTo(myLoc, destination).ordinal()] += DIRECTION_WEIGHT_TRAIL;
//
//        // Add cost for each space
//        for (int i=0;i<8;i++) {
//            directions[i] += costs[i];
//        }
//

        if (!bugging) {
            heading = map.directionTo(rc.getLocation(), destination);

            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            } else {
                bugging = true;
                bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                bugLeft = Path.shouldBugLeft(map, rc.getLocation(), destination);
            }
        } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
            bugging = false;

        if (bugging)
            bugMove();
    }




    //================================================================================
    // Macro Methods
    //================================================================================

    public void macroDirectionAssessment() throws GameActionException
    {
        directions = new double[]{ 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        MapLocation myLoc = rc.getLocation();

        // Weight for snail trail direction
        directions[MicroPathing.getNextDirection(myLoc.directionTo(destination), true, rc).ordinal()] += DIRECTION_WEIGHT_TRAIL;
        directions[map.directionTo(myLoc, destination).ordinal()] += DIRECTION_WEIGHT_TRAIL;
    }
}
