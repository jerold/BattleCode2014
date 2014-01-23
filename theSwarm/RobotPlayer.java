package theSwarm;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer
{
	static RobotController rc;
	
	
	public static void run(RobotController rcIn)
	{
		rc = rcIn;
		
		while (true)
		{
			try
			{
				if (rc.getType() == RobotType.HQ)
				{
                    Kerrigan Sarah = new Kerrigan(rc);
                    Sarah.run();
					//Hatchery hatchery = new Hatchery(rc);
					//hatchery.run();
				}

				else if (rc.getType() == RobotType.NOISETOWER)
				{
                    GenericTower tower = new GenericTower(rc, false);
                    tower.run();
				}
				else if (rc.getType() == RobotType.PASTR)
				{
                    hiveClusters hiveclusters = new hiveClusters(rc);
                    hiveclusters.run();
				}
				// other wise we must be a soldier
				else
				{
					// our hq spawns larva which run to a rally point and then morph into whatever unit is most critical at that point
					Larva larva = new Larva(rc);
					larva.run();
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
