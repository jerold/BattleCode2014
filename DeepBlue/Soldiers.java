package DeepBlue;

import battlecode.common.*;
import java.util.Random;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Soldiers {
    static RobotController rc;
    static UnitCache cache;
    static RoadMap map;
    static Navigator nav;
    static MapLocation destination;
    static Random rand = new Random();

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static void run(RobotController inRc) throws GameActionException
    {
        rc = inRc;
        cache = new UnitCache(rc);
        map = new RoadMap(rc, cache);
        nav = new Navigator(rc,cache, map);

        // Temp THIS IS NOT A GOOD IDEA
        nav.setDestination(cache.ENEMY_HQ);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            cache.reset();
            map.checkForUpdates();



            // Do unit strategy picker
            // strategy picks destinations and performs special tasks

            if (nav.engaging())
                nav.adjustFire(true); // Micro Movements based on enemy contact
            else
                nav.maneuver(); // Goes forward with Macro Pathing to destination, and getting closer to friendly units

            rc.yield();
        }
    }


}
