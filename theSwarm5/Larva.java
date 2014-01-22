package theSwarm5;

import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

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


                    if (rc.senseTeamMilkQuantity(rc.getTeam()) > 9000000)
                    {
                        rc.wearHat();
                    }

                    if (rc.readBroadcast(needNoiseTower) == 1)
                    {
                        rc.broadcast(needNoiseTower, 0);
                        Extractor extractor = new Extractor(rc);
                        extractor.run();
                    }
                    else if (rc.readBroadcast(needPastr) == 1)
                    {
                        rc.broadcast(needPastr, 0);
                        Drone drone = new Drone(rc);
                        drone.run();
                    }

                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
                    }

					rc.setIndicatorString(1, "Target:" + target);
                    rc.setIndicatorString(2, "Not Running fight micro 1");
                    if (FightMicro.fightMode(rc, target))
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
