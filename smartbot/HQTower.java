package smartbot;

import battlecode.common.*;

public class HQTower
{
	RobotController rc;
	MapLocation target;
	
	public HQTower(RobotController rc)
	{
		this.rc = rc;
		if(rc.getType() == RobotType.NOISETOWER)
		{
			MapLocation[] pastrs = rc.sensePastrLocations(rc.getTeam());
			
			for(int k = 0; k < pastrs.length; k++)
			{
				if(pastrs[k].distanceSquaredTo(rc.senseHQLocation()) < 10)
				{
					target = pastrs[k];
				}
			}
			
			if(target == null)
			{
				target = rc.senseHQLocation();
			}
		}
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				if(rc.getType() == RobotType.SOLDIER)
				{
					if(rc.isActive())
					{
						rc.construct(RobotType.NOISETOWER);
					}
				}
				else if(rc.getType() == RobotType.NOISETOWER)
				{
					for(int k = 20; k > 2; k -= 2)
					{
						Utilities.fireCircle(rc, k, target);
					}
				}
			}
			catch(Exception e){}
		}
	}
}
