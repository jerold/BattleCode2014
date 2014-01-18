package towerbot;

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
					new GenericTower(rc, false).run();
				}
				else
				{
					if(type == 0)
					{
						type = rc.readBroadcast(0);
					}
					else if(type == SmartHQ.DURAN)
					{
						//new Duran(rc).run();
					}
					else if(type == SmartHQ.GHOST)
					{
						//new Ghost(rc).run();
					}
					else if(type == SmartHQ.GOLIATH)
					{
						//new Goliath(rc).run();
					}
					else if(type == SmartHQ.MARINE)
					{
						//new Marines(rc).run();
					}
					else if(type == SmartHQ.MULE)
					{
						new MULE(rc, true).run();
					}
					else if(type == SmartHQ.TOWER)
					{
						new SmartTower(rc).run();
					}
					else if(type == SmartHQ.TROLL)
					{
						//new Scout(rc).run();
					}
				}
			}
			catch(Exception e){}
		}
	}
}
