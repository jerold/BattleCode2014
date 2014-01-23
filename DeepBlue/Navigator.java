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

    MapLocation dog;
    Direction dogHeading;

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

    /*
     * Goes forward with Macro Pathing to destination, and getting closer to friendly units
     */
    public void maneuver() throws GameActionException
    {
        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
            defaultMovement();
        else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
            smartMovement();
        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
            macroMovement();
        }
        rc.setIndicatorString(1, "Dir["+heading+"] NextStep("+nextStepNodeId+")["+nextStep.x+","+nextStep.y+"] Dest("+destinationNodeId+")["+destination.x+","+destination.y+"] @["+hasArrived+"] bug["+bugging+"]["+bugLeft+"]");
    }




    //================================================================================
    // Set and Check Destination Methods
    //================================================================================

    public void pathingStrategyChanged() throws GameActionException
    {
        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
            setDestination(destination);
        }
    }

    public void setDestination(MapLocation location) throws GameActionException
    {
        bugging = false;
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

        boolean oppositeBroadcast = false;
        if (nearRcNodeId >= map.nodeCount/2) // to save space only half of the pathing is sent, so get the larger half details by fetching their opposite, then inverting
            oppositeBroadcast = true;

        int channel = Utilities.startMacroChannels+nearRcNodeId*map.nodeCount+destinationNodeId;
        if (oppositeBroadcast)
            channel = Utilities.startMacroChannels+map.oppositeNodeId(nearRcNodeId)*map.nodeCount+map.oppositeNodeId(destinationNodeId);
        MapLocation signal = VectorFunctions.intToLoc(rc.readBroadcast(channel));

        nextStepNodeId = signal.x == RoadMap.TILE_VOID ? RoadMap.NO_PATH_EXISTS : signal.x;
        if (oppositeBroadcast) // Opposite channel means opposite result.
            nextStepNodeId = map.oppositeNodeId(nextStepNodeId);

        nextStep = map.locationForNode(nextStepNodeId);
//        System.out.println("SOLDIER Node["+nearRcNodeId+"] -> ["+nextStepNodeId+"] -> ["+destinationNodeId+"]  ~"+(Utilities.startMacroChannels+nearRcNodeId*map.nodeCount+destinationNodeId)+"~  "+signal.x+","+signal.y);
    }

    public MapLocation getNextStep() throws GameActionException
    {
        if (atFinalDestination())
            return destination;

        if (dog.isAdjacentTo(nextStep))
            readBroadcastNextStep();
        if (Utilities.distanceBetweenTwoPoints(dog, nextStep) < Utilities.distanceBetweenTwoPoints(dog, destination))
            return nextStep;

//            if (rc.getLocation().isAdjacentTo(nextStep))
//            readBroadcastNextStep();
//        if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), nextStep) < Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination))
//            return nextStep;

        return destination;
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

    private boolean shouldBug() throws GameActionException
    {
        return MicroPathing.doubledBack() && map.valueForLocation(rc.getLocation().add(rc.getLocation().directionTo(destination))) == RoadMap.TILE_VOID;
    }

    private void bugMove() throws GameActionException
    {
        if (bugLeft) {
            if (map.valueForLocation(rc.getLocation().add(heading.opposite().rotateRight())) == RoadMap.TILE_VOID) heading = heading.rotateLeft().rotateLeft();
            while (!rc.canMove(heading)) heading = heading.rotateRight();
            if (rc.canMove(heading)) rc.move(heading);
        } else {
            if (map.valueForLocation(rc.getLocation().add(heading.opposite().rotateLeft())) == RoadMap.TILE_VOID) heading = heading.rotateRight().rotateRight();
            while (!rc.canMove(heading)) heading = heading.rotateLeft();
            if (rc.canMove(heading)) rc.move(heading);
        }
    }




    //================================================================================
    // Default Methods
    //================================================================================

    public void defaultMovement() throws GameActionException
    {
        if (!atFinalDestination()) {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc);
            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            }

//
//            if (!bugging) {
////                heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc); // Heading set with slime tail avoidance
//                heading = rc.getLocation().directionTo(destination);
//                if(rc.canMove(heading)) {
//                    if (!rc.isActive()) rc.yield();
//                    rc.move(heading);
//                }
//                if (shouldBug()) {
//                    bugging = true;
//                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
//                    bugLeft = !bugLeft; // bugging direction is a basic flip-flop
//                }
//            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
//                bugging = false;
//
//            if (rc.isActive() && bugging)
//                bugMove();
        }
    }




    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartMovement() throws GameActionException
    {
        if (!atFinalDestination()) {

            if (!bugging) {
                heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), destination), true, rc);
                if (!rc.isActive()) rc.yield();
                if (rc.canMove(heading)) rc.move(heading);
                if (shouldBug()) {
                    bugging = true;
                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                    bugLeft = !bugLeft; // bugging direction is a basic flip-flop
                }
            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
                bugging = false;

            if (rc.isActive() && bugging)
                bugMove();

//
//            if (!bugging) {
//                dog = rc.getLocation().add(map.directionTo(rc.getLocation(), destination)); // Heading set by the map to avoid void space
//
//                if (map.valueForLocation(dog.add(dog.directionTo(destination))) == RoadMap.TILE_VOID) { // Dog appears to be stuck!
//                    dogHeading = dog.directionTo(destination);
//                    bugging = true;
//                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
//                    bugLeft = Path.shouldBugLeft(map, dog, destination); // Bugging is pre-simulated to pick the shortest direction
//                }
//            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
//                bugging = false;
//
//            if (bugging) {
//                if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), dog) < 2) {
//                    MapLocation step1 = sniffOutNextStep(dogHeading, dog);
//                    MapLocation step2 = sniffOutNextStep(dog.directionTo(step1), step1);
//                    MapLocation step3 = sniffOutNextStep(step1.directionTo(step2), step2);
//                    dog = new MapLocation((step1.x+step2.x+step3.x)/3,(step1.y+step2.y+step3.y)/3);
//                }
//            }
//
//            rc.setIndicatorString(2, "Dog: "+dog.x+","+dog.y+" Bug["+bugging+"] dist: "+Utilities.distanceBetweenTwoPoints(rc.getLocation(), dog));
//
//            heading = smartDirection(); // We follow the dog, and the dog does the bugging when needed.
//            if(rc.canMove(heading)) {
//                if (!rc.isActive()) rc.yield();
//                rc.move(heading);
//            } else {
//                System.out.println("I R Stuck");
//            }
        }
    }




    //================================================================================
    // Macro Methods
    //================================================================================

    public void macroMovement() throws GameActionException
    {
        if (!atFinalDestination()) {
            dog = rc.getLocation();

            MapLocation nextStep = getNextStep();
            int stepCount = 3;
            for (int i=0; i<stepCount; i++) {
                dog = dog.add(map.directionTo(dog, nextStep));
            }

            heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), dog), true, rc); // Heading is either a straight shot or a path built from the pre-computed breadth-first node paths
            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            }
        }
    }
}
