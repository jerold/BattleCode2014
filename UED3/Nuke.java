package UED3;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by fredkneeland on 1/7/14.
 *
 * is an almost dead ghost who finds a location where he can move and self destruct to cause more damage to the enemy than shooting can
 * he looks at every direction and picks the one where the total damage to enemy minus friendly damage is highest
 *
 */
public class Nuke
{
    RobotController rc;
    MapLocation target;

    public Nuke (RobotController rc, MapLocation target)
    {
        this.rc = rc;
        this.target = target;
    }

    public void run()
    {
        try
        {
            if (rc.getLocation().equals(target))
            {
                rc.selfDestruct();
            }
            else
            {
                if (rc.isActive())
                {
                    if (rc.canMove(rc.getLocation().directionTo(target)))
                    {
                        rc.move(rc.getLocation().directionTo(target));
                    }
                }
            }
            rc.selfDestruct();
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Ghost Exception");
        }
        rc.yield();
    }
}
