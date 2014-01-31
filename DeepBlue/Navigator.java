package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/13/14.
 *
 */
public class Navigator {

    static final int HAS_ARRIVED_DISTANCE = 4;
    static final int NEAR_WAYPOINT_DISTANCE = 2;

    RobotController rc;
    UnitCache cache;
    RoadMap map;

    MapLocation dog;
    Direction dogHeading;

    MapLocation destination;
    int destinationNodeId;
    public MapLocation nextStep;
    public int nextStepNodeId;
    public boolean hasArrived;

    Direction heading;
    double stayPut;
    RoadMap.PathingStrategy pathStrat;

    boolean bugging;
    boolean bugLeft;
    int bugStartDistanceFromDestination;
    MapLocation bugTerminal;

    boolean sneaking;

    Navigator(RobotController inRc,UnitCache inCache,RoadMap inMap)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        map.observingNavigator = this;

        dog = rc.getLocation();
        dogHeading = Direction.NORTH;

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
        bugTerminal = rc.getLocation();

        sneaking = false;
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
        rc.setIndicatorString(1, "["+map.pathingStrat+"] NextStep("+nextStep+") Dest("+destination+") B//"+bugging+"// @["+hasArrived+"]["+Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination)+"]");
//        rc.setIndicatorString(1, "["+map.pathingStrat+"] NextStep("+nextStepNodeId+")["+nextStep.x+","+nextStep.y+"] Dest("+destinationNodeId+")["+destination.x+","+destination.y+"] @["+hasArrived+"] bug["+bugging+"]["+bugLeft+"]");
    }

    private void setSneak(boolean setting)
    {
        sneaking = setting;
    }

    public void tryMove(Direction heading) throws GameActionException
    {
        if(rc.canMove(heading)) {
            if (!rc.isActive()) rc.yield();
            if (sneaking) rc.sneak(heading);
            else rc.move(heading);
        }
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
        if (rc.getLocation().directionTo(location) != rc.getLocation().directionTo(destination)) bugging = false;
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
            readBroadcastNextStep();
        }
    }

    private int readBroadcastStep(int origNodeId, int destNodeId) throws GameActionException
    {
//        boolean oppositeBroadcast = false;
//        if (origNodeId >= map.nodeCount/2) // to save space only half of the pathing is sent, so get the larger half details by fetching their opposite, then inverting
//            oppositeBroadcast = true;

        int channel = Utilities.startMacroChannels+origNodeId*map.nodeCount+destNodeId;
//        if (oppositeBroadcast)
//            channel = Utilities.startMacroChannels+map.oppositeNodeId(origNodeId)*map.nodeCount+map.oppositeNodeId(destNodeId);
        MapLocation signal = VectorFunctions.intToLoc(rc.readBroadcast(channel));

        int nsni = signal.x == RoadMap.TILE_VOID ? RoadMap.NO_PATH_EXISTS : signal.x;
//        if (oppositeBroadcast) // Opposite channel means opposite result.
//            nsni = map.oppositeNodeId(nsni);
        return nsni;
    }

    private void readBroadcastNextStep() throws GameActionException
    {
        destinationNodeId = map.idForNearestNode(destination);
        int nearRcNodeId = map.idForNearestNode(rc.getLocation());

//        System.out.println("Read Broadcast Dest["+destinationNodeId+"]"+destination);

        nextStepNodeId = readBroadcastStep(nearRcNodeId, destinationNodeId);
        nextStep = map.locationForNode(nextStepNodeId);
    }

    public MapLocation getNextStep() throws GameActionException
    {
        if (atFinalDestination())
            return destination;
        if (Utilities.distanceBetweenTwoPoints(dog, nextStep) < NEAR_WAYPOINT_DISTANCE)
            readBroadcastNextStep();
        if (Utilities.distanceBetweenTwoPoints(dog, nextStep) < Utilities.distanceBetweenTwoPoints(dog, destination))
            return nextStep;
        return destination;

//            if (rc.getLocation().isAdjacentTo(nextStep))
//            readBroadcastNextStep();
//        if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), nextStep) < Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination))
//            return nextStep;
//        return destination;
    }

    private boolean atFinalDestination() throws GameActionException
    {
        if (hasArrived)
            return true;
        if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < HAS_ARRIVED_DISTANCE) {
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

    private Direction bugMoveHeading() throws GameActionException
    {
        Direction newHeading = heading;
        if (bugLeft) {
            if (map.valueForLocation(rc.getLocation().add(heading.opposite().rotateRight())) == RoadMap.TILE_VOID) heading = heading.rotateLeft().rotateLeft();
            else if (map.valueForLocation(rc.getLocation().add(heading.rotateLeft().rotateLeft())) == RoadMap.TILE_VOID) heading = heading.rotateLeft().rotateLeft();
//            while (!rc.canMove(heading)) heading = heading.rotateRight();
            while (map.valueForLocation(rc.getLocation().add(heading)) == RoadMap.TILE_VOID) heading = heading.rotateRight();
            return newHeading;
        } else {
            if (map.valueForLocation(rc.getLocation().add(heading.opposite().rotateLeft())) == RoadMap.TILE_VOID) heading = heading.rotateRight().rotateRight();
            else if (map.valueForLocation(rc.getLocation().add(heading.rotateRight().rotateRight())) == RoadMap.TILE_VOID) heading = heading.rotateRight().rotateRight();
//            while (!rc.canMove(heading)) heading = heading.rotateLeft();
            while (map.valueForLocation(rc.getLocation().add(heading)) == RoadMap.TILE_VOID) heading = heading.rotateLeft();
            return newHeading;
        }
    }

    private Direction bugMoveHeading(MapLocation origin, Direction heading) throws GameActionException
    {
        Direction newHeading = heading;
        if (bugLeft) {
            if (map.valueForLocation(origin.add(heading.opposite().rotateRight())) == RoadMap.TILE_VOID) heading = heading.rotateLeft().rotateLeft();
            else if (map.valueForLocation(origin.add(heading.rotateLeft().rotateLeft())) == RoadMap.TILE_VOID) heading = heading.rotateLeft().rotateLeft();
            while (map.valueForLocation(origin.add(heading)) == RoadMap.TILE_VOID) heading = heading.rotateRight();
            return newHeading;
        } else {
            if (map.valueForLocation(origin.add(heading.opposite().rotateLeft())) == RoadMap.TILE_VOID) heading = heading.rotateRight().rotateRight();
            else if (map.valueForLocation(origin.add(heading.rotateRight().rotateRight())) == RoadMap.TILE_VOID) heading = heading.rotateRight().rotateRight();
            while (map.valueForLocation(origin.add(heading)) == RoadMap.TILE_VOID) heading = heading.rotateLeft();
            return newHeading;
        }
    }

    private MapLocation getBugTerminal(MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID)
            return null;

        MapLocation bugEnd = origin.add(origin.directionTo(destination));
        while (map.valueForLocation(bugEnd) == RoadMap.TILE_VOID) {
            bugEnd = bugEnd.add(bugEnd.directionTo(destination));
        }
        return bugEnd;
    }



    //================================================================================
    // Default Methods
    //================================================================================

    public void defaultMovement() throws GameActionException
    {
        if (!atFinalDestination()) {
            dog = rc.getLocation();
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc);
            tryMove(heading);
        }
    }




    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartMovement() throws GameActionException
    {
//        if (!atFinalDestination()) {
//            System.out.println("0");
//            MapLocation newDog = new MapLocation(dog.x, dog.y);
//            Direction newDogHeading = dogHeading.opposite().opposite();
//
//            System.out.println("1");
//            if (!bugging) {
//                int stepCount = 4;
//                for (int i=0; i<stepCount; i++) {
//                    System.out.println("2");
//                    if (map.valueForLocation(newDog.add(newDog.directionTo(destination))) != RoadMap.TILE_VOID) {
//                        System.out.println("3");
//                        newDogHeading = newDog.directionTo(destination);
//                        newDog = newDog.add(newDogHeading);
//                    } else {
//                        System.out.println("4");
//                        bugging = true;
//                        bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(newDog, destination);
//                        bugTerminal = getBugTerminal(newDog, destination);
//                        bugLeft = Path.shouldBugLeft(map, newDog, destination);
//                        System.out.println("4.1");
//                        break;
//                    }
//                }
//            } else {
//                System.out.println("5");
//                int stepCount = 4;
//                for (int i=0; i<stepCount; i++) {
//                    System.out.println("6");
//                    newDogHeading = bugMove(newDog, newDogHeading);
//                    if (map.valueForLocation(newDog.add(newDogHeading)) != RoadMap.TILE_VOID) {
//                        System.out.println("7");
//                        newDog = newDog.add(newDogHeading);
//                        if (newDog.x == bugTerminal.x && newDog.y == bugTerminal.y) {
//                            System.out.println("8");
//                            bugging = false;
//                            break;
//                        }
//                    }
//                }
//            }
//
//            if (Path.canSimplyPath(map, rc.getLocation(), newDog)) {
//                System.out.println("9");
//                dog = newDog;
//                dogHeading = newDogHeading;
////                heading = map.directionFromRC(dog);
//                heading = MicroPathing.getNextDirection(map.directionFromRC(dog), true, rc);
//                tryMove(heading);
//            }
//
//        } else {
//            heading = MicroPathing.getNextDirection(map.directionFromRC(destination).rotateRight(), true, rc);
//            tryMove(heading);
//        }

        if (!atFinalDestination()) {
            if (!bugging) {
                heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), destination), true, rc);
                tryMove(heading);

                if (shouldBug()) {
                    bugging = true;
                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                    bugTerminal = getBugTerminal(rc.getLocation(), destination);
                    bugLeft = !bugLeft; // bugging direction is a basic flip-flop

                    dog = rc.getLocation();
                    dogHeading = rc.getLocation().directionTo(destination);
                    MicroPathing.resetTrail();
                }
            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), bugTerminal) < NEAR_WAYPOINT_DISTANCE || Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
                bugging = false;

            if (bugging) {
//                MapLocation newDog = new MapLocation(dog.x, dog.y);
//                Direction newDogHeading = dogHeading.opposite().opposite();
//                int stepCount = 4;
//                for (int i=0; i<stepCount; i++) {
//                    newDog = newDog.add(bugMoveHeading(newDog, newDogHeading));
//                }
//                if (Utilities.distanceBetweenTwoPoints(newDog, rc.getLocation()) < 5) {
//                    dog = newDog;
//                    dogHeading = newDogHeading;
//                    heading = map.directionFromRC(dog);
//                    tryMove(heading);
//                }

                tryMove(bugMoveHeading());
            }
        } else {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc);
            tryMove(heading);
        }
    }




    //================================================================================
    // Macro Methods
    //================================================================================

    public void macroMovement() throws GameActionException
    {
//        if (!atFinalDestination()) {
//            if (!bugging) {
//                dog = rc.getLocation();
//
//                int stepCount = 3;
//                for (int i=0; i<stepCount; i++) {
////                dog = dog.add(map.directionTo(dog, nextStep));
//                    dog = dog.add(MicroPathing.getNextDirection(dog, map.directionTo(dog, getNextStep()), map));
//                }
//
//                heading = map.directionTo(rc.getLocation(), dog); // Heading is either a straight shot or a path built from the pre-computed breadth-first node paths
//                if(rc.canMove(heading)) {
//                    if (!rc.isActive()) rc.yield();
//                    rc.move(heading);
//                }
//
//                if (shouldBug()) {
//                    setDestination(destination);
//                    bugging = true;
//                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), getNextStep());
//                    bugLeft = Utilities.directionIsLeftOf(rc.getLocation().directionTo(getNextStep()), heading);
//                }
//            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), nextStep) <= bugStartDistanceFromDestination)
//                bugging = false;
//            if (rc.isActive() && bugging)
//                bugMove();
//        }

        if (!atFinalDestination()) {
            dog = rc.getLocation();

            int stepCount = 3;
            for (int i=0; i<stepCount; i++) {
//                dog = dog.add(map.directionTo(dog, nextStep));
                dog = dog.add(MicroPathing.getNextDirection(dog, map.directionTo(dog, getNextStep()), map));
            }
            heading = map.directionTo(rc.getLocation(), dog); // Heading is either a straight shot or a path built from the pre-computed breadth-first node paths
            heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), dog), false, rc);

            tryMove(heading);
        }else {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination).rotateRight(), true, rc);
            tryMove(heading);
        }



