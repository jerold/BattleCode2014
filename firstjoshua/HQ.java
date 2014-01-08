package firstjoshua;

import battlecode.common.*;

public class HQ
{
	public void run(RobotController rc)
	{
		Direction dirs[] = Direction.values();
		Shooter shooter = new Shooter(rc);
		int k = 0;
		boolean created = false;
		while(true)
		{
			try
			{
				if(rc.isActive() && rc.canMove(dirs[k]) && !created)
				{
					rc.spawn(dirs[k]);
					k = (k + 1) % 8;
					created = true;
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