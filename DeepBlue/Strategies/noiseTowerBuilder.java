package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class noiseTowerBuilder extends UnitStrategy {
    static RobotController rc;
    static MapLocation towerSpot;

    public static void initialize(RobotController rcIn, int get[]) throws GameActionException
    {
        rc = rcIn;
        towerSpot = TowerUtil.convertIntToMapLocation(get[0]);
        Soldiers.nav.setDestination(towerSpot);
    }

    public static void run() throws GameActionException
    {
        if (rc.getLocation().equals(towerSpot))
        {
            while (!rc.isActive())
                rc.yield();

            rc.construct(RobotType.NOISETOWER);
        }
    }
}
