package theSwarm3;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Drone {
    RobotController rc;
    MapLocation pastrSpot;

    public Drone(RobotController rc)
    {
        this.rc = rc;
        pastrSpot = HQFunctions.spotOfPastr(rc, true);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().equals(pastrSpot) || (rc.getLocation().isAdjacentTo(pastrSpot) && !rc.canMove(rc.getLocation().directionTo(pastrSpot))))
                    {
                        rc.construct(RobotType.PASTR);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, pastrSpot, false, true);
                    }
                }

            } catch (Exception e) {}
        }
    }
}
