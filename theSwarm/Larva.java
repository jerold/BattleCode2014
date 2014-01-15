package theSwarm;

import battlecode.common.*;

public class Larva {
	RobotController rc;
	MapLocation target;
	
	public Larva(RobotController rc)
	{
		this.rc = rc;
		try {
			target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
		} catch (GameActionException e) {
			e.printStackTrace();
		}
		rc.setIndicatorString(0, "Larva");
	}
	
	public void run()
	{
		while (true)
		{
			try
			{
                Robot[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                if (nearByEnemies.length > 0)
                {
                    int[] AllEnemyBots = FightMicro.AllEnemyBots(rc);
                    int[] AllEnemyNoiseTowers = FightMicro.AllEnemyNoiseTowers(rc);

                    FightMicro.FindAndRecordAllEnemies(rc, nearByEnemies, AllEnemyBots, AllEnemyNoiseTowers);
                }

				// we will only do stuff if we are active
				if (rc.isActive())
				{
					rc.setIndicatorString(1, "Target:" + target);
					if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
					{
						target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
					}
					else
					{
						Movement.MoveMapLocation(rc, target, false);
					}
					
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
