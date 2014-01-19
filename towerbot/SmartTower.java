package towerbot;

import battlecode.common.*;

public class SmartTower
{
    RobotController rc;
    MapLocation target;
    boolean first;
    
    public SmartTower(RobotController rc, boolean first)
    {
        this.rc = rc;
        this.first = first;
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                    try
                    {
                        target = TowerUtil.bestSpot3(rc);
                        if(!first)
                        {
                        	target = TowerUtil.getOppositeSpot(rc, target);
                        }
                        Utilities.MoveMapLocation(rc, target, false);

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
