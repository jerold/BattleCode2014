package theSwarm6;

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
    		return Utilities.convertIntToMapLocation(rc.readBroadcast(bestLocChannel));
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
			rc.broadcast(bestLocChannel, Utilities.convertMapLocationToInt(target));
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
    		return Utilities.convertIntToMapLocation(rc.readBroadcast(bestLocChannel));
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
	    					if(rc.senseTerrainTile(new MapLocation((k * width / 3) + t, (a * width / 3) + i)) == TerrainTile.VOID)
	    					{
	    						temp--;
	    					}
	    					else
	    					{
	    						temp += cows[(k * width / 3) + t][(a * width / 3) + i];
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
    				rc.setIndicatorString(1, target.toString());
    			}
    		}
    	}
    	
    	if(rc.senseHQLocation().distanceSquaredTo(target) > rc.senseHQLocation().distanceSquaredTo(getOppositeSpot(rc, target)))
    	{
    		target = new MapLocation(width - target.x, height - target.y);
    	}
    	
    	try
    	{
			rc.broadcast(bestLocChannel, Utilities.convertMapLocationToInt(target));
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
    
  //finds best corner to collect milk where the return is an int as follows:
    //1  2
    //3  4
    public static int findBestCorner(RobotController rc)
    {
        double[][] pasture = rc.senseCowGrowth();
        
        double[] voids = new double[4];
        double[] cows = new double[4];
        double[] distances = new double[4];

        double max = -1000;
        int corner = 0;
        double total = 0;
        MapLocation target = null;
        MapLocation current = rc.senseHQLocation();
        
        for(int k = 1; k <= 4; k++)
        {
        	switch(k)
            {
                case 1:
                    target = new MapLocation(5, 5);
                    break;
                case 2:
                    target = new MapLocation(rc.getMapWidth() - 6, 5);
                    break;
                case 3:
                    target = new MapLocation(5, rc.getMapHeight() - 6);
                    break;
                default:
                    target = new MapLocation(rc.getMapWidth() - 6, rc.getMapHeight() - 6);
                    break;
            }
        	
        	while(target.x != current.x || target.y != current.y)
        	{
        		if(rc.senseTerrainTile(current) == TerrainTile.VOID)
        		{
        			total++;
        		}
        		current = current.add(current.directionTo(target));
        	}
        	
        	voids[k - 1] = total;
        	distances[k - 1] = rc.senseHQLocation().distanceSquaredTo(target);
        	
        	total = 0;
        	current = rc.senseHQLocation();
        }

        //top left corner
        for(int k = 0; k < 10; k++)
        {
            for(int a = 0; a < 10; a++)
            {
                total += pasture[k][a];
            }
        }
        cows[0] = total;
            
        total = 0;

        //top right corner
        for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
        {
            for(int a = 0; a < 10; a++)
            {
                total += pasture[k][a];
            }
        }
        cows[1] = total;
        
        total = 0;

        //bottom left corner
        for(int k = 0; k < 10; k++)
        {
            for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
            {
                total += pasture[k][a];
            }
        }
        cows[2] = total;
        
        total = 0;

        //bottom right corner
        for(int k = rc.getMapWidth() - 11; k < rc.getMapWidth(); k++)
        {
            for(int a = rc.getMapHeight() - 11; a < rc.getMapHeight(); a++)
            {
                total += pasture[k][a];
            }
        }
        cows[3] = total;
        
        for(int k = 0; k < 4; k++)
        {
        	total = cows[k] * 1 - voids[k] * 50 - distances[k] * .001;
        	
        	if(total > max)
        	{
        		max = total;
        		corner = k + 1;
        	}
        }

        return corner;
    }
    
    public static boolean isGoodCorner(RobotController rc, int corner)
    {
    	int startX, startY;
    	int scope = 10;
    	int score = 0;
    	double[][] cows = rc.senseCowGrowth();
    	
    	switch(corner)
    	{
    		case 1:
    			startX = 0; 
    			startY = 0;
    			break;
    		case 2:
    			startX = rc.getMapWidth() - scope;
    			startY = 0;
    			break;
    		case 3:
    			startX = 0;
    			startY = rc.getMapHeight() - scope;
    			break;
    		default:
    			startX = rc.getMapWidth() - scope;
    			startY = rc.getMapHeight() - scope;
    			break;
    	}
    	
    	for(int k = startX; k < startX + scope; k++)
    	{
    		for(int a = startY; a < startY + scope; a++)
    		{
    			if(rc.senseTerrainTile(new MapLocation(k, a)) != TerrainTile.VOID)
    			{
    				score += (int)cows[k][a];
    			}
    		}
    	}
    	
    	if(score > 50)
    	{
    		return true;
    	}
    	
    	return false;
    }
}
