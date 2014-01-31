package DeepBlue;

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

    public static void pullInto(RobotController rc, int radius, MapLocation center, towerPastrRequest request)
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
                        	while(!rc.isActive())
                        	{
                        		if(rc.getHealth() < 50)
                        		{
                        			request.sendRequest(center, false);
                        		}
                        		rc.yield();
                        	}
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
	                        	while(!rc.isActive())
	                        	{
	                        		if(rc.getHealth() < 50)
	                        		{
	                        			request.sendRequest(center, false);
	                        		}
	                        		rc.yield();
	                        	}
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
    
    public static MapLocation[] generatePullLines(RobotController rc, MapLocation center)
    {
    	MapLocation[] lines = new MapLocation[16];
    	int numVoids = 8;
    	int distAway = 36;
        int radius = 17;
    	
    	for(int k = 0; k < directions.length; k++)
        {
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
            while(toFire.x < -2 || toFire.x >= rc.getMapWidth() + 2 || toFire.y < -2 || toFire.y >= rc.getMapHeight() + 2 || !rc.canAttackSquare(toFire))
            {
            	toFire = toFire.add(toFire.directionTo(center));
            }
            lines[k * 2] = toFire;
            do
            {
                toFire = toFire.add(toFire.directionTo(center));
            }
            while(toFire.distanceSquaredTo(center) > distAway);
            lines[k * 2 + 1] = toFire;
        }
    	
    	return lines;
    }
    
    //skip is how far it goes before firing again, like 1 would do every one and 2 would do every other one
    public static void fireLine(RobotController rc, MapLocation start, MapLocation end, int skip, towerPastrRequest request)
    {
    	MapLocation toFire = start;
    	
    	try
    	{
	    	do
	    	{
	    		while(!rc.isActive())
	    		{
	    			if(rc.getHealth() < 50)
	    			{
	    				request.sendRequest(rc.getLocation(), false);
	    			}
	    			rc.yield();
	    		}
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
    
    //finds the spotCount best spots on the map
    public static MapLocation[] findBestSpots(RobotController rc, int spotCount)
    {
    	MapLocation[] spots = new MapLocation[spotCount];
    	int[] spotScores = new int[spotCount];
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	int start = 5;
    	int skip;
    	int minDist;
    	if(width * height <= 450)
    	{
    		skip = 1;
    		minDist = 200;
    	}
    	else if(width * height <= 1000)
    	{
    		skip = 2;
    		minDist = 250;
    	}
    	else if(width * height <= 3000)
    	{
    		skip = 3;
    		minDist = 300;
    	}
    	else
    	{
    		skip = 3;
    		minDist = 400;
    	}
    	
    	MapLocation spot;
    	int score, minLoc;
    	
    	for(int k = start; k < width - start; k += skip)
    	{
    		for(int a = start; a < height - start; a += skip)
    		{
    			spot = new MapLocation(k, a);
    			boolean go = true;
    			for(int t = 0; t < spots.length; t++)
    			{
    				try
    				{
    					if(spots[t].x != 0 || spots[t].y != 0)
    					{
		    				if(spot.distanceSquaredTo(spots[t]) < minDist || spot.distanceSquaredTo(rc.senseEnemyHQLocation()) < 300)
		    				{
		    					go  = false;
		    				}
    					}
    				}
    				catch(Exception e){}
    			}
    			if(go)
    			{
	    			if(rc.senseTerrainTile(spot) != TerrainTile.VOID)
	    			{
		    			score = getSpotScore(rc, spot);
		    			minLoc = findMin(spotScores);
		    			
		    			if(score > spotScores[minLoc])
		    			{
		    				spots[minLoc] = spot;
		    				spotScores[minLoc] = score;
		    			}
	    			}
    			}
    		}
    	}
    	
    	return spots;
    }
    
    //finds the spotCount best spots on the map
    public static MapLocation[] findBestSpots2(RobotController rc, int spotCount)
    {
    	MapLocation[] spots = new MapLocation[spotCount];
    	int[] spotScores = new int[spotCount];
    	for(int k = spotScores.length - 1; k >= 0; k--)
    	{
    		spotScores[k] = 20;
    		spots[k] = new MapLocation(0,0);
    	}
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	int start = 5;
    	int skip;
    	int minDist;
    	if(width * height <= 450)
    	{
    		skip = 1;
    		minDist = 200;
    	}
    	else if(width * height <= 1000)
    	{
    		skip = 2;
    		minDist = 250;
    	}
    	else if(width * height <= 3000)
    	{
    		skip = 3;
    		minDist = 300;
    	}
    	else
    	{
    		skip = 3;
    		minDist = 1000;
    	}
    	
    	MapLocation spot;
    	int score, minLoc;
    	
    	for(int k = start; k < width - start; k += skip)
    	{
    		for(int a = start; a < height - start; a += skip)
    		{
    			spot = new MapLocation(k, a);
    			boolean go = true;
    			for(int t = 0; t < spots.length; t++)
    			{
    				try
    				{
    					if(spots[t].x != 0 || spots[t].y != 0)
    					{
		    				if(spot.distanceSquaredTo(spots[t]) < minDist || spot.distanceSquaredTo(rc.senseEnemyHQLocation()) < 300)
		    				{
		    					go  = false;
		    				}
    					}
    				}
    				catch(Exception e){}
    			}
    			if(go)
    			{
	    			if(rc.senseTerrainTile(spot) != TerrainTile.VOID)
	    			{
		    			score = getSpotScore(rc, spot);
		    			if(spot.distanceSquaredTo(rc.senseHQLocation()) <= spot.distanceSquaredTo(rc.senseEnemyHQLocation()) + 3)
		    			{
		    				minLoc = findMin(spotScores);
			    			
			    			if(score > spotScores[minLoc])
			    			{
			    				spots[minLoc] = spot;
			    				spotScores[minLoc] = score;
			    			}
		    			}
	    			}
    			}
    		}
    	}
    	
    	return spots;
    }
    
    private static int findMin(int[] nums)
    {
    	int temp = nums[0];
    	int spot = 0;
    	
    	for(int k = 1; k < nums.length; k++)
    	{
    		if(nums[k] < temp)
    		{
    			spot = k;
    			temp = nums[k];
    		}
    	}
    	
    	return spot;
    }
    
    public static int getSpotScore(RobotController rc, MapLocation target)
    {
        try
        {
            if (rc != null)
            {
                if (target != null)
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
                                int x = k - scope / 2 + t;
                                int y = a-scope /2 + i;
                                if (x >= 0 && y >= 0 && x < cows.length && y < cows[0].length)
                                {
                                    total += (int)cows[x][y];
                                }
                            }
                        }
                    }

                    return total;
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        return -100;
    }

    public static int getHQSpotScore(RobotController rc, MapLocation target)
    {
        try
        {
            if (rc != null)
            {
                if (target != null)
                {
                    double[][] cows = rc.senseCowGrowth();
                    int total = 0;
                    int scope = 15;
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
                                int x = k - scope / 2 + t;
                                int y = a-scope /2 + i;
                                if (x >= 0 && y >= 0 && x < cows.length && y < cows[0].length)
                                {
                                    total += (int)cows[x][y];
                                }
                            }
                        }
                    }

                    return total;
                }
            }
        } catch (Exception e) {e.printStackTrace();}
        return -100;
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
