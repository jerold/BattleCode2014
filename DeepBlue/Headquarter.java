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

    static MapLocation currentRallyPoint;

    static Direction allDirections[] = Direction.values();
    static int directionalLooks[] = new int[]{0,1,-1,2,-2,3,-3,4};

    public static void run(RobotController inRc) throws GameActionException
    {
        rc = inRc;
        cache = new UnitCache(rc);
        map = new RoadMap(rc, cache);
        System.out.println("My HQ:" + cache.MY_HQ + ", Enemy HQ:" + cache.ENEMY_HQ);

        currentRallyPoint = cache.MY_HQ;

        // TEST RALLY POINT
        setRallyPoint(new MapLocation(map.MAP_WIDTH/2, map.MAP_HEIGHT/2), 0);

        while (true) {
            if (!rc.isActive()) { rc.yield(); continue; }


//            if (Clock.getRoundNum()%200==0)
//                setRallyPoint(new MapLocation((int)(Math.random()*map.MAP_WIDTH), (int)(Math.random()*map.MAP_HEIGHT)), 0);



            cache.reset();
            map.checkForUpdates();

            tryToSpawn();

            rc.yield();
        }
    }

    public static void tryToSpawn() throws GameActionException {
        if(rc.isActive()&&rc.senseRobotCount()<GameConstants.MAX_ROBOTS){ // if(rc.isActive()&&rc.senseRobotCount()<2){
            for(int i=0;i<8;i++){
                Direction trialDir = allDirections[(cache.MY_HQ.directionTo(cache.ENEMY_HQ).ordinal()+i)%8];
                if(rc.canMove(trialDir)){
                    rc.spawn(trialDir);
                    break;
                }
            }
        }
    }

    public static void setRallyPoint(MapLocation rally, int rpNumber) throws GameActionException
    {
        int newRallyFirstStep = RoadMap.NO_PATH_EXISTS;
        if (map.mapUploaded) newRallyFirstStep = map.idForNodeNearOldRallyPointInDirectionOfNewRallyPoint(currentRallyPoint, rally);

        System.out.println("New Rally: "+map.idForNearestNode(rally)+"["+rally+"]  Old Rally: "+map.idForNearestNode(currentRallyPoint)+"["+currentRallyPoint+"] -> "+newRallyFirstStep+"["+map.locationForNode(newRallyFirstStep)+"]");

        rc.broadcast(Utilities.startRallyPointChannels+rpNumber*2, VectorFunctions.locToInt(rally));
        rc.broadcast(Utilities.startRallyPointChannels+rpNumber*2+1, newRallyFirstStep);

        currentRallyPoint = rally;
    }

    public static void setUnitNeeded(Soldiers.UnitStrategyType unitType) throws GameActionException
    {
        rc.broadcast(Utilities.unitNeededChannel, unitType.getValue());
    }
}
