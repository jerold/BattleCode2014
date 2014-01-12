package trollbot;

import battlecode.common.*;

public class TrollTower
{
	RobotController rc;
	MapLocation[] pastrs;
	MapLocation target;
	
	public TrollTower(RobotController rc)
	{
		this.rc = rc;
		pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
		target = null;
		
		for(int k = 0; k < pastrs.length; k++)
		{
			if(Utilities.MapLocationNextToEnemyHQ(rc, pastrs[k]))
			{
				target = pastrs[k];
			}
		}
		if(target == null)
		{
			int corner = Utilities.findOpposingCorner(rc);
			switch(corner)
			{
				case 1:
					target = new MapLocation(0,0);
					break;
				case 2:
					target = new MapLocation(rc.getMapWidth() - 1, 0);
					break;
				case 3:
					target = new MapLocation(0, rc.getMapHeight() - 1);
					break;
				default:
					target = new MapLocation(rc.getMapWidth() - 1, rc.getMapHeight() - 1);
					break;
			}
		}
	}
	
	public void run()
	{
		while(true)
		{
			if(rc.getType() == RobotType.SOLDIER)
			{
				Utilities.MoveMapLocation(rc, target, false);
				
				while(!rc.isActive()){}
				
				try
				{
					rc.construct(RobotType.NOISETOWER);
				}
				catch(Exception e){}
			}
			else if(rc.getType() == RobotType.NOISETOWER)
			{
				Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 100, rc.getTeam().opponent());
				for(int k = 0; k < enemies.length; k++)
				{
					try
					{
						if(rc.senseRobotInfo(enemies[k]).type == RobotType.PASTR && rc.isActive())
						{
							rc.attackSquare(rc.senseRobotInfo(enemies[k]).location);
						}
					}
					catch(Exception e){}
				}
			}
			rc.yield();
		}
	}
}
