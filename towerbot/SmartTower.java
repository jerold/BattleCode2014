package towerbot;

import battlecode.common.*;

public class SmartTower
{
    RobotController rc;
    MapLocation target;
    
    public SmartTower(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                    try
                    {
                        target = TowerUtil.bestSpot(rc);
                        Utilities.MoveMapLocation(rc, target, true);

                        if(rc.isActive())
                        {
                            rc.construct(RobotType.NOISETOWER);
                        }
                    }
                    catch(Exception e){}

            }
            rc.yield();
        }
    }
}
