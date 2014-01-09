package UED;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/7/14.
 *
 * Vice Admiral Stukov leads at the battlefield spawning troops at the HQ and giving units orders
 * alternates between goliaths which have 5 soldiers and ghost groups which increase in size over time
 *
 */
public class AlexeiStukov {
    static int numbOfSoldiers = 0;
    static final int GOLIATH = 1;
    static final int GHOST = 2;
    static final int DURAN = 3;
    static int ghostSendOuts = 2;
    static final int GOLIATH_SIZE = 5;
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

                        // after spawing soldiers we tell them what to be
                        if (numbOfSoldiers == 0)
                        {
                            rc.broadcast(1, DURAN);
                            //ghostSendOuts++;
                            // for now we broadcast 0 other soldiers going with Duran
                            rc.broadcast(2, ghostSendOuts);
                        }
                        else if (numbOfSoldiers  < (ghostSendOuts))
                        {
                            rc.broadcast(1, GHOST);
                            // reset Goliath squad
                            rc.broadcast(3, 0);
                        }
                        else
                        {
                            rc.broadcast(1, GOLIATH);
                        }
                        Utilities.SpawnSoldiers(rc);
                        numbOfSoldiers++;
                        if ((numbOfSoldiers - ghostSendOuts) >= GOLIATH_SIZE)
                        {
                        	numbOfSoldiers = 0;
                        }
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
