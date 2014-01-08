package attacker;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc)
	{
		if(rc.getType() == RobotType.HQ)
		{
			new HQ().run(rc);
		}
		else if(rc.getType() == RobotType.SOLDIER)
		{
			new HQCircle(rc).run();
		}
	}
}
