package Nerazim;

import battlecode.common.*;


import java.util.Random;


public class HQFunctions 
{

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

    static Random rand = new Random();
    static Direction[] directions = Direction.values();
	
	public static void SpawnSoldiers(RobotController rc)
    {
        try
        {
            if (rc.isActive() && rc.getType() == RobotType.HQ && (rc.senseRobotCount() < 25))
            {
                Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                int numb = 0;
                while (!rc.canMove(toEnemy) && numb < 10)
                {
                   numb++;
                    toEnemy = toEnemy.rotateRight();

                }

                if (toEnemy != Direction.NONE && rc.canMove(toEnemy))
                {
                    if (rc.isActive())
                    {
                        if (rc.getType() == RobotType.HQ)
                        {
                            rc.spawn(toEnemy);
                        }
                    }
                }
            }
        } catch (Exception e)
        {
        	e.printStackTrace();
        }
    }

	public static void findInitialRally(RobotController rc)
	{
		try
		{
			MapLocation target = rc.getLocation();
			MapLocation enemyHQSpot = rc.senseEnemyHQLocation(); //Movement.convertIntToMapLocation(rc.readBroadcast(enemyHQ));
			Direction dir = rc.getLocation().directionTo(enemyHQSpot);
			
			double distance = rc.getLocation().distanceSquaredTo(enemyHQSpot);
			
			distance = Math.sqrt(distance);
			
			int distanceInt = (int) (distance/4);
			
			for (int i = 0; i < distanceInt; i++)
			{
				target = target.add(target.directionTo(enemyHQSpot));
			}

            Direction dir34 = target.directionTo(rc.getLocation());

            while (rc.senseTerrainTile(target).equals(TerrainTile.VOID) || target.equals(rc.getLocation()) || target.isAdjacentTo(rc.getLocation()))
            {
                target = target.add(dir34);
            }

			rc.broadcast(rallyPoint, Movement.convertMapLocationToInt(target));

		} catch (Exception e) {}
	}

