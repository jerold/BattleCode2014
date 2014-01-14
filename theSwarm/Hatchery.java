package theSwarm;

import battlecode.common.*;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
	
	public Hatchery(RobotController rc)
	{
		this.rc = rc;
		
		HQFunctions.InitialLocationBroadcasts(rc);
		
		HQFunctions.findInitialRally(rc);
	}
	
	public void run()
	{
		while (true)
		{
			Movement.fire(rc);
			if (rc.isActive())
			{
				HQFunctions.SpawnSoldiers(rc);
			}
			
			if (Clock.getRoundNum() % 50 == 0 && Clock.getRoundNum() > 100)
			{
				HQFunctions.moveTargetLocationRandomly(rc);
			}
			rc.yield();
		}
	}
}
