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
    
    /*
     * Corners are as follows with p being the center.
     * 1   2
     *   p
     * 3   4
     */
    public static MapLocation[] generateSpokeLines(RobotController rc, MapLocation center, int corner)
    {
    	MapLocation[] lines = new MapLocation[50];
    	int index = 0;
    	MapLocation current = center;
    	Direction mainDir;
    	int edgeY, edgeX;
    	MapLocation temp;
    	
    	switch(corner)
    	{
    		case 1:
    			mainDir = Direction.NORTH_WEST;
    			edgeX = center.x + 4;
    			edgeY = center.y + 4;
    			break;
    		case 2:
    			mainDir = Direction.NORTH_EAST;
    			edgeX = center.x - 4;
    			edgeY = center.y + 4;
    			break;
    		case 3:
    			mainDir = Direction.SOUTH_WEST;
    			edgeX = center.x + 4;
    			edgeY = center.y - 4;
    			break;
    		default:
    			mainDir = Direction.SOUTH_EAST;
    			edgeX = center.x - 4;
    			edgeY = center.y - 4;
    			break;
    	}
    	
    	while(!voidBehind(rc, center, current, mainDir) && rc.canAttackSquare(current) && current.x > -3 &&
    		  current.x < rc.getMapWidth() + 3 && current.y > -3 && current.y < rc.getMapHeight() + 3)
    	{
    		temp = current;
    		if(corner == 1)
    		{
    			while(temp.x < edgeX && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateRight().rotateRight());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			temp = current;
    			while(temp.y < edgeY && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateLeft().rotateLeft());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			current = current.add(mainDir, 3);
    		}
    		else if(corner == 2)
    		{
    			while(temp.x > edgeX && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateLeft().rotateLeft());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			temp = current;
    			while(temp.y < edgeY && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateRight().rotateRight());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			current = current.add(mainDir, 3);
    		}
    		else if(corner == 3)
    		{
    			while(temp.x < edgeX && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateLeft().rotateLeft());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			temp = current;
    			while(temp.y > edgeY && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateRight().rotateRight());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			current = current.add(mainDir, 3);
    		}
    		else if(corner == 4)
    		{
    			while(temp.x > edgeX && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateRight().rotateRight());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			temp = current;
    			while(temp.y > edgeY && rc.canAttackSquare(temp) && rc.senseTerrainTile(temp) != TerrainTile.VOID)
    			{
    				temp = temp.add(mainDir.rotateLeft().rotateLeft());
    			}
    			if(!rc.canAttackSquare(temp))
    			{
    				temp = temp.add(temp.directionTo(current));
    			}
    			
				lines[index] = temp;
				index++;
				while(Math.sqrt(temp.distanceSquaredTo(current)) > 7)
				{
					temp = temp.add(temp.directionTo(current));
				}
				lines[index] = temp;
				index++;
				
    			current = current.add(mainDir, 3);
    		}
    		
    	}
    	while(!rc.canAttackSquare(current))
    	{
    		current = current.subtract(mainDir);
    	}
    	lines[index] = current;
		index++;
    	while(Math.sqrt(current.distanceSquaredTo(center)) > 4)
    	{
    		current = current.add(current.directionTo(center));
    	}
    	lines[index] = current;
		index++;
		
    	return lines;
    }
    
    private static boolean voidBehind(RobotController rc, MapLocation center, MapLocation current, Direction dir)
    {
    	for(int k = 0; k < 4; k++)
    	{
    		if(rc.senseTerrainTile(center) == TerrainTile.VOID)
    		{
    			return true;
    		}
    		if(rc.senseTerrainTile(center.add(dir.rotateRight())) == TerrainTile.VOID)
    		{
    			return true;
    		}
    		if(rc.senseTerrainTile(center.add(dir.rotateLeft())) == TerrainTile.VOID)
    		{
    			return true;
    		}
    		center = center.subtract(dir);
    	}
    	
    	return false;
    }
    
    public static MapLocation bestSpot(RobotController rc)
    {
    	double[][] cows = rc.senseCowGrowth();
    	MapLocation target = new MapLocation(5, 5);
    	int total = 0;
    	int skip, start;
    	int scope = 7;
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	
    	if(rc.getMapWidth() * rc.getMapHeight() < 2600)
    	{
    		skip = 2;
    		start = 6;
    	}
    	else
    	{
    		skip = 4;
    		start = 6;
    	}
    	
    	for(int k = start; k < width - start; k += skip)
    	{
    		for(int a = start; a < height - start; a += skip)
    		{
    			int score = 0;
    			for(int t = 0; t < scope; t += 1)
    			{
    				for(int i = 0; i < scope; i += 1)
    				{
    					score += (int)cows[k - scope / 2 + t][a - scope / 2 + i];
    					if(rc.senseTerrainTile(new MapLocation(k - scope / 2 + t, a - scope / 2 + i)) == TerrainTile.VOID)
    					{
    						score -= 2;
    					}
    				}
    			}
    			
    			if(score > total)
    			{
    				total = score;
    				target = new MapLocation(k, a);
    			}
    		}
    	}
    	
    	return target;
    }
}
