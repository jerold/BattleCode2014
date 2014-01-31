package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/30/14.
 */
public abstract class BlockadeRunner extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;
    static MapLocation ourHQ;
    static Direction toEnemyHQ;
    static MapLocation enemyHQ;
    static MapLocation rallyPoint;

    public static void initialize(RobotController rcIn)
    {
        rc = rcIn;
        ourHQ = rc.senseHQLocation();
        enemyHQ = rc.senseEnemyHQLocation();
        toEnemyHQ = ourHQ.directionTo(enemyHQ);
        rallyPoint = ourHQ.add(toEnemyHQ, 10);
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getLocation().distanceSquaredTo(ourHQ) < 50)
        {
            Robot[] nearByAllies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam());

            if (nearByAllies.length > 8)
            {
                target = ourHQ.add(toEnemyHQ.rotateRight().rotateRight(), 5);

                while (rc.senseTerrainTile(target).equals(TerrainTile.OFF_MAP))
                {
                    target = target.add(toEnemyHQ);
                }

                if (rc.getLocation().distanceSquaredTo(target) < 4)
                {
                    target = rallyPoint;
                }
            }
            else
            {
                target = ourHQ.add(toEnemyHQ.opposite());
            }
        }
        else if (rc.sensePastrLocations(rc.getTeam().opponent()).length > 0)
        {
            target = rc.sensePastrLocations(rc.getTeam().opponent())[0];
        }
        else
        {
            target = enemyHQ;
        }

        Soldiers.nav.setDestination(target);
    }
}
