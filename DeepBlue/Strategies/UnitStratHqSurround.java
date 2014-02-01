package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratHqSurround extends UnitStrategy {
    static RobotController rc;
    public static MapLocation enemyHQ;

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
        Direction[] dirs = Direction.values();
        enemyHQ = rc.senseEnemyHQLocation();
        for(Direction dir : dirs)
        {
        	if(Soldiers.map.getTileType(enemyHQ.add(dir)) != RoadMap.TileType.TTVoid)
        	{
        		enemyHQ = enemyHQ.add(dir);
        		break;
        	}
        }
        Soldiers.nav.setDestination(enemyHQ);
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getLocation().distanceSquaredTo(enemyHQ) < 16)
        {
            Soldiers.nav.setDestination(rc.getLocation());
        }
    }
}