//        if (!atFinalDestination()) {
//            heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), getNextStep()), false, rc);
//            if(rc.canMove(heading)) {
//                if (!rc.isActive()) rc.yield();
//                rc.move(heading);
//            }
//        }



//        if (!atFinalDestination()) {
//            dog = rc.getLocation();
//            if (!bugging) {
//                int stepCount = 3;
//                for (int i=0; i<stepCount; i++) {
////                dog = dog.add(map.directionTo(dog, nextStep));
//                    dog = dog.add(MicroPathing.getNextDirection(dog, map.directionTo(dog, getNextStep()), map));
//                }
//
//                if (shouldBug()) {
//                    bugging =true;
//                    setDestination(destination);
//                    heading = heading.opposite();
//                    bugStartDistanceFromDestination = 0;
//                }
//            } else {
//                int stepCount = 3;
//                for (int i=0; i<stepCount; i++) {
//                    dog = dog.add(MicroPathing.getNextDirection(dog, heading, map));
//                }
//
//                if (bugStartDistanceFromDestination > 10) {
//                    bugging = false;
//                    setDestination(destination);
//                } else bugStartDistanceFromDestination++;
//            }
//
//            heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), dog), false, rc);
////            heading = map.directionTo(rc.getLocation(), dog); // Heading is either a straight shot or a path built from the pre-computed breadth-first node paths
//            if(rc.canMove(heading)) {
//                if (!rc.isActive()) rc.yield();
//                rc.move(heading);
//            }
//        } else {
//            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination), true, rc);
//            if (!rc.isActive()) rc.yield();
//            if(rc.canMove(heading)) rc.move(heading);
//        }
    }
}
