package distract;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		if(rc.getType() == RobotType.HQ)
		{
			new HQ(rc).run();
		}
		else if(rc.getType() == RobotType.NOISETOWER)
		{
			new SoundTower(rc).run();
		}
		else if(rc.getType() == RobotType.PASTR)
		{
			int k = 5;
			int a = 0;
			while(true)
			{
				if(rc.getHealth() < 30)
				{
					int t = k / a;
				}
			}
		}
		else
		{
			try
			{
				if(rc.readBroadcast(0) <= 3)
				{
					new Distraction(rc).run();
				}
				else if(rc.readBroadcast(0) <= 4)
				{
					new PASTR(rc).run();
				}
				else if(rc.readBroadcast(0) <= 5)
				{
					new SoundTower(rc).run();
				}
				else if(rc.readBroadcast(0) % 3 == 1)
				{
					new TowerCircle(rc).run();
				}
				else
				{
					new HQCircle(rc).run();
				}
			}
			catch (Exception e){}
		}
	}
}
