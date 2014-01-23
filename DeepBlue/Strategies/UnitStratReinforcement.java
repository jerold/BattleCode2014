package DeepBlue.Strategies;

import DeepBlue.Soldiers;
import DeepBlue.UnitStrategy;
import DeepBlue.Utilities;
import battlecode.common.*;

/**
 * Created by AfterHours on 1/22/14.
 */
public abstract class UnitStratReinforcement extends UnitStrategy {
    public static void update() throws GameActionException
    {
        // Reinforement will become what the team needs or if nothing is needed become a frontliner by default

        Soldiers.UnitStrategyType type = Soldiers.UnitStrategyType.values()[Soldiers.rc.readBroadcast(Utilities.unitNeededChannel)];
        if (type == Soldiers.UnitStrategyType.NoType) Soldiers.changeStrategy(Soldiers.UnitStrategyType.FrontLiner);
        else Soldiers.changeStrategy(type);
    }

    public static void run() throws GameActionException {} // The life of a reinforcement unit is short...
}
