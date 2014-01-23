package theSwarm;

import battlecode.common.Clock;
import battlecode.common.MapLocation;
import battlecode.common.Robot;
import battlecode.common.RobotController;

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


    }

    public void run()
    {
        while (true)
        {
            rc.setIndicatorString(0, "Armies will be shattered.");
            rc.setIndicatorString(1, "Worlds will burn.");
            rc.setIndicatorString(2, "For I am the Queen of Blades.");

            try
            {
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

                if ((rc.senseRobotCount() > 10) && (rc.sensePastrLocations(rc.getTeam().opponent()).length == 0) && !build)
                {
                    rc.broadcast(needNoiseTower, 1);
                    rc.broadcast(needPastr, 1);
                    rc.broadcast(pastrBuilt, 0);
                    rc.broadcast(towerBuilt, 0);
                    build = true;
                    roundBuilt = Clock.getRoundNum();
                }

                /*
                if (build && ((roundBuilt-Clock.getRoundNum()) > 150) && (rc.sensePastrLocations(rc.getTeam()).length == 0))
                {
                    rc.setIndicatorString(2, "creating new pastr");
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
