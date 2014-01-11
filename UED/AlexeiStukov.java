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
    static final int BATTLECRUISER = 10;
    static int ghostSendOuts = 2;
    static final int GOLIATH_SIZE = 5;
    static boolean putUpDistractor = false;
    static boolean putUpMule = false;
    static boolean putUpSensorTower = false;
    static boolean firstRound = true;
    static boolean thorUp = false;
    RobotController rc;
    long[] teamMemory;
    MapLocation[] enemyPastrs;

    // channels for communication
    static final int EnemyHQChannel = 0;
    static final int OurHQChannel = 1;
    static final int TroopType = 2;
    static final int GhostNumb = 3;
    static final int GoliathOnline = 4;
    static final int GhostReady = 5;
    static final int BattleCruiserLoc = 6;
    static final int BattleCruiserLoc2 = 7;
    static final int BattleCruiserArrived = 8;
    static final int startBattleCruiserArray = 9;
    static final int endBattleCruiserArray = 59;
    static final int BattleCruiserInArray = 60;

    // this is arrays of our troop combinations
    static final int[] initialSpawn = {THOR, THOR, SUPPLY_DEPOT, THOR, THOR, THOR};



    public AlexeiStukov (RobotController rc)
    {
        this.rc = rc;
        teamMemory = rc.getTeamMemory();
    }


    public void run()
    {
        while(true) {
            if (rc.getType() == RobotType.HQ)
            {
                try
                {
                    rc.setIndicatorString(0, ""+rc.getActionDelay());
                    rc.setIndicatorString(1, ""+rc.isActive());

                    if (Clock.getRoundNum() > 350 && Clock.getRoundNum() % 100 == 0)
                    {
                        enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                    }

                    GameObject[] FarAwayEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                    GameObject[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                    if (nearByEnemies.length > 0)
                    {
                        Utilities.fire(rc);
                        //nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam().opponent());
                    }
                    if (rc.isActive())
                    {

                        if (numbOfSoldiers < initialSpawn.length)
                        {
                            rc.broadcast(TroopType, initialSpawn[numbOfSoldiers]);
                        }
                        // here we saved information about what our opponent does
                        else if (teamMemory.length > 0)
                        {

                        }
                        else if (numbOfSoldiers < initialSpawn.length + 5)
                        {
                            rc.broadcast(TroopType, GOLIATH);
                        }
                        // we have done our initial set up so now we will determine our strategy
                        else
                        {
                            // if our enemies are swarming our hq then we need to build a battlecruiser to kill them
                            if (FarAwayEnemies.length > 2)
                            {

                            }
                        }

                        Utilities.SpawnSoldiers(rc);
                        numbOfSoldiers++;

                        // after spawing soldiers we tell them what to be
                        /*

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
                        /*
                        if (numbOfSoldiers % 8 == 0 && numbOfSoldiers > 0)
                        {
                            rc.broadcast(BattleCruiserArrived, 1);
                        }
                        else if (numbOfSoldiers % 8 == 1)
                        {
                            rc.broadcast(BattleCruiserArrived, 0);
                        }

                        rc.broadcast(TroopType, BATTLECRUISER);
                        numbOfSoldiers++;


                        Utilities.SpawnSoldiers(rc);
                        */



                        /*
                        if (!thorUp)
                        {
                            if (numbOfSoldiers < 5)
                            {
                                numbOfSoldiers++;
                                Utilities.SpawnSoldiers(rc);
                                rc.broadcast(TroopType, THOR);
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
                            rc.broadcast(TroopType, DURAN);
                            //ghostSendOuts++;
                            // for now we broadcast 0 other soldiers going with Duran
                            //rc.broadcast(2, ghostSendOuts);
                            //rc.broadcast(4, 0);
                            numbOfSoldiers++;
                        }
                        else if (numbOfSoldiers <= ghostSendOuts)
                        {
                            Utilities.SpawnSoldiers(rc);
                            rc.broadcast(TroopType, GHOST);
                            // reset Goliath squad
                            //rc.broadcast(3, 0);
                            numbOfSoldiers++;
                        }
                        else
                        {
                            Utilities.SpawnSoldiers(rc);
                            rc.broadcast(TroopType, GOLIATH);
                            numbOfSoldiers++;
                        }

                        if (((numbOfSoldiers - ghostSendOuts)) > GOLIATH_SIZE)
                        {
                            //rc.broadcast(3, 5);
                            numbOfSoldiers = 1;
                        }
                        */
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
