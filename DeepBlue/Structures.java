package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Structures {
    static RobotController rc;
    static UnitCache cache;
    public static final int visBoxWidth = 5;

    static Direction allDirections[] = Direction.values();

    public static void run(RobotController inRc) throws GameActionException
    {
        rc = inRc;
        cache = new UnitCache(rc);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }

            cache.reset();

            if(rc.getType() == RobotType.PASTR) runPastr();
            if(rc.getType() == RobotType.NOISETOWER) runNoiseTower();

            rc.yield();
        }
    }




    //================================================================================
    // PASTR Methods
    //================================================================================

    public static void runPastr() throws GameActionException
    {
        new GenericPastr(rc).run();
    }

    public static void updatePastrDetails() throws GameActionException
    {
        int[] details = new int[5];

        details[0] = Clock.getRoundNum();

        RobotInfo[] friendly = cache.nearbyAllies();
        details[1] = friendly.length;

        RobotInfo[] enemy = cache.nearbyEnemies();
        details[2] = enemy.length;

        double cowCount = 0.0;
        for (int i = 0; i < visBoxWidth; i++) {
            for (int j = 0; j < visBoxWidth; j++) {
                cowCount += rc.senseCowsAtLocation(rc.getLocation().add(i-2, j-2));
            }
        }
        details[3] = (int)cowCount;

        details[4] = VectorFunctions.locToInt(rc.getLocation());

        Utilities.setDetailsForPastr(rc, details);
    }




    //================================================================================
    // NOISETOWER Methods
    //================================================================================

    public static void runNoiseTower() throws GameActionException
    {
        new GenericTower(rc, false).run();
    }

}
