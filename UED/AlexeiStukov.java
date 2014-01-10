package UED;

import battlecode.common.*;

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
    static final int SUPPLY_DEPOT = 4;
    static final int MULECALLDOWN = 5;
    static final int SENSORTOWER = 6;
    static final int MARINES = 7;
    static final int HELLION = 8;
    static final int THOR = 9;
    static int ghostSendOuts = 2;
    static final int GOLIATH_SIZE = 5;
    static boolean putUpDistractor = false;
    static boolean putUpMule = false;
    static boolean putUpSensorTower = false;
    static boolean firstRound = true;
    static boolean thorUp = false;

    public static void run(RobotController rc)
    {
        while(true) {
            if (rc.getType() == RobotType.HQ)
            {
                try
                {
                    rc.setIndicatorString(0, ""+rc.getActionDelay());
                    rc.setIndicatorString(1, ""+rc.isActive());

                    GameObject[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                    if (nearByEnemies.length > 0)
                    {
                        Utilities.fire(rc);
                        //nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam().opponent());
                    }
                    if (rc.isActive())
                    {
                        /*
                        // after spawing soldiers we tell them what to be

                        if (numbOfSoldiers == 0)
                        {
                            rc.broadcast(1, DURAN);
                            //ghostSendOuts++;
                            // for now we broadcast 0 other soldiers going with Duran
                            rc.broadcast(2, ghostSendOuts);
                            rc.broadcast(4, 0);
                        }
                        else if (numbOfSoldiers  <= (ghostSendOuts))
                        {
                            rc.broadcast(1, GHOST);
                            // reset Goliath squad
                            rc.broadcast(3, 0);
                        }
                        // we output 2 goliaths to  keep base from being overwhelmed
                        else if (numbOfSoldiers == ghostSendOuts+2)
                        {
                            rc.broadcast(1, GOLIATH);
                        }
                        // now we will output our main milk source
                        else if (numbOfSoldiers == ghostSendOuts+3 && !putUpDistractor)
                        {
                            rc.broadcast(1, SUPPLY_DEPOT);
                            rc.spawn(rc.getLocation().directionTo(rc.senseEnemyHQLocation()).opposite());
                        }
                        else if (numbOfSoldiers == ghostSendOuts+4 && !putUpMule)
                        {
                            rc.broadcast(1, MULECALLDOWN);
                        }
                        else if (numbOfSoldiers == ghostSendOuts+5 && !putUpSensorTower)
                        {
                            rc.broadcast(1, SENSORTOWER);
                        }
                        else if (numbOfSoldiers < ghostSendOuts+7)
                        {
                            rc.broadcast(1, MARINES);
                        }
                        else
                        {
                            rc.broadcast(1, GOLIATH);
                            putUpDistractor = true;
                            putUpSensorTower = true;
                            putUpMule = true;
                        }
                        if ((numbOfSoldiers != ghostSendOuts+1) || putUpDistractor)
                        {
                            Utilities.SpawnSoldiers(rc);
                        }
                        numbOfSoldiers++;
                        if (firstRound)
                        {
                            if (((numbOfSoldiers - ghostSendOuts) - 5) > GOLIATH_SIZE)
                            {
                                rc.broadcast(3, 5);
                                numbOfSoldiers = 1;
                            }
                        }
                        else
                        {
                            if (((numbOfSoldiers - ghostSendOuts) - 2) > GOLIATH_SIZE)
                            {
                                rc.broadcast(3, 5);
                                numbOfSoldiers = 1;
                            }
                        }
                        */
                        //rc.broadcast(1, HELLION);
                        //Utilities.SpawnSoldiers(rc);


                        if (!thorUp)
                        {
                            if (numbOfSoldiers < 5)
                            {
                                numbOfSoldiers++;
                                Utilities.SpawnSoldiers(rc);
                                rc.broadcast(1, THOR);
                            }
                            else
                            {
                                thorUp = true;
                                numbOfSoldiers = 3;
                            }
                        }
                        else if (numbOfSoldiers == 0)
                        {
                            Utilities.SpawnSoldiers(rc);
                            rc.broadcast(1, DURAN);
                            //ghostSendOuts++;
                            // for now we broadcast 0 other soldiers going with Duran
                            rc.broadcast(2, ghostSendOuts);
                            rc.broadcast(4, 0);
                            numbOfSoldiers++;
                        }
                        else if (numbOfSoldiers <= ghostSendOuts)
                        {
                            Utilities.SpawnSoldiers(rc);
                            rc.broadcast(1, GHOST);
                            // reset Goliath squad
                            rc.broadcast(3, 0);
                            numbOfSoldiers++;
                        }
                        else
                        {
                            Utilities.SpawnSoldiers(rc);
                            rc.broadcast(1, GOLIATH);
                            numbOfSoldiers++;
                        }

                        if (((numbOfSoldiers - ghostSendOuts)) > GOLIATH_SIZE)
                        {
                            rc.broadcast(3, 5);
                            numbOfSoldiers = 1;
                        }


                    }


                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println("HQ Exception");
                }
                rc.yield();
            }
        }
    }
}
