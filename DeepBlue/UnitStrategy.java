package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/22/14.
 */
public abstract class UnitStrategy {

    static MapLocation rallyPoint = Soldiers.cache.MY_HQ;
    static int rallyRecheckInterval = 5;
    static int lastRallyRecheck = -rallyRecheckInterval;

    abstract UnitStrategy update() throws GameActionException;
    abstract void run() throws GameActionException;



    public static void fetchRally(int rallyChannelMod) throws GameActionException
    {
        // Frontliner is the primary fighter strategy go to rally points and fight
        if (Clock.getRoundNum()-lastRallyRecheck > rallyRecheckInterval) {
            MapLocation rallyPointOnWire = VectorFunctions.intToLoc(Soldiers.rc.readBroadcast(Utilities.startRallyPointChannels+rallyChannelMod*2));
            if (rallyPointOnWire.x != rallyPoint.x && rallyPointOnWire.y != rallyPoint.y) {
                Soldiers.nav.setDestination(rallyPointOnWire);
                rallyPoint = rallyPointOnWire;
            }
            lastRallyRecheck = Clock.getRoundNum();
        }
    }
}