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
        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
            defaultMovement();
        else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
            smartMovement();
        if (map.pathingStrat == RoadMap.PathingStrategy.MacroPath) {
            macroMovement();
//                smartMovement();
        }
        rc.setIndicatorString(1, "Pathing["+map.pathingStrat+"] NextStep("+nextStepNodeId+")["+nextStep.x+","+nextStep.y+"] Dest("+destinationNodeId+")["+destination.x+","+destination.y+"] @["+hasArrived+"]");
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
        if (!atFinalDestination()) {
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
    }




    //================================================================================
    // Smart Methods
    //================================================================================
//
//    private Direction smartDirection() throws GameActionException
//    {
//        Direction dir = rc.getLocation().directionTo(dog);
//        if (map.valueForLocation(rc.getLocation().add(dir)) != map.TILE_VOID && rc.canMove(dir))
//            return dir;
//
//        // 45
//        Direction dirLeft = dir.rotateLeft();
//        Direction dirRight = dir.rotateRight();
//        if (map.valueForLocation(rc.getLocation().add(dirLeft)) != map.TILE_VOID && rc.canMove(dirLeft)) {
//            if (map.valueForLocation(rc.getLocation().add(dirRight)) != map.TILE_VOID && rc.canMove(dirRight)) {
//                if (Utilities.distanceBetweenTwoPoints(rc.getLocation().add(dirLeft), destination) < Utilities.distanceBetweenTwoPoints(rc.getLocation().add(dirRight), destination))
//                    return dirLeft;
//                else
//                    return dirRight;
//            } else {
//                return dirLeft;
//            }
//        } else if (map.valueForLocation(rc.getLocation().add(dirRight)) != map.TILE_VOID && rc.canMove(dirRight)) {
//            return dirRight;
//        }
//
//        // 90
//        dirLeft = dirLeft.rotateLeft();
//        dirRight = dirRight.rotateRight();
//        if (map.valueForLocation(rc.getLocation().add(dirLeft)) != map.TILE_VOID && rc.canMove(dirLeft)) {
//            if (map.valueForLocation(rc.getLocation().add(dirRight)) != map.TILE_VOID && rc.canMove(dirRight)) {
//                if (Utilities.distanceBetweenTwoPoints(rc.getLocation().add(dirLeft), destination) < Utilities.distanceBetweenTwoPoints(rc.getLocation().add(dirRight), destination))
//                    return dirLeft;
//                else
//                    return dirRight;
//            } else {
//                return dirLeft;
//            }
//        } else if (map.valueForLocation(rc.getLocation().add(dirRight)) != RoadMap.TILE_VOID && rc.canMove(dirRight)) {
//            return dirRight;
//        }
//
//        // Give the pup some space
//        return Direction.NONE;
//    }

//    private MapLocation sniffOutNextStep(Direction dogHead, MapLocation dogLoc)
//    {
//        if (bugLeft) {
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft().rotateLeft().rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft().rotateLeft().rotateLeft());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft().rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft().rotateLeft());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft());
//            if (map.valueForLocation(dogLoc.add(dogHead)) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead);
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight().rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight().rotateRight());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight().rotateRight().rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight().rotateRight().rotateRight());
//            return dogLoc.add(dogHead.opposite());
//        } else {
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight().rotateRight().rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight().rotateRight().rotateRight());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight().rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight().rotateRight());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateRight())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateRight());
//            if (map.valueForLocation(dogLoc.add(dogHead)) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead);
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft().rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft().rotateLeft());
//            if (map.valueForLocation(dogLoc.add(dogHead.rotateLeft().rotateLeft().rotateLeft())) != RoadMap.TILE_VOID)
//                return dogLoc.add(dogHead.rotateLeft().rotateLeft().rotateLeft());
//            return dogLoc.add(dogHead.opposite());
//        }
//    }

    public void smartMovement() throws GameActionException
    {
        if (!atFinalDestination()) {

            heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), destination), true, rc);
            if (!rc.isActive()) rc.yield();
            rc.move(heading);

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
