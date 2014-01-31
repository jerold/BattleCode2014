package DeepBlue.Strategies;

import DeepBlue.*;
import DeepBlue.Soldiers.UnitStrategyType;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 *
 *
 */
public abstract class UnitStratHQPastr extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;
    static Direction[] dirs = Direction.values();

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
        MapLocation ourHq = rc.senseHQLocation();
        MapLocation enemyHQ = rc.senseEnemyHQLocation();
        int closestDist = 0;

        for (int i = 0; i < 8; i++)
        {
            int currentDist;
            MapLocation current = ourHq.add(dirs[i]);
            if (!rc.senseTerrainTile(current).equals(TerrainTile.OFF_MAP) && !rc.senseTerrainTile(current).equals(TerrainTile.VOID))
            {
                currentDist = current.distanceSquaredTo(enemyHQ);
                if (currentDist > closestDist)
                {
                    closestDist = currentDist;
                    target = current;
                }
            }
        }

        Robot botAtSpot = (Robot) rc.senseObjectAtLocation(target);

        if (botAtSpot != null)
        {
            if (rc.senseRobotInfo(botAtSpot).type == RobotType.PASTR)
            {
                Soldiers.changeStrategy(UnitStrategyType.PastrDestroyer);
            }
            else if (rc.senseRobotInfo(botAtSpot).isConstructing)
            {
                Soldiers.changeStrategy(UnitStrategyType.PastrDestroyer);
            }
        }

        Soldiers.nav.setDestination(target);
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getLocation().equals(target))
        {
            if (rc.isActive())
            {
                rc.construct(RobotType.PASTR);
            }
        }
    }
}
