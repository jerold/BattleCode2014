package greedy;

import java.util.Random;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/7/14.
 */
public class RobotPlayer
{
    static int myType = 0;
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
                    HQBot.run(rc);
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
                        if (myType == PASTR)
                        {
                            PASTR pastr;
                            if (rc.readBroadcast(0) < 5)
                            {
                                pastr = new PASTR(rc, true);
                            }
                            else
                            {
                                pastr = new PASTR(rc, false);
                            }

                            pastr.run();
                        }
                        // trooops to protect our pastrs
                        else if (myType == MARINE)
                        {
                            Marine marine = new Marine(rc);
                            marine.run();

                        }
                        //troops to kill enemy pastrs
                        else
                        {
                            Marauder marauder = new Marauder(rc);
                            marauder.run();
                        }
                    }
                } catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("Soldier Exception");
                }
            }
        }
    }
}
