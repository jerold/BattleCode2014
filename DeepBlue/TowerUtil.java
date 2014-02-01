package DeepBlue;

import battlecode.common.*;

public class TowerUtil
{
	public static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

    public static MapLocation[] generatePullLines(RobotController rc, MapLocation center)
    {
    	int width = rc.getMapWidth();
    	int height = rc.getMapHeight();
    	double[][] cows = rc.senseCowGrowth();
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
            while(toFire.x < -2 || toFire.x >= width + 2 || toFire.y < -2 || toFire.y >= width + 2 || !rc.canAttackSquare(toFire))
            {
            	toFire = toFire.add(toFire.directionTo(center));
            }
            if(toFire.x >= 0 && toFire.x < width && toFire.y >= 0 && toFire.y < height)
            {
            	int t = 0;
	            while(cows[toFire.x][toFire.y] < 1 && !toFire.equals(center) && t < 6)
	            {
	            	toFire = toFire.add(toFire.directionTo(center));
	            	t++;
	            }
	            t = Math.min(t, 4);
	            toFire = toFire.add(toFire.directionTo(center).opposite(), t);
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
