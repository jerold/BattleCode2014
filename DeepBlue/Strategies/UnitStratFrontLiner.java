package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;


/**
 * Created by AfterHours on 1/22/14.
 */
public abstract class UnitStratFrontLiner extends UnitStrategy {

    static int rallyChannelMod = Utilities.FrontLineRally;

    public static void update() throws GameActionException
    {
        UnitStrategy.fetchRally(rallyChannelMod);

        // Komakozi check could go here.

        // Retreat to heal check could go here.
    }

    public static void run() throws GameActionException {

    }
}
