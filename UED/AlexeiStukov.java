package UED;

import java.util.Random;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/7/14.
 *
 * Vice Admiral Stukov leads at the battlefield spawning troops at the HQ and giving units orders
 * adapts to best counter enemy strategy
 *
 */
public class AlexeiStukov {
    int numbOfSoldiers = 0;
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
    static final int THOR2 = 11;
    static final int HELLION2 = 12;
    static final int SCV2 = 13;
    static final int MARAUDER = 14;
    static int ghostSendOuts = 2;
    static final int GOLIATH_SIZE = 5;
     boolean putUpDistractor = false;
    static boolean putUpMule = false;
    static boolean putUpSensorTower = false;
    static boolean firstRound = true;
    static boolean thorUp = false;
    RobotController rc;
    long[] teamMemory;
    MapLocation[] enemyPastrs;
    MapLocation[] ourPastrs;
    MapLocation GoliathPosition;
    MapLocation GoliathTarget;
    MapLocation center;
    MapLocation GoliathNextSpot;
    MapLocation BattleCruiserPosition;
    MapLocation BattleCruiserTarget;
    MapLocation BattleCruiserNextSpot;
    int a = 0;
    Direction[] directions = Direction.values();
    Random rand = new Random();
    int battleCruiserCount = 0;
    int numbOfSoldiers2 = 0;

    // channels for communication
    static final int EnemyHQChannel = 0;
    static final int OurHQChannel = 1;
    static final int TroopType = 2;
    static final int GhostNumb = 3;
    static final int GoliathOnline = 4;
    static final int GhostReady = 5;
    static final int BattleCruiserLoc = 6;
    static final int BattleCruiserNumber = 7;
    static final int BattleCruiserArrived = 8;
    static final int BattleCruiserReadyForNewCommand = 9;
    static final int startBattleCruiserArray = 10;
    static final int endBattleCruiserArray = 59;
    static final int BattleCruiserInArray = 60;
    static final int GoliathReadyForCommand = 61;
    static final int GoliathNextLocation = 62;
    static final int GoliathCurrentLocation = 63;
    static final int PastrStartChannel = 10000;


    // this is arrays of our troop combinations
    static final int[] initialSpawnArray = {THOR, THOR, SUPPLY_DEPOT, THOR, THOR, THOR};
    static final int[] BlockadeRunnerArray = {BATTLECRUISER};
    static final int[] KamikazeArray = {HELLION2, MARAUDER};
    static final int[] AllInMilkArray = {THOR, THOR2, SCV2};
    static final int[] SmallSquadPastrAttackArray = {DURAN, GHOST, DURAN, GHOST, MARAUDER, THOR, GOLIATH};
    static final int[] SecondCornerArray = {THOR2, THOR2, THOR2, THOR2, THOR2};
    static final int[] AllOutPastrRushArray = {GOLIATH, DURAN};
    static final int[] Balanced1PastrDefendArray = {GOLIATH, GOLIATH, THOR, GOLIATH, GOLIATH};
    static final int[] Balanced2PastrDefendArray = {GOLIATH, THOR, GOLIATH, THOR2, GOLIATH, GOLIATH};
    static final int[] TimingPushArray = {GOLIATH, GOLIATH, GOLIATH, GOLIATH, GOLIATH, HELLION, THOR};
    static final int[] ImmediatePastrTakeDownArray = {MARAUDER, GOLIATH};

    // these are our strategy possiblities
    static final int BlockadeRunner = 1;
    static final int Kamikaze = 2;
    static final int AllInMilk = 3;
    static final int SmallSquadPastrAttack = 4;
    static final int SecondCorner = 5;
    static final int AllOutPastrRush = 6;
    static final int Balanced1PastrDefend = 7;
    static final int Balanced2PastrDefend = 8;
    static final int TimingPush = 9;
    static final int ImmediatePastrTakeDown = 10;



    public AlexeiStukov (RobotController rc)
    {
        this.rc = rc;
        teamMemory = rc.getTeamMemory();
        center = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        while (rc.senseTerrainTile(center).equals(TerrainTile.VOID))
        {
            center = center.subtract(rc.getLocation().directionTo(center));
        }

        BattleCruiserTarget = rc.senseEnemyHQLocation();
        Direction dir = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        for (int i = 0; i < 7; i++)
        {
            BattleCruiserTarget = BattleCruiserTarget.subtract(dir);
        }

        while (rc.senseTerrainTile(BattleCruiserTarget).equals(TerrainTile.VOID))
        {
            dir = directions[rand.nextInt(8)];
            BattleCruiserTarget = BattleCruiserTarget.add(dir);
        }
        //GoliathTarget = center;
    }


