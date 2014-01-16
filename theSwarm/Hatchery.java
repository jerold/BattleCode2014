package theSwarm;

import battlecode.common.*;

public class Hatchery {
	
	RobotController rc;
	MapLocation target;
	
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
              //  rc.setIndicatorString(0, "1: "+rc.readBroadcast(20000) + " 2: "+rc.readBroadcast(20001) + " 3: "+rc.readBroadcast(20002) + "4: "+rc.readBroadcast(20003));
               
            } catch (Exception e) {}
            //rc.setIndicatorString(1, "Number of Enemies: "+FightMicro.NumbOfKnownEnemyBots(rc, FightMicro.AllEnemyBots(rc)));
            //rc.setIndicatorString(2, "All Enemy Bots:"+FightMicro.AllEnemyBots(rc)[1] + " 2: " +FightMicro.AllEnemyBots(rc)[2]);
            
			Movement.fire(rc);
			if (rc.isActive())
			{
				HQFunctions.SpawnSoldiers(rc);
			}
			
			if (Clock.getRoundNum() % 50 == 0 && Clock.getRoundNum() > 100)
			{
				HQFunctions.moveTargetLocationRandomly(rc);
				
                long[] AllEnemies = FightMicro.AllEnemyBots(rc);
                //long[] AllAllies = FightMicro.AllAlliedBotsInfo(rc);

                rc.setIndicatorString(0, ""+AllEnemies.length);
                rc.setIndicatorString(1, "Number of Enemies: " + FightMicro.NumbOfKnownEnemyBots(AllEnemies));
                /*
                System.out.println("Enemy Bots info: ");
				for (int i = 0; i < AllEnemies.length; i++)
	            {
	            	System.out.print(AllEnemies[i]);
	            	System.out.print(", ");
                    //rc.setIndicatorString(1, ""+FightMicro.NumbOfKnownEnemyBots(rc, FightMicro.AllEnemyBots(rc)));
	            }*/
				System.out.println();
				/*
				System.out.println();
                System.out.println("Our Bots Info: ");
                for (int j = 0; j<25; j++)
                {
                    //System.out.println(AllAllies[j]);
                }
				System.out.println();
				*/
			}
			rc.yield();
		}
	}
}
