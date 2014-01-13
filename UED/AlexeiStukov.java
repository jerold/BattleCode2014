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
    static final int CENTERMULE = 15;
    static final int CENTERTOWER = 16;
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
    int strategy = 0;
    int strategy2 = 0;
    int startingGoliathSize = 0;
    int startingBattleCruiserSize = 0;
    boolean smallMap = false;


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
    static final int RushEnemyHQ = 11;
    static final int RushEnemyPastrs = 12;
    static final int GoliathConvertToThors = 13;
    static final int GoliathNumber = 14;
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
    static final int[] AllOutPastrRushArray = {MARAUDER, DURAN};
    static final int[] Balanced1PastrDefendArray = {GOLIATH, GOLIATH, THOR, GOLIATH, GOLIATH};
    static final int[] Balanced2PastrDefendArray = {GOLIATH, THOR, GOLIATH, THOR2, GOLIATH, GOLIATH};
    static final int[] TimingPushArray = {GOLIATH, GOLIATH, GOLIATH, GOLIATH, GOLIATH, HELLION, THOR};
    static final int[] PastrDefendArray = {THOR, GOLIATH, THOR, GOLIATH};
    static final int[] SmallMapArray = {BATTLECRUISER, BATTLECRUISER, BATTLECRUISER, BATTLECRUISER, BATTLECRUISER, BATTLECRUISER, CENTERTOWER, CENTERMULE};

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
    static final int PastrDefend = 10;



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

        startingBattleCruiserSize = Utilities.BattleCruiserSize(rc);
        startingGoliathSize = Utilities.GoliathSquadSize(rc);

        // if we are on a small map and their is enough gap in between our hqs then we will set up in the middle
        if (Utilities.MapSize(rc) == 1 && (rc.getLocation().distanceSquaredTo(rc.senseEnemyHQLocation()) > 150))
        {
            smallMap = true;
            Direction dir3 = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
            BattleCruiserTarget = center.add(dir3).add(dir3);
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

                    /*if (Clock.getRoundNum() > 350 && Clock.getRoundNum() % 100 == 0)
                    {
                        enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                        ourPastrs = rc.sensePastrLocations(rc.getTeam());
                    }*/

                    enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                    ourPastrs = rc.sensePastrLocations(rc.getTeam());



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

                        rc.setIndicatorString(1, "Hello World");

                        strategy2 = strategy;

                        /**
                         * Here we will determine our current strategy against our opponent
                         *
                         * note: This strategy will always take place after we have put out our first 6 units which will be our standard start
                         *
                         */
                        // in this case our opponent is probably beating us so we must take down their pastrs and quickly
                        if (opponentMilk > 7500000 && Clock.getRoundNum() < 750)
                        {
                            // if we aren't almost to the win quantity then we need to respond
                            if (ourMilk < 90000000 || ourPastrs.length == 0)
                            {
                                // in this case our enemy has built their pastrs by their hq so we rush it
                                if (Utilities.MapLocationsNextToEnemyHQ(rc, enemyPastrs))
                                {
                                    rc.broadcast(RushEnemyHQ, 1);
                                    strategy = Kamikaze;
                                }
                                else
                                {
                                    rc.broadcast(RushEnemyPastrs, 1);
                                    strategy = AllOutPastrRush;
                                }
                            }
                            // we can keep doing what we are
                            else
                            {
                                // in this case we have one pastr to defend
                                if (ourPastrs.length < 3)
                                {
                                    strategy = Balanced1PastrDefend;
                                }
                                else
                                {
                                    strategy = Balanced2PastrDefend;
                                }
                            }
                        }
                        // if the enemy has more milk than us and only has one pastr then they are probably going with a strategy where they defend one pastr
                        else if (opponentMilk > ourMilk && enemyPastrs.length == 1)
                        {
                            // if we haven't gotten both corners set up then
                            if (ourPastrs.length < 3)
                            {
                                strategy = SecondCorner;
                            }
                            else
                            {
                                strategy = AllInMilk;
                            }
                        }
                        // if our enemy is swarming our base then we need to do a break out
                        else if (FarAwayEnemies.length > 2 || (strategy == BlockadeRunner && rc.readBroadcast(BattleCruiserArrived) == 0))
                        {
                            strategy = BlockadeRunner;
                        }
                        // if our enemy has only a few pastrs then we should do a timing push
                        else if (enemyPastrs.length < 3 && enemyPastrs.length > 0)
                        {
                            strategy = TimingPush;
                        }
                        // if our enemey has lots of pastrs we should send out small groups to go around killing them
                        else if (enemyPastrs.length > 2)
                        {
                            strategy = SmallSquadPastrAttack;
                        }
                        // in this case our opponent has no pastrs up
                        else
                        {
                            if (opponentMilk > ourMilk || ourMilk < 2500000)
                            {
                                strategy = TimingPush;
                                rc.broadcast(GoliathConvertToThors, 1);
                            }
                            else if (ourPastrs.length > 1)
                            {
                                strategy = PastrDefend;
                            }
                            else
                            {
                                strategy = BlockadeRunner;
                            }

                        }

                        if (strategy == strategy2) {}
                        else
                        {
                            numbOfSoldiers2 = 0;
                        }



                        /*
                        if (smallMap)
                        {
                            rc.broadcast(TroopType, SmallMapArray[(numbOfSoldiers % SmallMapArray.length)]);
                            //rc.broadcast(TroopType, BATTLECRUISER);
                        }
                        else if (numbOfSoldiers < initialSpawnArray.length)
                        {
                            rc.broadcast(TroopType, initialSpawnArray[numbOfSoldiers]);
                        }
                        else
                        {
                            if (strategy == BlockadeRunner)
                            {
                                rc.broadcast(TroopType, BlockadeRunnerArray[(numbOfSoldiers2%BlockadeRunnerArray.length)]);
                            }
                            else if (strategy == Kamikaze)
                            {
                                rc.broadcast(TroopType, KamikazeArray[(numbOfSoldiers2%KamikazeArray.length)]);
                            }
                            else if (strategy == AllInMilk)
                            {
                                rc.broadcast(TroopType, AllInMilkArray[(numbOfSoldiers2%AllInMilkArray.length)]);
                            }
                            else if (strategy == SmallSquadPastrAttack)
                            {
                                rc.broadcast(TroopType, SmallSquadPastrAttackArray[(numbOfSoldiers2%SmallSquadPastrAttackArray.length)]);
                            }
                            else if (strategy == SecondCorner)
                            {
                                rc.broadcast(TroopType, SecondCornerArray[(numbOfSoldiers2%SecondCornerArray.length)]);
                            }
                            else if (strategy == AllOutPastrRush)
                            {
                                rc.broadcast(TroopType, AllOutPastrRushArray[(numbOfSoldiers2%AllOutPastrRushArray.length)]);
                            }
                            else if (strategy == Balanced1PastrDefend)
                            {
                                rc.broadcast(TroopType, Balanced1PastrDefendArray[(numbOfSoldiers2%Balanced1PastrDefendArray.length)]);
                            }
                            else if (strategy == Balanced2PastrDefend)
                            {
                                rc.broadcast(TroopType, Balanced2PastrDefendArray[(numbOfSoldiers2%Balanced2PastrDefendArray.length)]);
                            }
                            else if (strategy == TimingPush)
                            {
                                rc.broadcast(TroopType, TimingPushArray[(numbOfSoldiers2%TimingPushArray.length)]);
                            }
                            else
                            {
                                rc.broadcast(TroopType, PastrDefendArray[(numbOfSoldiers2%PastrDefendArray.length)]);
                            }
                            numbOfSoldiers2++;
                        }
                        */
                        rc.broadcast(TroopType, HELLION2);

                        numbOfSoldiers++;
                        Utilities.SpawnSoldiers(rc);

                        // these tell our troops to move forward when they have collected
                        if (rc.readBroadcast(BattleCruiserNumber) >= startingBattleCruiserSize)
                        {
                            rc.broadcast(BattleCruiserArrived, 1);
                        }

                        if (rc.readBroadcast(GoliathNumber) >= startingGoliathSize)
                        {
                            rc.broadcast(GoliathOnline, 1);
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
