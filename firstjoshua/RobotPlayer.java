package firstjoshua;

import battlecode.common.*;

public class RobotPlayer
{
	public static void run(RobotController rc) throws GameActionException
	{
		if(rc.getType() == RobotType.HQ)
		{
			new HQ().run(rc);
		}
		else
		{
			if(rc.readBroadcast(0) == 0)
			{
				new PASTR(rc).run();
			}
			else// if(rc.readBroadcast(0) == 1)
			{
				new SoundTower(rc).run();
			}
			/*else
			{
				new SoundTower2(rc).run();
			}*/
		}
	}
}
