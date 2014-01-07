package pastrattacker;

import battlecode.common.*;

public class HQ
{
	public void run(RobotController rc)
	{
		Direction dirs[] = Direction.values();
		Shooter shooter = new Shooter(rc);
		int k = 0;
		while(true)
		{
			rc.setIndicatorString(0, "(" + rc.getMapWidth() + ", " + rc.getMapHeight() + ")");
			try
			{
				if(rc.isActive() && rc.canMove(dirs[k]) && rc.senseRobotCount() < GameConstants.MAX_ROBOTS)
				{
					rc.spawn(dirs[k]);
					k = (k + 1) % 8;
				}
				if(rc.isActive())
				{
					shooter.fire();
				}
			}
			catch(Exception e){}
			
			rc.yield();
		}
	}
}