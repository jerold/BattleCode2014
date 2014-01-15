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
        tryToSpawn();

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            cache.reset();

            //tryToSpawn();

            map.checkForUpdates();

            rc.yield();
        }
    }

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[i];
                if(rc.canMove(trialDir)){
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }
}
