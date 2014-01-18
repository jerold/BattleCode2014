package theSwarm2;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Extractor {
    RobotController rc;
    MapLocation towerSpot;

    public Extractor(RobotController rc)
    {
        this.rc = rc;
        towerSpot = HQFunctions.spotOfSensorTower(rc, true);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().equals(towerSpot) || (rc.getLocation().isAdjacentTo(towerSpot) && !rc.canMove(rc.getLocation().directionTo(towerSpot))))
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
