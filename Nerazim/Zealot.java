package Nerazim;

import battlecode.common.*;

/**
 * Created by fredkneeland on 1/26/14.
 *
 * Zealots form the backbone of the Nerazim army
 */
public class Zealot
{
    RobotController rc;
    MapLocation target;

    public Zealot(RobotController rc)
    {
        rc.setIndicatorString(0, "Zealot");
        this.rc = rc;
        try
        {
            target = new MapLocation(rc.getMapWidth()/2, rc.getMapHeight()/2);

        } catch (Exception e) {}
    }

    public void run()
    {
        while (true)
        {
            try
            {
                if (FightMicro2.fightMode(rc, null))
                {

                }
                else if (rc.isActive())
                {
                    MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
                    MapLocation[] alliedPastrs = rc.sensePastrLocations(rc.getTeam());
                    if (enemyPastrs.length > 0)
                    {
                        target = enemyPastrs[0];
                    }
                    else if (alliedPastrs.length > 0)
                    {
                        target = alliedPastrs[0];
                        target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                        target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                        target = target.add(target.directionTo(rc.senseEnemyHQLocation()));
                    }


                    Movement.MoveMapLocation(rc, target, false, false);
                }
            } catch (Exception e) {}
            rc.yield();
        }
    }
}
