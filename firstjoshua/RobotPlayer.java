package firstjoshua;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		if(rc.getType() == RobotType.HQ)
		{
			new HQ().run(rc);
		}
		else
		{
			new Soldier().run(rc);
		}
	}
}
