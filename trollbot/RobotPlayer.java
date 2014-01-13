package trollbot;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		boolean created = false;
		while(true)
		{
			try
			{
				if(rc.getType() == RobotType.HQ)
				{
					MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam().opponent());
					MapLocation target = null;
					for(int k = 0; k < pastrs.length; k++)
					{
						if(Utilities.MapLocationNextToEnemyHQ(rc, pastrs[k]))
						{
							target = pastrs[k];
						}
					}
					if(rc.isActive() && !created && target != null)
					{
						rc.broadcast(0, Utilities.convertMapLocationToInt(target));
						rc.spawn(Direction.NORTH);
						created = true;
					}
				}
				else if(rc.getType() == RobotType.SOLDIER)
				{
					new TrollTower(rc, Utilities.convertIntToMapLocation(rc.readBroadcast(0))).run();
				}
				else
				{
					new GenericTower(rc, true, Utilities.convertIntToMapLocation(rc.readBroadcast(0))).run();
				}
			}
			catch(Exception e){}
		}
	}
}
