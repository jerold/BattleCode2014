package theSwarm;

import battlecode.common.*;

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
					Hatchery hatchery = new Hatchery(rc);
					hatchery.run();
				}
				
				else if (rc.getType() == RobotType.NOISETOWER)
				{
					
				}
				else if (rc.getType() == RobotType.PASTR)
				{
					
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
