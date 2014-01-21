package theSwarm4;

import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Different types correspond to different times to turn into pastures
 * 1 means that it will turn immediately into a pasture when it reaches a destination
 * 2 means it will turn into a pasture once it is next to a noise tower
 * 3 means it will wait until there are no enemies nearby, there is a tower, and it has been waiting at least 500 turns
 */
public class Drone {
    RobotController rc;
    MapLocation pastrSpot;
    int type;

    public Drone(RobotController rc, int type)
    {
        this.rc = rc;
        this.type = type;
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
                    if (rc.getLocation().x == pastrSpot.x && rc.getLocation().y == pastrSpot.y)
                    {
                    	if(type == 2)
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    	}
                    	else if(type == 3)
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    		for(int k = 0; k < 500; k++){rc.yield();}
                    		while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0){rc.yield();}
                    	}
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
