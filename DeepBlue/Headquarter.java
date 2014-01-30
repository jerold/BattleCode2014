package DeepBlue;

import battlecode.common.*;

/**
 * Created by Jerold Albertson on 1/12/14.
 *
 */
public class Headquarter {
    static RobotController rc;
    static UnitCache cache;
    static RoadMap map;
    static int numbOfSoldiers = 0;
    static boolean started = false;

    static MapLocation[] rallyPoints = new MapLocation[2];

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static void run(RobotController inRc)
    {
        try
        {
            rc = inRc;
            cache = new UnitCache(rc);
            map = new RoadMap(rc, cache);
            System.out.println("My HQ:" + cache.MY_HQ + ", Enemy HQ:" + cache.ENEMY_HQ);

            while (true) {
                try
                {
                    if (rc.isActive())
                    {
                        cache.reset();
                        map.checkForUpdates();

                        tryToSpawn();
                    }
                } catch (Exception e) {}
                rc.yield();
            }
        } catch (Exception e) {}
    }

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){ // if(rc.isActive()&&rc.senseRobotCount()<2){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[(cache.MY_HQ.directionTo(cache.ENEMY_HQ).ordinal()+i)%8];
                if(rc.canMove(trialDir)){
                    setUnitNeeded(null);
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }

    public static void setRallyPoint(MapLocation rally, int rpNumber) throws GameActionException
    {
        if (rallyPoints[rpNumber] != null) System.out.println("New Rally: "+map.idForNearestNode(rally)+"["+rally+"]  Old Rally: "+map.idForNearestNode(rallyPoints[rpNumber])+"["+rallyPoints[rpNumber]+"]");
        rc.broadcast(Utilities.startRallyPointChannels+rpNumber*2, VectorFunctions.locToInt(rally));
        rallyPoints[rpNumber] = rally;
    }

    public static void setUnitNeeded(Soldiers.UnitStrategyType unitType) throws GameActionException
    {
        int type = 0;
        if (numbOfSoldiers < 5)
        {
            type = Utilities.unitNeededScout;
        }
        else
        {
            //type = Utilities.unitNeededHQSurround;
            type = Utilities.unitNeededPastrKiller;
        }

        if (numbOfSoldiers > 10)
        {
            if (!started)
            {
                started = true;
                towerPastrRequest.startBuilding(rc);
            }
            type = Utilities.unitNeededOurPastrKiller;
        }

        numbOfSoldiers++;
        rc.broadcast(Utilities.unitNeededChannel, type);
    }
}
