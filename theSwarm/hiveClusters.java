package theSwarm;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/22/14.
 */
public class hiveClusters {
    RobotController rc;

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

    public hiveClusters(RobotController rc)
    {
        this.rc = rc;
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.getType() == RobotType.PASTR)
                {
                    if (rc.getHealth() < 30)
                    {
                        rc.broadcast(pastrBuilt, 0);
                    }
                    else if (rc.readBroadcast(pastrBuilt) == 0)
                    {
                        rc.broadcast(pastrBuilt, 1);
                    }
                }
                else if (rc.getType() == RobotType.NOISETOWER)
                {
                    if (rc.getHealth() < 30)
                    {
                        rc.broadcast(towerBuilt, 0);
                    }
                }

            } catch (Exception e) {}
            rc.yield();
        }
    }
}
