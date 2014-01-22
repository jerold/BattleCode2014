package theSwarm5;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    int fightZone;
    int roundSet = 0;

    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
	
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
            try
            {
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                if (rc.isActive())
                {
                    Movement.fire(rc, enemies, null);
                    HQFunctions.SpawnSoldiers(rc);
                }

                int broadcast = rc.readBroadcast(rallyPoint2);
                if (broadcast != 0)
                {
                    if (broadcast != fightZone)
                    {
                        fightZone = broadcast;
                        roundSet = Clock.getRoundNum();
                    }
                    // now it is time for us to move on
                    else if (roundSet + 100 < Clock.getRoundNum())
                    {
                        fightZone = 0;
                        rc.broadcast(rallyPoint2, 0);
                    }
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
            } catch (Exception e) {}
			rc.yield();
		}
	}
}