    public void run()
    {
        while(true)
        {
            if (rc.getType() == RobotType.HQ)
            {
                try
                {
                    //rc.setIndicatorString(0, ""+rc.getActionDelay());
                    //rc.setIndicatorString(1, ""+rc.isActive());

                    if (Clock.getRoundNum() > 350 && Clock.getRoundNum() % 100 == 0)
                    {
                        enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                        ourPastrs = rc.sensePastrLocations(rc.getTeam());
                    }



                    // give BattleCruiser next location
                    Utilities.GiveBattleCruisersNextLocation(rc, BattleCruiserTarget);

                    // if the goliaths want a new command then we give them a new location
                    Utilities.GiveGoliathsNextLocation(rc, center);

                    GameObject[] FarAwayEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                    GameObject[] nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 24, rc.getTeam().opponent());
                    if (nearByEnemies.length > 0)
                    {
                        Utilities.fire(rc);
                        //nearByEnemies = rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam().opponent());
                    }
                    if (rc.isActive())
                    {
                        // here we determine what we should do
                        double opponentMilk = rc.senseTeamMilkQuantity(rc.getTeam().opponent());
                        double ourMilk = rc.senseTeamMilkQuantity(rc.getTeam());
                        // if our opponent has almost gotten all their milk and we haven't

                        rc.setIndicatorString(1, ""+opponentMilk);
                        rc.setIndicatorString(2, ""+ourMilk);



                        //if (opponentMilk > 7500000 &&)
                        // added condition to test our battle cruiser
                        /*
                        if (battleCruiserCount < 9)
                        {
                            rc.broadcast(TroopType, BATTLECRUISER);
                            battleCruiserCount++;
                        }
                        // there is a certain sequence which we will always do
                        else */if (numbOfSoldiers < initialSpawnArray.length)
                        {
                            rc.broadcast(TroopType, initialSpawnArray[numbOfSoldiers]);
                        }
                        // here we saved information about what our opponent does
                        else if (teamMemory[0] == 1)
                        {
                        }

                        // here we build our first Goliath task force which we will swell in size as time goes on
                        else if (numbOfSoldiers < (initialSpawnArray.length + 5))
                        {
                            rc.broadcast(TroopType, GOLIATH);
                        }
                        // we have done our initial set up so now we will determine our strategy
                        else
                        {
                            // if our enemies are swarming our hq then we need to build a battlecruiser to kill them
                            if (FarAwayEnemies.length > 2 && battleCruiserCount < 9)
                            {
                                rc.broadcast(TroopType, BATTLECRUISER);
                                battleCruiserCount++;
                            }
                            else if (enemyPastrs.length == 0)
                            {
                                // we will alternate between defending our pastr and gathering an army
                                if (numbOfSoldiers % 3 == 0)
                                {
                                    rc.broadcast(TroopType, THOR);
                                }
                                else
                                {
                                    rc.broadcast(TroopType, GOLIATH);
                                }
                            }
                            else if (enemyPastrs.length < 3)
                            {
                                // in this case our opponent has built their pastrs close to their
                                if (Utilities.MapLocationsNextToEnemyHQ(rc, enemyPastrs))
                                {
                                    // eventually we should set up a noise tower to keep cows away from their pastr

                                }
                                // if our pastr is under attack then we send out goliaths to take out theirs
                                else if (Utilities.PastrUnderAttack(rc))
                                {

                                }
                                // other wise we should set up more pastr and out milk the enemy
                                else
                                {

                                }
                            }
                            // our enemy has built tons of pastr which we must destroy
                            else
                            {
                                int numbOfPastrs = rc.readBroadcast(PastrStartChannel);
                                int index = -1;
                                if (numbOfSoldiers % 6 == 0)
                                {
                                    /*
                                    if (numbOfPastrs > 0)
                                    {
                                        for (int j = 0; j < numbOfPastrs; j++)
                                        {
                                            int numbOfDefenders = rc.readBroadcast((PastrStartChannel + (j*5) + 2));
                                            if (numbOfDefenders > 0 && numbOfDefenders < 5)
                                            {
                                                rc.broadcast(TroopType, THOR);
                                                index = j;
                                            }
                                        }
                                        if (index == -1)
                                        {
                                            rc.broadcast(TroopType, THOR);
                                        }
                                    }
                                    */
                                    rc.broadcast(TroopType, THOR);
                                }
                                else
                                {
                                    rc.broadcast(TroopType, GOLIATH);
                                }
                            }
                            if (numbOfSoldiers % 6 == 1 || numbOfSoldiers == 11)
                            {
                                rc.broadcast(GoliathOnline, 1);
                            }
                        }
                        Utilities.SpawnSoldiers(rc);
                        numbOfSoldiers++;

                        if (battleCruiserCount > 8)
                        {
                            rc.broadcast(BattleCruiserArrived, 1);
                        }

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
