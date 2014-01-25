package DeepBlue.Strategies;

import DeepBlue.*;
import battlecode.common.*;


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
            MapLocation rallyPointOnWire = VectorFunctions.intToLoc(Soldiers.rc.readBroadcast(Utilities.startRallyPointChannels));
            if (rallyPointOnWire.x != rallyPoint.x && rallyPointOnWire.y != rallyPoint.y) {
                boolean hadArrived = Soldiers.nav.hasArrived;
                rallyPoint = rallyPointOnWire;
                Soldiers.nav.setDestination(rallyPoint);
                fetchNextStep(hadArrived);
            }
            lastRallyRecheck = Clock.getRoundNum();
        }

        // Komakozi check could go here.

        // Retreat to heal check could go here.
    }

    private static void fetchNextStep(boolean hadArrived) throws GameActionException
    {
        int firstStep = Soldiers.rc.readBroadcast(Utilities.startRallyPointChannels+1);
        if (hadArrived && firstStep != RoadMap.NO_PATH_EXISTS) {
            Soldiers.nav.nextStepNodeId = firstStep;
            Soldiers.nav.nextStep = Soldiers.map.locationForNode(firstStep);
        }
    }

    public static void run() throws GameActionException {

    }
}
