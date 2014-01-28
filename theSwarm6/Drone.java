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
    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;
    static final int morphRoach = 17;

    RobotController rc;
    MapLocation pastrSpot;
    towerPastrRequest request;
    int type, start;

    public Drone(RobotController rc, int type, MapLocation target)
    {
        this.rc = rc;
        this.type = type;
        pastrSpot = target;
        request = new towerPastrRequest(rc);
        start = Clock.getRoundNum();
        
        rc.setIndicatorString(0, "Drone");
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
                    	if(rc.senseTeamMilkQuantity(rc.getTeam()) > 9000000){}
                    	else if(type == 2)
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    	}
                    	else if(type > 3)
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    		for(int k = 0; k < type; k++){rc.yield();}
                    	}
                    	else
                    	{
                    		while(!towerNear(rc)){rc.yield();}
                    		for(int k = 0; k < type; k++){rc.yield();}
                    	}
                    	while(rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent()).length > 0){rc.yield();}
                        request.madeIt(true);
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
