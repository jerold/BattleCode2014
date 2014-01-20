package towerbot;

import battlecode.common.*;

public class fastMULE
{
    RobotController rc;
    MapLocation target;
    boolean corner1;

    public fastMULE(RobotController rc, boolean corner1)
    {
        this.rc = rc;
        this.corner1 = corner1;
        
        target = TowerUtil.bestSpot3(rc);
        
        if(!corner1)
        {
        	target = TowerUtil.getOppositeSpot(rc, target);
        }
        
        rc.setIndicatorString(0, "MULE");

    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                Utilities.MoveMapLocation(rc, target, false);

                if(rc.isActive())
                {
                    try
                    {
                        rc.construct(RobotType.PASTR);
                    }
                    catch (Exception e){}
                }
            }
        }
    }
}
