package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;

/**
 * Created by fredkneeland on 1/28/14.
 *
 *
 * This bot is a pastr killer that gathers at a rally point
 * 1/2 map height 1/3 map width or 2/3 map width
 *
 * It tries to avoid action with enemies and attempts to circle around behind enemy pastrs
 * while avoiding their soldiers until it can kill an enemy
 */
public abstract class UnitStratDarkTemplar extends UnitStrategy {
    static RobotController rc;
    static MapLocation target;

    public static void initialize(RobotController rcIn)
    {
        rc = rcIn;
    }

    public static void upDate() throws GameActionException
    {
        MapLocation closestPastr;
        rc.setIndicatorString(0, "dark templar");
        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        if (enemyPastrs.length > 0)
        {
            if (enemyPastrs.length == 1)
            {
                closestPastr = enemyPastrs[0];
            }
            else
            {
                MapLocation closest = enemyPastrs[enemyPastrs.length-1];
                int closestDist = closest.distanceSquaredTo(rc.getLocation());
                for (int i = enemyPastrs.length; --i>=0;)
                {
                    MapLocation current = enemyPastrs[i];
                    int dist = current.distanceSquaredTo(rc.getLocation());
                    if (dist < closestDist)
                    {
                        closestDist = dist;
                        closest = current;
                    }
                }
                closestPastr = closest;
                target = closestPastr;
            }
        }
        else
        {
            MapLocation tempTarget;
            if (rc.getRobot().getID() % 200 < 100)
            {
                tempTarget = new MapLocation(rc.getMapWidth()/3, rc.getMapHeight()/2);
            }
            else
            {
                tempTarget = new MapLocation(2*rc.getMapWidth()/3, rc.getMapHeight()/2);
            }

            target = tempTarget;
        }

        Robot[] seenEnemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
        Robot[] seenEnemies2 = seenEnemies;
        seenEnemies = FightMicro.findSoldiers(rc, seenEnemies);

        if (seenEnemies.length > 0)
        {
            Robot[] inRangeEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
            inRangeEnemies = FightMicro.findSoldiers(rc, inRangeEnemies);

            if (inRangeEnemies.length > 0)
            {
                if (inRangeEnemies.length == 1)
                {
                    FightMicro.fire(rc, seenEnemies2, null);
                    target = rc.getLocation();
                }
                else
                {
                    Direction dir = rc.getLocation().directionTo(rc.senseLocationOf(inRangeEnemies[0]));

                    if (rc.getLocation().add(dir.rotateRight()).distanceSquaredTo(target) < rc.getLocation().add(dir.rotateLeft()).distanceSquaredTo(target))
                    {
                        target = rc.getLocation().add(dir.rotateRight());
                    }
                    else
                    {
                        target = rc.getLocation().add(dir.rotateLeft());
                    }
                }
            }
            else
            {
                if (seenEnemies.length == 1)
                {
                    MapLocation enemy = rc.senseLocationOf(seenEnemies[0]);
                    if (rc.getLocation().distanceSquaredTo(enemy) < rc.getLocation().add(rc.getLocation().directionTo(target)).distanceSquaredTo(enemy))
                    {
                        target = rc.getLocation().add(rc.getLocation().directionTo(target));
                    }
                    else
                    {
                        Direction dir = rc.getLocation().directionTo(enemy).opposite();

                        if (rc.getLocation().add(dir.rotateLeft()).distanceSquaredTo(target) < rc.getLocation().add(dir.rotateRight()).distanceSquaredTo(target))
                        {
                            target = rc.getLocation().add(dir.rotateLeft());
                        }
                        else
                        {
                            target = rc.getLocation().add(dir.rotateLeft());
                        }
                    }
                }
                else
                {
                    MapLocation[] enemyBots = FightMicro.locationOfBots(rc, seenEnemies);
                    MapLocation center = FightMicro.centerOfEnemies(enemyBots);
                    Direction dir = rc.getLocation().directionTo(center).opposite();

                    if (rc.getLocation().add(dir.rotateLeft()).distanceSquaredTo(target) < rc.getLocation().add(dir.rotateRight()).distanceSquaredTo(target))
                    {
                        target = rc.getLocation().add(dir.rotateLeft());
                    }
                    else
                    {
                        target = rc.getLocation().add(dir.rotateLeft());
                    }
                }
            }
        }
        else
        {
            Robot[] inRangeEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());

            if (inRangeEnemies.length > 0)
            {

            }
        }

        Soldiers.nav.setDestination(target);
    }


}
