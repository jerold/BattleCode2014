package towerbot;

import battlecode.common.*;

public class GenericTower
{

    RobotController rc;
    MapLocation target;
    MapLocation[] lines1, lines2, lines3, lines4;

    public GenericTower(RobotController rc)
    {
        this.rc = rc;
        target = rc.getLocation();
        
        lines1 = TowerUtil.generateSpokeLines(rc, target, 1);
        lines2 = TowerUtil.generateSpokeLines(rc, target, 2);
        lines3 = TowerUtil.generateSpokeLines(rc, target, 3);
        lines4 = TowerUtil.generateSpokeLines(rc, target, 4);
        rc.setIndicatorString(0, "Tower");
    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.NOISETOWER)
            {
            	for(int k = 0; k < lines1.length; k += 2)
            	{
            		if(lines1[k] != null)
            		{
            			TowerUtil.fireLine(rc, lines1[k], lines1[k + 1], 1);
            		}
            	}
            	for(int k = 0; k < lines2.length; k += 2)
            	{
            		if(lines2[k] != null)
            		{
            			TowerUtil.fireLine(rc, lines2[k], lines2[k + 1], 1);
            		}
            	}
            	for(int k = 0; k < lines3.length; k += 2)
            	{
            		if(lines3[k] != null)
            		{
            			TowerUtil.fireLine(rc, lines3[k], lines3[k + 1], 1);
            		}
            	}
            	for(int k = 0; k < lines4.length; k += 2)
            	{
            		if(lines4[k] != null)
            		{
            			TowerUtil.fireLine(rc, lines4[k], lines4[k + 1], 1);
            		}
            	}
            }

            rc.yield();
        }
    }
}
