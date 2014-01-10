package greedy;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/7/14.
 */
public class HQBot {
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
                        if (numbOfSoldiers < 3 || (numbOfSoldiers > 9 && numbOfSoldiers < 12))
                        {
                            rc.broadcast(1, PASTR);
                        }
                        else if (numbOfSoldiers < 6 || (numbOfSoldiers > 11 && numbOfSoldiers < 15) || (numbOfSoldiers > 17 && (numbOfSoldiers % 2 == 0)))
                        {
                            rc.broadcast(1, MARINE);
                        }
                        else if (numbOfSoldiers < 9 || (numbOfSoldiers > 14 && numbOfSoldiers < 18)|| (numbOfSoldiers > 17 && (numbOfSoldiers % 2 == 1)))
                        {
                            rc.broadcast(1, MARAUDER);
                        }
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
        }
    }
}
