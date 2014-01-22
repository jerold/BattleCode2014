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
    int destinationNodeId;
    MapLocation nextStep;
    int nextStepNodeId;
    boolean hasArrived;

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
        destinationNodeId = 0;
        nextStep = destination;
        nextStepNodeId = 0;
        hasArrived = false;

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
        } else {
            // Movement while in range of enemies
        }
    }

    /*
     * Goes forward with Macro Pathing to destination, and getting closer to friendly units
     */
    public void maneuver() throws GameActionException
    {
        if (!hasArrived) {
            if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
                defaultMovement();
            else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
                smartMovement();
            if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
//                macroMovement();
                smartMovement();
            }
        }
//        rc.setIndicatorString(1, "Pathing["+map.pathingStrat+"] NextStep("+nextStepNodeId+")["+nextStep.x+","+nextStep.y+"] Dest("+destinationNodeId+")["+destination.x+","+destination.y+"]");
    }




    //================================================================================
    // Set and Check Destination Methods
    //================================================================================

    public void pathingStrategyChanged() throws GameActionException
    {
        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
            bugging = false;
            setDestination(destination);
        }
    }

    public void setDestination(MapLocation location) throws GameActionException
    {
        destination = location;
        depart();
    }

    private void arrive()
    {
        hasArrived = true;
    }

    private void depart() throws GameActionException
    {
        hasArrived = false;
        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
            destinationNodeId = map.idForNearestNode(destination);
            readBroadcastNextStep();
        }
    }

    private void readBroadcastNextStep() throws GameActionException
    {
        int nearRcNodeId = map.idForNearestNode(rc.getLocation());
        int channel = Utilities.startMacroChannels + nearRcNodeId*map.nodeCount+destinationNodeId;
        MapLocation signal = VectorFunctions.intToLoc(rc.readBroadcast(channel));
        nextStepNodeId = signal.x;
        nextStep = map.locationForNode(nextStepNodeId);
        System.out.println("SOLDIER Node["+nearRcNodeId+"] -> ["+nextStepNodeId+"] -> ["+destinationNodeId+"]  ~"+(Utilities.startMacroChannels+nearRcNodeId*map.nodeCount+destinationNodeId)+"~  "+signal.x+","+signal.y);
    }

    public MapLocation getNextStep() throws GameActionException
    {
        if (atFinalDestination())
            return destination;
        if (rc.getLocation().isAdjacentTo(nextStep))
            readBroadcastNextStep();
        return nextStep;
    }

    private boolean atFinalDestination() throws GameActionException
    {
        if (hasArrived)
            return true;
        if (rc.getLocation().isAdjacentTo(destination)) {
            arrive();
            return true;
        }
        return false;
    }

//    public void addWaypoint(MapLocation location) throws GameActionException
//    {
//        destination = location;
//        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
//            MapLocation[] extension = Path.getMacroPath(map, pathToDestination[pathToDestination.length-1], destination);
//            MapLocation[] fullPath = new MapLocation[pathToDestination.length - 1 + extension.length];
//            for(int i=0; i<pathToDestination.length-1;i++)
//                fullPath[i] = pathToDestination[i];
//            for(int i=0; i<extension.length;i++)
//                fullPath[pathToDestination.length-1+i] = pathToDestination[i];
//            pathToDestination = fullPath;
//        }
//    }

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
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc); // Heading set with slime tail avoidance
            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                    rc.move(heading);
            } else {
                bugging = true;
                bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                bugLeft = !bugLeft; // bugging direction is a basic flip-flop
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
        if (!bugging) {
            MicroPathing.addLocationToTrail(rc.getLocation()); // Snail Trail
            heading = map.directionTo(rc.getLocation(), destination); // Heading set by the map to avoid void space

            if(MicroPathing.canMove(heading, true, rc) && rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            } else {
                heading = rc.getLocation().directionTo(destination);
                bugging = true;
                bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                bugLeft = Path.shouldBugLeft(map, rc.getLocation(), destination); // Bugging is pre-simulated to pick the shortest direction
            }
        } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
            bugging = false;

        if (bugging)
            bugMove();
    }




    //================================================================================
    // Macro Methods
    //================================================================================

    public void macroMovement() throws GameActionException
    {
        if (!bugging) {
            MicroPathing.addLocationToTrail(rc.getLocation()); // Snail Trail
            heading = map.directionTo(rc.getLocation(), getNextStep()); // Heading is either a straight shot or a path built from the pre-computed breadth-first node paths

            if(MicroPathing.canMove(heading, true, rc) && rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            } else {
                heading = rc.getLocation().directionTo(destination);
                bugging = true;
                bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                bugLeft = Path.shouldBugLeft(map, rc.getLocation(), destination); // Bugging is pre-simulated to pick the shortest direction
            }
        } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
            bugging = false;

        if (bugging)
            bugMove();
    }
}
