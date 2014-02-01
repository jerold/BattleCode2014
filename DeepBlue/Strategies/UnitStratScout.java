package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class UnitStratScout extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;
    static MapLocation oldTarget;

    public static void initialize(RobotController rcIn)
    {
        rc = rcIn;
    }

    public static void upDate() throws GameActionException
    {
        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        int time = Clock.getRoundNum() % 750;

        if (enemyPastrs.length > 0)
        {
            MapLocation closest = enemyPastrs[enemyPastrs.length-1];
            int smallestDist = rc.getLocation().distanceSquaredTo(closest);

            for (int i = enemyPastrs.length - 1; --i>=0;)
            {
                MapLocation current = enemyPastrs[i];
                int currentDist = rc.getLocation().distanceSquaredTo(current);
                if (currentDist < smallestDist)
                {
                    smallestDist = currentDist;
                    closest = current;
                }
            }
            target = closest;
        }
        else if (time % 75 == 0)
        {
            target = rc.getLocation();
            while ((target.distanceSquaredTo(rc.senseEnemyHQLocation()) < 50 || target.distanceSquaredTo(rc.getLocation()) < 50))
            {
                if (time < 75)
                {
                    target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
                }
                else if (time < 150)
                {
                    target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/10);
                }
                else if (time < 225)
                {
                    target = new MapLocation(rc.getMapWidth()/10, rc.getMapHeight()/2);
                }
                else if (time < 300)
                {
                    target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight() - 5);
                }
                else if (time < 375)
                {
                    target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/2);
                }
                else if (time < 450)
                {
                    target = new MapLocation(rc.getMapWidth()-5, rc.getMapHeight()/10);
                }
                else if (time < 525)
                {
                    target = new MapLocation(rc.getMapWidth()/2, 10);
                }
                else if (time < 600)
                {
                    target = new MapLocation(rc.getMapWidth() - 5, rc.getMapHeight() - 5);
                }
                else if (time < 675)
                {
                    target = new MapLocation(5, rc.getMapHeight() - 5);
                }
                else if (time < 750)
                {
                    target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
                }
                time += 75;
                time %= 750;
            }
        }
        else if (target == null)
        {
            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        }

        if (oldTarget == null || !oldTarget.equals(target))
        {
            Soldiers.nav.setDestination(target);
        }
    }
}