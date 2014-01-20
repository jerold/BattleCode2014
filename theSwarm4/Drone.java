package theSwarm4;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
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
        pastrSpot = TowerUtil.bestSpot3(rc);
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.getLocation().x == pastrSpot.x && rc.getLocation().y == pastrSpot.y && towerNear(rc))
                    {
                    	for(int k = 0; k < 500; k++){rc.yield();}
                        rc.construct(RobotType.PASTR);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, pastrSpot, false, false);
                    }
                }

            } catch (Exception e) {}
        }
    }
    
    private boolean towerNear(RobotController rc)
    {
    	Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam());
    	for(Robot bot : bots)
    	{
    		try
    		{
	    		if(rc.senseRobotInfo(bot).type == RobotType.NOISETOWER)
	    		{
	    			return true;
	    		}
    		}
    		catch(Exception e){}
    	}
    	
    	return false;
    }
}
