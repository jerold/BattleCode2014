package theSwarm;

import battlecode.common.*;

public class TowerUtil
{
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	public static int bestLocChannel = 60000;
	
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
                	while(!rc.isActive()){rc.yield();}
                    rc.attackSquare(toFire);
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
                	while(!rc.isActive()){rc.yield();}
                    rc.attackSquare(toFire);
                }
            }
            catch(Exception e){}
        }
    }

    public static void pullInto(RobotController rc, int radius, MapLocation center)
    {
        for(int k = 0; k < directions.length; k++)
        {
        	int numVoids = 8;
        	int distAway = 36;
            int voids = 0;
            MapLocation toFire = center;
            for(int a = 0; a < radius && voids < numVoids; a++)
            {
            	toFire = toFire.add(directions[k]);
            	if(rc.senseTerrainTile(toFire) == TerrainTile.VOID)
            	{
            		voids++;
            	}
            }
            try
            {
                do
                {
                    if(toFire.x >= -2 && toFire.x < rc.getMapWidth() + 2 && toFire.y >= -2 && toFire.y < rc.getMapHeight() + 2 && rc.canAttackSquare(toFire))
                    {
                        try
                        {
                        	while(!rc.isActive()){rc.yield();}
                            rc.attackSquare(toFire);
                        }
                        catch(Exception e){}
                    }
                    toFire = toFire.add(toFire.directionTo(center));
                }
                while(toFire.distanceSquaredTo(center) > distAway);
            }
            catch(Exception e){}
            if(k == 4)
            {
            	voids = 0;
	            toFire = center;
	            for(int a = 0; a < radius && voids < numVoids; a++)
	            {
	            	toFire = toFire.add(directions[k + 2]);
	            	if(rc.senseTerrainTile(toFire) == TerrainTile.VOID)
	            	{
	            		voids++;
	            	}
	            }
	            try
	            {
	                while(toFire.distanceSquaredTo(center) > distAway)
	                {
	                    if(toFire.x >= -2 && toFire.x < rc.getMapWidth() + 2 && toFire.y >= -2 && toFire.y < rc.getMapHeight() + 2 && rc.canAttackSquare(toFire))
	                    {
	                        try
	                        {
	                        	while(!rc.isActive()){rc.yield();}
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
    	
    	while(!voidBehind(rc, center, current) && rc.canAttackSquare(current) && current.x > -3 &&
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
    	while(current.distanceSquaredTo(center) > 49)
    	{
    		current = current.add(current.directionTo(center));
    	}
    	lines[index] = current;
		index++;
		
    	return lines;
    }
    
    private static boolean voidBehind(RobotController rc, MapLocation center, MapLocation current)
    {
    	for(int k = 0; k < 3; k++)
    	{
    		if(rc.senseTerrainTile(current) == TerrainTile.VOID)
    		{
    			return true;
    		}
    		current = current.add(current.directionTo(center));
    	}
    	
    	return false;
    }
    
    public static MapLocation bestSpot(RobotController rc)
    {
    	double[][] cows = rc.senseCowGrowth();
    	MapLocation target = new MapLocation(5, 5);
    	int total = 5;
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
    
    public static MapLocation bestSpot2(RobotController rc)
    {
    	try
    	{
    	if(rc.readBroadcast(bestLocChannel) != 0)
    	{
    		return Movement.convertIntToMapLocation(rc.readBroadcast(bestLocChannel));
    	}
    	}
    	catch(Exception e){}
    	
    	double[][] cows = rc.senseCowGrowth();
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	MapLocation start = new MapLocation(0, 0);
    	MapLocation target = new MapLocation(0, 0);
    	int total = 0;
    	
    	for(int k = 0; k < 3; k++)
    	{
    		for(int a = 0; a < 3; a++)
    		{
    			int temp = 0;
    			for(int t = 0; t < width / 3; t++)
    			{
    				for(int i = 0; i < height / 3; i++)
    				{
    					temp += cows[(k * width / 3) + t][(a * width / 3) + i];
    					if(rc.senseTerrainTile(new MapLocation((k * width / 3) + t, (a * width / 3) + i)) == TerrainTile.VOID)
    					{
    						temp--;
    					}
    				}
    			}
    			
    			if(temp > total)
    			{
    				total = temp;
    				start = new MapLocation(k * width / 3, a * width / 3);
    			}
    		}
    	}
    	
    	total = 0;
    	int scope = 8;
    	int skip = 2;
    	int begin = 4;
    	for(int k = start.x + begin; k < start.x + width / 3 - begin; k += skip)
    	{
    		for(int a = start.y + begin; a < start.y + height / 3 - begin; a += skip)
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
    				rc.setIndicatorString(1, target.toString());
    			}
    		}
    	}
    	
    	try
    	{
			rc.broadcast(bestLocChannel, Movement.convertMapLocationToInt(target));
		}
    	catch (Exception e){}
    	
    	return target;
    }
    
    public static MapLocation bestSpot3(RobotController rc)
    {
    	try
    	{
    	if(rc.readBroadcast(bestLocChannel) != 0)
    	{
    		return Movement.convertIntToMapLocation(rc.readBroadcast(bestLocChannel));
    	}
    	}
    	catch(Exception e){}
    	
    	double[][] cows = rc.senseCowGrowth();
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	MapLocation start = new MapLocation(0, 0);
    	MapLocation target = new MapLocation(0, 0);
    	int total = 0;
    	
    	for(int k = 0; k < 3; k++)
    	{
    		for(int a = 0; a < 3; a++)
    		{
    			if(a < 3 - k)
    			{
	    			int temp = 0;
	    			for(int t = 0; t < width / 3; t++)
	    			{
	    				for(int i = 0; i < height / 3; i++)
	    				{
	    					if(rc.senseTerrainTile(new MapLocation((k * width / 3) + t, (a * height / 3) + i)) == TerrainTile.VOID)
	    					{
	    						temp--;
	    					}
	    					else
	    					{
	    						temp += cows[(k * width / 3) + t][(a * height / 3) + i];
	    					}
	    				}
	    			}
	    			
	    			if(temp > total)
	    			{
	    				total = temp;
	    				start = new MapLocation(k * width / 3, a * height / 3);
	    			}
    			}
    		}
    	}
    	
    	total = 0;
    	int scope = 8;
    	int skip = 2;
    	int begin = 4;
    	for(int k = start.x + begin; k < start.x + width / 3 - begin && k < width; k += skip)
    	{
    		for(int a = start.y + begin; a < start.y + height / 3 - begin && a < height; a += skip)
    		{
    			int score = 0;
    			for(int t = 0; t < scope; t += 1)
    			{
    				for(int i = 0; i < scope; i += 1)
    				{
    					if(rc.senseTerrainTile(new MapLocation(k - scope / 2 + t, a - scope / 2 + i)) == TerrainTile.VOID)
    					{
    						score -= 2;
    					}
    					else
    					{
    						score += (int)cows[k - scope / 2 + t][a - scope / 2 + i];
    					}
    				}
    			}
    			
    			if(score > total)
    			{
    				total = score;
    				target = new MapLocation(k, a);
    				rc.setIndicatorString(1, "Calculating:"+target.toString());
    			}
    		}
    	}
    	
    	if(rc.senseHQLocation().distanceSquaredTo(target) > rc.senseHQLocation().distanceSquaredTo(getOppositeSpot(rc, target)))
    	{
    		target = new MapLocation(width - target.x, height - target.y);
    	}
    	
    	try
    	{
			rc.broadcast(bestLocChannel, Movement.convertMapLocationToInt(target));
		}
    	catch (Exception e){}
    	
    	return target;
    }
    
    public static int getSpotScore(RobotController rc, MapLocation target)
    {
    	double[][] cows = rc.senseCowGrowth();
    	int total = 0;
    	int scope = 8;
    	int k = target.x;
    	int a = target.y;
		for(int t = 0; t < scope; t += 1)
		{
			for(int i = 0; i < scope; i += 1)
			{
				if(rc.senseTerrainTile(new MapLocation(k - scope / 2 + t, a - scope / 2 + i)) == TerrainTile.VOID)
				{
					total -= 2;
				}
				else
				{
					total += (int)cows[k - scope / 2 + t][a - scope / 2 + i];
				}
			}
    	}
    	
    	return total;
    }
    
    public static MapLocation getOppositeSpot(RobotController rc, MapLocation loc)
    {
    	return new MapLocation(rc.getMapWidth() - loc.x - 1, rc.getMapHeight() - loc.y - 1);
    }
    
    public static boolean[] goodSpokeDirs(RobotController rc, MapLocation target)
    {
    	boolean[] spokes = {true, true, true, true};
    	Robot[] bots = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent());
    	MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
    	for(Robot bot : bots)
    	{
    		try
    		{
	    		if(target.directionTo(rc.senseRobotInfo(bot).location) == Direction.NORTH_WEST)
	    		{
	    			spokes[0] = false;
	    		}
	    		else if(target.directionTo(rc.senseRobotInfo(bot).location) == Direction.NORTH_EAST)
	    		{
	    			spokes[1] = false;
	    		}
	    		else if(target.directionTo(rc.senseRobotInfo(bot).location) == Direction.SOUTH_WEST)
	    		{
	    			spokes[2] = false;
	    		}
	    		else if(target.directionTo(rc.senseRobotInfo(bot).location) == Direction.SOUTH_EAST)
	    		{
	    			spokes[3] = false;
	    		}
    		}
    		catch(Exception e){}
    	}
    	
    	for(MapLocation pastr : pastrs)
    	{
    		try
    		{
    			if((target.directionTo(pastr) == Direction.NORTH_WEST ||
    				target.directionTo(pastr) == Direction.NORTH ||
    				target.directionTo(pastr) == Direction.WEST) && 
    			    rc.canAttackSquare(pastr))
	    		{
	    			spokes[0] = false;
	    			rc.setIndicatorString(1, "1");
	    		}
    			else if((target.directionTo(pastr) == Direction.NORTH_EAST ||
    					target.directionTo(pastr) == Direction.NORTH ||
    					target.directionTo(pastr) == Direction.EAST) && 
    					rc.canAttackSquare(pastr))
	    		{
	    			spokes[1] = false;
	    			rc.setIndicatorString(1, "2");
	    		}
    			else if((target.directionTo(pastr) == Direction.SOUTH_WEST ||
    					target.directionTo(pastr) == Direction.SOUTH ||
    					target.directionTo(pastr) == Direction.WEST) && 
    					rc.canAttackSquare(pastr))
	    		{
	    			spokes[2] = false;
	    			rc.setIndicatorString(1, "3");
	    		}
    			else if((target.directionTo(pastr) == Direction.SOUTH_EAST ||
    					target.directionTo(pastr) == Direction.SOUTH ||
    					target.directionTo(pastr) == Direction.EAST) && 
    					rc.canAttackSquare(pastr))
	    		{
	    			spokes[3] = false;
	    			rc.setIndicatorString(1, "4");
	    		}
    		}
    		catch(Exception e){}
    	}
    	
    	return spokes;
    }
    
    public static int convertMapLocationToInt(MapLocation loc)
    {
        int x = loc.x;
        int y = loc.y;
        int total = (x*100) + y;
        return total;
    }

    public static MapLocation convertIntToMapLocation(int value)
    {
        int x = value / 100;
        int y = value % 100;
        MapLocation loc = new MapLocation(x, y);
        return loc;
    }
}
