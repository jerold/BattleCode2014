package Nerazim;

import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/26/14.
 */
public class RobotPlayer {
    static int myType = 0;
    static final int ZEALOT = 1;
    static final int PROBE = 2;
    static final int DARKTEMPLAR = 3;
    static final int STALKER = 4;

    // channels
    static final int type = 0;


    public static void run(RobotController rc)
    {
        while (true)
        {
            try
            {
                if (rc.getType() == RobotType.HQ)
                {
                    Zeratul.run(rc);
                }
                else if (rc.getType() == RobotType.SOLDIER)
                {
                    if (true)
                    {
                        DarkTemplar darkTemplar = new DarkTemplar(rc);
                        darkTemplar.run();
                    }

                    if (myType == 0)
                    {
                        myType = rc.readBroadcast(type);
                    }

                    if (myType == ZEALOT)
                    {
                        Zealot zealot = new Zealot(rc);
                        zealot.run();
                    }
                    else if (myType == PROBE)
                    {
                        Probe probe = new Probe(rc);
                        probe.run();
                    }
                    else if (myType == DARKTEMPLAR)
                    {
                        DarkTemplar darkTemplar = new DarkTemplar(rc);
                        darkTemplar.run();
                    }
                    else if (myType == STALKER)
                    {
                        Stalker stalker = new Stalker(rc);
                        stalker.run();
                    }
                }
                else if (rc.getType() == RobotType.NOISETOWER)
                {
                    GenericTower genericTower = new GenericTower(rc, false);
                    genericTower.run();
                }
            } catch (Exception e) {}
            rc.yield();
        }
    }
}
