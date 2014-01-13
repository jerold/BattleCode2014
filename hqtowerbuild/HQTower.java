package hqtowerbuild;

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
			}
			catch(Exception e){}
		}
	}
}
