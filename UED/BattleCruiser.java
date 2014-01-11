package UED;

import java.util.Random;

import battlecode.common.*;

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
    }

    public void run()
    {
        while (true)
        {
            try
            {
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
                        rc.setIndicatorString(1, "BattleCruiserMovement");
                        Utilities.BattleCruiserMovement(rc);
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
