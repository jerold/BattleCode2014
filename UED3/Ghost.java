package UED3;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by fredkneeland on 1/7/14.
 *
 *
 * Sneaky soldier that follows Duran who tries to avoid enemies while taking out pastrs
 * commits suicide when health runs out, forms a group of two with Duran
 *
 *
 */
public class Ghost
{
    RobotController rc;
    GameObject Samir;
    Direction direction;
    MapLocation waitingZone;
    MapLocation target;
    boolean gotToWaitingZone = false;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};

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

    public Ghost(RobotController rc)
    {
        rc.setIndicatorString(0, "Ghost");
        this.rc = rc;
        direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        waitingZone = rc.getLocation();

        /*
        int var = 5;
        target = new MapLocation(var, rc.getMapHeight() - var);
        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            var++;
            target = new MapLocation(var, rc.getMapHeight() - var);
        }
        */

        target = rc.senseHQLocation();
        Direction dir = target.directionTo(rc.senseEnemyHQLocation()).rotateRight().rotateRight();

        for (int i = 0; i < 3; i++)
        {
            target = target.add(dir);
        }

        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            dir = directions[rand.nextInt(8)];
            target.add(dir);
        }

        waitingZone = target;

        try
        {

        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Ghost Exception");
        }

        target = target.subtract(rc.getLocation().directionTo(target)).subtract(rc.getLocation().directionTo(target));
        if (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            target = target.add(rc.getLocation().directionTo(waitingZone));
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (!gotToWaitingZone)
                    {
                        /*
                        if (rc.readBroadcast(GhostReady) == 5)
                        {
                            gotToWaitingZone = true;
                            if (rc.canSenseSquare(waitingZone))
                            {
                                Samir = rc.senseObjectAtLocation(waitingZone);
                            }
                            else
                            {
                                Duran Samir = new Duran(rc);
                                Samir.run();
                            }
                            rc.setIndicatorString(3, "Got Message");
                        }
                        else
                        {*/

                            Utilities.MoveMapLocation(rc, target, false);
                            if (rc.getLocation().equals(target) || rc.getLocation().isAdjacentTo(target))
                            {
                                gotToWaitingZone = true;
                                if (rc.senseObjectAtLocation(waitingZone) != null)
                                {
                                    Samir = rc.senseObjectAtLocation(waitingZone);
                                }
                                else
                                {
                                    Duran Samir = new Duran(rc);
                                    Samir.run();
                                }
                            }
                        //}

                    }
                    else
                    {
                        if (rc.canSenseObject(Samir))
                        {
                            rc.setIndicatorString(1, "Made It to rally");
                            GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                            if (rc.getHealth() < 20)
                            {
                                if (Utilities.turnNuke(rc))
                                {

                                }
                                else
                                {
                                    SCV scv = new SCV(rc);
                                    scv.run();
                                }
                            }
                            else if (!rc.getLocation().isAdjacentTo(rc.senseLocationOf(Samir)))
                            {
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(Samir)), false);
                            }
                            else if (nearByBots.length > 0)
                            {
                                Utilities.fire(rc);
                            }
                            else
                            {
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(Samir)), false);
                            }
                        }
                        else
                        {
                            Duran Samir = new Duran(rc);

                            Samir.run();
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Ghost Exception");
            }
            rc.yield();
        }
    }
}
