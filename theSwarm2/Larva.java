package theSwarm2;

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
	
	public Larva(RobotController rc)
	{
		this.rc = rc;
		try {
			target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		rc.setIndicatorString(0, "Larva");
        ourIndex = FightMicro.ourSlotInMessaging(rc);

        AllEnemyBots = FightMicro.AllEnemyBots(rc);
        AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);
        nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
        AllAlliedBots = FightMicro.AllAlliedBotsInfo(rc);

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

					rc.setIndicatorString(1, "Target:" + target);
                    if (FightMicro.fightMode(rc, nearByEnemies))//, AllAlliedBots, AllEnemyBots))
                    {
                        rc.setIndicatorString(2, "Running fight micro");
                    }
					else if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
					{
						target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));

                        FightMicro.PostOurInfoToWall(rc, ourIndex);
                        nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                        if (nearByEnemies.length > 0)
                        {
                            AllEnemyBots = FightMicro.AllEnemyBots(rc);
                            //AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);

                            //FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);

                            if (rc.getHealth() < (nearByEnemies.length * 5) + 15)
                            {
                                FightMicro.removeOurSelvesFromBoard(rc, FightMicro.AllAlliedBotsInfo(rc), ourIndex);
                            }
                        }
					}
					else
					{
						Movement.MoveMapLocation(rc, target, false);
					}
				}
                /*
                if (!rc.isActive() || rc.senseTerrainTile(rc.getLocation()).equals(TerrainTile.ROAD) && Clock.getRoundNum() % 2 == 0)
                {
                    AllAlliedBots = FightMicro.AllAlliedBotsInfo(rc);
                    FightMicro.PostOurInfoToWall(rc, ourIndex);
                    nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                    if (nearByEnemies.length > 0)
                    {
                        AllEnemyBots = FightMicro.AllEnemyBots(rc);
                        AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);

                        FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);

                        if (rc.getHealth() < nearByEnemies.length * 5)
                        {
                            FightMicro.removeOurSelvesFromBoard(rc, FightMicro.AllAlliedBotsInfo(rc), ourIndex);
                        }
                    }
                }*/
				
			} catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("RobotPlayer Exception");
            }
            rc.yield();
			
		}
	}

}
