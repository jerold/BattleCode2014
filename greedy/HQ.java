package greedy;

import battlecode.common.RobotController;
import battlecode.common.RobotType;
import firstTest.*;

/**
 * Created by fredkneeland on 1/7/14.
 */
public class HQ
{
    static int numbOfSoldiers = 0;
    static final int PASTR = 1;
    static final int MARINE = 2;
    static final int MARAUDER = 3;
    public static void run(RobotController rc)
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
                        if (numbOfSoldiers < 7)
                        {
                            rc.broadcast(1, PASTR);
                        }
                        else if (numbOfSoldiers < 13)
                        {
                            rc.broadcast(1, MARINE);
                        }
                        else
                        {
                            rc.broadcast(1, MARAUDER);
                        }
                        numbOfSoldiers++;
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("HQ Exception");
                }
            }
        }
    }
}