	public static void InitialLocationBroadcasts(RobotController rc)
	{
		try
		{
			MapLocation ourHQSpot = rc.getLocation();

			rc.broadcast(ourHQ, Movement.convertMapLocationToInt(ourHQSpot));

			MapLocation enemyHQSpot = rc.senseEnemyHQLocation();

			rc.broadcast(enemyHQ, Movement.convertMapLocationToInt(enemyHQSpot));

		} catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("RobotPlayer Exception");
        }

	}

	// methods to give robots channels
	public static int enemyHQChannel()
	{
		return enemyHQ;
	}

	public static int ourHQChannel()
	{
		return ourHQ;
	}

	public static int rallyPointChannel()
	{
		return rallyPoint;
	}

    public static int rallyPoint2Channel()
    {
        return rallyPoint2;
    }

	public static void moveTargetLocationRandomly(RobotController rc)
	{
		try
		{
			MapLocation target = Movement.convertIntToMapLocation(rc.readBroadcast(rallyPoint));
			Direction dir = directions[rand.nextInt(8)];
			MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());

			if (enemyPastrs.length > 0)
			{
				target = enemyPastrs[0];
			}
			else
			{
				for (int i = 0; i < 3; i++)
				{
					target = target.add(dir);
				}
			}

			rc.broadcast(rallyPoint, Movement.convertMapLocationToInt(target));

		} catch (Exception e) {}
	}

    /**
     * This method returns the current target location for scouring the map in search of enemy pastrs
     */
    public static MapLocation newTarget(RobotController rc)
    {
        MapLocation target = null;
        int time = Clock.getRoundNum() % 750;

        target = rc.getLocation();
        while ((target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 50 || target.distanceSquaredTo(rc.getLocation()) < 50))
        {
            if (time < 75)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            }
            else if (time < 150)
            {
                target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/10);
            }
            else if (time < 225)
            {
                target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/2);
            }
            else if (time < 300)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight() - 5);
            }
            else if (time < 375)
            {
                target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/2);
            }
            else if (time < 450)
            {
                target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/10);
            }
            else if (time < 525)
            {
                target = new MapLocation(rc.getMapWidth()/2, 10);
            }
            else if (time < 600)
            {
                target = new MapLocation(rc.getMapWidth() - 5, rc.getMapHeight() - 5);
            }
            else if (time < 675)
            {
                target = new MapLocation(5, rc.getMapHeight() - 5);
            }
            else if (time < 750)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            }
            time += 75;
            time %= 750;
        }

        return target;
    }

    public static boolean setTargetLocation(RobotController rc, boolean wentForPastr)
    {
        boolean goingForPastr = false;
        try
        {
            MapLocation target = Movement.convertIntToMapLocation(rc.readBroadcast(rallyPoint));
            MapLocation target2 = rc.getLocation();
            Direction dir = directions[rand.nextInt(8)];
            MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
            MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
            int[] AllEnemies = FightMicro2.AllEnemyBots(rc);
            MapLocation closestPastr = null;
            int closestDist = 1000000;
            boolean initialRally = false;
            boolean setTarget2 = false;

            for (int i = 0; i < enemyPastrs.length; i++)
            {
                int dist = target.distanceSquaredTo(enemyPastrs[i]);
                int distToEnemyHQ = enemyPastrs[i].distanceSquaredTo(rc.senseEnemyHQLocation());
                if (dist < closestDist && distToEnemyHQ > 0)
                {
                    closestDist = dist;
                    closestPastr = enemyPastrs[i];
                }
            }

            if (ourPastrs.length > 0)
            {
                rc.broadcast(defendPastr, 1);
            }
            else
            {
                rc.broadcast(pastrBuilt, 0);
            }


            if (enemyPastrs.length > 0 && closestPastr != null)
            {
                target = target.add(target.directionTo(closestPastr), 3);

                while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                {
                    target = target.add(target.directionTo(closestPastr));
                }
                rc.broadcast(takeDownEnemyPastr, 1);

                if (closestPastr.distanceSquaredTo(rc.senseEnemyHQLocation()) < 10)
                {
                    rc.broadcast(enemyPastrInRangeOfHQ, 1);
                }
                else
                {
                    rc.broadcast(enemyPastrInRangeOfHQ, 0);
                }
            }
            else if (rc.readBroadcast(pastLoc) != 0)
            {
                target = Movement.convertIntToMapLocation(rc.readBroadcast(pastLoc));
                if (ourPastrs.length > 0)
                {
                    target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                    target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                    target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                }

                rc.broadcast(takeDownEnemyPastr, 0);
                rc.broadcast(enemyPastrInRangeOfHQ, 0);
            }
            else
            {
                if (Clock.getRoundNum() < 10000)
                {
                    findInitialRally(rc);
                    initialRally = true;
                }
                else
                {
                }
                rc.broadcast(takeDownEnemyPastr, 0);
                rc.broadcast(enemyPastrInRangeOfHQ, 0);
            }

            if (!initialRally)
            {
                rc.broadcast(rallyPoint, Movement.convertMapLocationToInt(target));
            }

            for (int i = 0; i < enemyPastrs.length; i++)
            {
                int dist = target2.distanceSquaredTo(enemyPastrs[i]);
                int distToEnemyHQ = enemyPastrs[i].distanceSquaredTo(rc.senseEnemyHQLocation());
                if (dist < closestDist && distToEnemyHQ > 0)
                {
                    closestDist = dist;
                    closestPastr = enemyPastrs[i];
                }
            }

            if (enemyPastrs.length > 0 && closestPastr != null)
            {
                target2 = closestPastr;
            }
            else if (AllEnemies.length > 0 && AllEnemies[0] != 0)
            {
                target2 = FightMicro2.getBotLocation(AllEnemies[0]);
            }
            else
            {
                target2 = newTarget(rc);
            }
            rc.broadcast(rallyPoint2, Movement.convertMapLocationToInt(target2));

        } catch (Exception e) {}
        return goingForPastr;
    }

    public static MapLocation spotOfSensorTower(RobotController rc, boolean corner1)
    {
        rand = new Random();
        MapLocation target = null;
        int corner;

        if (corner1)
        {
            corner = findBestCorner(rc);
        }
        else
        {
            corner = findOpposingCorner(rc);
        }

        switch(corner)
        {
            case 1:
                target = new MapLocation(0, 10);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 1, 10);
                break;
            case 3:
                target = new MapLocation(0, rc.getMapHeight() - 11);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 1, rc.getMapHeight() - 11);
                break;
        }

        Direction dir = directions[rand.nextInt(8)];
        // make sure we don't try to build on a void space
        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            target = target.add(dir);
            dir = directions[rand.nextInt(8)];
        }

        return target;
    }

    public static MapLocation spotOfPastr(RobotController rc, boolean corner1)
    {
        MapLocation target;
        int[] lookPlaces = {1,1,0,3,6,7,4,5,2,3,0,2,2,3,1,4,5,3,2,5,6};
        int counter = 0;
        Direction dir;
        int corner = 0;
        if (corner1)
        {
            corner = findBestCorner(rc);
        }
        else
        {
            corner = findOpposingCorner(rc);
        }
        rand = new Random();
        switch(corner)
        {
            case 1:
                target = new MapLocation(2, 2);
                break;
            case 2:
                target = new MapLocation(rc.getMapWidth() - 3, 2);
                break;
            case 3:
                target = new MapLocation(2, rc.getMapHeight() - 3);
                break;
            default:
                target = new MapLocation(rc.getMapWidth() - 3, rc.getMapHeight() - 3);
                break;
        }

        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {

            dir = directions[rand.nextInt(8)];
            target = target.add(dir);
            counter++;
        }
        return target;
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

        double max = 0;
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

    public static int findOpposingCorner(RobotController rc)
    {
        int corner = findBestCorner(rc);

        switch(corner)
        {
            case 1:
                corner = 4;
                break;
            case 2:
                corner = 3;
                break;
            case 3:
                corner = 2;
                break;
            default:
                corner = 1;
                break;
        }

        return corner;
    }


    //==================================================================================================\\
    //
    ///////////// These methods take the broadcasted information and determine strategy with them \\\\\\\\
    //
    //===================================================================================================\\

    public static int maxNumbEnemySoldiers(RobotController rc)
    {
        int numb = 0;

        return numb;
    }
    
    public static boolean checkDoublePastr(RobotController rc, MapLocation m1, MapLocation m2){
    	try{
    		int mapSize = Utilities.getMapSize(rc);
    		MapLocation enemy = rc.senseEnemyHQLocation();
    		MapLocation pastr1 = m1;
    		MapLocation pastr2 = m2;
    		int distToEnemy1 = m2.distanceSquaredTo(enemy);
    		int voidsBetween = 0;
    		MapLocation temp = pastr1;
    		while(temp.x != pastr2.x || temp.y != pastr2.y)
    		{
    			if(rc.senseTerrainTile(temp) == TerrainTile.VOID)
    			{
    				voidsBetween++;
    			}
    			temp = temp.add(temp.directionTo(pastr2));
    		}
    		if(mapSize >= 100){
    			if(TowerUtil.getSpotScore(rc, pastr1) > 50){
    				if((pastr1.distanceSquaredTo(pastr2) > mapSize/2. || (voidsBetween > 1 && pastr1.distanceSquaredTo(pastr2) > 100)) && distToEnemy1 > 100){
    					return true;
    				} else {
    					return false;
    				}
    			} else {
    				return false;
    			}
    		} else {
    			return false;
    		}
    	}catch(Exception e){
    		e.printStackTrace();
    		return false;
    	}
    }

    public static boolean HQShoot(RobotController rc) throws GameActionException
    {
        int radius = 15;
        Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, radius, rc.getTeam().opponent());
        Robot[] enemies2 = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
        Direction[] dirs = Direction.values();
        Robot target = null;
        int maxValue = 0;

        for(int k = 0; k < enemies.length; k++)
        {
            MapLocation loc = rc.senseRobotInfo(enemies[k]).location;
            int value = 2;
            for (int a = 0; a < 8; a++)
            {
                try
                {
                    if (rc.canSenseSquare(loc.add(dirs[a])))
                    {
                        if (rc.senseObjectAtLocation(loc.add(dirs[a])) != null)
                        {
                            if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam().opponent())
                            {
                                value++;
                            }
                            else if(rc.senseObjectAtLocation(loc.add(dirs[a])).getTeam() == rc.getTeam())
                            {
                                value--;
                            }
                        }
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }

            if(value > maxValue)
            {
                maxValue = value;
                target = enemies[k];
            }
        }

        if(target != null)
        {
            if (target != null)
            {
                if (rc.canAttackSquare(rc.senseRobotInfo(target).location))
                {
                    rc.attackSquare(rc.senseRobotInfo(target).location);
                    return true;
                }
            }
        }
        else if (enemies2.length > 0)
        {
            MapLocation location = null;
            MapLocation loc = null;
            maxValue = 0;
            for (int j = 0; j < enemies2.length; j++)
            {

                int value = 0;
                MapLocation loc2 = rc.senseRobotInfo(enemies2[j]).location;
                Direction dir = rc.getLocation().directionTo(loc2).rotateRight().rotateRight();

                for (int l = 0; l < 3; l++)
                {
                    loc = loc2.subtract(dir);
                    dir = dir.rotateLeft();
                    if (rc.getLocation().distanceSquaredTo(loc) <= 15)
                    {
                        for (int k = 0; k < 8; k++)
                        {
                            try
                            {
                                if (rc.canSenseSquare(loc.add(dirs[k])))
                                {
                                    GameObject enemy = rc.senseObjectAtLocation(loc.add(dirs[k]));

                                    if (enemy != null)
                                    {
                                        if(enemy.getTeam() == rc.getTeam().opponent())
                                        {
                                            value++;
                                        }
                                        else if(enemy.getTeam() == rc.getTeam())
                                        {
                                            value--;
                                        }
                                    }
                                }
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (value > 0)
                    {
                        location = loc;
                        l = 3;
                        j = enemies2.length;
                    }
                }
                if (maxValue < value)
                {
                    maxValue = value;
                    location = loc;
                }
            }

            if (rc.canAttackSquare(location))
            {
                rc.attackSquare(location);
                return true;
            }
        }

        return false;

    }
}
