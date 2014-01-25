package theSwarm;

import battlecode.common.*;

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
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;
    static final int morphRoach = 17;
	
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
                if (FightMicro.fightMode(rc, target))
                {
                    rc.setIndicatorString(2, "Running fight micro");
                }
				else if (rc.isActive())
				{
                    if (Clock.getRoundNum() < 20)
                    {
                        MapLocation pastrSpot;
                        pastrSpot = TowerUtil.bestSpot3(rc);
                        rc.broadcast(pastLoc, Movement.convertMapLocationToInt(pastrSpot));
                        rc.yield();
                    }

                    if (Clock.getRoundNum() % 10 == 0)
                    {
                        //HQFunctions.setTargetLocation(rc, true);
                    }
                    int noiseTowerBroadcast = rc.readBroadcast(needNoiseTower);
                    int pastrBroadcast = rc.readBroadcast(needPastr);

                    rc.setIndicatorString(1, "Hydralisk:"+rc.readBroadcast(morphHydralisk)+", Zergling: "+ rc.readBroadcast(morphZergling)+", Roach: "+rc.readBroadcast(morphRoach));

                    if (noiseTowerBroadcast != 0)
                    {
                        rc.broadcast(needNoiseTower, 0);
                        Extractor extractor = new Extractor(rc, noiseTowerBroadcast);
                        extractor.run();
                    }
                    else if (pastrBroadcast != 0)
                    {
                        rc.broadcast(needPastr, 0);
                        Drone drone = new Drone(rc, pastrBroadcast);
                        drone.run();
                    }
                    else if (rc.readBroadcast(morphHydralisk) == 1)
                    {
                        rc.broadcast(morphHydralisk, 0);
                        Hydralisk hydralisk = new Hydralisk(rc);
                        hydralisk.run();
                    }
                    else if (rc.readBroadcast(morphZergling) == 1)
                    {
                        rc.broadcast(morphZergling, 0);
                        Zergling zergling = new Zergling(rc);
                        zergling.run();
                    }
                    else if (rc.readBroadcast(morphRoach) == 1)
                    {
                        rc.broadcast(morphRoach, 0);
                        Roach roach = new Roach(rc);
                        roach.run();
                    }

                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
                    }

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
