package theSwarm5;

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
    static Random rand = new Random();
    static Direction[] directions = Direction.values();
	
	public static void SpawnSoldiers(RobotController rc)
    {
        try
        {
            if (rc.isActive() && rc.getType() == RobotType.HQ && (rc.senseRobotCount() < 25))
            {
                Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                while (!rc.canMove(toEnemy))
                {
                    toEnemy = toEnemy.rotateLeft();
                    /*
                    for (int i = 0; i < 7; i++)
                    {
                        toEnemy = toEnemy.rotateLeft();

                        if (rc.canMove(toEnemy))
                        {
                            i = 47;
                        }
                        else if (i == 6)
                        {
                            toEnemy = Direction.NONE;
                        }
                    }*/
                }

                if (toEnemy != Direction.NONE)
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
			
			int distanceInt = (int) (distance/3);
			
			for (int i = 0; i < distanceInt; i++)
			{
				target = target.add(target.directionTo(enemyHQSpot));
			}

            //target = rc.senseEnemyHQLocation();

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
        int time = Clock.getRoundNum() % 1000;

        target = rc.getLocation();
        while ((target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 50 || target.distanceSquaredTo(rc.getLocation()) < 50))
        {
            if (time < 100)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            }
            else if (time < 200)
            {
                target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/10);
            }
            else if (time < 300)
            {
                target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/2);
            }
            else if (time < 400)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight() - 5);
            }
            else if (time < 500)
            {
                target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/2);
            }
            else if (time < 600)
            {
                target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/10);
            }
            else if (time < 700)
            {
                target = new MapLocation(rc.getMapWidth()/2, 10);
            }
            else if (time < 800)
            {
                target = new MapLocation(rc.getMapWidth() - 5, rc.getMapHeight() - 5);
            }
            else if (time < 900)
            {
                target = new MapLocation(5, rc.getMapHeight() - 5);
            }
            else if (time < 1001)
            {
                target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
            }
            time += 100;
        }

        return target;
    }

    public static boolean setTargetLocation(RobotController rc, boolean wentForPastr)
    {
        boolean goingForPastr = false;
        try
        {
            MapLocation target = Movement.convertIntToMapLocation(rc.readBroadcast(rallyPoint));
            Direction dir = directions[rand.nextInt(8)];
            MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
            MapLocation closestPastr = null;
            int closestDist = 1000000;
            boolean initialRally = false;

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
            else
            {
                if (Clock.getRoundNum() < 100)
                {
                    findInitialRally(rc);
                    initialRally = true;
                }
                else
                {
                    target = newTarget(rc);
                }

                rc.broadcast(takeDownEnemyPastr, 0);
                rc.broadcast(enemyPastrInRangeOfHQ, 0);

            }
            /*
            else
            {
                if (Clock.getRoundNum() > 500 && rc.senseRobotCount() > 10 && rc.sensePastrLocations(rc.getTeam()).length == 0 && !wentForPastr)
                {
                    target = spotOfPastr(rc, true);
                    rc.broadcast(needNoiseTower, 1);
                    rc.broadcast(needPastr, 1);
                    target = target.add(target.directionTo(rc.senseEnemyHQLocation()), 3);
                    goingForPastr = true;
                }
                else
                {
                    findInitialRally(rc);
                    initialRally = true;
                }
            }*/

            if (!initialRally)
            {
                rc.broadcast(rallyPoint, Movement.convertMapLocationToInt(target));
                if (Clock.getRoundNum() < 10)
                {
                    rc.broadcast(rallyPoint2, Movement.convertMapLocationToInt(target));
                }

            }

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
}
