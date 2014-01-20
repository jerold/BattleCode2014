package team104_2;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;

/**
 * Created by fredkneeland on 1/11/14.
 */
public class Marauder {
    RobotController rc;
    MapLocation target;
    MapLocation[] enemyPastr;

    public Marauder(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (target == null || rc.getLocation().isAdjacentTo(target) || rc.getLocation().equals(target))
                    {
                        enemyPastr = rc.sensePastrLocations(rc.getTeam().opponent());
                        if (enemyPastr.length > 0)
                        {
                            target = Utilities.ClosestPastr(rc, enemyPastr);
                        }
                        else
                        {
                            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
                        }
                    }
                    else
                    {
                        Utilities.MoveMapLocation(rc, target, false);
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("SCV Exception");
            }
            rc.yield();
        }
    }
}
