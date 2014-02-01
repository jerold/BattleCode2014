package DeepBlue.Strategies;

import DeepBlue.Headquarter;
import DeepBlue.Soldiers;
import DeepBlue.UnitStrategy;
import DeepBlue.Utilities;
import battlecode.common.*;
import firstjoshua.Soldier;

/**
 * Created by AfterHours on 1/22/14.
 */
public abstract class UnitStratReinforcement extends UnitStrategy
{
    static RobotController rc;
    static MapLocation target;
    static MapLocation rallyPoint;
    static MapLocation oldTarget;

    public static void initialize(RobotController rcIn)
    {
        rc = rcIn;
        rallyPoint = rc.senseHQLocation().add(rc.senseEnemyHQLocation().directionTo(rc.senseHQLocation()), 10);
    }

    public static void update() throws GameActionException
    {
        MapLocation[] enemyPastrs = rc.sensePastrLocations(rc.getTeam().opponent());
        int[] get = Soldiers.request.checkForNeed();

        if (enemyPastrs.length > 0)
        {
            Soldiers.changeStrategy(Soldiers.UnitStrategyType.PastrDestroyer);
            UnitStratPastrKiller.initialize(rc);
        }
        else if(get[0] != -1)
        {
            if(get[2] == 0)
            {
                Soldiers.changeStrategy(Soldiers.UnitStrategyType.NoiseTowerBuilder);
                noiseTowerBuilder.initialize(rc, get);
            }
            else
            {
                Soldiers.changeStrategy(Soldiers.UnitStrategyType.PastrBuilder);
                pastrBuilder.initialize(rc, get);
            }
        }
        else
        {
            target = rallyPoint;
        }

        if (oldTarget == null || !oldTarget.equals(target))
        {
            oldTarget = target;
            Soldiers.nav.setDestination(target);
        }
    }
}
