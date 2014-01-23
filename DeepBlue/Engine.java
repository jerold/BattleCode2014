package DeepBlue;

import battlecode.common.*;

/**
 * Created by AfterHours on 1/23/14.
 */
public class Engine {

    RobotController rc;
    UnitCache cache;
    RoadMap map;
    Navigator nav;

    Engine(RobotController inRc,UnitCache inCache,RoadMap inMap, Navigator inNav)
    {
        rc = inRc;
        cache = inCache;
        map = inMap;
        nav = inNav;
    }

    public boolean engaging() throws GameActionException
    {
        return cache.nearbyEnemies().length > 0;
    }

    /*
     * The Engine doesn't care, the engine only kills.  And it's a good killer... A damn good killer.
     */
    public void adjustFire() throws GameActionException
    {
        // Go Felix!
        RobotInfo targetInfo = cache.nearbyEnemies()[0];
        if (rc.canAttackSquare(targetInfo.location))
            rc.attackSquare(targetInfo.location);
        else if (!rc.isActive()) rc.yield();
        rc.move(rc.getLocation().directionTo(targetInfo.location));
    }
}
