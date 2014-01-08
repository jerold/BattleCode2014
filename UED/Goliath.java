package UED;


import java.util.Random;

import battlecode.common.Direction;
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
	Random rand;
	Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
	RobotController rc;
	MapLocation targetZone;
	Direction direction;
	boolean gotToWaitingZone = false;
	
	public Goliath(RobotController rc)
	{
		this.rc = rc;
		targetZone = rc.getLocation();
		direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
		for (int i = 0; i < 5; i++)
		{
			targetZone = targetZone.add(direction);
		}
		
		while (rc.senseTerrainTile(targetZone).equals(TerrainTile.VOID))
		{
			direction = directions[rand.nextInt(8)];
			targetZone.add(direction);
		}
	}
	
	public void run()
	{
		// first we need to move to our target location
		//if (!)
	}
}
