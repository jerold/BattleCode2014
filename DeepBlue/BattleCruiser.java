package DeepBlue;

import battlecode.common.*;

import java.util.Random;

/**
 * Created by fredkneeland on 1/10/14.
 */
public class BattleCruiser
{
    Random rand = new Random();
    Direction[] directions = {Direction.NORTH, Direction.NORTH_EAST, Direction.EAST, Direction.SOUTH_EAST, Direction.SOUTH, Direction.SOUTH_WEST, Direction.WEST, Direction.NORTH_WEST};
    RobotController rc;
    MapLocation target;
    MapLocation targetZone;
    Direction direction;
    MapLocation enemyHQSpot;
    MapLocation ourHQSpot;
    boolean arrived = false;
    boolean arrived2 = false;
    MapLocation newTarget;

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

    /**
     *
     * BattleCruiser builds huge army then when it is time to go
     * every round the force will switch between two channels for targeted area
     * if no other battlecruiser has yet given a map location to move towards then
     * the current one will set it otherwise the battlecruiser will move toward the map location
     *
     * @param rc
     */
    public BattleCruiser(RobotController rc)
    {
        rc.setIndicatorString(0, "Battle Cruiser");
        this.rc = rc;

        targetZone = rc.getLocation();
        direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        for (int i = 0; i < 3; i++)
        {
            targetZone = targetZone.subtract(direction);
        }

        while (rc.senseTerrainTile(targetZone).equals(TerrainTile.VOID))
        {
            direction = directions[rand.nextInt(8)];
            targetZone.add(direction);
        }

        try
        {
            rc.broadcast(BattleCruiserNumber, rc.readBroadcast(BattleCruiserNumber) + 1);
        } catch (Exception e)
        {
            e.printStackTrace();
            rc.setIndicatorString(0, "Error");
            System.out.println("Thor Exception");
        }

    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.getHealth() < 30)
                {
                    rc.broadcast(BattleCruiserNumber, rc.readBroadcast(BattleCruiserNumber) - 1);
                    Hellion hellion = new Hellion(rc);
                    hellion.run();
                }
                if (rc.isActive())
                {
                    // wait at target zone until all troops arrive
                    if (!arrived)
                    {
                        if (Utilities.BattleCruiserReady(rc))
                        {
                            arrived = true;
                        }
                        if (rc.getLocation().isAdjacentTo(targetZone) || arrived2)
                        {
                            arrived2 = true;
                            if (Utilities.fightMode(rc))
                            {
                            }
                            else
                            {
                                Utilities.MoveDirection(rc, rc.getLocation().directionTo(targetZone), false);
                            }
                        }
                        else
                        {
                            Utilities.MoveDirection(rc, rc.getLocation().directionTo(targetZone), false);
                        }
                    }
                    else
                    {
                        if (Utilities.fightMode(rc))
                        {
                        }
                        else if (newTarget == null)
                        {
                            newTarget = Utilities.convertIntToMapLocation(rc.readBroadcast(BattleCruiserLoc));
                        }
                        else if (rc.getLocation().isAdjacentTo(newTarget) || rc.getLocation().equals(newTarget))
                        {
                            if (newTarget.equals(Utilities.convertIntToMapLocation(rc.readBroadcast(BattleCruiserLoc))))
                            {
                                if (rc.readBroadcast(BattleCruiserNumber) == rc.senseNearbyGameObjects(Robot.class, 15, rc.getTeam()).length)
                                {
                                    rc.broadcast(BattleCruiserReadyForNewCommand, 1);
                                }
                            }
                            else
                            {
                                newTarget = Utilities.convertIntToMapLocation(rc.readBroadcast(BattleCruiserLoc));
                            }
                        }
                        else
                        {
                            Utilities.MoveMapLocation(rc, newTarget, false);
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
