package towerbot;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by fredkneeland on 1/8/14.
 */
public class MULE
{
    RobotController rc;
    int type;
    MapLocation target;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    Direction dir;
    boolean corner1;

    public MULE(RobotController rc, boolean corner1)
    {
    	double[][] cows = rc.senseCowGrowth();
        this.rc = rc;
        this.corner1 = corner1;
        int width = rc.getMapWidth();
        int height = rc.getMapHeight();
        int offMap = 0;
        int voidTotal = 0;
        int cowTotal = 0;
        
        target = TowerUtil.bestSpot3(rc);
        if(!corner1)
        {
        	target = TowerUtil.getOppositeSpot(rc, target);
        }
        
        for(int k = target.x - 10; k < target.x + 10; k++)
        {
        	for(int a = target.y - 10; a < target.y + 10; a++)
        	{
        		if(k < 0 || k >= width || a < 0 || a >= height)
        		{
        			offMap++;
        		}
        		else
        		{
	        		if(rc.senseTerrainTile(new MapLocation(k, a)) == TerrainTile.VOID)
	        		{
	        			if(target.distanceSquaredTo(new MapLocation(k, a)) < 10)
	        			{
	        				voidTotal++;
	        			}
	        		}
	        		else
	        		{
	        			cowTotal += (int)cows[k][a];
	        		}
        		}
        	}
        }
        
        if(offMap > 250)
        {
        	if(cowTotal > 200)
        	{
        		type = 1;
        	}
        	else
        	{
        		type = 2;
        	}
        }
        else if(offMap > 150)
        {
        	if(cowTotal > 400)
        	{
        		type = 1;
        	}
        	else
        	{
        		type = 2;
        	}
        }
        else
        {
        	if(voidTotal > 2)
        	{
        		type = 1;
        	}
        	else
        	{
	    		type = 2;
        	}
        }
        rc.setIndicatorString(1, "" + type);
        rc.setIndicatorString(0, "MULE");

    }

    public void run()
    {
        while(true)
        {
            if(rc.getType() == RobotType.SOLDIER)
            {
                Utilities.MoveMapLocation(rc, target, false);
                
                if(type == 2)
                {
	                while(!towerNear(rc) || rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam().opponent()).length > 0)
	                {
	                	rc.yield();
	                }
	                
	                for(int k = 0; k < 10; k++)
	                {
	                	rc.yield();
	                }
                }

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
