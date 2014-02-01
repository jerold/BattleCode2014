package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/29/14.
 */
public abstract class UnitStratHqSurround extends UnitStrategy {
    static RobotController rc;
<<<<<<< HEAD
    static MapLocation enemyHQ;
=======
    static public MapLocation enemyHQ;
>>>>>>> cce9400f75ee768a5db6e3f01881986ef198c8e8

    public static void initialize(RobotController rcIn) throws GameActionException
    {
        rc = rcIn;
<<<<<<< HEAD
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
=======
        enemyHQ = rc.senseEnemyHQLocation();
>>>>>>> cce9400f75ee768a5db6e3f01881986ef198c8e8
        Soldiers.nav.setDestination(enemyHQ);
    }

    public static void upDate() throws GameActionException
    {
        if (rc.getLocation().distanceSquaredTo(enemyHQ) < 25)
        {
            Soldiers.nav.setDestination(rc.getLocation());
        }
    }
}
