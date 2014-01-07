package firstjoshua;

import battlecode.common.*;

public class Soldier
{
	public void run(RobotController rc)
	{
		Shooter shooter = new Shooter(rc);
		while(true)
		{
			try
			{
				shooter.fire();
			}
			catch(Exception e){}
		}
	}
}
