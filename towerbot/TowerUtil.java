package towerbot;

import battlecode.common.*;

public class TowerUtil
{
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	
	public static void fireCircle(RobotController rc, int radius, MapLocation center)
    {
        for(int k = 0; k < directions.length; k++)
        {
            while(!rc.isActive()){rc.yield();}
            MapLocation toFire = center.add(directions[k], radius);
            try
            {
                if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                {
                    rc.attackSquare(toFire);
                    rc.yield();
                }
            }
            catch(Exception e){}
            while(!rc.isActive()){rc.yield();}
            toFire = center;
            for(int a = 0; a < radius / 2; a++)
            {
                toFire = toFire.add(directions[k]);
                toFire = toFire.add(directions[(k + 1) % directions.length]);
            }
            try
            {
                if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                {
                    rc.attackSquare(toFire);
                    rc.yield();
                }
            }
            catch(Exception e){}
        }
    }

    public static void pullInto(RobotController rc, int radius, MapLocation center)
    {
        for(int k = 0; k < directions.length; k++)
        {
            while(!rc.isActive()){rc.yield();}
            MapLocation toFire = center.add(directions[k], radius);
            try
            {
                while(toFire.distanceSquaredTo(center) > 10)
                {
                	while(!rc.isActive()){rc.yield();}
                    if(toFire.x >= 0 && toFire.x < rc.getMapWidth() && toFire.y >= 0 && toFire.y < rc.getMapHeight() && rc.canAttackSquare(toFire))
                    {
                        try
                        {
                            rc.attackSquare(toFire);
                        }
                        catch(Exception e){}
                    }
                    toFire = toFire.add(toFire.directionTo(center));
                }
            }
            catch(Exception e){}
        }
    }
    
    //skip is how far it goes before firing again, like 1 would do every one and 2 would do every other one
    public static void fireLine(RobotController rc, MapLocation start, MapLocation end, int skip)
    {
    	MapLocation toFire = start;
    	
    	try
    	{
	    	do
	    	{
	    		while(!rc.isActive()){rc.yield();}
	    		if(rc.canAttackSquare(toFire))
	            {
	                try
	                {
	                    rc.attackSquare(toFire);
	                }
	                catch(Exception e){}
	            }
	            toFire = toFire.add(toFire.directionTo(end), skip);
	    	}
	    	while(toFire.x != end.x || toFire.y != end.y);
    	}
    	catch(Exception e){}
    }
    
    public static MapLocation[] generateSpokeLines(RobotController rc, MapLocation center, int radius, int skim)
    {
    	MapLocation[] lines = new MapLocation[(radius / skim) * 4 + 1];
    	MapLocation current = center;
    	int index = 0;
    	int dist = 7;
    	
    	while(Math.sqrt(current.distanceSquaredTo(center)) < radius)
    	{
    		MapLocation temp1 = current;
    		while(temp1.y > -2)
    		{
    			temp1 = temp1.add(Direction.NORTH_WEST);
    		}
    		lines[index] = temp1;
    		index++;
    		while(Math.sqrt(temp1.distanceSquaredTo(current)) > dist)
    		{
    			temp1 = temp1.add(Direction.SOUTH_EAST);
    		}
    		lines[index] = temp1;
    		index++;
    		temp1 = current;
    		while(temp1.x < 62)
    		{
    			temp1 = temp1.add(Direction.SOUTH_EAST);
    		}
    		lines[index] = temp1;
    		index++;
    		while(Math.sqrt(temp1.distanceSquaredTo(current)) > dist)
    		{
    			temp1 = temp1.add(Direction.NORTH_WEST);
    		}
    		lines[index] = temp1;
    		index++;
    		
    		current = current.add(Direction.SOUTH_WEST, skim);
    		rc.setIndicatorString(1, "(" + current.x + ", " + current.y + ")");
    	}
    	lines[index] = current;
    	index++;
    	
    	while(Math.sqrt(current.distanceSquaredTo(center)) > dist / 2)
		{
			current = current.add(Direction.NORTH_EAST);
		}
		lines[index] = current;
		index++;
    	
    	return lines;
    }
}
