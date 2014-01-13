package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 */
public class Soldiers {
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

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            cache.reset();

            map.checkForUpdates();

            rc.yield();
        }
    }


}
