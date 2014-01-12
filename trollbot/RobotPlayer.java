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
					if(rc.isActive() && !created)
					{
						rc.spawn(Direction.NORTH);
						created = true;
					}
				}
				else
				{
					new TrollTower(rc).run();
				}
			}
			catch(Exception e){}
		}
	}
}
