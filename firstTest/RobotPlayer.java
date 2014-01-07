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
					bestSpots = Utilities.BestPastureSpots(rc);
                    countofBestSpots = bestSpots.length;
					
					if (rc.isActive() && rc.senseRobotCount() < 25) {
						
						
						Direction toEnemy = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
						if (rc.senseObjectAtLocation(rc.getLocation().add(toEnemy)) == null) {
							rc.spawn(toEnemy);
							rc.broadcast(1, numbOfSoldiers);
							numbOfSoldiers++;
							if (numbOfSoldiers / 2 < countofBestSpots)
							{
								rc.broadcast(2, bestSpots[numbOfSoldiers/2].x);
								rc.broadcast(3, bestSpots[numbOfSoldiers/2].y);
							}
							
						}
					}
				} catch (Exception e) {
					System.out.println("HQ Exception");
				}
			}
			
			if (rc.getType() == RobotType.SOLDIER) {
				try {
					if (rc.isActive()) {
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
						Robot[] nearbyEnemies = rc.senseNearbyGameObjects(Robot.class,10,rc.getTeam().opponent());
						Direction dir;
						if (nearbyEnemies.length > 0)
						{
							rc.attackSquare(rc.senseLocationOf(nearbyEnemies[0]));
						}
						else if (myType == 1)
						{
							if (x == 0)
							{
								x = rc.readBroadcast(2);
								y = rc.readBroadcast(3);
                                myType = 2;
								target = new MapLocation(x,y);
							}
							
							else if (rc.getLocation() != target)
							{
								dir = rc.getLocation().directionTo(target);
								Utilities.MoveDirection(rc, dir, true);
							}
							else
							{
								rc.construct(RobotType.PASTR);
							}
						}
						else
						{
							dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
							//move(rc, dir);
							//Utilities.MoveDirection(rc, dir, true);
							Utilities.MoveMapLocation(rc, rc.senseEnemyHQLocation(), true);
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
