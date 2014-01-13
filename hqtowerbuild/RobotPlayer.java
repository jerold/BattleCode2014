package hqtowerbuild;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		int type = 0;
		
		while(true)
		{
			try
			{
				if(rc.getType() == RobotType.HQ)
				{
					new SmartHQ(rc).run();
				}
				else if(rc.getType() == RobotType.NOISETOWER)
				{
					new HQTower(rc).run();
				}
				else
				{
					if(type == 0)
					{
						type = rc.readBroadcast(0);
					}
					if(type == SmartHQ.DURAN)
					{
						new Duran(rc).run();
					}
					else if(type == SmartHQ.GHOST)
					{
						new Ghost(rc).run();
					}
					else if(type == SmartHQ.MULE)
					{
						new SupplyDepot(rc).run();
					}
					else if(type == SmartHQ.TOWER)
					{
						new HQTower(rc).run();
					}
				}
			}
			catch(Exception e){}
		}
	}
}
