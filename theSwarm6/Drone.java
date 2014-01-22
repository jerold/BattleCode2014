package theSwarm6;

import battlecode.common.*;

/**
 * Different types correspond to different times to turn into pastures
 * 1 means that it will turn immediately into a pasture when it reaches a destination
 * 2 means it will turn into a pasture once it is next to a noise tower
 * 3 means the bot waits for another pasture to be created before starting construction
 * Any other number, that is how long the bot will wait before beginning construction of a pasture
 * A corresponding negative number will go to the opposite corner.
 */
public class Drone {
	static final int pastLoc = 10;
    RobotController rc;
    MapLocation pastrSpot;
    int type;

    public Drone(RobotController rc, int type)
    {
        this.rc = rc;
        this.type = type;
        pastrSpot = TowerUtil.bestSpot3(rc);
        if(this.type < 0)
        {
        	pastrSpot = TowerUtil.getOppositeSpot(rc, pastrSpot);
        	this.type *= -1;
        }

        try
        {
            rc.broadcast(pastLoc, Utilities.convertMapLocationToInt(pastrSpot));
        } catch (Exception e) {}
        
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
                    		while(rc.sensePastrLocations(rc.getTeam()).length == 0){rc.yield();}
                    	}
                    	else
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    		for(int k = 0; k < type; k++){rc.yield();}
                    	}
                    	//while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0){rc.yield();}
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
