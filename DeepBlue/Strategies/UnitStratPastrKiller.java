package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class UnitStratPastrKiller extends UnitStrategy {
    public static RobotController rc;
    public static MapLocation target;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
    }

    public static void upDate() throws GameActionException
    {
        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());

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
        else
        {
            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        }

        Soldiers.nav.setDestination(target);
    }


}
