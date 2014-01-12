package smartbot;

import battlecode.common.*;

/*
 * Corners are:
 * 1 2
 * 3 4
 */
public class Scout
{
	RobotController rc;
	MapLocation[] pastrs;
	Robot[] enemies;
	int[][] cows;
	int cowTotal, soldierTotal, towerTotal;
	int distAround = 7;
	
	public Scout(RobotController rc)
	{
		this.rc = rc;
		pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
		cows = new int[distAround][distAround];
	}
	
	public void run()
	{
		while(true)
		{
			for(int k = 0; k < pastrs.length; k++)
			{
				if(Utilities.MapLocationNextToEnemyHQ(rc, pastrs[k]))
				{
				}
				else
				{
					for(int a = 0; a < distAround; a++)
					{
						for(int t = 0; t < distAround; t++)
						{
							cows[a][t] = 0;
						}
					}
					cowTotal = 0;
					soldierTotal = 0;
					towerTotal = 0;
					goToPastr(k);
					fillInfo(k);
					if(soldierTotal == 0)
					{
						Utilities.MoveMapLocation(rc, pastrs[k], false);
					}
					interpret();
					broadcastInfo(k);
				}
			}
			
			pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
		}
	}
	
	//Goes to whichever corner indicated as best as it can without being seen.
	private void goToPastr(int pastr)
	{
		MapLocation target;
		target = pastrs[pastr];
		Direction toTarget = rc.getLocation().directionTo(target);
		target = target.add(toTarget.opposite(), 6);
		
		while(rc.senseTerrainTile(target) == TerrainTile.VOID)
		{
			target = target.add(toTarget.opposite());
		}
		
		Utilities.MoveMapLocation(rc, target, false);
	}
	
	//Takes the info to put in the array given which corner it is on.
	//For the enemies, 0 is unknown, 1 is blank, 2 is soldier, 3 is tower, 4 is pasture
	private void fillInfo(int pastr)
	{
		enemies = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent());
		
		for(int k = 0; k < enemies.length; k++)
		{
			try
			{
				int x = rc.senseRobotInfo(enemies[k]).location.x;
				int y = rc.senseRobotInfo(enemies[k]).location.y;
				
				if(Math.abs(pastrs[pastr].x - x) < distAround / 2 && Math.abs(pastrs[pastr].y - y) < distAround / 2)
				{
					if(rc.senseRobotInfo(enemies[k]).type == RobotType.SOLDIER)
					{
						soldierTotal++;
					}
					else if(rc.senseRobotInfo(enemies[k]).type == RobotType.NOISETOWER)
					{
						towerTotal++;
					}
				}
			}
			catch(Exception e){}
		}
		
		for(int k = pastrs[pastr].x - distAround / 2; k <= pastrs[pastr].x + distAround / 2; k++)
		{
			for(int a = pastrs[pastr].y - distAround / 2; a <= pastrs[pastr].y + distAround / 2; a++)
			{
				if(rc.canSenseSquare(new MapLocation(k, a)))
				{
					try
					{
						cows[k - pastrs[pastr].x - distAround / 2][a - pastrs[pastr].x + distAround / 2] = (int)rc.senseCowsAtLocation(new MapLocation(k, a));
					}
					catch(Exception e){}
				}
			}
		}
	}
	
	//Takes the info taken from the corners and computes cows enemy soldiers, and enemy towers there.
	private void interpret()
	{
		for(int k = 0; k < distAround; k++)
		{
			for(int a = 0; a < distAround; a++)
			{
				cowTotal += cows[k][a];
			}
		}
		

		rc.setIndicatorString(0, "Cow Total: " + cowTotal);
		rc.setIndicatorString(1, "Soldier Total: " + soldierTotal);
		rc.setIndicatorString(2, "Tower Total: " + towerTotal);
	}
	
	//Broadcasts the info on to the rest of the team.
	private void broadcastInfo(int pastr)
	{
	}
}
