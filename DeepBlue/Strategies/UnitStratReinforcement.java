package DeepBlue.Strategies;

import DeepBlue.Headquarter;
import DeepBlue.Soldiers;
import DeepBlue.UnitStrategy;
import DeepBlue.Utilities;
import battlecode.common.*;

/**
 * Created by AfterHours on 1/22/14.
 */
public abstract class UnitStratReinforcement extends UnitStrategy {

    static int rallyChannelMod = Utilities.ReinforcementRally;

    public static void update() throws GameActionException
    {
        UnitStrategy.fetchRally(rallyChannelMod);

        // Reinforement will become what the team needs or if nothing is needed become a frontliner by default

//        if (Soldiers.cache.nearbyAllies().length > 8) {
            Soldiers.UnitStrategyType type = Soldiers.UnitStrategyType.values()[Soldiers.rc.readBroadcast(Utilities.unitNeededChannel)];
            if (type == Soldiers.UnitStrategyType.NoType) Soldiers.changeStrategy(Soldiers.UnitStrategyType.FrontLiner);
            else Soldiers.changeStrategy(type);
            Soldiers.rc.broadcast(Utilities.unitNeededChannel, Soldiers.UnitStrategyType.NoType.getValue());
//        }
    }

    public static void run() throws GameActionException {} // The life of a reinforcement unit is short...
}
