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
        rc.setIndicatorString(1, "["+map.pathingStrat+"] NextStep("+nextStep+") Dest("+destination+") @["+hasArrived+"]["+Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination)+"]");
//        rc.setIndicatorString(1, "["+map.pathingStrat+"] NextStep("+nextStepNodeId+")["+nextStep.x+","+nextStep.y+"] Dest("+destinationNodeId+")["+destination.x+","+destination.y+"] @["+hasArrived+"] bug["+bugging+"]["+bugLeft+"]");
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
        }
    }




    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartMovement() throws GameActionException
    {
//        if (!atFinalDestination()) {
//
//
//        } else {
//            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination).rotateRight(), true, rc);
//            if (!rc.isActive()) rc.yield();
//            if(rc.canMove(heading)) rc.move(heading);
//        }





        if (!atFinalDestination()) {
            if (!bugging) {
                heading = MicroPathing.getNextDirection(map.directionTo(rc.getLocation(), destination), true, rc);

                if (!rc.isActive()) rc.yield();
                if(rc.canMove(heading)) rc.move(heading);

                if (shouldBug()) {
                    bugging = true;
                    bugStartDistanceFromDestination = (int)Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination);
                    bugLeft = !bugLeft; // bugging direction is a basic flip-flop
                }
            } else if (Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination) < bugStartDistanceFromDestination)
                bugging = false;
            if (rc.isActive() && bugging)
                bugMove();
        } else {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination).rotateRight(), true, rc);
            if (!rc.isActive()) rc.yield();
            if(rc.canMove(heading)) rc.move(heading);
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

            if(rc.canMove(heading)) {
                if (!rc.isActive()) rc.yield();
                rc.move(heading);
            }
        }else {
            heading = MicroPathing.getNextDirection(rc.getLocation().directionTo(destination).rotateRight(), true, rc);
            if (!rc.isActive()) rc.yield();
            if(rc.canMove(heading)) rc.move(heading);
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
