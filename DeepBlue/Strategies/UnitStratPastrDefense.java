package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 */
public abstract class UnitStratPastrDefense extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;

    public static void initialize(RobotController rcIn)
    {
        rc = rcIn;
    }

    public static void update() throws GameActionException
    {
       MapLocation[] ourPastrs = rc.sensePastrLocations(rc.getTeam());

        if (ourPastrs.length > 0)
        {
            MapLocation closest = ourPastrs[ourPastrs.length-1];
            int closestDist = rc.getLocation().distanceSquaredTo(closest);

            for (int i = ourPastrs.length-1; --i>=0;)
            {
                MapLocation current = ourPastrs[i];
                int dist = rc.getLocation().distanceSquaredTo(current);

                if (dist < closestDist)
                {
                    closestDist = dist;
                    closest = current;
                }
            }

            target = closest;
            MapLocation enemyHQ = rc.senseEnemyHQLocation();

            target = target.add(target.directionTo(enemyHQ));
            target = target.add(target.directionTo(enemyHQ));
            target = target.add(target.directionTo(enemyHQ));

        }
        else
        {
            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);
        }

        Soldiers.nav.setDestination(target);
    }
}
