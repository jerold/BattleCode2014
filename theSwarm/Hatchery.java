package theSwarm;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    boolean build = true;
    boolean build2 = true;
	
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
            Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

			if (rc.isActive())
			{
                Movement.fire(rc, enemies, null);
				HQFunctions.SpawnSoldiers(rc);
			}

			if (Clock.getRoundNum() % 5 == 0 && Clock.getRoundNum() > 100)
			{
                HQFunctions.setTargetLocation(rc, true);
			}
			
			if(Clock.getRoundNum() % 500 == 0 && rc.sensePastrLocations(rc.getTeam()).length == 0)
			{
				build = true;
				build2 = true;
			}
			
			try
			{
				if(build && rc.readBroadcast(4) == 0 && rc.readBroadcast(5) == 0)
				{
					rc.broadcast(4, 1);
					rc.broadcast(5, 50);
					build = false;
				}
				else if(build2 && rc.readBroadcast(4) == 0 && rc.readBroadcast(5) == 0)
				{
					rc.broadcast(4, -1);
					rc.broadcast(5, -3);
					build2 = false;
				}
			}
			catch(Exception e) {}
			
			
			rc.yield();
		}
	}
}
