package theSwarm4;

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
        towerSpot = TowerUtil.bestSpot3(rc);
        towerSpot = towerSpot.add(towerSpot.directionTo(rc.senseHQLocation()));
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().x == towerSpot.x && rc.getLocation().y == towerSpot.y)
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false, false);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
