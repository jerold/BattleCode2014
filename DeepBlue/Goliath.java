package DeepBlue;


import battlecode.common.*;

import java.util.Random;

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
    MapLocation newTarget;
    int waitTime = 0;

    // channels for communication
    static final int EnemyHQChannel = 0;
    static final int OurHQChannel = 1;
    static final int TroopType = 2;
    static final int GhostNumb = 3;
    static final int GoliathOnline = 4;
    static final int GhostReady = 5;
    static final int BattleCruiserLoc = 6;
    static final int BattleCruiserLoc2 = 7;
    static final int BattleCruiserArrived = 8;
    static final int startBattleCruiserArray = 9;
    static final int endBattleCruiserArray = 59;
    static final int BattleCruiserInArray = 60;
    static final int GoliathReadyForCommand = 61;
    static final int GoliathNextLocation = 62;
    static final int GoliathCurrentLocation = 63;
    static final int PastStartChannel = 10000;
	
	public Goliath(RobotController rc)
	{
        rc.setIndicatorString(0, "Goliath");
		this.rc = rc;
		targetZone = rc.getLocation();
		direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		for (int i = 0; i < 3; i++)
		{
			targetZone = targetZone.subtract(direction);
		}
		
		while (rc.senseTerrainTile(targetZone).equals(TerrainTile.VOID))
		{
			direction = directions[rand.nextInt(8)];
			targetZone.add(direction);
		}

        try
        {
            if (rc.readBroadcast(GoliathNextLocation) == 0)
            {
                rc.broadcast(GoliathNextLocation, Utilities.convertMapLocationToInt(targetZone));
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Goliath Exception");
        }
	}

	public void run()
	{
		while (true)
		{
			try
			{
                if (rc.isActive())
                {
                    if (Utilities.fightMode(rc))
                    {

                    }
                    // first we need to move to our target location
                    else if (!gotToWaitingZone)
                    {
                        if (rc.getLocation().equals(targetZone))
                        {
                            gotToWaitingZone = true;
                        }
                        // if the bot at targetZone announces that we are ready to go
                        else if (rc.readBroadcast(GoliathOnline) == 1)
                        {
                            gotToWaitingZone = true;
                            allTroopsArrived = true;
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
                            if (Utilities.fightMode(rc))
                            {

                            }
                            else if (rc.readBroadcast(GoliathOnline) == 1)
                            {
                                allTroopsArrived = true;
                            }
                        }
                        else
                        {
                            if (Utilities.fightMode(rc))
                            {
                            }
                            else if (newTarget == null)
                            {
                                newTarget = Utilities.convertIntToMapLocation(rc.readBroadcast(GoliathNextLocation));
                            }
                            else if (rc.getLocation().isAdjacentTo(newTarget) || rc.getLocation().equals(newTarget))
                            {
                                if (newTarget.equals(Utilities.convertIntToMapLocation(rc.readBroadcast(GoliathNextLocation))))
                                {
                                    rc.broadcast(GoliathReadyForCommand, 1);
                                }
                                else
                                {
                                    newTarget = Utilities.convertIntToMapLocation(rc.readBroadcast(GoliathNextLocation));
                                    waitTime = 0;
                                }
                            }
                            else
                            {
                                waitTime = 0;
                                Utilities.MoveMapLocation(rc, newTarget, false);
                            }

                            /*
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
                            */
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
