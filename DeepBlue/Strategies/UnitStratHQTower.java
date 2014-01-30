package DeepBlue.Strategies;

import DeepBlue.*;
import DeepBlue.Soldiers.UnitStrategyType;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratHQTower extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;
    static Direction[] dirs = Direction.values();

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
        int closestDist = 100000;
        MapLocation ourHQ = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();

        for (int i = 0; i < 8; i++)
        {
            MapLocation current = ourHQ.add(dirs[i]);

            if (!rc.senseTerrainTile(current).equals(TerrainTile.OFF_MAP) && !rc.senseTerrainTile(current).equals(TerrainTile.VOID))
            {
                int currentDist = current.distanceSquaredTo(enemyHQ);

                if (currentDist < closestDist)
                {
                    closestDist = currentDist;
                    target = current;
                }
            }
        }
        Soldiers.nav.setDestination(target);
    }

    public static void upDate() throws GameActionException
    {
        rc.setIndicatorString(0, "upDate HQTower");
        if (rc.isActive())
        {
            if (rc.getLocation().equals(target))
            {
                rc.setIndicatorString(1, "Construct");
                rc.construct(RobotType.NOISETOWER);
            }
        }
    }
}
