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
    RoadMap.PathingStrategy pathStrat;

    // Every Navigator needs a Destination and a Direction
    MapLocation me;
    MapLocation destination;
    Direction heading;
    double[] directionalForces;
    boolean sneaking;

    // Dog Values for walking that dog
    MapLocation dog;
    Direction dogHeading;
    boolean dogOnWall;
    boolean dogBugging;
    boolean dogSitting;
    MapLocation dogBugTerminal;
    int dogSteps;

    // Keepers
    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};



    Navigator(RobotController inRc,UnitCache inCache,RoadMap inMap)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        map.observingNavigator = this;
        pathStrat = RoadMap.PathingStrategy.DefaultBug;

        me = rc.getLocation();
        heading = me.directionTo(cache.MY_HQ).opposite();
        destination = me.add(heading);
        directionalForces = new double[]{0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,};

        sneaking = false;
        resetDog();
    }





    //================================================================================
    // Check and Set Destination Methods
    //================================================================================

    public void maneuver() throws GameActionException
    {
        me = rc.getLocation();
        if (map.pathingStrat == RoadMap.PathingStrategy.DefaultBug)
            defaultMovement();
        else if (map.pathingStrat == RoadMap.PathingStrategy.SmartBug)
            smartMovement();
        rc.setIndicatorString(1, "["+map.pathingStrat+"]  Dest"+destination+"  Term"+dogBugTerminal+"  Dog"+dog+"  Sit["+dogSitting+"]  DD["+(int)Utilities.distanceBetweenTwoPoints(me, destination)+"] EQ["+(int)Utilities.distanceBetweenTwoPoints(me, cache.ENEMY_HQ)+"]");
    }

    public void setSneak(boolean setting)
    {
        sneaking = setting;
    }

    public void pathingStrategyChanged() throws GameActionException
    {
        resetDog();
        setDestination(destination);
    }

    public void setDestination(MapLocation location)
    {
        destination = location;
    }






    //================================================================================
    // Movement Assistance Methods
    //================================================================================

    public boolean tryMove() throws GameActionException
    {
        if(rc.canMove(heading) && Utilities.distanceBetweenTwoPoints(me, cache.ENEMY_HQ) > 6) {
            if (!rc.isActive()) rc.yield();
            if (sneaking) rc.sneak(heading);
            else rc.move(heading);
            return true;
        }
        return false;
    }

    private void moveWithGuidance() throws GameActionException
    {
        int forwardInt = heading.ordinal();
        for(int directionalOffset:directionalLooks){
            heading = allDirections[(forwardInt+directionalOffset+8)%8];
            if (tryMove()) break;
        }
    }

    public boolean canSimplyPath(RoadMap map, MapLocation origin, MapLocation destination)
    {
        if (origin.x < 0 || origin.x >= map.MAP_WIDTH || origin.y < 0 || origin.y >= map.MAP_HEIGHT) return false;
        if (destination.x < 0 || destination.x >= map.MAP_WIDTH || destination.y < 0 || destination.y >= map.MAP_HEIGHT) return false;
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID) return false;

        MapLocation stepLocation = origin;
        MapLocation lastStep = new MapLocation(-1, -1);
        while (!stepLocation.equals(destination)) {
            MapLocation nextStepLocation = stepLocation.add(stepLocation.directionTo(destination));
            if (nextStepLocation.equals(lastStep) || map.getTileType(nextStepLocation) == RoadMap.TileType.TTVoid) return false;
            lastStep = stepLocation;
            stepLocation = nextStepLocation;
        }
        return true;
    }

    private void resetDog()
    {
        dog = new MapLocation(me.x, me.y);
        dogHeading = heading;
        dogOnWall = false;
        dogBugging = false;
        dogSitting = false;
        dogBugTerminal = dog;
        dogSteps = 0;
    }

    private void sitDog()
    {
        dogSitting = true;
        slowDog();
    }

    private void slowDog()
    {
        dogSteps = 0;
    }

    private void dogBug()
    {
        MapLocation newDog = new MapLocation(dog.x, dog.y);
        Direction newDogHeading = allDirections[dogHeading.ordinal()];

        dogSteps++;
        for (int i=0; i<dogSteps; i++) {
            if (newDog.isAdjacentTo(dogBugTerminal)) {dogBugging = false; break;}
            if (map.getTileType(newDog.add(newDogHeading.rotateLeft().rotateLeft().rotateLeft())) == RoadMap.TileType.TTVoid) newDogHeading = newDogHeading.rotateLeft().rotateLeft().rotateLeft();
            else if (map.getTileType(newDog.add(newDogHeading.rotateLeft().rotateLeft())) == RoadMap.TileType.TTVoid) newDogHeading = newDogHeading.rotateLeft().rotateLeft();
            while (map.getTileType(newDog.add(newDogHeading)) == RoadMap.TileType.TTVoid) newDogHeading = newDogHeading.rotateRight();
            newDog = newDog.add(newDogHeading);
        }
        if (canSimplyPath(map, me, newDog)) {
            dog = newDog;
            dogHeading = newDogHeading;
        } else slowDog();
    }

    private void dogRun()
    {
        MapLocation newDog = new MapLocation(dog.x, dog.y);

        while (!newDog.equals(destination) && map.getTileType(newDog.add(newDog.directionTo(destination))) != RoadMap.TileType.TTVoid) newDog = newDog.add(newDog.directionTo(destination));
        Direction newDogHeading = newDog.directionTo(destination);
        if (canSimplyPath(map, me, newDog)) {
            if (!newDog.equals(destination)) {
                dogBugging = true;
                setTerminal(newDog);
            } else sitDog();
            dog = newDog;
            dogHeading = newDogHeading.rotateRight().rotateRight();
        }
    }

    private void walkDog()
    {
        if (dogSitting) {
            if (me.equals(dog) && !dog.equals(destination)) dogSitting = false;
            else if (me.isAdjacentTo(dog) && !rc.canMove(me.directionTo(dog))) dogSitting = false;
            else return;
        }

        if (dogBugging) {
            dogBug();
        } else if (!dog.equals(destination)) {
            dogRun();
        }
    }

    private void setTerminal(MapLocation loc)
    {
        dogBugTerminal = loc.add(loc.directionTo(destination));
        while (map.getTileType(dogBugTerminal.add(dogBugTerminal.directionTo(destination))) == RoadMap.TileType.TTVoid) dogBugTerminal = dogBugTerminal.add(dogBugTerminal.directionTo(destination));
        dogBugTerminal = dogBugTerminal.add(dogBugTerminal.directionTo(destination));
    }




    //================================================================================
    // Default Methods
    //================================================================================

    public void defaultMovement() throws GameActionException
    {
        heading = me.directionTo(destination);
        tryMove();
    }





    //================================================================================
    // Smart Methods
    //================================================================================

    public void smartMovement() throws GameActionException
    {
        walkDog();
        heading = me.directionTo(dog);
        moveWithGuidance();
    }
}