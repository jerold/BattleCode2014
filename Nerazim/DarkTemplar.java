package Nerazim;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/26/14.
 */
public class DarkTemplar {
    RobotController rc;
    boolean madeIt = false;
    MapLocation closestPastr;

    public DarkTemplar(RobotController rc)
    {
        rc.setIndicatorString(0, "Dark Templar");
        this.rc = rc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (!madeIt)
                    {
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
                            }
                        }
                        else
                        {
                            closestPastr = null;
                        }
                        // if we don't see any enemy pastrs then we will go to a target location
                        if (closestPastr == null)
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

                           // tempTarget = rc.senseHQLocation().add(rc.senseHQLocation().directionTo(rc.senseEnemyHQLocation()));

                            Movement.MoveMapLocation3(rc, tempTarget, false, false);
                        }
                        else
                        {
                            MapLocation newTarget = closestPastr;
                            for (int i = 10; --i>=0;)
                            {
                                newTarget = newTarget.add(newTarget.directionTo(rc.getLocation()));
                            }
                            Movement.MoveMapLocation3(rc, newTarget, false, false);
                            madeIt = true;
                        }
                    }
                    else
                    {
                        rc.setIndicatorString(0, "Dark Templar");
                        rc.setIndicatorString(2, "Made It:"+Clock.getRoundNum());
                        if (rc.getLocation().distanceSquaredTo(closestPastr) <= 10)
                        {
                            rc.setIndicatorString(1, "Close");
                            Robot[] seenEnemies = rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam().opponent());
                            MapLocation[] nearAllies = FightMicro2.locationOfBots(rc, rc.senseNearbyGameObjects(Robot.class, 10, rc.getTeam()));
                            if (seenEnemies.length > 0)
                            {
                                FightMicro2.fire(rc, seenEnemies, nearAllies);
                            }
                            else
                            {
                                MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                                if (enemyPastrs.length == 0)
                                {
                                    madeIt = false;
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
                                }
                            }
                        }
                        else if (FightMicro2.cloakedMove(rc, closestPastr))
                        {
                            rc.setIndicatorString(1, "Fighting");
                        }
                        else
                        {
                            rc.setIndicatorString(1, "not fighting: "+closestPastr);
                             FightMicro2.MoveDirection(rc, rc.getLocation().directionTo(closestPastr).rotateLeft(), false);
                        }

                        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());

                        if (enemyPastrs.length == 0)
                        {
                            madeIt = false;
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
                        }
                    }
                }

            } catch (Exception e) {}
            rc.yield();
        }
    }
}
