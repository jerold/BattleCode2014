package team104;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

public class CenterMULE
{
    RobotController rc;
    public CenterMULE(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
    	MapLocation target;
    	
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
            	try
                {
            		target = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
                    while(rc.senseTerrainTile(target) == TerrainTile.VOID)
                    {
                    	target = target.add(rc.getLocation().directionTo(rc.senseHQLocation()));
                    }
                    target = target.add(rc.getLocation().directionTo(rc.senseHQLocation()));
                    Utilities.MoveMapLocation(rc, target, true);

                    if(rc.isActive())
                    {
                        rc.construct(RobotType.PASTR);
                    }
                }
                catch(Exception e){}
            }
        }
    }
}
