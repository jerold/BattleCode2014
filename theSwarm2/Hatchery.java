package theSwarm2;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
	
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
			Movement.fire(rc, enemies);
			if (rc.isActive())
			{
				HQFunctions.SpawnSoldiers(rc);
			}

			if (Clock.getRoundNum() % 5 == 0 && Clock.getRoundNum() > 100)
			{
				//HQFunctions.moveTargetLocationRandomly(rc);
                if (goneForPastr && (rc.sensePastrLocations(rc.getTeam()).length > 0 || roundNum > (Clock.getRoundNum() - 250)))
                {
                    HQFunctions.setTargetLocation(rc, goneForPastr);
                }
                else
                {
                    goneForPastr = HQFunctions.setTargetLocation(rc, goneForPastr);
                    roundNum = Clock.getRoundNum();
                }
				
                int[] AllEnemies = FightMicro.AllEnemyBots(rc);
                int[] AllAllies = FightMicro.AllAlliedBotsInfo(rc);
                //long[] AllAllies = FightMicro.AllAlliedBotsInfo(rc);

                rc.setIndicatorString(0, ""+AllEnemies.length);
                rc.setIndicatorString(1, "Number of Enemies: " + FightMicro.NumbOfKnownEnemyBots(AllEnemies));
                rc.setIndicatorString(2, "Number of Allies: " + FightMicro.NumbOfAllies(AllAllies));
                
                System.out.println("Enemy Bots info: ");
				for (int i = 0; i < AllEnemies.length; i++)
	            {
	            	System.out.print(FightMicro.getBotID(AllEnemies[i]));
	            	System.out.print(", ");
                    //rc.setIndicatorString(1, ""+FightMicro.NumbOfKnownEnemyBots(rc, FightMicro.AllEnemyBots(rc)));
	            }
				System.out.println();
			}
			rc.yield();
		}
	}
}
