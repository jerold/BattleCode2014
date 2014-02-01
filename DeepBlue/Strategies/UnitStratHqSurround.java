package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratHqSurround extends UnitStrategy {
    static RobotController rc;
    static public MapLocation enemyHQ;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
        enemyHQ = rc.senseEnemyHQLocation();
        Soldiers.nav.setDestination(enemyHQ);
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getLocation().distanceSquaredTo(enemyHQ) < 25)
        {
            Soldiers.nav.setDestination(rc.getLocation());
        }
    }
}
