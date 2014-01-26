package theSwarm;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;
import java.util.Random;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class QueenOfBlades {

    RobotController rc;
    MapLocation target;
    boolean goneForPastr = false;
    int roundNum = 0;
    int fightZone;
    int roundSet = 0;
    boolean build = false;
    int roundBuilt = 0;
    boolean setUpPastr = false;
    boolean enemySetUpPastr = false;
    Random rand;

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

    public QueenOfBlades(RobotController rc)
    {
        this.rc = rc;

        HQFunctions.InitialLocationBroadcasts(rc);

        HQFunctions.findInitialRally(rc);

        rand = new Random();
    }

    public void run()
    {
        while (true)
        {
            rc.setIndicatorString(0, "Armies will be shattered.");
            rc.setIndicatorString(1, "Worlds will burn.");
            rc.setIndicatorString(2, "For I am the Queen of Blades.");
            //rc.setIndicatorString(1, "" + rand.nextInt());

            try
            {
                MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                Robot[] enemies = rc.senseNearbyGameObjects(Robot.class, 35, rc.getTeam().opponent());

                if (rc.isActive())
                {
                    Movement.fire(rc, enemies, null);
                    HQFunctions.SpawnSoldiers(rc);
                    numbOfSoldiers++;
                    rc.broadcast(morphHydralisk, 1);
                }

                if (Clock.getRoundNum() % 50 == 0)
                {
                    System.out.println();
                    System.out.println("Enemy Bots: ");
                    int[] AllEnemies = FightMicro.AllEnemyBots(rc);
                    for (int i = 0; i<AllEnemies.length; i++)
                    {
                        System.out.print(FightMicro.getBotLocation(AllEnemies[i]));
                    }
                    System.out.println();
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

                if (!enemySetUpPastr)
                {
                    if (enemyPastrs.length > 0)
                    {
                        enemySetUpPastr = true;
                    }
                }

                int robotCount = rc.senseRobotCount();
                if ((robotCount > 12) && (enemyPastrs.length == 0) && !build)
                {
                    build = true;
                }
                // if our enemy has built a pastr which we have destroyed we will probably have a military advantage which we should utilize before we loose it
                else if (enemySetUpPastr && enemyPastrs.length == 0 && robotCount > 5)
                {
                    build = true;
                }
                else if (enemyPastrs.length > 0 && enemyPastrs[0].distanceSquaredTo(rc.senseEnemyHQLocation()) < 10 && robotCount > 5)
                {
                    build = true;
                }

                int towerSpot = rc.readBroadcast(towerLoc);
                int pastrSpot = rc.readBroadcast(pastLoc);

                if (Movement.convertIntToMapLocation(towerSpot).distanceSquaredTo(Movement.convertIntToMapLocation(pastrSpot)) > 50)
                {
                    MapLocation spot = TowerUtil.getOppositeSpot(rc, Movement.convertIntToMapLocation(towerSpot));
                    rc.broadcast(towerLoc, Movement.convertMapLocationToInt(spot));
                }



                if (build)
                {
                    rc.broadcast(needNoiseTower, 1);
                    rc.broadcast(needPastr, 1);
                    rc.broadcast(pastrBuilt, 0);
                    rc.broadcast(towerBuilt, 0);
                    build = false;
                    roundBuilt = Clock.getRoundNum();
                }

                /*
                if (build && ((roundBuilt-Clock.getRoundNum()) > 150) && (rc.sensePastrLocations(rc.getTeam()).length == 0))
                {
                    build = false;
                }*/


                if (Clock.getRoundNum() % 2 == 0 && Clock.getRoundNum() > 0)
                {
                    HQFunctions.setTargetLocation(rc, true);
                }
            } catch (Exception e) {}
            rc.yield();
        }
    }
}
