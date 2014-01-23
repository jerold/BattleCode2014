package DeepBlue.Strategies;

import DeepBlue.Soldiers;
import DeepBlue.UnitStrategy;
import DeepBlue.Utilities;
import DeepBlue.VectorFunctions;
import battlecode.common.Clock;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;

/**
 * Created by AfterHours on 1/22/14.
 */
public abstract class UnitStratFrontLiner extends UnitStrategy {
    static MapLocation rallyPoint = Soldiers.cache.MY_HQ;
    static int rallyRecheckInterval = 5;
    static int lastRallyRecheck = -rallyRecheckInterval;

    public static void update() throws GameActionException
    {
        // Frontliner is the primary fighter strategy go to rally points and fight
        if (Clock.getRoundNum()-lastRallyRecheck > rallyRecheckInterval) {
            MapLocation rallyPointOnWire = VectorFunctions.intToLoc(Soldiers.rc.readBroadcast(Utilities.rallyPointChannel1));
            if (rallyPointOnWire.x != rallyPoint.x && rallyPointOnWire.y != rallyPoint.y) {
                rallyPoint = rallyPointOnWire;
                Soldiers.nav.setDestination(rallyPoint);
            }
            lastRallyRecheck = Clock.getRoundNum();
        }

        // Komakozi check could go here.

        // Retreat to heal check could go here.
    }

    public static void run() throws GameActionException {

    }
}
