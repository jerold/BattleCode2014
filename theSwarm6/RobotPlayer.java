package theSwarm6;

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
					Hatchery hatchery = new Hatchery(rc);
					hatchery.run();
				}

				else if (rc.getType() == RobotType.NOISETOWER)
				{
                    new GenericTower(rc, false).run();
				}
				else if (rc.getType() == RobotType.PASTR)
				{
					new GenericPastr(rc).run();
				}
				// other wise we must be a soldier
				else
				{
					// our hq spawns larva which run to a rally point and then morph into whatever unit is most critical at that point
					new Larva(rc).run();
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
