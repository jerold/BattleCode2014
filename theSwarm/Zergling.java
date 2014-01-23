package theSwarm;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class Zergling {

    RobotController rc;
    MapLocation target;
    int ourIndex;

    Robot[] nearByEnemies;
    int[] AllEnemyNoiseTowers;
    int[] AllEnemyBots;
    int[] AllAlliedBots;

    // these are the channels that we will use to communicate to our bots
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

    public Zergling(RobotController rc)
    {
        this.rc = rc;
        try {
            target = Movement.convertIntToMapLocation(rc.readBroadcast(HQFunctions.rallyPointChannel()));
        } catch (GameActionException e) {
            e.printStackTrace();
        }
        rc.setIndicatorString(0, "Zergling");
    }

    public void run()
    {
        while (true)
        {
            try
            {

                if (rc.readBroadcast(pastLoc) != 0)
                {
                    Roach roach = new Roach(rc);
                    roach.run();
                }
                else if (FightMicro.fightMode(rc, target))
                {
                }
                else if (rc.isActive())
                {

                    if (rc.getLocation().equals(target) || rc.getLocation().distanceSquaredTo(target) < 10)
                    {
                        int channel = rc.readBroadcast(HQFunctions.rallyPoint2Channel());
                        if (channel > 5)
                        {
                            target = Movement.convertIntToMapLocation(channel);
                        }
                    }


                    Movement.MoveMapLocation(rc, target, false, false);

                }
                else
                {

                }

                rc.setIndicatorString(1, ""+target);

            } catch (Exception e)
            {
                e.printStackTrace();
                System.out.println("RobotPlayer Exception");
            }
            rc.yield();

        }
    }
}
