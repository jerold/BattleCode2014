package team104;

import java.util.Random;

import battlecode.common.*;


/**
 * Created by fredkneeland on 1/7/14.
 *
 *
 * Leader who avoids enemy units whenever possible in an attempt to get to enemy pastr will turn into nuke if health declines
 * is followed by ghosts every rendition of Duran gets additional ghost as enemy defense forces are projected to grow over time
 */
public class Duran
{
    RobotController rc;
    MapLocation waitingZone;
    Direction direction;
    boolean arrived = false;
    boolean supportTeamUp = false;
    int numbOfGhosts = 0;
    MapLocation[] enemyPastrs;
    MapLocation target;
    MapLocation frontOfEnemy;
    MapLocation firstEnemyPastr;
    int arrivedTime = 0;
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

    public Duran(RobotController rc)
    {
        rc.setIndicatorString(0, "Duran");
        this.rc = rc;
        direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        waitingZone = rc.getLocation();
        try
        {
            numbOfGhosts = 1; //rc.readBroadcast(2);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Duran Exception");
        }

        int var = 5;
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

        frontOfEnemy = rc.senseEnemyHQLocation().subtract(rc.getLocation().directionTo(rc.senseHQLocation())).subtract(rc.getLocation().directionTo(rc.senseHQLocation())).subtract(rc.getLocation().directionTo(rc.senseHQLocation())).subtract(rc.getLocation().directionTo(rc.senseHQLocation()));
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    // first we need to get to starting location
                    if (!arrived)
                    {
                        if (rc.getLocation().equals(waitingZone))
                        {
                            arrived = true;
                            arrivedTime = Clock.getRoundNum();
                        }
                        else
                        {
                            Utilities.MoveMapLocation(rc, (waitingZone), false);
                        }
                    }
                    else
                    {
                        // now we need to wait till all supporting ghosts arrive
                        if (!supportTeamUp)
                        {
                            if (numbOfGhosts <= rc.senseNearbyGameObjects(Robot.class, 2, rc.getTeam()).length || (arrivedTime < (Clock.getRoundNum() - 100*numbOfGhosts)))
                            {
                                supportTeamUp = true;
                                rc.broadcast(GhostReady, 5);
                                for (int i = 0; i < 5; i++)
                                {
                                    rc.yield();
                                }

                            }
                        }
                        else
                        {
                            // now it is time to move forward trying to kill enemy pastrs
                            enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                            // if we don't see any pastrs then we will advance toward the enemy HQ till we start getting close
                            if (enemyPastrs.length == 0)
                            {
                                GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                                if (nearByBots.length > 0 && rc.senseRobotInfo((Robot) nearByBots[0]).health < 201)
                                {
                                    Utilities.fire(rc);
                                }

                                MapLocation[] ourPastrs =  rc.sensePastrLocations(rc.getTeam());

                                if (ourPastrs.length > 0)
                                {
                                    Utilities.MoveDirection(rc, rc.getLocation().directionTo(ourPastrs[0]), false);
                                }

                                /*
                                int var = 5;
                                target = new MapLocation(var, rc.getMapHeight() - var);
                                while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
                                {
                                    var++;
                                    target = new MapLocation(var, rc.getMapHeight() - var);
                                }
                                while (enemyPastrs.length == 0)
                                {
                                    enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                                    //Utilities.avoidEnemiesMove(rc, target);
                                    Utilities.MoveMapLocation(rc, target, false);
                                }
                                */
                                //Utilities.MoveMapLocation(rc, target, false);
                            }
                            // if we see enemyPastrs then lets head towards the nearest one
                            else
                            {
                                int enemyPastrsIndex = 0;
                                target = enemyPastrs[0];
                                for (int i = 1; i < enemyPastrs.length; i++)
                                {
                                    if (rc.getLocation().distanceSquaredTo(enemyPastrs[i]) < rc.getLocation().distanceSquaredTo(target))
                                    {
                                        target = enemyPastrs[i];
                                        enemyPastrsIndex = i;
                                    }
                                }
                                //target = enemyPastrs[0].subtract(rc.getLocation().directionTo(enemyPastrs[0])).subtract(rc.getLocation().directionTo(enemyPastrs[0]));
                                //target = target.subtract((rc.getLocation().directionTo(target))).subtract((rc.getLocation().directionTo(target)));
                                /*while (rc.getLocation().distanceSquaredTo(target) > 35)
                                {
                                    Utilities.avoidEnemiesMove(rc, target);
                                }*/



                                Utilities.MoveMapLocation(rc, target, false);

                                firstEnemyPastr = enemyPastrs[enemyPastrsIndex];

                                while (firstEnemyPastr.equals(enemyPastrs[enemyPastrsIndex]))
                                {
                                    enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                                    try
                                    {
                                        GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());


                                        if (nearByBots.length > 0  && rc.senseRobotInfo((Robot) nearByBots[0]).health < 201)
                                        {
                                            Utilities.fire(rc);
                                        }
                                        else
                                        {
                                            nearByBots = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                                            if (nearByBots.length > 1)
                                            {
                                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByBots[0])), false);
                                            }
                                            else
                                            {
                                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(firstEnemyPastr), false);
                                            }
                                        }
                                    } catch (Exception e)
                                    {
                                        e.printStackTrace();
                                        System.out.println("Duran Exception");
                                    }
                                    rc.yield();
                                }

                                /*
                                GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                                while (nearByBots.length > 0)
                                {
                                    if (rc.getLocation().distanceSquaredTo(rc.senseLocationOf(nearByBots[0])) > 10)
                                    {
                                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(rc.senseLocationOf(nearByBots[0])), false);
                                    }
                                    else
                                    {
                                        Utilities.fire(rc);
                                    }
                                    nearByBots = rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam().opponent());

                                }
                                */
                            }
                        }
                    }
                }
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Duran Exception");
            }
            rc.yield();
        }
    }
}
