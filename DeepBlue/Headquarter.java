package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Headquarter {
    static RobotController rc;
    static UnitCache cache;
    static RoadMap map;

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static void run(RobotController inRc) throws GameActionException
    {
        rc = inRc;
        cache = new UnitCache(rc);
        map = new RoadMap(rc, cache);

        // TEST RALLY POINT
        setRallyPoint(new MapLocation(map.MAP_WIDTH/2, map.MAP_HEIGHT/2));

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            tryToSpawn();

            cache.reset();
            map.checkForUpdates();

            rc.yield();
        }
    }

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){ // if(rc.isActive()&&rc.senseRobotCount()<2){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[i];
                if(rc.canMove(trialDir)){
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }

    public static void setRallyPoint(MapLocation rally) throws GameActionException
    {
        rc.broadcast(Utilities.rallyPointChannel1, VectorFunctions.locToInt(rally));
    }

    public static void setUnitNeeded(Soldiers.UnitStrategyType unitType) throws GameActionException
    {
        rc.broadcast(Utilities.unitNeededChannel, unitType.getValue());
    }
}
