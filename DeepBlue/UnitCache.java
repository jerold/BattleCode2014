package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 */
public class UnitCache {

    public enum UnitType {
        MARINE,
        FIRETEAMLEADER,
        SCOUT,
        ENGINEER
    }

    public enum StructureType {
        PASTR,
        DUMMYPASTR,
        NOISETOWER,
        TROLLNOISETOWER
    }

    RobotController rc;

    double SLOPE;
    public MapLocation MY_HQ;
    public MapLocation ENEMY_HQ;
    double DISTANCE_BETWEEN_HQS;
    public RobotType rcType;

    UnitCache(RobotController rc)
    {
        this.rc = rc;
        MY_HQ = rc.senseHQLocation();
        ENEMY_HQ = rc.senseEnemyHQLocation();
        if (MY_HQ.x != ENEMY_HQ.x)
            SLOPE = (double)(MY_HQ.y - ENEMY_HQ.y) / (MY_HQ.x - ENEMY_HQ.x);
        else
            SLOPE = MY_HQ.y > ENEMY_HQ.y ? (1<<30) : -(1<<30);
        DISTANCE_BETWEEN_HQS = Utilities.distanceBetweenTwoPoints(MY_HQ, ENEMY_HQ);
        rcType = rc.getType();
    }

    // Like all up on.
    public Robot[] adjacentRobots(MapLocation loc, Team team)
            throws GameActionException
    {
        return rc.senseNearbyGameObjects(Robot.class, loc, 2, team);
    }

    // Got some space, but in my bubble yo.
    public Robot[] strikeRobots(MapLocation loc, Team team)
            throws GameActionException
    {
        return rc.senseNearbyGameObjects(Robot.class, loc, 8, team);
    }

    private RobotInfo[] nearbyEnemiesCache = null;
    public RobotInfo[] nearbyEnemies() throws GameActionException
    {
        if (nearbyEnemiesCache != null) return nearbyEnemiesCache;

        Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, Integer.MAX_VALUE, rc.getTeam().opponent());

        nearbyEnemiesCache = new RobotInfo[enemies.length];
        for (int i = enemies.length; --i >= 0;)
            nearbyEnemiesCache[i] = rc.senseRobotInfo(enemies[i]);

        return nearbyEnemiesCache;
    }

    private RobotInfo[] nearbyAlliesCache = null;
    public RobotInfo[] nearbyAllies() throws GameActionException
    {
        if (nearbyAlliesCache != null) return nearbyAlliesCache;

        Robot[] allies = rc.senseNearbyGameObjects(Robot.class, Integer.MAX_VALUE, rc.getTeam());

        nearbyAlliesCache = new RobotInfo[Math.min(20, allies.length)];
        for (int i = nearbyAlliesCache.length; --i >= 0;)
            nearbyAlliesCache[i] = rc.senseRobotInfo(allies[i]);

        return nearbyAlliesCache;
    }

    public void reset()
    {
        nearbyAlliesCache = null;
        nearbyEnemiesCache = null;
    }
}
