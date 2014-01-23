package theSwarm;

import battlecode.common.MapLocation;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

/**
 * Created by fredkneeland on 1/16/14.
 */
public class Extractor
{
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

    RobotController rc;
    MapLocation towerSpot;

    public Extractor(RobotController rc, int type)
    {
        this.rc = rc;
        try
        {
            int loc = rc.readBroadcast(towerLoc);
            if (loc == 0)
            {
                towerSpot = TowerUtil.bestSpot3(rc);
                towerSpot = towerSpot.add(towerSpot.directionTo(rc.senseHQLocation()));
            }
            else
            {
                towerSpot = Movement.convertIntToMapLocation(loc);
            }

            if(type < 0)
            {
                towerSpot = TowerUtil.getOppositeSpot(rc, towerSpot);
            }
            rc.broadcast(towerLoc, Movement.convertMapLocationToInt(towerSpot));
            rc.broadcast(needNoiseTower, 0);
        } catch (Exception e) {}
        
        rc.setIndicatorString(0, "Extractor");
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (rc.isActive())
                {
                    if (rc.readBroadcast(towerBuilt) == 1)
                    {
                        Hydralisk hydralisk = new Hydralisk(rc);
                        hydralisk.run();
                    }
                    else if (rc.getLocation().distanceSquaredTo(towerSpot) < 1)
                    {
                        rc.construct(RobotType.NOISETOWER);
                    }
                    else
                    {
                        Movement.MoveMapLocation(rc, towerSpot, false, false);
                    }
                }
            } catch (Exception e) {}
        }
    }
}
