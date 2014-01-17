package towerbot;

import battlecode.common.*;

public class GenericTower
{

    RobotController rc;
    MapLocation target;
    MapLocation[] lines;

    public GenericTower(RobotController rc)
    {
        this.rc = rc;
        target = Utilities.spotOfPastr(rc, true);
        
        lines = TowerUtil.generateSpokeLines(rc, target, 20, 4);
        rc.setIndicatorString(0, "Tower");
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.NOISETOWER)
            {
            	for(int k = 0; k < lines.length; k += 2)
            	{
            		TowerUtil.fireLine(rc, lines[k], lines[k + 1], 1);
            	}
            }

            rc.yield();
        }
    }
}
