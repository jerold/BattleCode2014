package theSwarm;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class Kerrigan {
    RobotController rc;
    MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    int fightZone;
    int roundSet = 0;
    boolean build = false;
    boolean build2 = false;
    int roundBuilt = 0;
    boolean offense = false;
    boolean twoPastrs = false;
    boolean stratDetermined = false;

    // these are the channels that we will use to communicate to our bots
    static final int enemyHQ = 1;
    static final int ourHQ = 2;
    static final int rallyPoint = 3;
    static final int needNoiseTower = 4;
    static final int needPastr = 5;
    static final int takeDownEnemyPastr = 6;
    static final int enemyPastrInRangeOfHQ = 7;
    static final int rallyPoint2 = 8;
    static final int defendPastr = 9;
    static final int pastLoc = 10;
    static final int morphZergling = 11;
    static final int morphHydralisk = 12;
    static final int hydraliskCount = 13;
    static final int towerLoc = 14;
    static final int towerBuilt = 15;
    static final int pastrBuilt = 16;
    static final int morphRoach = 17;

    static int numbOfSoldiers = 0;

    public Kerrigan(RobotController rc)
    {
        this.rc = rc;

        HQFunctions.InitialLocationBroadcasts(rc);

        HQFunctions.findInitialRally(rc);


    }

    public void run()
    {
        while (true)
        {
            rc.setIndicatorString(0, "Mengsk will suffer!");
            //rc.setIndicatorString(1, "But I am not alone,");
            //rc.setIndicatorString(2, "For I am the Swarm");

            try
            {
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());
                if (Clock.getRoundNum() % 2 == 0)
                {
                    HQFunctions.setTargetLocation(rc, true);
                }

                if (rc.isActive())
                {
                    Movement.fire(rc, enemies, null);
                    HQFunctions.SpawnSoldiers(rc);
                    numbOfSoldiers++;
                    if (offense)
                    {
                        if (numbOfSoldiers > 5)
                        {
                            rc.broadcast(morphHydralisk, 1);
                        }
                        else
                        {
                            rc.broadcast(morphZergling, 1);
                        }
                    }
                    else if (twoPastrs)
                    {
                        if (rc.readBroadcast(pastLoc) == 0)
                        {
                            rc.broadcast(morphHydralisk, 1);
                        }
                        else
                        {
                            if (numbOfSoldiers % 3 == 0)
                            {
                                rc.broadcast(morphHydralisk, 1);
                            }
                            else
                            {
                                rc.broadcast(morphRoach, 1);
                            }
                        }
                    }
                    else
                    {
                        if (rc.readBroadcast(pastLoc) == 0)
                        {
                            if (numbOfSoldiers % 2 == 0)
                            {
                                rc.broadcast(morphHydralisk, 1);
                            }
                            else
                            {
                                rc.broadcast(morphZergling, 1);
                            }
                        }
                        else
                        {
                            if (numbOfSoldiers % 3 == 0)
                            {
                                rc.broadcast(morphHydralisk, 1);
                            }
                            else
                            {
                                rc.broadcast(morphRoach, 1);
                            }
                        }
                    }
                }

                int pastrSpotInt = rc.readBroadcast(pastLoc);
                if (!stratDetermined && pastrSpotInt != 0)
                {
                    MapLocation spot = Movement.convertIntToMapLocation(pastrSpotInt);
                    MapLocation spot2 = TowerUtil.getOppositeSpot(rc, spot);
                    if (TowerUtil.getSpotScore(rc, spot) <= 60)
                    {
                        offense = true;
                    }

                    if (HQFunctions.checkDoublePastr(rc, spot, spot2))
                    {
                        twoPastrs = true;
                    }
                }

                int broadcast = rc.readBroadcast(rallyPoint2);
                if (broadcast != 0)
                {
                    if (broadcast != fightZone)
                    {
                        fightZone = broadcast;
                        roundSet = Clock.getRoundNum();
                    }
                    // now it is time for us to move on
                    else if (roundSet + 75 < Clock.getRoundNum())
                    {
                        fightZone = 0;
                        rc.broadcast(rallyPoint2, 0);
                    }
                }

                if ((rc.readBroadcast(hydraliskCount) > 6) && (rc.sensePastrLocations(rc.getTeam().opponent()).length == 0) && !build && !offense)
                {
                    rc.broadcast(needNoiseTower, 1);
                    rc.broadcast(needPastr, 1);
                    build = true;
                    roundBuilt = Clock.getRoundNum();
                }

                if (build && ((Clock.getRoundNum()-roundBuilt) > 30) && (rc.getMapHeight() > 50) && !build2 && twoPastrs)
                {
                    rc.broadcast(needNoiseTower, -1);
                    rc.broadcast(needPastr, -1);
                    build2 = true;
                }

                else if (build && ((Clock.getRoundNum() - roundBuilt) > 250) && (twoPastrs) && rc.sensePastrLocations(rc.getTeam().opponent()).length == 0)
                {
                    build = false;
                    int towerSpot = rc.readBroadcast(towerLoc);
                    MapLocation spot = TowerUtil.getOppositeSpot(rc, Movement.convertIntToMapLocation(towerSpot));
                    rc.broadcast(towerLoc, Movement.convertMapLocationToInt(spot));
                    int pastrSpot = rc.readBroadcast(pastLoc);
                    spot = TowerUtil.getOppositeSpot(rc, Movement.convertIntToMapLocation(pastrSpot));
                    rc.broadcast(pastLoc, Movement.convertMapLocationToInt(spot));
                }

                int towerSpot = rc.readBroadcast(towerLoc);
                int pastrSpot = rc.readBroadcast(pastLoc);

                if (Movement.convertIntToMapLocation(towerSpot).distanceSquaredTo(Movement.convertIntToMapLocation(pastrSpot)) > 50)
                {
                    MapLocation spot = TowerUtil.getOppositeSpot(rc, Movement.convertIntToMapLocation(towerSpot));
                    rc.broadcast(towerLoc, Movement.convertMapLocationToInt(spot));
                }

            } catch (Exception e) {}
            rc.yield();
        }
    }
}
