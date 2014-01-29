package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratHqSurround extends UnitStrategy {
    static RobotController rc;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
        Soldiers.nav.setDestination(rc.senseEnemyHQLocation());
    }

    public static void upDate()
    {

    }
}
