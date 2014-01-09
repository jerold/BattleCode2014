package UED;

import battlecode.common.*;

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

    public Ghost(RobotController rc)
    {
        rc.setIndicatorString(0, "Ghost");
        this.rc = rc;
        direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        waitingZone = rc.getLocation();

        /*
        for (int i = 0; i < 3; i++)
        {
            waitingZone = waitingZone.add(direction);
        }*/
        int var = 5;
        target = new MapLocation(var, rc.getMapHeight() - var);
        while (rc.senseTerrainTile(target).equals(TerrainTile.VOID))
        {
            var++;
            target = new MapLocation(var, rc.getMapHeight() - var);
        }

        waitingZone = target;

        try
        {

        } catch (Exception e)
        {
            e.printStackTrace();
            System.out.println("Ghost Exception");
        }
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (!gotToWaitingZone)
                {
                    target = target.subtract(rc.getLocation().directionTo(target)).subtract(rc.getLocation().directionTo(target));
                    Utilities.MoveMapLocation(rc, target, false);
                    if (rc.getLocation().equals(target))
                    {
                        gotToWaitingZone = true;
                        Samir = rc.senseObjectAtLocation(waitingZone);
                    }

                }
                else
                {
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
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Ghost Exception");
            }
            rc.yield();
        }
    }
}
