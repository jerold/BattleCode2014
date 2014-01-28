package theSwarm6;

import battlecode.common.*;

public class Larva {
	RobotController rc;
	MapLocation target;
    int ourIndex;
    towerPastrRequest request;

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
		request = new towerPastrRequest(rc);
		rc.setIndicatorString(0, "Larva");
        //ourIndex = FightMicro.ourSlotInMessaging(rc);


	}
	
	public void run()
	{
		while (true)
		{
			try
			{
				// we will only do stuff if we are active
				if (rc.isActive())
				{
                    int[] get = request.checkForNeed();
                    if(get[0] != -1)
                    {
                    	if(get[2] == 0)
                    	{
                    		new Extractor(rc, get[1], TowerUtil.convertIntToMapLocation(get[0])).run();
                    	}
                    	else
                    	{
                    		new Drone(rc, get[1], TowerUtil.convertIntToMapLocation(get[0])).run();
                    	}
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
