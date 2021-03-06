package theSwarm4;

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
				//HQFunctions.moveTargetLocationRandomly(rc);
                /*
                if (goneForPastr && (rc.sensePastrLocations(rc.getTeam()).length > 0 || roundNum > (Clock.getRoundNum() - 250)))
                {
                    HQFunctions.setTargetLocation(rc, goneForPastr);
                }
                else
                {
                    goneForPastr = HQFunctions.setTargetLocation(rc, goneForPastr);
                    roundNum = Clock.getRoundNum();
                }*/
                HQFunctions.setTargetLocation(rc, true);
                //HQFunctions.findInitialRally(rc);

			}
			
			if(build)
			{
				try
				{
					rc.broadcast(4, 1);
					rc.broadcast(5, 3);
					build = false;
				}
				catch(Exception e){}
			}
			rc.yield();
		}
	}
}
