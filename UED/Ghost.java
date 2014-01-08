package UED;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/7/14.
 *
 *
 * Sneaky soldier that follows Duran who tries to avoid enemies while taking out pastrs
 * commits suicide when health runs out, forms a group of two with Duran
 */
public class Ghost
{
    RobotController rc;
    GameObject Samir;
    Direction direction;
    MapLocation waitingZone;

    public Ghost(RobotController rc)
    {
        this.rc = rc;
        direction = rc.getLocation().directionTo(rc.senseEnemyHQLocation());
        waitingZone = rc.getLocation();

        for (int i = 0; i < 3; i++)
        {
            waitingZone = waitingZone.add(direction);
        }

        try
        {
            Samir = rc.senseObjectAtLocation(waitingZone);
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
                GameObject[] nearByBots = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

                if (!rc.getLocation().isAdjacentTo(rc.senseLocationOf(Samir)))
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
            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("Ghost Exception");
            }
            rc.yield();
        }
    }
}
