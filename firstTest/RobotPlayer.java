package firstTest;

import java.util.Random;


import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

public class RobotPlayer {
static Random rand;
static Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
static int numbOfSoldiers = 0;
static int numbOfShepards = 0;
static int myType = 0;
static int x = 0;
static int y = 0;
static MapLocation target;
static MapLocation[] bestSpots;
static int countofBestSpots;
	
	public static void run(RobotController rc) {
		rand = new Random();
		
		while(true) {
			if (rc.getType() == RobotType.HQ) {
				try {					
					//Check if a robot is spawnable and spawn one if it is
					//bestSpots = Utilities.BestPastureSpots(rc);
                    //countofBestSpots = bestSpots.length;

                    Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                    Direction dir;
                    if (nearbyEnemies.length > 0)
                    {
                        Shooter shooter = new Shooter(rc);
                        shooter.fire();
                    }
					else if (rc.isActive() && rc.senseRobotCount() < 25)
                    {

					    Utilities.SpawnSoldiers(rc);
                        numbOfSoldiers++;
                        rc.broadcast(1, numbOfSoldiers);
                        if (numbOfSoldiers % 2 == 0)
                        {
                            numbOfShepards++;
                            if (numbOfShepards > 3)
                            {
                                numbOfShepards = 1;
                            }
                            rc.broadcast(2, numbOfShepards);
                        }

					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
				}
			}
			
			if (rc.getType() == RobotType.SOLDIER) {
				try {
					if (rc.isActive())
                    {


						if (myType == 0)
						{
							myType = rc.readBroadcast(1);
							if (myType % 2 == 0)
							{
								myType = 1;
							}
							else
							{
								myType = 2;
							}
						}

                        // then we are a shepherd
                        if (myType == 1)
                        {
                            Shepherd shepherd;
                            if (rc.readBroadcast(1) > 6)
                            {
                                 shepherd = new Shepherd(false, rc);
                            }
                            else
                            {
                                 shepherd = new Shepherd(true, rc);
                            }
                            shepherd.run(rc);
                        }
                        // in the last resort we rush towards the enemy HQ shooting anyone we see
                        else
                        {
                            Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
                            Direction dir;
                            if (nearbyEnemies.length > 0)
                            {
                                Shooter shooter = new Shooter(rc);
                                shooter.fire();
                            }
                            else
                            {
                                dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
                                //move(rc, dir);
                                //Utilities.MoveDirection(rc, dir, true);
                                Utilities.MoveMapLocation(rc, rc.senseEnemyHQLocation(), true);
                            }
                        }
					}
				} catch (Exception e) {
					System.out.println("Soldier Exception");
				}
			}
			
			rc.yield();
		}
	}
	
	public static void move(RobotController rc, Direction dir)
	{
		if (rc.isActive())
		{
			while (!rc.canMove(dir))
			{
				dir = directions[rand.nextInt(8)];
			}
			try
			{
				rc.sneak(dir);
			}
			catch (Exception e) 
			{
				System.out.println("Soldier Exception");
			}
		}
	}
}
