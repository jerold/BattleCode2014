package theSwarm4;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import sun.util.logging.resources.logging;

public class Larva {
	RobotController rc;
	MapLocation target;
    int ourIndex;

    Robot[] nearByEnemies;
    int[] AllEnemyNoiseTowers;
    int[] AllEnemyBots;
    int[] AllAlliedBots;

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
	
	public Larva(RobotController rc)
	{
		this.rc = rc;
		try {
			target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		rc.setIndicatorString(0, "Larva");
        //ourIndex = FightMicro.ourSlotInMessaging(rc);


	}
	
	public void run()
	{
		while (true)
		{
			try
			{
                //System.out.println("Hello world");
				// we will only do stuff if we are active
				if (rc.isActive())
				{

					int k = rc.readBroadcast(needPastr);
                    if (rc.readBroadcast(needNoiseTower) == 1)
                    {
                        rc.broadcast(needNoiseTower, 0);
                        Extractor extractor = new Extractor(rc);
                        extractor.run();
                    }
                    else if (k != 0)
                    {
                        rc.broadcast(needPastr, 0);
                        Drone drone = new Drone(rc, k);
                        drone.run();
                    }

                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
                    }

					rc.setIndicatorString(1, "Target:" + target);
                    rc.setIndicatorString(2, "Not Running fight micro 1");
                    MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
                    if ((rc.readBroadcast(defendPastr) == 1) && FightMicro.defenseMicro(rc, Utilities.convertIntToMapLocation(rc.readBroadcast(pastLoc))))
                    {

                    }
                    else if (FightMicro.fightMode(rc, target))
                    {
                        rc.setIndicatorString(2, "Running fight micro");
                    }
					else
					{
                        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                        if (enemyPastrs.length == 0)
                        {
                            int location = rc.readBroadcast(HQFunctions.rallyPoint2Channel());
                            if (location != 0)
                            {
                                target = Movement.convertIntToMapLocation(location);
                            }
                        }
						Movement.MoveMapLocation(rc, target, false, true);
					}
				}
                else
                {

                }
				
			} catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("RobotPlayer Exception");
            }
            rc.yield();
			
		}
	}

}
