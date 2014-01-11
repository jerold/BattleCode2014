package DeepBlue;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by fredkneeland on 1/9/14.
 */
public class Hellion {
    RobotController rc;
    MapLocation target;

    public Hellion(RobotController rc)
    {
        this.rc = rc;
        rc.setIndicatorString(0, "Hellion");
        target = rc.senseEnemyHQLocation().subtract(rc.getLocation().directionTo(rc.senseHQLocation()));

    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    Utilities.MoveMapLocation(rc, target, false);
                }
            } catch (Exception e)
            {
                rc.setIndicatorString(1, "Error");
                e.printStackTrace();
                System.out.println("Hellion Exception");
            }
            rc.yield();
        }
    }

}
