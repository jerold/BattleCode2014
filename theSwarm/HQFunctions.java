package theSwarm;

import java.util.Random;

import battlecode.common.*;

public class HQFunctions 
{
	
	// these are the channels that we will use to communicate to our bots
		static final int enemyHQ = 1;
		static final int ourHQ = 2;
		static final int rallyPoint = 3;
		static Random rand = new Random();
		static Direction[] directions = Direction.values();
	
	public static void SpawnSoldiers(RobotController rc)
    {
        try
        {
            if (rc.isActive() && rc.getType() == RobotType.HQ && (rc.senseRobotCount() < 25))
            {
                Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {}
                else
                {
                    for (int i = 0; i < 7; i++)
                    {
                        toEnemy = toEnemy.rotateLeft();

                        if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null)
                        {
                            i = 47;
                        }
                        else if (i == 6)
                        {
                            toEnemy = Direction.NONE;
                        }
                    }
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
			
			rc.setIndicatorString(1, "target: "+distanceInt);
			rc.setIndicatorString(2, ""+enemyHQSpot);
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
}
