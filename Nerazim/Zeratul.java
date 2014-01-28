package Nerazim;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import theSwarm.*;

/**
 * Created by fredkneeland on 1/26/14.
 */
public class Zeratul {
    static int numbOfSoldiers = 0;
    static int numbOfSoldiers2 = 3;
    static final int ZEALOT = 1;
    static final int PROBE = 2;
    static final int DARKTEMPLAR = 3;
    static final int STALKER = 4;
    static int roundCount = 0;

    // channels
    static final int type = 0;
    static final int pastr1 = 1;
    static final int pastr2 = 2;
    static final int pastr3 = 3;
    static final int pastr1Count = 4;
    static final int pastr2Count = 5;


    public static void run(RobotController rc)
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (HQFunctions.HQShoot(rc))
                    {
                    }
                    else
                    {
                        MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());
                        if (numbOfSoldiers > 5 && rc.readBroadcast(pastr1Count) == 0 || rc.readBroadcast(pastr2Count) == 0)
                        {
                            numbOfSoldiers2 = 0;
                            roundCount = Clock.getRoundNum();
                        }

                        else if (numbOfSoldiers > 5 && ourPastrs.length < 2 && (Clock.getRoundNum() - roundCount > 200))
                        {
                            numbOfSoldiers2 = 0;
                            roundCount = Clock.getRoundNum();

                            if (ourPastrs.length > 0)
                            {
                                if (Movement.convertIntToMapLocation(rc.readBroadcast(pastr1)).equals(ourPastrs[0]))
                                {
                                    rc.broadcast(pastr2Count, 0);
                                }
                                else
                                {
                                    rc.broadcast(pastr1Count, 0);
                                }
                            }
                            else
                            {
                                rc.broadcast(pastr1Count, 0);
                                rc.broadcast(pastr2Count, 0);
                            }

                        }
                        HQFunctions.SpawnSoldiers(rc);

                        if (true)
                        {
                            rc.broadcast(type, DARKTEMPLAR);
                        }
                        else if (numbOfSoldiers < 6)
                        {
                            if (numbOfSoldiers % 3 == 2)
                            {
                                rc.broadcast(type, STALKER);
                            }
                            else
                            {
                                rc.broadcast(type, PROBE);
                            }
                            roundCount = Clock.getRoundNum();
                        }
                        else if (numbOfSoldiers2 < 2)
                        {
                            if (numbOfSoldiers2 % 3 == 2)
                            {
                                rc.broadcast(type, STALKER);
                            }
                            else
                            {
                                rc.broadcast(type, PROBE);
                            }
                            numbOfSoldiers2++;
                        }
                        else
                        {
                            if (numbOfSoldiers % 2 ==0)
                            {
                                rc.broadcast(type, ZEALOT);
                            }
                            else
                            {
                                rc.broadcast(type, DARKTEMPLAR);
                            }
                        }
                        numbOfSoldiers++;
                    }

                }
            } catch (Exception e) {}
        }
    }
}
