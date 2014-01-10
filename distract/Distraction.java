package distract;

import battlecode.common.*;

public class Distraction
{
	RobotController rc;
	boolean moved;
	
	public Distraction(RobotController rc)
	{
		this.rc = rc;
		moved = true;
	}
	
	public void run()
	{
		while(true)
		{
			try
			{
				if(!moved)
				{
					if(rc.isActive())
					{
						Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite(), false);
						moved = true;
					}
				}
				else
				{
					if(rc.isActive())
					{
						rc.construct(RobotType.PASTR);
					}
				}
			}
			catch(Exception e){}
		}
	}
}
