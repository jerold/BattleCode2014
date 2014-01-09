package UED;


import java.util.Random;

import battlecode.common.Direction;
import battlecode.common.GameObject;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;
import battlecode.common.TerrainTile;

/**
 * Created by fredkneeland on 1/7/14.
 *
 * This bot forms into powerful squads which attempt to crush the enemy by destroying bots as they spawn
 * swarms until it reaches the HQ where it spreads out and around just outside of range and kills new units
 * does not commit suicide just dies
 */
public class Goliath 
{
	Random rand = new Random();
	Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	RobotController rc;
	MapLocation targetZone;
	Direction direction;
	boolean gotToWaitingZone = false;
	boolean allTroopsArrived = false;
	boolean amLeader = false;
	MapLocation pastLeaderSpot;
	GameObject leader;
	MapLocation target;
	
	public Goliath(RobotController rc)
	{
        rc.setIndicatorString(0, "Goliath");
		this.rc = rc;
		targetZone = rc.getLocation();
		direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		for (int i = 0; i < 3; i++)
		{
			targetZone = targetZone.add(direction);
		}
		
		while (rc.senseTerrainTile(targetZone).equals(TerrainTile.VOID))
		{
			direction = directions[rand.nextInt(8)];
			targetZone.add(direction);
		}

		pastLeaderSpot = targetZone;
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
                if (rc.isActive())
                {
                    GameObject[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
                    // if we are almost dead then we run to corner to create pastr
                    if (rc.getHealth() < 20)
                    {
                        if (Utilities.turnNuke(rc))
                        {

                        }
                        else
                        {
                            SCV scv = new SCV(rc);
                            scv.run();
                        }
                    }
                    else if (nearByEnemies.length > 0)
                    {
                        Utilities.fire(rc);
                    }
                    // first we need to move to our target location
                    else if (!gotToWaitingZone)
                    {
                        if (rc.getLocation().equals(targetZone))
                        {
                            gotToWaitingZone = true;
                        }
                        // if the bot at targetZone announces that we are ready to go
                        else if (rc.readBroadcast(3) == 5)
                        {
                            gotToWaitingZone = true;
                            allTroopsArrived = true;
                            leader = rc.senseObjectAtLocation(targetZone);
                        }
                        else
                        {
                            Utilities.MoveDirection(rc, rc.getLocation().directionTo(targetZone), false);
                        }
                    }
                    else
                    {
                        if (!allTroopsArrived)
                        {
                            nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
                            if (rc.getHealth() < 20)
                            {
                                if (Utilities.turnNuke(rc))
                                {

                                }
                                else
                                {
                                    SCV scv = new SCV(rc);
                                    scv.run();
                                }
                            }
                            else if (nearByEnemies.length > 0)
                            {
                                Utilities.fire(rc);
                            }

                            if (rc.readBroadcast(3) == 5)
                            {
                                allTroopsArrived = true;
                            }

                            for (int i = 0; i < 6; i++)
                            {
                                rc.yield();
                            }
                            amLeader = true;
                            rc.setIndicatorString(0, "Goliath Leader");
                        }
                        else
                        {
                            // if we have low health either we go nuke or we slip away to build a pastr
                            if (rc.getHealth() < 20)
                            {
                                if (Utilities.turnNuke(rc))
                                {

                                }
                                else
                                {
                                    SCV scv = new SCV(rc);
                                    scv.run();
                                }
                            }
                            // if we are the leader then we start heading toward the enemy HQ
                            else if (amLeader)
                            {
                                Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());

                                GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());
                                GameObject[] nearByBots2 = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam());

                                // we don't want to get to close to the enemy HQ
                                if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 20)
                                {
                                    if (nearByBots2.length > 1)
                                    {
                                        Utilities.fire(rc);
                                    }
                                    else if (nearByBots.length > 1)
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByBots[0])), false);
                                    }
                                    else
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseHQLocation()), false);
                                    }
                                }
                                else
                                {

                                    if (rc.isActive())
                                    {
                                        if (nearByBots2.length > 0)
                                        {
                                            Utilities.fire(rc);
                                        }
                                        else if (nearByBots.length > 0)
                                        {
                                            dir = rc.getLocation().directionTo(rc.senseLocationOf(nearByBots[0]));
                                        }

                                        if (rc.canMove(dir))
                                        {
                                            rc.move(dir);
                                        }
                                        else if (rc.senseTerrainTile(rc.getLocation().add(dir)).equals(TerrainTile.VOID))
                                        {
                                            MapLocation target2 = rc.getLocation().add(dir);
                                            if (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
                                            {
                                                int j = 0;
                                                while (rc.senseTerrainTile(target2).equals(TerrainTile.VOID))
                                                {
                                                    target2 = target2.add(dir);
                                                }
                                                Utilities.MoveMapLocation(rc, target2, true);
                                            }
                                        }
                                        else
                                        {
                                            while (!rc.canMove(dir))
                                            {
                                                dir = directions[rand.nextInt(8)];
                                            }
                                            if (rc.isActive())
                                            {
                                                rc.move(dir);
                                            }
                                        }
                                    }
                                }
                            }
                            // if we are not the leader then we follow
                            else
                            {
                                // if we can't see the leader then we become a leader
                                if (!rc.canSenseObject(leader))
                                {
                                    amLeader = true;
                                }
                                else if (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) < 35)
                                {
                                    amLeader = true;
                                }
                                else
                                {
                                    GameObject[] nearByBots2 = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
                                    target = rc.senseLocationOf(leader);
                                    // if we are getting to far away then we try to catch up to leader
                                    if (rc.getLocation().distanceSquaredTo(target) > 10)
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(target), false);
                                    }
                                    // otherwise we shoot at anything nearby
                                    else if (nearByBots2.length > 0)
                                    {
                                        Utilities.fire(rc);
                                    }
                                    // if we can't shoot anything we keep following leader
                                    else
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(pastLeaderSpot), false);
                                    }
                                    pastLeaderSpot = target;
                                }
                            }
                        }
                    }
                }
			} catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Goliath Exception");
            }
            rc.yield();
		}
	}
}
