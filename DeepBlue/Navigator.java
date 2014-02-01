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


    // Trail Values used as breadcrumbs
    int maxTrailLength;
    MapLocation[] trail;
    int headIndex;
    int trailLength;

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

        maxTrailLength = 3;
        resetTrail();
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
        rc.setIndicatorString(1, "["+map.pathingStrat+"]  Dest"+destination+"  Term"+dogBugTerminal+"  Dog"+dog+"  Sit["+dogSitting+"]  ["+Utilities.distanceBetweenTwoPoints(rc.getLocation(), destination)+"]");
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
        resetTrail();
    }





    //================================================================================
    // Trail Keeping Methods
    //================================================================================

    public void resetTrail()
    {
        trail = new MapLocation[maxTrailLength];
        headIndex = 0;
        trailLength = 0;
        while(trailLength<2) addLocationToTrail(new MapLocation(-1, -1));
    }

    public boolean directionInTrail(Direction dir)
    {
        MapLocation resultingLocation = me.add(dir);
        for(int i=0;i<trailLength;i++){
            MapLocation m = getLocationFromTrail(i);
            if(!m.equals(me) && resultingLocation.equals(m)) return true;
        }
        return false;
    }

    public boolean didDoubleBack()
    {
        for(int i=1; i<trailLength; i++){
            MapLocation m = getLocationFromTrail(i);
            if(m.equals(me)) return true;
        }
        return false;
    }

    public MapLocation getLocationFromTrail(int index)
    {
        return trail[(headIndex-index)%maxTrailLength];
    }

    public void addLocationToTrail(MapLocation loc)
    {
        trailLength = trailLength+1 < maxTrailLength ? trailLength+1 : maxTrailLength;
        headIndex++;
        trail[headIndex%maxTrailLength] = loc;
    }





    //================================================================================
    // Movement Assistance Methods
    //================================================================================

    public boolean tryMove() throws GameActionException
    {
        if(rc.canMove(heading) && !directionInTrail(heading)) {
            if (!rc.isActive()) rc.yield();
            if (sneaking) rc.sneak(heading);
            else rc.move(heading);
            addLocationToTrail(rc.getLocation());
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

    public boolean canSimplyPath(RoadMap map, MapLocation origin, MapLocation destination) throws GameActionException
    {
        if (origin.x < 0 || origin.x >= map.MAP_WIDTH || origin.y < 0 || origin.y >= map.MAP_HEIGHT) return false;
        if (destination.x < 0 || destination.x >= map.MAP_WIDTH || destination.y < 0 || destination.y >= map.MAP_HEIGHT) return false;
        if (map.roadMap[origin.x][origin.y] == RoadMap.TILE_VOID || map.roadMap[destination.x][destination.y] == RoadMap.TILE_VOID) return false;

        MapLocation stepLocation = origin;
        MapLocation lastStep = new MapLocation(-1, -1);
        while (!stepLocation.equals(destination)) {
            MapLocation nextStepLocation = stepLocation.add(stepLocation.directionTo(destination));
//            System.out.print("L[x:"+lastStep.x+", y:"+lastStep.y+"] ");
//            System.out.print("S[x:"+stepLocation.x+", y:"+stepLocation.y+"] ");
//            System.out.print("N[x:"+nextStepLocation.x+", y:"+nextStepLocation.y+"]("+map.valueForLocation(nextStepLocation)+")   ");
//            System.out.print(map.valueForLocation(nextStepLocation));

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
        dogBugTerminal = null;
        dogSteps = 0;
    }

    private void sitDog()
    {
        dogSitting = true;
        dogSteps = 0;
    }

    private void walkDog() throws GameActionException
    {
//        System.out.println("Dog "+dog+" ["+dogHeading+"] S("+dogSitting+") T("+dogBugTerminal+") D("+destination+") M("+me+")");
        if (dogSitting) {
            if (me.equals(dog) && !dog.equals(destination)) dogSitting = false;
            else return;
        }

        MapLocation newDog = new MapLocation(dog.x, dog.y);
        Direction newDogHeading = allDirections[dogHeading.ordinal()];
        if (dogBugging) {
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
            } else sitDog();
        } else if (!newDog.equals(destination)) {
            while (!newDog.equals(destination) && map.getTileType(newDog.add(newDog.directionTo(destination))) != RoadMap.TileType.TTVoid) newDog = newDog.add(newDog.directionTo(destination));
            if (canSimplyPath(map, me, newDog)) {
                if (!newDog.equals(destination)) {

                    dogBugging = true;
                    newDogHeading = newDog.directionTo(destination);
                    dogBugTerminal = newDog.add(newDogHeading);
                    while (map.getTileType(dogBugTerminal.add(dogBugTerminal.directionTo(destination))) == RoadMap.TileType.TTVoid) dogBugTerminal = dogBugTerminal.add(dogBugTerminal.directionTo(destination));
                    dogBugTerminal = dogBugTerminal.add(dogBugTerminal.directionTo(destination));
//                    System.out.println((map.valueForLocation(dogBugTerminal) == RoadMap.TILE_VOID ? 1 : 0) + "" + dogBugTerminal + "  ");

                } else sitDog();
                dog = newDog;
                dogHeading = newDogHeading.rotateRight().rotateRight();
            }
        }
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