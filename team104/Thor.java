package team104;

import battlecode.common.*;
import greedy.Marine;

import java.util.Random;

/**
 * Created by fredkneeland on 1/10/14.
 *
 * This bot gets in a group of 5 right at the begining and then sets out to set up
 * the pastr sound tower combination
 *
 */
public class Thor
{
    RobotController rc;
    MapLocation waitingZone;
    Direction direction;
    MapLocation ourHQ;
    MapLocation enemyHQ;
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    boolean timeToGo = false;
    MapLocation target;
    MapLocation target2;
    Direction direction2;
    final int numbOfSoldiersBeforeThor = 0;
    MapLocation target3;
    MapLocation target4;
    int[] lookPlaces = {1,1,0,3,6,7,4,5,2,3,0,2,2,3,1,4,5,3,2,5,61,5,4,3,2,2,2,3,2,2,3};
    int counter = 0;
    boolean sensorOver = false;
    boolean firstPastr = false;
    boolean corner1;

    public Thor(RobotController rc, boolean corner1)
    {
        this.rc = rc;
        this.corner1 = corner1;
        ourHQ = rc.senseHQLocation();
        enemyHQ = rc.senseEnemyHQLocation();
        direction = ourHQ.directionTo(enemyHQ);
        waitingZone = ourHQ;
        target = Utilities.spotOfSensorTower(rc, corner1);
        target2 = target;

        direction2 = ourHQ.directionTo(target);
        target = target.subtract(direction2).subtract(direction2);

        while (rc.senseTerrainTile(target).equals(TerrainTile.OFF_MAP) || rc.senseTerrainTile(target).equals(TerrainTile.VOID) || target.equals(target2))
        {
            direction2 = directions[lookPlaces[counter]];
            target = target.add(direction2);
            counter++;
        }

        target3 = Utilities.spotOfPastr(rc, corner1);
        target4 = target3;
        direction2 = ourHQ.directionTo(target3);
        target3 = target3.subtract(direction2);
        counter = 0;



        while (rc.senseTerrainTile(target3).equals(TerrainTile.OFF_MAP) || rc.senseTerrainTile(target3).equals(TerrainTile.VOID) || target3.equals(target4))
        {
            direction2 = directions[rand.nextInt(8)];
            counter++;
            counter = counter % lookPlaces.length;
            target3 = target3.add(direction2);
        }

        for (int i = 0; i < 4; i++)
        {
            waitingZone = waitingZone.subtract(direction);
        }

        if (rc.senseTerrainTile(waitingZone).equals(TerrainTile.OFF_MAP) || rc.senseTerrainTile(waitingZone).equals(TerrainTile.VOID))
        {
            direction = direction.rotateLeft().rotateLeft();
            for (int i = 0; i < 4; i++)
            {
                waitingZone = waitingZone.add(direction);
            }
        }

        direction = ourHQ.directionTo(enemyHQ);

        if (rc.senseTerrainTile(waitingZone).equals(TerrainTile.OFF_MAP) || rc.senseTerrainTile(waitingZone).equals(TerrainTile.VOID))
        {
            direction = direction.rotateRight().rotateRight();
            for (int i = 0; i < 4; i++)
            {
                waitingZone = waitingZone.add(direction);
            }
        }

        rc.setIndicatorString(0, "Thor");

        while (rc.senseTerrainTile(waitingZone).equals(TerrainTile.OFF_MAP) || rc.senseTerrainTile(waitingZone).equals(TerrainTile.VOID))
        {
            direction = directions[rand.nextInt(8)];
            waitingZone = waitingZone.add(direction);
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
                    // till we are ready to go
                    if (!timeToGo)
                    {
                        timeToGo = true;
                        /*
                        if (rc.senseRobotCount() > (4 + numbOfSoldiersBeforeThor))
                        {
                            timeToGo = true;
                        }
                        Utilities.MoveDirection(rc, rc.getLocation().directionTo(waitingZone), false);
                        */
                    }
                    else
                    {
                        rc.setIndicatorString(1, ""+target);

                        rc.setIndicatorString(2, "Made It");
                        // if someone else has gotten to sensorTower location then we will go elsewhere otherwise we become senserTower
                        if (sensorOver)
                        {
                            rc.setIndicatorString(2, "Looking for Mule");
                            rc.setIndicatorString(1, ""+target4);
                            // if we are at target 4 then we become a mule
                            if (rc.getLocation().equals(target4))
                            {
                                MULE mule = new MULE(rc, corner1);
                                mule.run();
                            }
                            else if (rc.canSenseSquare(target4))
                            {
                                if (rc.senseObjectAtLocation(target4) != null)
                                {
                                    /*
                                    rc.broadcast(2, 0);
                                    Duran Samir = new Duran(rc);
                                    Samir.run();
                                    */
                                    Marines marines = new Marines(rc);
                                    marines.run();
                                }
                                else
                                {
                                    Utilities.MoveDirection(rc, rc.getLocation().directionTo(target4), true);
                                }
                            }
                            else
                            {
                                rc.setIndicatorString(0, "Moving Around");
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(target4), true);
                            }
                        }
                        else
                        {
                            if (!firstPastr)
                            {
                                Utilities.MoveMapLocation(rc, target, true);
                                firstPastr = true;
                            }
                            else
                            {
                                if (rc.getLocation().equals(target2))
                                {
                                    rc.construct(RobotType.NOISETOWER);
                                }
                                else if (rc.senseObjectAtLocation(target2) != null)
                                {
                                    sensorOver = true;
                                }

                                else
                                {
                                    Utilities.MoveDirection(rc, rc.getLocation().directionTo(target2), true);
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                rc.setIndicatorString(0, "Error");
                System.out.println("Thor Exception");
            }
            rc.yield();
        }
    }
}
