package firstjoshua;

import battlecode.common.*;

public class HQ
{
	public void run(RobotController rc)
	{
		Direction dirs[] = Direction.values();
		Shooter shooter = new Shooter(rc);
		int k = 0;
		int created = 0;
		while(true)
		{
			try
			{
				if(rc.isActive() && rc.canMove(dirs[k]) && created < 2)
				{
					rc.broadcast(0, created);
					rc.spawn(dirs[k]);
					k = (k + 1) % 8;
					created++;
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