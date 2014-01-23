package ultraGreedy;

import battlecode.common.GameActionException;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class RobotPlayer
{
    static int myType = 0;
    static final int PASTR = 1;
    static final int MARINE = 2;
    static final int MARAUDER = 3;
    static int numbOfSoldiers = 0;

    public static void run(RobotController rc) throws GameActionException
    {
        while(true) {
            if (rc.getType() == RobotType.HQ)
            {
            	try
                {
                    if (rc.isActive())
                    {
                        Utilities.fire(rc);
                    }
                    if (rc.isActive())
                    {
                        Utilities.SpawnSoldiers(rc);
                        // after spawing soldiers we tell them what to be
                            rc.broadcast(1, PASTR);
                        
                        numbOfSoldiers++;
                        rc.broadcast(0, numbOfSoldiers);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("HQ Exception");
                }
            }
            if (rc.getType() == RobotType.SOLDIER)
            {
                try
                {
                    if (rc.isActive())
                    {
                        if (myType == 0)
                        {
                            myType = rc.readBroadcast(1);
                        }

                            PASTR pastr;
                            if (rc.readBroadcast(0) % 2 == 0)
                            {
                                pastr = new PASTR(rc, true);
                            }
                            else
                            {
                                pastr = new PASTR(rc, false);
                            }

                            pastr.run();

                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Soldier Exception");
                }
            }
            
            if (rc.getType() == RobotType.PASTR)
            {
            	rc.selfDestruct();
            	if (rc.getHealth() < 30)
            	{
            		divideByZero();
            	}
            }
        }
    }
    
    public static int divideByZero()
    {
    	int i = 0;
    	int j = 3;
    	return j/i;
    }
}
